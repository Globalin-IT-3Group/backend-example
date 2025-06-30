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
    private final VideoSignalHandler videoSignalHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(studyChatHandler, "/ws/study")
                .setAllowedOrigins("*");

//      ex. ws://localhost:8080/ws/chat?roomId=123&userId=5
        registry.addHandler(personalChatHandler, "/ws/chat")
                .setAllowedOrigins("*");

        registry.addHandler(videoSignalHandler, "/ws/signal")
                .setAllowedOrigins("*");
    }
}
