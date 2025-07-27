package com.example.rabbitmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;

public class IoTMessage {
    @JsonProperty("messageId") private String messageId;
    @JsonProperty("deviceId") private String deviceId;
    @JsonProperty("deviceType") private String deviceType;
    @JsonProperty("location") private String location;
    @JsonProperty("sensorType") private String sensorType;
    @JsonProperty("value") private double value;
    @JsonProperty("unit") private String unit;
    @JsonProperty("timestamp") private LocalDateTime timestamp;
    @JsonProperty("metadata") private Map<String, Object> metadata;

    public IoTMessage() {}

    public IoTMessage(String messageId, String deviceId, String deviceType, String location,
                     String sensorType, double value, String unit) {
        this.messageId = messageId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.location = location;
        this.sensorType = sensorType;
        this.value = value;
        this.unit = unit;
        this.timestamp = LocalDateTime.now();
    }

    public String getRoutingKey() {
        if (sensorType != null) {
            return String.format("sensor.%s.%s", sensorType, location);
        } else {
            return String.format("device.%s.%s", deviceType, getEventType());
        }
    }

    private String getEventType() {
        if (metadata != null && metadata.containsKey("battery")) return "battery";
        if (metadata != null && metadata.containsKey("status")) return "status";
        return "telemetry";
    }

    // Getters and setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    @Override
    public String toString() {
        return "IoTMessage{" + "messageId='" + messageId + '\'' + ", deviceId='" + deviceId + '\'' + 
               ", sensorType='" + sensorType + '\'' + ", value=" + value + ", location='" + location + '\'' + '}';
    }
}