package com.yunfei.yunfeiapiclientsdk.exception;

public class GlobalApiException extends Exception{

    private int code;

    public GlobalApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public GlobalApiException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
