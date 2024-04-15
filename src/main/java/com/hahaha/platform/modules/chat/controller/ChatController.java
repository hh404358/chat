package com.hahaha.platform.modules.chat.controller;

import com.hahaha.platform.common.web.controller.BaseController;
import com.hahaha.platform.common.web.domain.AjaxResult;
import com.hahaha.platform.modules.chat.service.ChatMsgService;
import com.hahaha.platform.modules.chat.vo.ChatVo01;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 聊天（单发）
 */
@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatController extends BaseController {

    @Autowired
    private ChatMsgService chatMsgService;

    /**
     * 发送信息
     */

    @PostMapping("/sendMsg")
    public AjaxResult sendMsg(@Validated @RequestBody ChatVo01 chatVo) {
        return AjaxResult.success(chatMsgService.sendFriendMsg(chatVo));
    }

}
