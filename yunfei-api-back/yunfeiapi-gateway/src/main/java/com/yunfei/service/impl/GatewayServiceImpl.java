package com.yunfei.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.yunfei.exception.BusinessException;
import com.yunfei.filter.RequestLog;
import com.yunfei.service.GatewayService;
import com.yunfei.yunfeiapiclientsdk.exception.ErrorCode;
import com.yunfei.yunfeiapiclientsdk.utils.SignUtils;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceLog;
import com.yunfei.yunfeiapicommon.model.entity.User;
import com.yunfei.yunfeiapicommon.model.enums.InterfaceInfoStatusEnum;
import com.yunfei.yunfeiapicommon.service.InnerInterfaceInfoService;
import com.yunfei.yunfeiapicommon.service.InnerInterfaceLogService;
import com.yunfei.yunfeiapicommon.service.InnerUserInterfaceInfoService;
import com.yunfei.yunfeiapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.yunfei.yunfeiapiclientsdk.exception.ErrorCode.NOT_FOUND_ERROR;


/**
 * @author houyunfei
 */
@Service
@Slf4j
public class GatewayServiceImpl implements GatewayService {


    private static final List<String> blackList = Arrays.asList();

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerInterfaceLogService interfaceLogService;


    @Override
    public RequestLog recordRequestLog(ServerWebExchange exchange) {
        log.info("开始记录GateWay网关全局过滤器请求日志信息==========================================================>");
        // 1.记录请求日志
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        Long startTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        // 记录请求流量，获取请求内容长度
        long requestContentLength = request.getHeaders().getContentLength();
        // /api/name/user
        String path = request.getPath().value();
        String method = request.getMethod().toString();
        String url = request.getURI().toString().trim();
        // 获取公网ip
        String ipAddress = headers.getFirst("X-Real-IP");
        URI requestURI = request.getURI();
        String originalUrl = requestURI.getScheme() + "://" + requestURI.getAuthority() + requestURI.getPath();
        if (StringUtils.isBlank(ipAddress)) {
            log.info("未携带X-Real-IP请求头，尝试获取");
            ipAddress = request.getRemoteAddress().getHostString();
        }
        if (StringUtils.isBlank(ipAddress)) {
            log.info("未获取到ip地址");
            // throw new BusinessException(NOT_FOUND_ERROR, "获取公网ip失败");
        }
        String hostString = request.getLocalAddress().getHostString();
        long contentLength = headers.getContentLength();
        log.info("contentLength：{}", contentLength);
        String source = headers.getFirst("source");
        log.info("source：{}", source);

        log.info("请求ip地址：{}", ipAddress);
        log.info("请求唯一标识：{}", request.getId());
        log.info("请求路径：{}", path);
        log.info("请求实际url：{}", url);
        log.info("原始地址originalUrl:{}", originalUrl);
        log.info("请求方式：{}", method);
        log.info("QueryParams：{}", request.getQueryParams());
        log.info("请求流量（bytes）：{}", requestContentLength);
        log.info("请求来源地址：{}", hostString);
        log.info("<=====================================================================================================================");
        RequestLog requestLog = new RequestLog();
        requestLog.setStartTime(startTime);
        requestLog.setRequestContentLength(requestContentLength);
        requestLog.setPath(path);
        requestLog.setMethod(method);
        requestLog.setUrl(url);
        requestLog.setIpAddress(ipAddress);
        requestLog.setOriginalUrl(originalUrl);
        requestLog.setSource(source);
        return requestLog;
    }

    @Override
    public boolean isInBlackIpList(ServerWebExchange exchange, GatewayFilterChain chain, RequestLog requestLog) {
        // 1.黑白名单
        String hostString = exchange.getRequest().getLocalAddress().getHostString();
        if (blackList.contains(hostString)) {
            log.info("请求被拦截,处于黑名单，请求来源地址：{}", hostString);
            return true;
        }
        return false;
    }

    @Override
    public User checkInvokeUserAuth(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String body = headers.getFirst("body");
        body = new String(body.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");

        // Api密钥是否分配给用户校验
        User invokeUser = null;
        invokeUser = innerUserService.getInvokeUser(accessKey);
        if (ObjUtil.isEmpty(invokeUser)) {
            throw new BusinessException(NOT_FOUND_ERROR, "请求用户密钥不存在");
        }
        if (invokeUser.getRemainCoins() < 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户金币不足");
        }
        if (!invokeUser.getAccessKey().equals(accessKey)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "API密钥accessKey解析失败");
        }

        // 随机数不得大于一万
        if (Integer.parseInt(nonce) > 10000) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "API密钥解析失败");
        }

        // 防重发，时间和当前时间不得超过五分钟
        if (timestamp != null && System.currentTimeMillis() - Long.parseLong(timestamp) > 300000) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "API密钥解析失败");
        }

        // 解析sign，查询secretKey是否和header中的一致
        String secretKeyInServer = invokeUser.getSecretKey();
        String signInServer = SignUtils.genSign(body, secretKeyInServer);

        // 检查请求中的签名是否为空，或者是否与服务器的签名不一致
        if (sign == null || !sign.equals(signInServer)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "API密钥解析失败");
        }
        return invokeUser;
    }

    @Override
    public InterfaceInfo checkInterfaceInfoStatus(ServerWebExchange exchange, GatewayFilterChain chain, User invokeUser, RequestLog requestLog) {
        String method = requestLog.getMethod();
        String originalUrl = requestLog.getOriginalUrl();
        ServerHttpRequest request = exchange.getRequest();

        if ("GET".equals(method)) {
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            log.info("GET请求参数：{}", queryParams);
        } else if ("POST".equals(method)) {
            log.info("POST请求参数：");
        }
        // 数据库查询模拟接口是否存在，以及请求方法是否匹配 这里要使用原始Url
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(originalUrl, method);
        if (ObjUtil.isEmpty(interfaceInfo)) {
            throw new BusinessException(NOT_FOUND_ERROR, "请求接口不存在");
        }
        // 判断接口是否可用
        if (InterfaceInfoStatusEnum.OFFLINE.getValue() == (interfaceInfo.getStatus())) {
            throw new BusinessException(NOT_FOUND_ERROR, "接口未上线");
        }

        // 判断用户是否有足够的金币调用接口
        if (invokeUser.getRemainCoins() < interfaceInfo.getConsumeCoins()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户金币不足");
        }
        log.info("用户id：{}，接口id：{}", invokeUser.getId(), interfaceInfo.getId());
        requestLog.setInterfaceInfoId(interfaceInfo.getId());
        return interfaceInfo;
    }

    @Override
    public boolean invokeInterfaceCount(Long interfaceId, Long userId) {
        // 用户调用该 接口调用次数+1
        boolean invoke = innerUserInterfaceInfoService.invokeCount(interfaceId, userId);
        log.info("网关调用接口调用次数+1: {}", interfaceId);
        //  用户金币-1
        boolean consume = innerUserService.consumeCoins(userId, interfaceId);
        // 该接口的总调用次数+1
        boolean invokeCount = innerInterfaceInfoService.invokeCount(interfaceId);
        return invoke && consume && invokeCount;
    }

    @Override
    public void logSave(RequestLog requestLog) {
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
        interfaceLog.setSource(requestLog.getSource());
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
