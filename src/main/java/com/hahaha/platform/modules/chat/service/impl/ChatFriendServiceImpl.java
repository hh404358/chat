package com.hahaha.platform.modules.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hahaha.platform.common.constant.AppConstants;
import com.hahaha.platform.common.enums.YesOrNoEnum;
import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.redis.RedisUtils;
import com.hahaha.platform.common.shiro.ShiroUtils;
import com.hahaha.platform.common.web.service.impl.BaseServiceImpl;
import com.hahaha.platform.modules.chat.mapper.ChatFriendMapper;
import com.hahaha.platform.modules.chat.domain.*;
import com.hahaha.platform.modules.chat.enums.ApplySourceEnum;
import com.hahaha.platform.modules.chat.enums.ApplyStatusEnum;
import com.hahaha.platform.modules.chat.enums.ApplyTypeEnum;
import com.hahaha.platform.modules.chat.enums.FriendTypeEnum;
import com.hahaha.platform.modules.chat.service.*;
import com.hahaha.platform.modules.chat.vo.*;
import com.hahaha.platform.modules.push.enums.PushMsgEnum;
import com.hahaha.platform.modules.push.service.ChatPushService;
import com.hahaha.platform.modules.push.vo.PushParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 好友表 服务层实现
 *
 * </p>
 */
@Service("chatFriendService")
public class ChatFriendServiceImpl extends BaseServiceImpl<ChatFriend> implements ChatFriendService {

    @Resource
    private ChatFriendMapper chatFriendMapper;

    @Resource
    @Lazy
    private ChatUserService chatUserService;

    @Resource
    private ChatApplyService chatApplyService;

    @Resource
    private ChatPushService chatPushService;

    @Resource
    private ChatGroupService groupService;

    @Resource
    private ChatGroupInfoService groupInfoService;



    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatFriendMapper);
    }

    @Override
    public List<ChatFriend> queryList(ChatFriend t) {
        List<ChatFriend> dataList = chatFriendMapper.queryList(t);
        return dataList;
    }

    @Override
    public FriendVo07 findFriend(String param) {
        // 好友
        ChatUser chatUser;
        // 来源
        ApplySourceEnum sourceEnum = null;

        // 按手机搜索
        if ((chatUser = chatUserService.queryByPhone(param)) != null) {
            sourceEnum = ApplySourceEnum.PHONE;
        }
        // 按微信号搜索
        else if ((chatUser = chatUserService.queryOne(new ChatUser().setChatNo(param))) != null) {
            sourceEnum = ApplySourceEnum.CHAT_NO;
        }
        if (chatUser == null) {
            throw new BaseException("查无此人");
        }
        if (ShiroUtils.getPhone().equals(chatUser.getPhone())) {
            throw new BaseException("不能添加自己为好友");
        }
        FriendVo07 friendVo = formatFriendVo(chatUser);
        if (friendVo.getSource() == null) {
            friendVo.setSource(sourceEnum);
        }
        return friendVo;
    }

    @Transactional
    @Override
    public void applyFriend(FriendVo02 friendVo) {
        //当前登录人
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 验证是否是自己
        if (userId.equals(friendId)) {
            throw new BaseException("你不能添加自己为好友");
        }
        // 查询好友
        ChatUser user = chatUserService.getById(friendId);
        if (user == null) {
            throw new BaseException("好友不存在");
        }
        ChatFriend friend1 = getFriend(userId, friendId);
        ChatFriend friend2 = getFriend(friendId, userId);
        if (friend1 != null && friend2 != null) {
            throw new BaseException("已经是你的好友了，不能重复添加");
        }
        // 申请好友
        chatApplyService.applyFriend(friendId, friendVo.getSource(), friendVo.getReason());
    }

    @Transactional
    @Override
    public void agree(Long applyId) {
        ChatApply apply = verifyApply(applyId);
        ChatUser fromUser = chatUserService.getById(apply.getFromId());
        if (fromUser == null) {
            throw new BaseException("该申请用户已不存在");
        }
        // 更新申请
        chatApplyService.updateById(new ChatApply()
                .setId(apply.getId())
                .setApplyStatus(ApplyStatusEnum.AGREE));
        if (ApplyTypeEnum.FRIEND.equals(apply.getApplyType())) {
            agreeFriend(apply, fromUser);
        } else {
            agreeGroup(apply, fromUser);
        }
    }

    /**
     * 同意朋友
     */
    private void agreeFriend(ChatApply apply, ChatUser fromUser) {
        Long toId = ShiroUtils.getUserId();
        Long fromId = apply.getFromId();
        Date now = DateUtil.date();
        ApplySourceEnum source = apply.getApplySource();
        ChatUser toUser = chatUserService.getById(toId);
        // 添加好友列表
        List<ChatFriend> friendList = new ArrayList<>();
        ChatFriend friend1 = new ChatFriend().setFromId(toId).setToId(fromId);
        if (this.queryOne(friend1) == null) {
            friendList.add(friend1.setCreateTime(now)
                    .setSource(source)
                    .setBlack(YesOrNoEnum.NO)
                    .setTop(YesOrNoEnum.NO)
                    .setRemark(fromUser.getNickName()));
        }
        ChatFriend friend2 = new ChatFriend().setFromId(fromId).setToId(toId);
        if (this.queryOne(friend2) == null) {
            friendList.add(friend2.setCreateTime(now)
                    .setSource(source)
                    .setTop(YesOrNoEnum.NO)
                    .setBlack(YesOrNoEnum.NO)
                    .setRemark(toUser.getNickName()));
        }
        if (CollectionUtils.isEmpty(friendList)) {
            return;
        }
        // 增加好友数据
        this.batchAdd(friendList);
        // 发送通知
        chatPushService.pushMsg(ChatUser.initParam(fromUser).setContent(AppConstants.NOTICE_FRIEND_CREATE).setToId(toId), PushMsgEnum.ALERT);
        chatPushService.pushMsg(ChatUser.initParam(toUser).setContent(AppConstants.NOTICE_FRIEND_CREATE).setToId(fromId), PushMsgEnum.ALERT);
    }

    /**
     * 同意群组
     */
    private void agreeGroup(ChatApply apply, ChatUser fromUser) {
        Long toId = ShiroUtils.getUserId();
        Long fromId = apply.getFromId();
        Long groupId = apply.getTargetId();
        // 查询群
        ChatGroup group = groupService.getById(groupId);
        if (group == null) {
            throw new BaseException(AppConstants.GRUOP_NOT_EXIST);
        }
        if (!group.getMaster().equals(toId)) {
            throw new BaseException(AppConstants.NO_PARAM);
        }
        ChatGroupInfo groupInfo = groupInfoService.getGroupInfo(groupId, fromId, YesOrNoEnum.NO);
        // 加群
        if (groupInfo == null) {
            groupInfoService.add(new ChatGroupInfo(fromId, groupId));
        }
        // 发送通知
        String content = StrUtil.format(AppConstants.NOTICE_GROUP_JOIN, fromUser.getNickName());
        List<PushParamVo> pushParamList = groupService.queryGroupPushFrom(groupId, null, content);
        chatPushService.pushMsg(pushParamList, PushMsgEnum.ALERT);
    }

    @Override
    public void refused(Long applyId) {
        ChatApply apply = verifyApply(applyId);
        // 更新申请
        chatApplyService.updateById(new ChatApply().setId(apply.getId()).setApplyStatus(ApplyStatusEnum.REFUSED));
    }

    @Override
    public void ignore(Long applyId) {
        ChatApply apply = verifyApply(applyId);
        // 更新申请
        chatApplyService.updateById(new ChatApply().setId(apply.getId()).setApplyStatus(ApplyStatusEnum.IGNORE));
    }

    @Override
    public void setBlack(FriendVo03 friendVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        this.updateById(new ChatFriend().setId(friend.getId()).setBlack(friendVo.getBlack()));
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Transactional
    @Override
    public void delFriend(Long friendId) {
        Long userId = ShiroUtils.getUserId();
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        this.deleteById(friend.getId());
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Override
    public void setRemark(FriendVo05 friendVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        ChatFriend cf = new ChatFriend().setId(friend.getId()).setRemark(friendVo.getRemark());
        this.updateById(cf);
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Override
    public void setTop(FriendVo09 friendVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        this.updateById(new ChatFriend().setId(friend.getId()).setTop(friendVo.getTop()));
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Override
    public List<FriendVo06> friendList(String param) {
        List<FriendVo06> list = chatFriendMapper.friendList(ShiroUtils.getUserId());
        List<FriendVo06> dataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            dataList.addAll(list);
        }
        if (!StringUtils.isEmpty(param)) {
            // 过滤
            dataList = dataList.stream().filter(data -> data.getNickName().contains(param)).collect(Collectors.toList());
        }
        return dataList;
    }

    /**
     * 格式化好友
     */
    private FriendVo07 formatFriendVo(ChatUser chatUser) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatUser.getUserId();
        FriendVo07 friendVo = BeanUtil.toBean(chatUser, FriendVo07.class);
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            return friendVo;
        }
        if (getFriend(friendId, userId) != null) {
            friendVo.setIsFriend(YesOrNoEnum.YES);
        }
        return friendVo.setBlack(friend.getBlack())
                .setNickName(friend.getRemark())
                .setSource(friend.getSource());
    }

    @Override
    public FriendVo07 getInfo(Long friendId) {
        Long userId = ShiroUtils.getUserId();
        ChatUser chatUser = chatUserService.getById(friendId);
        if (chatUser == null) {
            throw new BaseException("用户信息不存在");
        }
        if (userId.equals(friendId)) {
            FriendVo07 friendVo = BeanUtil.toBean(chatUser, FriendVo07.class);
            return friendVo.setIsFriend(YesOrNoEnum.YES)
                    .setSource(ApplySourceEnum.SYS)
                    .setUserType(FriendTypeEnum.SELF);
        }
        return formatFriendVo(chatUser);
    }

    /**
     * 校验申请
     */
    private ChatApply verifyApply(Long applyId) {
        ChatApply apply = chatApplyService.getById(applyId);
        if (apply == null || !ShiroUtils.getUserId().equals(apply.getToId())
                || !ApplyStatusEnum.NONE.equals(apply.getApplyStatus())) {
            throw new BaseException("申请已过期，请刷新后重试");
        }
        return apply;
    }

    @Override
    public ChatFriend getFriend(Long userId, Long friendId) {
        String key = makeFriendKey(userId, friendId);
        if (redisUtils.hasKey(key)) {
            return JSONUtil.toBean(redisUtils.get(key), ChatFriend.class);
        }
        ChatFriend friend = queryOne(new ChatFriend().setFromId(userId).setToId(friendId));
        if (friend == null) {
            return null;
        }
        redisUtils.set(key, JSONUtil.toJsonStr(friend), AppConstants.REDIS_FRIEND_TIME, TimeUnit.DAYS);
        return friend;
    }

    @Override
    public List<Long> queryFriendId(Long userId) {
        return chatFriendMapper.queryFriendId(userId);
    }

    /**
     * 生成好友缓存
     */
    private String makeFriendKey(Long userId, Long friendId) {
        return StrUtil.format(AppConstants.REDIS_FRIEND, userId, friendId);
    }

    /**
     * 删除好友缓存
     */
    private void delFriendCache(Long userId, Long friendId) {
        redisUtils.delete(makeFriendKey(userId, friendId));
    }

}
