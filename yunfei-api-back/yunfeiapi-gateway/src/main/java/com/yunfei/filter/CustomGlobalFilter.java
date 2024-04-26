package com.yunfei.filter;

import com.yunfei.service.GatewayService;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceLog;
import com.yunfei.yunfeiapicommon.model.entity.User;
import com.yunfei.yunfeiapicommon.service.InnerInterfaceInfoService;
import com.yunfei.yunfeiapicommon.service.InnerInterfaceLogService;
import com.yunfei.yunfeiapicommon.service.InnerUserInterfaceInfoService;
import com.yunfei.yunfeiapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;


/**
 * 自定义全局过滤器
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;


    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerInterfaceLogService interfaceLogService;

    @Resource
    private GatewayService gatewayService;


    /**
     * 设置执行顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        // 数字越低越先执行
        return -1;
    }

    /**
     * 过滤器执行逻辑
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.记录请求日志
        RequestLog requestLog = gatewayService.recordRequestLog(exchange);

        // 2.请求拦截 - 黑白名单
        boolean flag = gatewayService.isInBlackIpList(exchange, chain, requestLog);
        if (flag) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // 3,用户鉴权（判断 ak、sk 是否合法）
        User invokeUser = gatewayService.checkInvokeUserAuth(exchange, chain);

        // 4.请求的模拟接口是否存在？
        InterfaceInfo interfaceInfo = gatewayService.checkInterfaceInfoStatus(exchange, chain, invokeUser, requestLog);
        Long interfaceInfoId = interfaceInfo.getId();

        return handleResponse(exchange, chain, interfaceInfoId, invokeUser.getId(), requestLog);
    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @param requestLog
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, Long interfaceId, Long userId, RequestLog requestLog) {
        HttpStatus statusCode = requestLog.getStatusCode();
        // 获取原始的响应对象
        ServerHttpResponse originalResponse = exchange.getResponse();
        // 获取数据缓冲工厂
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        // 获取响应的状态码
        statusCode = originalResponse.getStatusCode();
        // 判断状态码是否为200 OK
        log.info("响应状态码：{}", statusCode.value());
        requestLog.setStatusCode(statusCode);
        if (statusCode == HttpStatus.OK) {
            // 创建一个装饰后的响应对象(开始穿装备，增强能力)
            log.info("成功调用方法，开始响应处理");
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                // 重写writeWith方法，用于处理响应体的数据
                // 这段方法就是只要当我们的模拟接口调用完成之后,等它返回结果，
                // 就会调用writeWith方法,我们就能根据响应结果做一些自己的处理
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    log.info("响应体是否是Flux类型： {}", (body instanceof Flux));
                    // 判断响应体是否是Flux类型
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        // 返回一个处理后的响应体
                        // (这里就理解为它在拼接字符串,它把缓冲区的数据取出来，一点一点拼接好)
                        return super.writeWith(fluxBody.collectList().flatMap(list -> {
                            log.info("响应结果：" + list);
                            gatewayService.invokeInterfaceCount(interfaceId, userId);

                            // 读取响应体的内容并转换为字节数组
                            byte[] content = null;
                            if (list.size() != 1) {
                                content = list.stream().map(DataBuffer::asByteBuffer).reduce((buffer1, buffer2) -> {
                                    ByteBuffer mergedBuffer = ByteBuffer.allocate(buffer1.remaining() + buffer2.remaining());
                                    mergedBuffer.put(buffer1);
                                    mergedBuffer.put(buffer2);
                                    mergedBuffer.flip();
                                    return mergedBuffer;
                                }).orElse(ByteBuffer.allocate(0)).array();
                            } else {
                                // 处理单个数据块
                                DataBuffer dataBuffer = list.get(0);
                                // 读取响应体的内容并转换为字节数组
                                content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer); // 释放掉内存
                            }
                            // 在这里执行您的后续处理逻辑
                            // 读取响应体的内容并转换为字节数组
                            // 得到响应体
                            requestLog.setResponseContentLength((long) content.length);
                            String data = new String(content, StandardCharsets.UTF_8);

                            // 打印日志
                            log.info("响应结果：{}", data);

                            // 返回一个包含处理后的响应体的 DataBuffer
                            return Mono.just(bufferFactory.wrap(content)).doFinally(signalType -> logSave(requestLog));
                        }));
                    } else {
                        log.error("网关处理响应异常{}", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            // 对于200 OK的请求,将装饰后的响应对象传递给下一个过滤器链,并继续处理(设置repsonse对象为装饰过的) 记录成功日志 获取响应长度
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }
        // 对于非200 OK的请求，直接返回，进行降级处理
        return chain.filter(exchange).doFinally(signalType -> logSave(requestLog));
    }

    private void logSave(RequestLog requestLog) {
        log.info("开始存储日志......");
        long endTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        requestLog.setEndTime(endTime);
        long startTime = requestLog.getStartTime();

        // 计算响应时间
        long responseTime = endTime - startTime;
        requestLog.setResponseTime(responseTime);

        log.info("请求开始时间：{}，请求结束时间：{}，请求耗时：{}ms", startTime, endTime, responseTime);
        interfaceLogSave(requestLog);
        log.info("接口调用日志存储完成......");
    }

    private void interfaceLogSave(RequestLog requestLog) {
        String method = requestLog.getMethod();
        String originalUrl = requestLog.getOriginalUrl();
        Long requestContentLength = requestLog.getRequestContentLength();
        Long responseContentLength = requestLog.getResponseContentLength();
        String ipAddress = requestLog.getIpAddress();
        Long userId = requestLog.getUserId();
        Long interfaceInfoId = requestLog.getInterfaceInfoId();
        HttpStatus statusCode = requestLog.getStatusCode();
        Long startTime = requestLog.getStartTime();
        Long responseTime = requestLog.getResponseTime();

        InterfaceLog interfaceLog = new InterfaceLog();
        interfaceLog.setInterfaceId(interfaceInfoId);
        interfaceLog.setRequestTime(new Date(startTime));
        interfaceLog.setRequestMethod(method);
        interfaceLog.setRequestUrl(originalUrl);
        interfaceLog.setRequestContentLength(requestContentLength);
        interfaceLog.setResponseStatusCode((long) statusCode.value());
        interfaceLog.setRequestContentLength(responseContentLength);
        interfaceLog.setUserId(userId);
        interfaceLog.setClientIp(ipAddress);
        interfaceLog.setRequestDuration(responseTime);
        interfaceLogService.saveInterfaceLog(interfaceLog);
    }
}
