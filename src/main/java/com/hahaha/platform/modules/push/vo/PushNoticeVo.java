package com.hahaha.platform.modules.push.vo;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 推送对象
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushNoticeVo {


    /**
     * 好友_申请
     */
    private Dict friendApply = Dict.create();

}
