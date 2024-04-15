package com.hahaha.platform.modules.sms.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.hahaha.platform.common.config.PlatformConfig;
import com.hahaha.platform.common.enums.YesOrNoEnum;
import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.redis.RedisUtils;
import com.hahaha.platform.modules.sms.enums.SmsTemplateEnum;
import com.hahaha.platform.modules.sms.enums.SmsTypeEnum;
import com.hahaha.platform.modules.sms.service.SmsService;
import com.hahaha.platform.modules.sms.vo.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * token 服务层
 */
@Service("smsService")
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Dict sendSms(SmsVo smsVo) {
        // 验证手机号
        if (!Validator.isMobile(smsVo.getPhone())) {
            throw new BaseException("请输入正确的手机号");
        }
        SmsTypeEnum smsType = smsVo.getType();
        String key = smsType.getPrefix().concat(smsVo.getPhone());
        // 生成验证码
        String code = String.valueOf(RandomUtil.randomInt(1000, 9999));
        // 发送短信
        if (YesOrNoEnum.YES.equals(PlatformConfig.SMS)) {
            Dict dict = Dict.create()
                    .set("code", code);
            doSendSms(smsVo.getPhone(), SmsTemplateEnum.VERIFY_CODE, dict);
        }
        // 存入缓存 key：prefix+phone
        redisUtils.set(key, code, smsType.getTimeout(), TimeUnit.MINUTES);
        return Dict.create().set("code", code).set("expiration", smsType.getTimeout());
    }

    @Override
    public void verifySms(String phone, String code, SmsTypeEnum type) {
        // 验证手机号
        if (!Validator.isMobile(phone)) {
            throw new BaseException("请输入正确的手机号");
        }
        // 从缓存取出验证码并判断
        String key = type.getPrefix().concat(phone);
        if (!redisUtils.hasKey(key)) {
            throw new BaseException("验证码已过期，请重新获取");
        }
        String value = redisUtils.get(key);
        if (value.equalsIgnoreCase(code)) {
            redisUtils.delete(key);
        } else {
            throw new BaseException("验证码不正确，请重新获取");
        }
    }

    /**
     *  TODO
     * 执行发送短信
     * @param phone
     * @param templateCode
     * @param dict
     */
    private void doSendSms(String phone, SmsTemplateEnum templateCode, Dict dict) {

    }

}
