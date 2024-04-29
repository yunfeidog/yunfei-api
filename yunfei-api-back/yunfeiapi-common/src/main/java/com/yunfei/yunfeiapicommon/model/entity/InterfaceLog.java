package com.yunfei.yunfeiapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 接口调用记录表
 *
 * @TableName interface_log
 */
@TableName(value = "interface_log")
@Data
public class InterfaceLog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 请求长度
     */
    private Long requestContentLength;

    /**
     * 响应长度
     */
    private Long responseStatusCode;
    /**
     * 响应长度
     */
    private Long responseContentLength;

    /**
     * 请求处理时间
     */
    private Long requestDuration;

    /**
     * 用户ip
     */
    private String clientIp;

    /**
     * 调用来源
     */
    private String source;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0-未删除 1-删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
