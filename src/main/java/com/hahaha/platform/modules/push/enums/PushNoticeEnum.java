package com.hahaha.platform.modules.push.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum PushNoticeEnum {

    /**
     * TODO
     * 帖子_小红点
     */
    TOPIC_RED("TOPIC_RED", "帖子_小红点"),
    /**
     * TODO
     * 帖子_回复
     */
    TOPIC_REPLY("TOPIC_REPLY", "帖子_回复"),
    /**
     * 好友_申请
     */
    FRIEND_APPLY("FRIEND_APPLY", "好友_申请"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    PushNoticeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
