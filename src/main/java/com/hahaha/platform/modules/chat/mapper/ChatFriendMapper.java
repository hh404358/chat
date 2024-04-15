package com.hahaha.platform.modules.chat.mapper;

import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.modules.chat.domain.ChatFriend;
import com.hahaha.platform.modules.chat.vo.FriendVo06;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 好友表 数据库访问层
 *
 * </p>
 */
@Repository
public interface ChatFriendMapper extends BaseDao<ChatFriend> {

    /**
     * 查询列表
     */
    List<ChatFriend> queryList(ChatFriend chatFriend);

    /**
     * 查询好友列表
     */
    List<FriendVo06> friendList(Long userId);

    /**
     * 查询好友id
     */
    List<Long> queryFriendId(Long userId);

}
