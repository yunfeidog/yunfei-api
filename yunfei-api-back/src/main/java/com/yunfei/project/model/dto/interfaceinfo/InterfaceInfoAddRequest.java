package com.yunfei.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口创建请求
 *
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应参数
     */
    private String responseParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 返回格式类型
     */
    private String returnFormat;

    /**
     * 消耗金币
     */
    private Long consumeCoins;

    /**
     * 请求示例
     */
    private String requestExample;
}
