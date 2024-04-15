package com.hahaha.platform.modules.auth.service.impl;

import cn.hutool.json.JSONUtil;
import com.hahaha.platform.common.config.PlatformConfig;
import com.hahaha.platform.common.constant.HeadConstant;
import com.hahaha.platform.common.redis.RedisUtils;
import com.hahaha.platform.common.shiro.LoginUser;
import com.hahaha.platform.common.shiro.ShiroUtils;
import com.hahaha.platform.modules.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * token 服务层
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String generateToken() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        String token = loginUser.getToken();
        // 存储redis
        redisUtils.set(makeToken(token), JSONUtil.toJsonStr(loginUser), PlatformConfig.TIMEOUT, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public LoginUser queryByToken(String token) {
        String key = makeToken(token);
        if (!redisUtils.hasKey(key)) {
            return null;
        }
        // 续期
        redisUtils.expire(key, PlatformConfig.TIMEOUT, TimeUnit.MINUTES);
        // 转换
        return JSONUtil.toBean(redisUtils.get(key), LoginUser.class);
    }

    @Override
    public void deleteToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return;
        }
        redisUtils.delete(makeToken(token));
    }

    private String makeToken(String token) {
        return HeadConstant.TOKEN_REDIS_APP + token;
    }

}
