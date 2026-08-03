package com.interviewiq.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        // 1 hour timeout
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            // Send connection established event
            emitter.send(SseEmitter.event().name("CONNECT").data("Connected successfully"));
        } catch (IOException e) {
            log.error("Error sending connection event to user {}", userId, e);
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendEventToUser(UUID userId, Object eventData) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("NOTIFICATION").data(eventData));
                log.debug("Sent SSE event to user {}", userId);
            } catch (IOException e) {
                log.error("Failed to send SSE event to user {}", userId, e);
                emitters.remove(userId);
            }
        }
    }
}
