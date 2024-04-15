package com.hahaha.platform.modules.chat.controller;

import com.hahaha.platform.common.web.controller.BaseController;
import com.hahaha.platform.common.web.domain.AjaxResult;
import com.hahaha.platform.common.web.page.TableDataInfo;
import com.hahaha.platform.modules.chat.service.ChatApplyService;
import com.hahaha.platform.modules.chat.service.ChatFriendService;
import com.hahaha.platform.modules.chat.vo.ApplyVo01;
import com.hahaha.platform.modules.chat.vo.FriendVo02;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 申请
 */
@RestController
@Slf4j
@RequestMapping("/apply")
public class ApplyController extends BaseController {

    @Resource
    private ChatFriendService chatFriendService;

    @Resource
    private ChatApplyService chatApplyService;

    /**
     * 申请添加
     */
     
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody FriendVo02 friendVo) {
        chatFriendService.applyFriend(friendVo);
        return AjaxResult.success();
    }

    /**
     * 申请记录
     */
     
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage("create_time desc");
        return getDataTable(chatApplyService.list());
    }

    /**
     * 申请详情
     */
     
    @GetMapping("/info/{applyId}")
    public AjaxResult getInfo(@PathVariable Long applyId) {
        return AjaxResult.success(chatApplyService.getInfo(applyId));
    }

    /**
     * 同意申请
     */
     
    @PostMapping("/agree")
    public AjaxResult agree(@Validated @RequestBody ApplyVo01 applyVo) {
        chatFriendService.agree(applyVo.getApplyId());
        return AjaxResult.success();
    }

    /**
     * 拒绝申请
     */
     
    @PostMapping("/refused")
    public AjaxResult refused(@Validated @RequestBody ApplyVo01 applyVo) {
        chatFriendService.refused(applyVo.getApplyId());
        return AjaxResult.success();
    }

    /**
     * 忽略申请
     */
     
    @PostMapping("/ignore")
    public AjaxResult ignore(@Validated @RequestBody ApplyVo01 applyVo) {
        chatFriendService.ignore(applyVo.getApplyId());
        return AjaxResult.success();
    }

}
