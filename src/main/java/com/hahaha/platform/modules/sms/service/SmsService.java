package com.hahaha.platform.modules.sms.service;

import cn.hutool.core.lang.Dict;
import com.hahaha.platform.modules.sms.enums.SmsTypeEnum;
import com.hahaha.platform.modules.sms.vo.SmsVo;

/**
 * 短信 服务层
 */
public interface SmsService {

    /**
     * 发送短信
     */
    Dict sendSms(SmsVo smsVo);

    /**
     * 验证短信
     */
    void verifySms(String phone, String code, SmsTypeEnum type);

}
