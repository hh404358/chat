package com.hahaha.platform.modules.chat.service;

import com.hahaha.platform.common.web.service.BaseService;
import com.hahaha.platform.modules.chat.domain.ChatFriend;
import com.hahaha.platform.modules.chat.vo.*;

import java.util.List;

/**
 * <p>
 * 好友表 服务层
 *
 * </p>
 */
public interface ChatFriendService extends BaseService<ChatFriend> {

    /**
     * 搜索好友
     */
    FriendVo07 findFriend(String param);

    /**
     * 添加好友
     */
    void applyFriend(FriendVo02 friendVo);

    /**
     * 同意申请
     */
    void agree(Long applyId);

    /**
     * 拒绝申请
     */
    void refused(Long applyId);

    /**
     * 忽略申请
     */
    void ignore(Long applyId);

    /**
     * 设置黑名单
     */
    void setBlack(FriendVo03 friendVo);

    /**
     * 删除好友
     */
    void delFriend(Long friendId);

    /**
     * 设置备注
     */
    void setRemark(FriendVo05 friendVo);

    /**
     * 是否置顶
     */
    void setTop(FriendVo09 friendVo);

    /**
     * 好友列表
     */
    List<FriendVo06> friendList(String param);

    /**
     * 好友详情
     */
    FriendVo07 getInfo(Long friendId);

    /**
     * 获取好友信息
     */
    ChatFriend getFriend(Long userId, Long friendId);

    /**
     * 查询好友id
     */
    List<Long> queryFriendId(Long userId);

}
