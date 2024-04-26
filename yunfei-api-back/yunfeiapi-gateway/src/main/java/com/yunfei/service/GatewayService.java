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

    InterfaceInfo checkInterfaceInfoStatus(ServerWebExchange exchange, GatewayFilterChain chain, User invokeUser, RequestLog requestLog);
}
