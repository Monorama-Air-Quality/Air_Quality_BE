package com.sungjin.airquailitymonitordemo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sungjin.airquailitymonitordemo.entity.SensorData;
import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.service.SensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SensorDataService sensorDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("New WebSocket connection established: {}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            SensorDataRequestDto sensorDataDto = objectMapper.readValue(payload, SensorDataRequestDto.class);

            // 센서 데이터 처리
            sensorDataService.processSensorData(sensorDataDto);

            // 응답 전송
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("status", "success", "timestamp", LocalDateTime.now())
            )));

        } catch (Exception e) {
            log.error("Error handling message", e);
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Map.of("status", "error", "message", e.getMessage())
                )));
            } catch (IOException ex) {
                log.error("Error sending error message", ex);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        log.info("WebSocket connection closed: {} with status: {}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error occurred: {}", exception.getMessage());
    }
}