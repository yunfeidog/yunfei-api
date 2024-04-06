package com.yunfei.yunfeiapiclientsdk.exception;

import lombok.Data;

/**
 * 异常响应
 */
@Data
public class ErrorResponse {

    private int code;

    private String message;
}
