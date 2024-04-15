package com.hahaha.platform.common.web.exception;

import com.hahaha.platform.common.enums.ResultCodeEnum;
import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        log.error("全局异常:" + e.getMessage());
        /**
         * 路径不存在
         */
        if (e instanceof NoHandlerFoundException
                || e instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            return AjaxResult.result(ResultCodeEnum.NOT_FOUND);
        }
        /**
         * 校验异常
         */
        if (e instanceof MethodArgumentNotValidException) {
            return AjaxResult.fail(((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage());
        }
        /**
         * 自定义异常
         */
        if (e instanceof BaseException) {
            return AjaxResult.fail(e.getMessage());
        }
        return AjaxResult.fail();
    }

}
