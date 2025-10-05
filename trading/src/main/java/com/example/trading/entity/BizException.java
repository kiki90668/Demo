package com.example.trading.entity;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException { //自定義商業錯誤
    private int code;
    private String message;

    public BizException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException(int code ,Throwable cause) {
        super(cause);
        this.code = code;
        this.message = cause.getMessage();
    }

    public BizException(int code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }
}
