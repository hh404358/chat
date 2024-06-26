package com.hahaha.platform.modules.chat.vo;

import com.hahaha.platform.common.enums.YesOrNoEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GroupVo06 {

    @NotNull(message = "群id不能为空")
    private Long groupId;

    @NotNull(message = "状态不能为空")
    private YesOrNoEnum keepGroup;
}
