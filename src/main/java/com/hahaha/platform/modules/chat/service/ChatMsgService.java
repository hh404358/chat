package com.hahaha.platform.modules.chat.service;

import com.hahaha.platform.common.web.service.BaseService;
import com.hahaha.platform.modules.chat.domain.ChatMsg;
import com.hahaha.platform.modules.chat.vo.ChatVo01;
import com.hahaha.platform.modules.chat.vo.ChatVo02;
import com.hahaha.platform.modules.chat.vo.ChatVo03;

/**
 * 聊天消息 服务层
 */
public interface ChatMsgService extends BaseService<ChatMsg> {

    /**
     * 发送消息
     */
    ChatVo03 sendFriendMsg(ChatVo01 chatVo);

    /**
     * 发送群消息
     */
    ChatVo03 sendGroupMsg(ChatVo02 chatVo);

}
