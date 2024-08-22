package com.ds.websocket.message;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
//            String ip = request.getRemoteAddress().getHostString();
//            log.info("客户端IP:" + ip);
            String token = servletServerHttpRequest.getServletRequest().getParameter("token");
            // TODO 校验Token
            // 模拟用户（实际校验Token后，取出用户信息）
//            String userId = servletServerHttpRequest.getServletRequest().getParameter("uid");
//            // TODO 判断用户是否存在
//            UserDO user = new UserDO();
//            user.setId(userId);
//            attributes.put(WebSocketConstant.USER_KEY, user);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
