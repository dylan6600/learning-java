package com.ds.chatroom.config;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
public class HttpHandShakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("【握手拦截器】beforeHandshake");
        if(request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest  serverHttpRequest=(ServletServerHttpRequest) request;
            HttpSession session = serverHttpRequest.getServletRequest().getSession();
            String sessionId = session.getId();
            log.info("【握手拦截器】beforeHandshake sessionId="+sessionId);
            attributes.put("sessionId",sessionId);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("【握手拦截器】afterHandshake");
        if(request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest  serverHttpRequest=(ServletServerHttpRequest) request;
            HttpSession session = serverHttpRequest.getServletRequest().getSession();
            String sessionId = session.getId();
            log.info("【握手拦截器】afterHandshake sessionId="+sessionId);
        }
    }
}
