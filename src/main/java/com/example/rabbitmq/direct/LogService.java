package com.example.rabbitmq.direct;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class LogService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    
    private final RabbitTemplate rabbitTemplate;

    public LogService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public LogEntry logEntry(String applicationName, LogEntry.LogLevel level, String message, 
                           String source, String thread, String exception, 
                           Map<String, Object> metadata) {
        
        LogEntry logEntry = new LogEntry(
                UUID.randomUUID().toString(),
                applicationName,
                level,
                message,
                source,
                thread
        );
        
        logEntry.setException(exception);
        logEntry.setMetadata(metadata);

        logger.info("Creating log entry: {} - Level: {}", logEntry.getLogId(), level);

        try {
            // Send to direct exchange with routing key based on log level
            rabbitTemplate.convertAndSend(
                RabbitConfig.LOG_DIRECT_EXCHANGE,
                level.getRoutingKey(),  // Routing key determines which queue receives the message
                logEntry
            );
            
            logger.debug("Log entry {} sent to exchange with routing key: {}", 
                        logEntry.getLogId(), level.getRoutingKey());
                        
        } catch (Exception e) {
            logger.error("Failed to send log entry to exchange: {}", e.getMessage());
            throw new RuntimeException("Failed to send log entry", e);
        }

        return logEntry;
    }

    // Convenience methods for different log levels
    public LogEntry logError(String applicationName, String message, String source, 
                           String exception) {
        return logEntry(applicationName, LogEntry.LogLevel.ERROR, message, source, 
                       Thread.currentThread().getName(), exception, 
                       Map.of("severity", "high", "requiresAttention", true));
    }

    public LogEntry logWarning(String applicationName, String message, String source) {
        return logEntry(applicationName, LogEntry.LogLevel.WARNING, message, source,
                       Thread.currentThread().getName(), null,
                       Map.of("severity", "medium", "monitoringRequired", true));
    }

    public LogEntry logInfo(String applicationName, String message, String source) {
        return logEntry(applicationName, LogEntry.LogLevel.INFO, message, source,
                       Thread.currentThread().getName(), null,
                       Map.of("severity", "low", "informational", true));
    }

    public LogEntry logDebug(String applicationName, String message, String source) {
        return logEntry(applicationName, LogEntry.LogLevel.DEBUG, message, source,
                       Thread.currentThread().getName(), null,
                       Map.of("severity", "trace", "debugOnly", true));
    }

    // Sample log entries for demonstration
    public void generateSampleLogs() {
        logError("payment-service", "Payment processing failed for order #12345", 
                "PaymentProcessor.java:142", "java.net.ConnectException: Connection timeout");
        
        logWarning("user-service", "User authentication took longer than expected", 
                  "AuthController.java:89");
        
        logInfo("order-service", "New order created successfully", 
               "OrderController.java:56");
        
        logDebug("inventory-service", "Checking stock levels for product SKU-789", 
                "InventoryManager.java:201");
    }
}