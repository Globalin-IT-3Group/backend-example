package com.example.kotsuexample.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final StudyChatHandler studyChatHandler;
    private final PersonalChatHandler personalChatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(studyChatHandler, "/study")
                .setAllowedOrigins("*");

        registry.addHandler(personalChatHandler, "/personal")
                .setAllowedOrigins("*");
    }
}
