package com.hahaha.platform.common.web.controller;

import com.hahaha.platform.common.aspectj.IgnoreAuth;
import com.hahaha.platform.common.core.EnumUtils;
import com.hahaha.platform.common.enums.ResultCodeEnum;
import com.hahaha.platform.common.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误请求处理
 */
@RestController
@RequestMapping("/error")
@Slf4j
public class ErrorController {

    @IgnoreAuth
    @RequestMapping("/{code}")
    public AjaxResult error(@PathVariable String code) {
        ResultCodeEnum resultCode = EnumUtils.toEnum(ResultCodeEnum.class, code, ResultCodeEnum.FAIL);
        switch (resultCode) {
            case SUCCESS:
                return AjaxResult.success();
            default:
                return AjaxResult.result(resultCode);
        }

    }

}
