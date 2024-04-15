package com.hahaha.platform.modules.push.service;

import com.hahaha.platform.modules.push.enums.PushMsgEnum;
import com.hahaha.platform.modules.push.enums.PushNoticeEnum;
import com.hahaha.platform.modules.push.vo.PushParamVo;

import java.util.List;

/**
 * 用户推送 服务层
 */
public interface ChatPushService {

    /**
     * 发送个人消息
     */
    void pushMsg(PushParamVo from, PushMsgEnum msgType);

    /**
     * 发送个人消息
     */
    void pushMsg(List<PushParamVo> userList, PushMsgEnum msgType);

    /**
     * 发送群组消息
     */
    void pushGroupMsg(PushParamVo from, PushParamVo group, PushMsgEnum msgType);

    /**
     * 发送群组消息
     */
    void pushGroupMsg(List<PushParamVo> userList, PushParamVo group, PushMsgEnum msgType);

    /**
     * 拉取离线消息
     */
    void pullMsg(Long userId);

    /**
     * 发送通知
     */
    void pushNotice(PushParamVo paramVo, PushNoticeEnum pushNotice);

    /**
     * 发送通知
     */
    void pushNotice(List<PushParamVo> userList, PushNoticeEnum pushNotice);

}
