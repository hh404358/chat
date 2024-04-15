package com.hahaha.platform.modules.sms.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信类型枚举
 */
@Getter
@AllArgsConstructor
public enum SmsTypeEnum {

    /**
     * 注册
     */
    REGISTERED("1", "chat:code:reg:", 5),
    /**
     * 登录
     */
    LOGIN("2", "chat:code:login:", 5),
    /**
     * 忘记密码
     */
    FORGET("3", "chat:code:forget:", 5),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String prefix;
    private Integer timeout;

}
