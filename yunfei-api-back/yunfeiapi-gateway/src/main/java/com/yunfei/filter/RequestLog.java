package com.yunfei.filter;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author houyunfei
 */
@Data
public class RequestLog {
    // 请求方法
    private String method = null;

    // 请求地址
    private String path = null;

    // 请求url
    private String url;

    // 客户端实际请求地址
    private String originalUrl;

    // 记录请求流量，获取请求内容长度
    private Long requestContentLength = null;

    // 记录响应流量，获取响应内容长度
    private Long responseContentLength = null;

    // 公网ip
    private String ipAddress = null;

    // userId
    private Long userId = null;

    // interfaceInfoId
    private Long interfaceInfoId = null;

    // 响应状态码
    HttpStatus statusCode = null;

    private Long startTime = null;

    private Long endTime = null;

    private String source = null;

    // 请求响应时间
    private Long responseTime = null;
}
