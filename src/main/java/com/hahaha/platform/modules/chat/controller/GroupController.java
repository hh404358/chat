package com.hahaha.platform.modules.chat.controller;

import com.hahaha.platform.common.web.controller.BaseController;
import com.hahaha.platform.common.web.domain.AjaxResult;
import com.hahaha.platform.modules.chat.service.ChatGroupService;
import com.hahaha.platform.modules.chat.service.ChatMsgService;
import com.hahaha.platform.modules.chat.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 群组
 */
@RestController
@Slf4j
@RequestMapping("/group")
public class GroupController extends BaseController {

    @Resource
    private ChatGroupService chatGroupService;

    @Resource
    private ChatMsgService chatMsgService;

    /**
     * 建立群组
     */
     
    @PostMapping("/createGroup")
    public AjaxResult createGroup(@Validated @RequestBody List<Long> list) {
        chatGroupService.createGroup(list);
        return AjaxResult.success();
    }

    /**
     * 获取群详情
     */
     
    @GetMapping("/getInfo/{groupId}")
    public AjaxResult getInfo(@PathVariable Long groupId) {
        return AjaxResult.success(chatGroupService.getInfo(groupId));
    }

    /**
     * 邀请进群
     */
     
    @PostMapping("/invitationGroup")
    public AjaxResult invitationGroup(@Validated @RequestBody GroupVo01 groupVo) {
        Long groupId = groupVo.getGroupId();
        List<Long> list = groupVo.getList();
        chatGroupService.invitationGroup(groupId, list);
        return AjaxResult.success();
    }

    /**
     * 踢出群组
     */
     
    @PostMapping("/kickedGroup")
    public AjaxResult kickedGroup(@Validated @RequestBody GroupVo01 groupVo) {
        Long groupId = groupVo.getGroupId();
        List<Long> list = groupVo.getList();
        chatGroupService.kickedGroup(groupId, list);
        return AjaxResult.success();
    }

    /**
     * 修改群名
     */
     
    @PostMapping("/editGroupName")
    public AjaxResult editGroupName(@Validated @RequestBody GroupVo02 groupVo) {
        chatGroupService.editGroupName(groupVo);
        return AjaxResult.success();
    }



    /**
     * 是否置顶
     */
     
    @PostMapping("/editTop")
    public AjaxResult editTop(@Validated @RequestBody GroupVo04 groupVo) {
        Long groupId = groupVo.getGroupId();
        chatGroupService.editTop(groupId, groupVo.getTop());
        return AjaxResult.success();
    }

    /**
     * 是否免打扰
     */
     
    @PostMapping("/editDisturb")
    public AjaxResult editDisturb(@Validated @RequestBody GroupVo05 groupVo) {
        Long groupId = groupVo.getGroupId();
        chatGroupService.editDisturb(groupId, groupVo.getDisturb());
        return AjaxResult.success();
    }

    /**
     * 是否保存群组
     */
     
    @PostMapping("/editKeepGroup")
    public AjaxResult editKeepGroup(@Validated @RequestBody GroupVo06 groupVo) {
        Long groupId = groupVo.getGroupId();
        chatGroupService.editKeepGroup(groupId, groupVo.getKeepGroup());
        return AjaxResult.success();
    }

    /**
     * 退出群组
     */
     
    @GetMapping("/logoutGroup/{groupId}")
    public AjaxResult logoutGroup(@PathVariable Long groupId) {
        chatGroupService.logoutGroup(groupId);
        return AjaxResult.success();
    }

    /**
     * 解散群组
     */
     
    @GetMapping("/removeGroup/{groupId}")
    public AjaxResult removeGroup(@PathVariable Long groupId) {
        chatGroupService.removeGroup(groupId);
        return AjaxResult.success();
    }



    /**
     * 加入群组
     */
     
    @GetMapping("/joinGroup/{groupId}")
    public AjaxResult joinGroup(@PathVariable Long groupId) {
        chatGroupService.joinGroup(groupId);
        return AjaxResult.success();
    }

    /**
     * 查询群列表
     */
     
    @GetMapping("/groupList")
    public AjaxResult groupList() {
        return AjaxResult.success(chatGroupService.groupList());
    }

    /**
     * 发送信息
     */
     
    @PostMapping("/Msg")
    public AjaxResult sendMsg(@Validated @RequestBody ChatVo02 chatVo) {
        return AjaxResult.success(chatMsgService.sendGroupMsg(chatVo));
    }

}
