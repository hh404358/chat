package com.hahaha.platform.modules.auth.controller;

import cn.hutool.core.lang.Dict;
import com.hahaha.platform.common.aspectj.IgnoreAuth;
import com.hahaha.platform.common.aspectj.SubmitRepeat;
import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.shiro.ShiroLoginAuth;
import com.hahaha.platform.common.shiro.ShiroLoginPhone;
import com.hahaha.platform.common.web.controller.BaseController;
import com.hahaha.platform.common.web.domain.AjaxResult;
import com.hahaha.platform.modules.auth.vo.AuthVo01;
import com.hahaha.platform.modules.auth.vo.AuthVo02;
import com.hahaha.platform.modules.auth.vo.AuthVo03;
import com.hahaha.platform.modules.auth.vo.AuthVo04;
import com.hahaha.platform.modules.chat.domain.ChatUser;
import com.hahaha.platform.modules.chat.service.ChatUserService;
import com.hahaha.platform.modules.sms.enums.SmsTypeEnum;
import com.hahaha.platform.modules.sms.service.SmsService;
import com.hahaha.platform.modules.sms.vo.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 认证
 */
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private SmsService smsService;

    /**
     * 发送验证码（登录/注册/忘记密码）
     */
    @IgnoreAuth
     
    @PostMapping(value = "/sendCode")
    @SubmitRepeat
    public AjaxResult sendCode(@Validated @RequestBody SmsVo smsVo) {
        ChatUser chatUser = chatUserService.queryByPhone(smsVo.getPhone());
        switch (smsVo.getType()) {
            case LOGIN:
            case FORGET:
                if (chatUser == null) {
                    throw new BaseException("用户未注册，请先注册");
                }
                break;
            case REGISTERED:
                if (chatUser != null) {
                    throw new BaseException("用户已注册，请直接登录");
                }
                break;
        }
        return AjaxResult.success(smsService.sendSms(smsVo)).put("msg", "验证码已发送");
    }

    /**
     * 注册方法
     */
    @IgnoreAuth
     
    @PostMapping(value = "/register")
    public AjaxResult register(@Validated @RequestBody AuthVo01 authVo) {
        // 验证
        smsService.verifySms(authVo.getPhone(), authVo.getCode(), SmsTypeEnum.REGISTERED);
        // 注册
        chatUserService.register(authVo);
        return AjaxResult.successMsg("注册成功，请登录");
    }

    /**
     * 登录方法（根据手机+密码登录）
     */
    @IgnoreAuth
     
    @PostMapping("/login")
    public AjaxResult login(@Validated @RequestBody AuthVo02 authVo) {
        // 执行登录
        ShiroLoginAuth loginAuth = new ShiroLoginAuth(authVo.getPhone(), authVo.getPassword());
        Dict dict = chatUserService.doLogin(loginAuth);
        return AjaxResult.success(dict);
    }

    /**
     * 登录方法（根据手机+验证码登录）
     */
    @IgnoreAuth
     
    @PostMapping("/loginByCode")
    public AjaxResult loginByCode(@Validated @RequestBody AuthVo03 authVo) {
        // 验证
        smsService.verifySms(authVo.getPhone(), authVo.getCode(), SmsTypeEnum.LOGIN);
        // 执行登录
        ShiroLoginPhone loginPhone = new ShiroLoginPhone(authVo.getPhone());
        Dict dict = chatUserService.doLogin(loginPhone);
        return AjaxResult.success(dict);
    }

    /**
     * 找回密码（根据手机）
     */
    @IgnoreAuth
     
    @PostMapping("/forget")
    public AjaxResult forget(@Validated @RequestBody AuthVo04 authVo) {
        // 验证
        smsService.verifySms(authVo.getPhone(), authVo.getCode(), SmsTypeEnum.FORGET);
        // 查询用户
        ChatUser cu = chatUserService.queryByPhone(authVo.getPhone());
        if (cu == null) {
            throw new BaseException("手机号不存在");
        }
        // 重置密码
        chatUserService.resetPass(cu.getUserId(), authVo.getPassword());
        return AjaxResult.successMsg("您的密码重置成功，请重新登录");
    }

}