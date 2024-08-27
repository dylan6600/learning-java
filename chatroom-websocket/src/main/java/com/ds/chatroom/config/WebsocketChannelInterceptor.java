package com.ds.chatroom.config;


import com.ds.chatroom.controller.UserChatController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
public class WebsocketChannelInterceptor implements ChannelInterceptor {
    /**
     * 在消息被实际发送到频道之前调用
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        log.info("WebsocketChannelInterceptor ==> preSend");
        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * 发送消息调用后立即调用
     *
     * @param message
     * @param channel
     * @param sent
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
//        log.info("WebsocketChannelInterceptor ==> postSend");
        ChannelInterceptor.super.postSend(message, channel, sent);
    }

    /**
     * 发送消息调用后立即调用
     *
     * @param message
     * @param channel
     * @param sent
     * @param ex
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//        log.info("WebsocketChannelInterceptor ==> afterSendCompletion");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (headerAccessor.getCommand() == null) return; //避免非stomp消息类型，例如心跳检测

        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        log.info("SocketChannelIntecepter -> sessionId = "+sessionId);

        switch (headerAccessor.getCommand()) {
            case CONNECT:
                connect(sessionId);
                break;
            case DISCONNECT:
                disconnect(sessionId);
                break;
            case SUBSCRIBE:
                break;
            case UNSUBSCRIBE:
                break;
            default:
                break;
        }
    }

    private void connect(String sessionId) {
        log.info("connect sessionId="+sessionId);
    }
    private void disconnect(String sessionId) {
        log.info("disconnect sessionId="+sessionId);
        UserChatController.onlineUser.remove(sessionId);
    }
}
