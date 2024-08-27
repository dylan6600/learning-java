package com.ds.chatroom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册端点，发布或者订阅消息的时候需要连接此端点
     * setAllowedOrigins 非必须，*表示允许其他域进行连接
     * withSockJS  表示开始sockejs支持
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //原生websocket
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*");
        //sockJS
        registry.addEndpoint("/endpoint-websocket")
                .addInterceptors(new HttpHandShakeInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 配置消息代理(中介)
     * enableSimpleBroker 服务端推送给客户端的路径前缀
     * setApplicationDestinationPrefixes  客户端发送数据给服务器端的一个前缀
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用SimpleBroker，使得订阅到此"topic"前缀的客户端可以收到greeting消息.
        registry.enableSimpleBroker("/topic", "/chat");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebsocketChannelInterceptor());
        registration.interceptors(new AuthChannelInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebsocketChannelInterceptor());
    }
}
