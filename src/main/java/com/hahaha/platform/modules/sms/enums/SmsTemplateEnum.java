package com.hahaha.platform.modules.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信模板枚举
 */
@Getter
@AllArgsConstructor
public enum SmsTemplateEnum {

    /**
     * 验证码-00
     */
    VERIFY_CODE("SMS_209335004", "验证码-00"),
    ;

    private String code;
    private String info;



}
