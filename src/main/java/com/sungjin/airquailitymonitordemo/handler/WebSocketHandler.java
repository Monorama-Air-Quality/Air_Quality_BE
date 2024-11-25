package com.sungjin.airquailitymonitordemo.handler;import com.fasterxml.jackson.databind.JsonNode;import com.fasterxml.jackson.databind.ObjectMapper;import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;import com.sungjin.airquailitymonitordemo.entity.SensorData;import com.sungjin.airquailitymonitordemo.service.SensorDataService;import lombok.RequiredArgsConstructor;import lombok.extern.slf4j.Slf4j;import org.springframework.stereotype.Component;import org.springframework.web.socket.CloseStatus;import org.springframework.web.socket.TextMessage;import org.springframework.web.socket.WebSocketSession;import org.springframework.web.socket.handler.TextWebSocketHandler;import java.io.IOException;import java.time.Instant;import java.time.LocalDateTime;import java.time.ZoneId;import java.util.Map;import java.util.concurrent.ConcurrentHashMap;@Component@Slf4j@RequiredArgsConstructorpublic class WebSocketHandler extends TextWebSocketHandler {    private final SensorDataService sensorDataService;    private final ObjectMapper objectMapper;    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();    private final Map<String, String> sessionDeviceMap = new ConcurrentHashMap<>();  // 세션별 디바이스 ID 저장    @Override    protected void handleTextMessage(WebSocketSession session, TextMessage message) {        String sessionId = session.getId();        try {            String payload = message.getPayload();            log.info("Received message from session {}: {}", sessionId, payload);            JsonNode jsonNode = objectMapper.readTree(payload);            String type = jsonNode.get("type").asText();            if ("SUBSCRIBE".equals(type)) {                handleSubscribe(session, jsonNode);            } else if ("SENSOR_DATA".equals(type)) {                handleSensorData(session, jsonNode.get("payload"));            } else {                log.warn("Unknown message type: {}", type);            }        } catch (Exception e) {            log.error("Error handling message from session {}: {}", sessionId, e.getMessage(), e);        }    }    private void handleSensorData(WebSocketSession session, JsonNode payload) throws IOException {        JsonNode data = payload.get("data");        String deviceId = sessionDeviceMap.get(session.getId());        Long projectId = payload.get("projectId").asLong();  // projectId 추출        if (deviceId == null) {            log.warn("No device ID mapped for session: {}", session.getId());            return;        }        try {            LocalDateTime timestamp = Instant.parse(payload.get("timestamp").asText())                    .atZone(ZoneId.of("Asia/Seoul"))  // Asia/Seoul 타임존 사용                    .toLocalDateTime();            Double latitude = payload.has("latitude") ? payload.get("latitude").asDouble() : null;            Double longitude = payload.has("longitude") ? payload.get("longitude").asDouble() : null;            log.info("Received data - deviceId: {}, projectId: {}, location: ({}, {})",                    deviceId, projectId, latitude, longitude);            SensorDataRequestDto sensorDataDto = SensorDataRequestDto.builder()                    .deviceId(deviceId)                    .projectId(projectId)  // projectId 설정                    .timestamp(timestamp)                    .pm25Value(data.get("pm25").get("value").asDouble())                    .pm25Level(data.get("pm25").get("level").asInt())                    .pm10Value(data.get("pm10").get("value").asDouble())                    .pm10Level(data.get("pm10").get("level").asInt())                    .temperature(data.get("temperature").get("value").asDouble())                    .temperatureLevel(data.get("temperature").get("level").asInt())                    .humidity(data.get("humidity").get("value").asDouble())                    .humidityLevel(data.get("humidity").get("level").asInt())                    .co2Value(data.get("co2").get("value").asDouble())                    .co2Level(data.get("co2").get("level").asInt())                    .vocValue(data.get("voc").get("value").asDouble())                    .vocLevel(data.get("voc").get("level").asInt())                    .latitude(latitude)                    .longitude(longitude)                    .rawData(convertRawData(data.get("_raw")))                    .build();            log.info("Processing sensor data for device: {} in project: {}", deviceId, projectId);            SensorData processedData = sensorDataService.processSensorData(sensorDataDto);            if (processedData != null) {                String response = objectMapper.writeValueAsString(Map.of(                        "status", "success",                        "data", processedData,                        "timestamp", LocalDateTime.now()                ));                session.sendMessage(new TextMessage(response));                log.info("Successfully processed and stored sensor data for device: {}", deviceId);            } else {                log.error("Failed to process sensor data for device: {}", deviceId);                throw new RuntimeException("Failed to process sensor data");            }        } catch (Exception e) {            log.error("Error processing sensor data for device {} in project {}: {}",                    deviceId, projectId, e.getMessage(), e);            throw e;        }    }    private byte[] convertRawData(JsonNode rawArray) {        if (rawArray == null || !rawArray.isArray()) {            return new byte[0];        }        byte[] rawData = new byte[rawArray.size()];        for (int i = 0; i < rawArray.size(); i++) {            rawData[i] = (byte) rawArray.get(i).asInt();        }        return rawData;    }    private void handleSubscribe(WebSocketSession session, JsonNode message) throws IOException {        JsonNode payload = message.get("payload");        if (payload != null && payload.has("topic")) {            String topic = payload.get("topic").asText();            // topic에서 deviceId 추출 (예: "device/NzcBrkdRvtKaiI8Tm/JbFA==" -> "NzcBrkdRvtKaiI8Tm/JbFA==")            String deviceId = topic.substring(topic.lastIndexOf('/') + 1);            sessionDeviceMap.put(session.getId(), deviceId);  // 세션과 디바이스 ID 매핑 저장            log.info("Session {} subscribed to topic: {} with deviceId: {}",                    session.getId(), topic, deviceId);            String response = objectMapper.writeValueAsString(Map.of(                    "status", "success",                    "message", "Subscribed to " + topic,                    "timestamp", LocalDateTime.now()            ));            session.sendMessage(new TextMessage(response));        }    }    @Override    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {        log.info("WebSocket Connection Closed - Session ID: {}, Status: {}",                session.getId(), status);        sessions.remove(session.getId());        sessionDeviceMap.remove(session.getId());  // 세션 매핑 제거    }    @Override    public void afterConnectionEstablished(WebSocketSession session) {        log.info("WebSocket Connection Established - Session ID: {}", session.getId());        log.info("Remote Address: {}", session.getRemoteAddress());        log.info("URI: {}", session.getUri());        sessions.put(session.getId(), session);    }    @Override    public void handleTransportError(WebSocketSession session, Throwable exception) {        log.error("Transport error occurred on session {}", session.getId(), exception);    }}