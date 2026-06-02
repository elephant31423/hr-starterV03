package com.example.hrstarter.config;

import com.example.hrstarter.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 設定廣播消息的前綴
        config.enableSimpleBroker("/topic", "/user");
        // 設定前端發送消息給後端的前綴
        config.setApplicationDestinationPrefixes("/app");

        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊連接端點，允許跨域
        registry.addEndpoint("/ws-hr")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 1. 從 header 拿到 Token (前端傳來的 Authorization)
                    String token = accessor.getFirstNativeHeader("Authorization");
                    log.info("WebSocket 連接嘗試，Token: {}", token);
                    // 2. 解析 Token 拿到用戶名或 ID (這裡簡化，假設解析後是 userId)
                    if (token == null || !token.startsWith("Bearer ")) {
                        log.error("WebSocket 連線失敗：Token 缺失");
                        return null; // 返回 null 會直接拒絕連線，不會噴 1002 錯誤
                    }
                    try {
                        String actualToken = token.substring(7);
                        // 解析並設定身份...
                        String userId = String.valueOf(jwtUtils.getUserId(actualToken));
                        log.info("WebSocket 連接成功，UserID: {}", userId);
                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return userId;
                            }
                        });
                    } catch (Exception e) {
                        log.error("Token 解析異常: {}", e.getMessage());
                        return null;
                    }
                }
                return message;
            }
        });
    }
}