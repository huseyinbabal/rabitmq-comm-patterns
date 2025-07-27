package com.example.rabbitmq.topic;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.IoTMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/iot")
@Tag(name = "Topic Exchange Pattern", description = "IoT device management using topic exchange pattern matching")
public class IoTController {

    private static final Logger logger = LoggerFactory.getLogger(IoTController.class);
    private final RabbitTemplate rabbitTemplate;

    public IoTController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/sensor/temperature")
    @Operation(summary = "Send temperature sensor data")
    public ResponseEntity<IoTMessage> sendTemperatureData(@RequestBody TemperatureSensorRequest request) {
        IoTMessage message = new IoTMessage(
            UUID.randomUUID().toString(),
            request.getDeviceId(),
            "temperature_sensor",
            request.getLocation(),
            "temperature",
            request.getTemperature(),
            "celsius"
        );

        String routingKey = message.getRoutingKey();
        rabbitTemplate.convertAndSend(RabbitConfig.IOT_TOPIC_EXCHANGE, routingKey, message);
        logger.info("Temperature data sent with routing key: {}", routingKey);
        
        return ResponseEntity.ok(message);
    }

    @PostMapping("/sensor/motion")
    @Operation(summary = "Send motion sensor data")
    public ResponseEntity<IoTMessage> sendMotionData(@RequestBody MotionSensorRequest request) {
        IoTMessage message = new IoTMessage(
            UUID.randomUUID().toString(),
            request.getDeviceId(),
            "motion_sensor",
            request.getLocation(),
            "motion",
            request.isMotionDetected() ? 1.0 : 0.0,
            "boolean"
        );

        String routingKey = message.getRoutingKey();
        rabbitTemplate.convertAndSend(RabbitConfig.IOT_TOPIC_EXCHANGE, routingKey, message);
        logger.info("Motion data sent with routing key: {}", routingKey);
        
        return ResponseEntity.ok(message);
    }

    @PostMapping("/device/battery")
    @Operation(summary = "Send device battery status")
    public ResponseEntity<IoTMessage> sendBatteryStatus(@RequestBody BatteryStatusRequest request) {
        IoTMessage message = new IoTMessage(
            UUID.randomUUID().toString(),
            request.getDeviceId(),
            request.getDeviceType(),
            request.getLocation(),
            null,
            request.getBatteryLevel(),
            "percentage"
        );
        message.setMetadata(Map.of("battery", request.getBatteryLevel(), "charging", request.isCharging()));

        String routingKey = message.getRoutingKey();
        rabbitTemplate.convertAndSend(RabbitConfig.IOT_TOPIC_EXCHANGE, routingKey, message);
        logger.info("Battery status sent with routing key: {}", routingKey);
        
        return ResponseEntity.ok(message);
    }

    @PostMapping("/sample-data")
    @Operation(summary = "Generate sample IoT data")
    public ResponseEntity<String> generateSampleData() {
        // Generate various sample messages to demonstrate routing
        sendTemperatureData(new TemperatureSensorRequest("temp-001", "livingroom", 22.5));
        sendTemperatureData(new TemperatureSensorRequest("temp-002", "bedroom", 20.0));
        sendMotionData(new MotionSensorRequest("motion-001", "frontdoor", true));
        sendBatteryStatus(new BatteryStatusRequest("camera-001", "camera", "frontdoor", 85.0, false));
        
        return ResponseEntity.ok("Sample IoT data generated successfully");
    }

    public static class TemperatureSensorRequest {
        private String deviceId;
        private String location;
        private double temperature;

        public TemperatureSensorRequest() {}
        public TemperatureSensorRequest(String deviceId, String location, double temperature) {
            this.deviceId = deviceId; this.location = location; this.temperature = temperature;
        }

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
    }

    public static class MotionSensorRequest {
        private String deviceId;
        private String location;
        private boolean motionDetected;

        public MotionSensorRequest() {}
        public MotionSensorRequest(String deviceId, String location, boolean motionDetected) {
            this.deviceId = deviceId; this.location = location; this.motionDetected = motionDetected;
        }

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public boolean isMotionDetected() { return motionDetected; }
        public void setMotionDetected(boolean motionDetected) { this.motionDetected = motionDetected; }
    }

    public static class BatteryStatusRequest {
        private String deviceId;
        private String deviceType;
        private String location;
        private double batteryLevel;
        private boolean charging;

        public BatteryStatusRequest() {}
        public BatteryStatusRequest(String deviceId, String deviceType, String location, double batteryLevel, boolean charging) {
            this.deviceId = deviceId; this.deviceType = deviceType; this.location = location; 
            this.batteryLevel = batteryLevel; this.charging = charging;
        }

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public double getBatteryLevel() { return batteryLevel; }
        public void setBatteryLevel(double batteryLevel) { this.batteryLevel = batteryLevel; }
        public boolean isCharging() { return charging; }
        public void setCharging(boolean charging) { this.charging = charging; }
    }
}