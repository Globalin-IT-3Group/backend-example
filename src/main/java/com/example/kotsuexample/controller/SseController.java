package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
@Slf4j
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@CurrentUser Integer userId) {
        log.info("🔔 SSE 연결 요청: userId = {}", userId);
        return sseService.subscribe(userId);
    }
}
