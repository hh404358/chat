package com.hahaha.platform.modules.chat.service;

import com.github.pagehelper.PageInfo;
import com.hahaha.platform.common.web.service.BaseService;
import com.hahaha.platform.modules.chat.domain.ChatApply;
import com.hahaha.platform.modules.chat.enums.ApplySourceEnum;
import com.hahaha.platform.modules.chat.vo.ApplyVo03;

/**
 * <p>
 * 好友申请表 服务层
 *
 * </p>
 */
public interface ChatApplyService extends BaseService<ChatApply> {

    /**
     * 申请好友
     */
    void applyFriend(Long acceptId, ApplySourceEnum source, String reason);

    /**
     * 申请记录
     */
    PageInfo list();

    /**
     * 查询详情
     */
    ApplyVo03 getInfo(Long applyId);
}
