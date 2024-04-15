package com.hahaha.platform.modules.auth.service;

import com.hahaha.platform.common.shiro.LoginUser;

/**
 * token 服务层
 */
public interface TokenService {

    /**
     * 生成token
     */
    String generateToken();

    /**
     * 通过token查询
     */
    LoginUser queryByToken(String token);

    /**
     * 删除token
     */
    void deleteToken(String token);

}
