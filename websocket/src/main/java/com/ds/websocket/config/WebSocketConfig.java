package com.ds.websocket.config;

import com.ds.websocket.message.DefaultWebSocketHandler;
import com.ds.websocket.message.WebSocket;
import com.ds.websocket.message.WebSocketImpl;
import com.ds.websocket.message.WebSocketInterceptor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@ConditionalOnProperty(prefix = "websocket", value = "enable", havingValue = "true")
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${websocket.intercept:false}")
    private boolean intercepted;

    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }


    @Bean
    public DefaultWebSocketHandler defaultWebSocketHandler() {
        return new DefaultWebSocketHandler();
    }

    @Bean
    public WebSocket webSocket() {
        return new WebSocketImpl();
    }

    @Bean
    @ConditionalOnProperty(prefix = "websocket", value = "intercept", havingValue = "true")
    public WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        if (intercepted) {
            registry.addHandler(defaultWebSocketHandler(), "ws/message")
                    .addInterceptors(webSocketInterceptor())
                    .setAllowedOrigins("*");
            return;
        }
        registry.addHandler(defaultWebSocketHandler(), "ws/message").setAllowedOrigins("*");
    }

}
