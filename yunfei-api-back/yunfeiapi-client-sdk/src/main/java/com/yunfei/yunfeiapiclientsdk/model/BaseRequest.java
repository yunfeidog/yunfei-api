package com.yunfei.yunfeiapiclientsdk.model;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;

import java.util.Map;

/**
 * 基础请求
 *
 * @author PYW
 */
@Data
public class BaseRequest {

    private String path;

    private String method;

    private Map<String, Object> requestParams;

    private HttpServletRequest userRequest;
}
