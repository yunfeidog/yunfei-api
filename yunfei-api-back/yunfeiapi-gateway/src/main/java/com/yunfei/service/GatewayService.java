package com.yunfei.service;

import com.yunfei.filter.RequestLog;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import com.yunfei.yunfeiapicommon.model.entity.User;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author houyunfei
 */
public interface GatewayService {

    /**
     * 获取本次请求的日志信息
     *
     * @param exchange
     * @return
     */
    RequestLog recordRequestLog(ServerWebExchange exchange);

    /**
     * 请求拦截 主要是黑名单
     *
     * @param exchange
     * @param chain
     * @param requestLog
     * @return true:拦截 false:不拦截
     */
    boolean isInBlackIpList(ServerWebExchange exchange, GatewayFilterChain chain, RequestLog requestLog);

    /**
     * 检查用户是否有权限访问
     *
     * @param exchange
     * @param chain
     * @return 用户信息
     */
    User checkInvokeUserAuth(ServerWebExchange exchange, GatewayFilterChain chain);

    /**
     * 检查接口信息是否正常 是否可以调用等信息
     *
     * @param exchange
     * @param chain
     * @param invokeUser
     * @param requestLog
     * @return
     */
    InterfaceInfo checkInterfaceInfoStatus(ServerWebExchange exchange, GatewayFilterChain chain, User invokeUser, RequestLog requestLog);

    /**
     * 调用接口成功之后 接口调用次数+1 扣除用户金币
     *
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean invokeInterfaceCount(Long interfaceId, Long userId);
}
