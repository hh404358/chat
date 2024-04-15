package com.hahaha.platform.modules.chat.mapper;

import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.modules.chat.domain.ChatMsg;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 聊天消息 数据库访问层
 *
 * </p>
 */
@Repository
public interface ChatMsgMapper extends BaseDao<ChatMsg> {

    /**
     * 查询列表
     */
    List<ChatMsg> queryList(ChatMsg chatMsg);

}
