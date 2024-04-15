package com.hahaha.platform.modules.chat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 好友类型
 */
@Getter
public enum FriendTypeEnum {

    /**
     * 正常
     */
    NORMAL("normal", "正常"),
    /**
     * 自己
     */
    SELF("self", "自己"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    FriendTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
