package com.hahaha.platform.modules.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hahaha.platform.common.constant.AppConstants;
import com.hahaha.platform.common.enums.YesOrNoEnum;
import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.redis.RedisUtils;
import com.hahaha.platform.common.web.service.impl.BaseServiceImpl;
import com.hahaha.platform.modules.chat.mapper.ChatGroupInfoMapper;
import com.hahaha.platform.modules.chat.domain.ChatGroupInfo;
import com.hahaha.platform.modules.chat.service.ChatGroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务层实现
 *
 * </p>
 */
@Service("chatGroupInfoService")
public class ChatGroupInfoServiceImpl extends BaseServiceImpl<ChatGroupInfo> implements ChatGroupInfoService {

    @Resource
    private ChatGroupInfoMapper chatGroupInfoMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatGroupInfoMapper);
    }

    @Override
    public List<ChatGroupInfo> queryList(ChatGroupInfo t) {
        List<ChatGroupInfo> dataList = chatGroupInfoMapper.queryList(t);
        return dataList;
    }

    @Override
    public ChatGroupInfo getGroupInfo(Long groupId, Long userId, YesOrNoEnum verify) {
        String key = StrUtil.format(AppConstants.REDIS_GROUP_INFO, groupId, userId);
        ChatGroupInfo info;
        // 缓存存在
        if (redisUtils.hasKey(key)) {
            info = JSONUtil.toBean(redisUtils.get(key), ChatGroupInfo.class);
        }
        // 缓存不存在
        else if ((info = this.queryOne(new ChatGroupInfo().setUserId(userId).setGroupId(groupId))) != null) {
            redisUtils.set(key, JSONUtil.toJsonStr(info), AppConstants.REDIS_GROUP_TIME, TimeUnit.DAYS);
        }
        if (YesOrNoEnum.NO.equals(verify)) {
            return info;
        }
        if (info == null) {
            throw new BaseException("你不在当前群中");
        }
        return info;
    }

    @Override
    public void delGroupInfoCache(Long groupId, List<Long> userList) {
        userList.forEach(e -> {
            redisUtils.delete(StrUtil.format(AppConstants.REDIS_GROUP_INFO, groupId, e));
        });
    }

    @Override
    public Long countByGroup(Long groupId) {
        return queryCount(new ChatGroupInfo().setGroupId(groupId));
    }

    @Override
    public List<Long> queryUserList(Long groupId) {
        // 查询所有成员
        List<ChatGroupInfo> infoList = this.queryList(new ChatGroupInfo().setGroupId(groupId));
        return infoList.stream().map(ChatGroupInfo::getUserId).collect(Collectors.toList());
    }

    @Override
    public List<ChatGroupInfo> queryUserList(Long groupId, List<Long> userList) {
        List<ChatGroupInfo> dataList = this.queryList(new ChatGroupInfo().setGroupId(groupId));
        if (!CollectionUtils.isEmpty(userList)) {
            dataList = dataList.stream().filter(data -> userList.contains(data.getUserId())).collect(Collectors.toList());
        }
        return dataList;
    }

    @Override
    public Map<Long, ChatGroupInfo> queryUserMap(Long groupId) {
        // 查询所有成员
        List<ChatGroupInfo> dataList = this.queryList(new ChatGroupInfo().setGroupId(groupId));
        return dataList.stream().collect(Collectors.toMap(ChatGroupInfo::getUserId, a -> a, (k1, k2) -> k1));
    }

    @Override
    public void delByGroup(Long groupId) {
        chatGroupInfoMapper.delete(new QueryWrapper<>(new ChatGroupInfo().setGroupId(groupId)));
        // 删除群成员
        String redisKey = StrUtil.format(AppConstants.REDIS_GROUP_INFO, groupId, "*");
        redisUtils.delete(redisKey);
    }

}
