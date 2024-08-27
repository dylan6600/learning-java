package com.ds.chatroom.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Component
@Slf4j
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
        log.info("【SubscribeEventListener监听器事件 类型】"+headerAccessor.getCommand().getMessageType());
        log.info("【SubscribeEventListener监听器事件 sessionId】"+headerAccessor.getSessionAttributes().get("sessionId"));
    }
}
