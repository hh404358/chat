package com.hahaha.platform.modules.chat.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hahaha.platform.common.constant.AppConstants;
import com.hahaha.platform.common.enums.YesOrNoEnum;
import com.hahaha.platform.common.shiro.ShiroUtils;
import com.hahaha.platform.common.utils.TimerUtils;
import com.hahaha.platform.common.web.service.impl.BaseServiceImpl;
import com.hahaha.platform.modules.chat.mapper.ChatMsgMapper;
import com.hahaha.platform.modules.chat.domain.*;
import com.hahaha.platform.modules.chat.enums.FriendTypeEnum;
import com.hahaha.platform.modules.chat.enums.MsgStatusEnum;
import com.hahaha.platform.modules.chat.service.*;
import com.hahaha.platform.modules.chat.vo.ChatVo01;
import com.hahaha.platform.modules.chat.vo.ChatVo02;
import com.hahaha.platform.modules.chat.vo.ChatVo03;
import com.hahaha.platform.modules.chat.vo.ChatVo04;
import com.hahaha.platform.modules.push.enums.PushMsgEnum;
import com.hahaha.platform.modules.push.enums.PushTalkEnum;
import com.hahaha.platform.modules.push.service.ChatPushService;
import com.hahaha.platform.modules.push.vo.PushParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 聊天消息 服务层实现
 *
 * </p>
 */
@Service("chatMsgService")
public class ChatMsgServiceImpl extends BaseServiceImpl<ChatMsg> implements ChatMsgService {

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private ChatFriendService friendService;

    @Autowired
    private ChatGroupService groupService;

    @Autowired
    private ChatGroupInfoService groupInfoService;

    @Autowired
    private ChatPushService chatPushService;

    @Autowired
    private ChatUserService chatUserService;


    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatMsgMapper);
    }

    @Override
    public List<ChatMsg> queryList(ChatMsg t) {
        List<ChatMsg> dataList = chatMsgMapper.queryList(t);
        return dataList;
    }

    @Transactional
    @Override
    public ChatVo03 sendFriendMsg(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        // 自己给自己发消息
        if (userId.equals(friendId)) {
            return self(chatVo);
        }
        // 发送给好友的消息
        return friend(chatVo);
    }

    /**
     * 保存消息
     */
    private ChatMsg saveMsg(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        ChatMsg chatMsg = new ChatMsg()
                .setFromId(userId)
                .setToId(chatVo.getUserId())
                .setMsgType(chatVo.getMsgType())
                .setTalkType(PushTalkEnum.SINGLE)
                .setContent(chatVo.getContent())
                .setCreateTime(DateUtil.date());
        this.add(chatMsg);
        return chatMsg;
    }

    /**
     * 自己消息
     */
    private ChatVo03 self(ChatVo01 chatVo) {
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        // 组装推送
        PushParamVo paramVo = ChatUser.initParam(chatUserService.getById(userId))
                .setUserType(FriendTypeEnum.SELF)
                .setContent(content)
                .setToId(friendId)
                .setMsgId(IdWorker.getId());
        // 推送
        // 异步执行
        TimerUtils.instance().addTask((timeout) -> {
            // 推送
            chatPushService.pushMsg(paramVo, msgType);
        }, 2, TimeUnit.SECONDS);
        // 返回结果(好友详情）
        return doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }

    /**
     * 好友消息
     */
    private ChatVo03 friend(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        // 校验好友
        ChatUser toUser = chatUserService.getById(friendId);
        if (toUser == null) {
            return doResult(MsgStatusEnum.FRIEND_DELETED);
        }
        ChatFriend friend1 = friendService.getFriend(userId, friendId);
        if (friend1 == null) {
            return doResult(MsgStatusEnum.FRIEND_TO);
        }
        ChatFriend friend2 = friendService.getFriend(friendId, userId);
        if (friend2 == null) {
            return doResult(MsgStatusEnum.FRIEND_FROM);
        }
        if (YesOrNoEnum.YES.equals(friend2.getBlack())) {
            return doResult(MsgStatusEnum.FRIEND_BLACK);
        }

        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        // 组装推送
        PushParamVo paramVo = ChatUser.initParam(chatUserService.getById(userId))
                .setNickName(friend2.getRemark())
                .setTop(friend2.getTop())
                .setContent(content)
                .setToId(friendId)
                .setMsgId(chatMsg.getId());
        ChatVo04 chatVo04 = null;
        if (PushMsgEnum.TRTC_VOICE_START.equals(msgType)
                || PushMsgEnum.TRTC_VIDEO_START.equals(msgType)) {
            chatVo04 = new ChatVo04()
                    .setUserId(friendId)
                    .setTrtcId(AppConstants.REDIS_TRTC_USER + friendId)
                    .setPortrait(toUser.getPortrait())
                    .setNickName(friend1.getRemark());
        }
        // 推送
        chatPushService.pushMsg(paramVo, msgType);
        return doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId())
                .setUserInfo(chatVo04);
    }


    @Override
    public ChatVo03 sendGroupMsg(ChatVo02 chatVo) {
        String content = chatVo.getContent();
        Long fromId = ShiroUtils.getUserId();
        Long groupId = chatVo.getGroupId();
        // 查询群组
        ChatGroup group = groupService.getById(groupId);
        if (group == null) {
            return doResult(MsgStatusEnum.GROUP_NOT_EXIST);
        }
        // 查询群明细
        ChatGroupInfo groupInfo = groupInfoService.getGroupInfo(groupId, fromId, YesOrNoEnum.NO);
        if (groupInfo == null) {
            return doResult(MsgStatusEnum.GROUP_INFO_NOT_EXIST);
        }
        // 保存数据
        ChatMsg chatMsg = new ChatMsg()
                .setFromId(fromId)
                .setToId(groupId)
                .setMsgType(chatVo.getMsgType())
                .setTalkType(PushTalkEnum.GROUP)
                .setContent(content)
                .setCreateTime(DateUtil.date());
        this.add(chatMsg);
        // 查询群列表
        List<PushParamVo> userList = groupService.queryFriendPushFrom(chatMsg);
        // 群信息
        PushParamVo groupUser = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(group.getName())
                .setPortrait(group.getPortrait());
        // 推送
        chatPushService.pushGroupMsg(userList, groupUser, chatVo.getMsgType());
        return doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }

    /**
     * 把聊天消息全部转为chatvo3作为返回发送结果
     */
    private ChatVo03 doResult(MsgStatusEnum status) {
        return new ChatVo03().setStatus(status);
    }

}
