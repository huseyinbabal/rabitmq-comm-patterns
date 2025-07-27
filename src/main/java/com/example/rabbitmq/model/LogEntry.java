package com.example.rabbitmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public class LogEntry {
    
    @JsonProperty("logId")
    private String logId;
    
    @JsonProperty("applicationName")
    private String applicationName;
    
    @JsonProperty("level")
    private LogLevel level;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("thread")
    private String thread;
    
    @JsonProperty("exception")
    private String exception;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public LogEntry() {}

    public LogEntry(String logId, String applicationName, LogLevel level, String message, 
                   String source, String thread) {
        this.logId = logId;
        this.applicationName = applicationName;
        this.level = level;
        this.message = message;
        this.source = source;
        this.thread = thread;
        this.timestamp = LocalDateTime.now();
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public LogLevel getLevel() { return level; }
    public void setLevel(LogLevel level) { this.level = level; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getThread() { return thread; }
    public void setThread(String thread) { this.thread = thread; }

    public String getException() { return exception; }
    public void setException(String exception) { this.exception = exception; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    @Override
    public String toString() {
        return "LogEntry{" +
                "logId='" + logId + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", level=" + level +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    public enum LogLevel {
        ERROR("error"),
        WARNING("warning"), 
        INFO("info"),
        DEBUG("debug");

        private final String routingKey;

        LogLevel(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public static LogLevel fromRoutingKey(String routingKey) {
            for (LogLevel level : values()) {
                if (level.routingKey.equals(routingKey)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Unknown routing key: " + routingKey);
        }
    }
}