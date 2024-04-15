package com.hahaha.platform.common.exception;

import com.hahaha.platform.common.enums.ResultCodeEnum;
import lombok.Getter;

/**
 * 自定义异常
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    @Getter
    private ResultCodeEnum resultCode;

    /**
     * 错误消息
     */
    private String message;


    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(ResultCodeEnum resultCode) {
        super(resultCode.getInfo());
        this.resultCode = resultCode;
        this.message = resultCode.getInfo();

    }

    public BaseException(ResultCodeEnum resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
        this.message = message;
    }



}
