package com.hahaha.platform.modules.ws;

import cn.hutool.extra.spring.SpringUtil;
import com.hahaha.platform.common.constant.AppConstants;
import com.hahaha.platform.common.constant.HeadConstant;
import com.hahaha.platform.common.shiro.LoginUser;
import com.hahaha.platform.modules.auth.service.TokenService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
public class BootWebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 接受前端传来的参数
        String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter(HeadConstant.TOKEN_HEADER_ADMIN);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        TokenService tokenService = SpringUtil.getBean("tokenService");
        LoginUser loginUser = tokenService.queryByToken(token);
        if (loginUser == null) {
            return false;
        }
        //将参数放到attributes
        attributes.put(AppConstants.USER_ID, loginUser.getUserId());
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
