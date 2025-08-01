package com.example.rabbitmq.direct;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    // Track error frequencies for intelligent alerting
    private final ConcurrentHashMap<String, AtomicInteger> errorCounts = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.ERROR_LOG_QUEUE)
    public void processErrorLog(LogEntry logEntry) {
        try {
            logger.error("🚨 CRITICAL ERROR DETECTED - App: {}, Message: {}", 
                        logEntry.getApplicationName(), logEntry.getMessage());
            
            // Track error frequency
            String errorKey = logEntry.getApplicationName() + ":" + extractErrorType(logEntry.getMessage());
            int errorCount = errorCounts.computeIfAbsent(errorKey, k -> new AtomicInteger(0))
                                      .incrementAndGet();
            
            // Send immediate alert for critical errors
            sendImmediateAlert(logEntry);
            
            // Check for error patterns that require escalation
            if (errorCount >= 5) {
                sendEscalationAlert(logEntry, errorCount);
            }
            
            // Create incident ticket for errors with exceptions
            if (logEntry.getException() != null) {
                createIncidentTicket(logEntry);
            }
            
            // Update monitoring dashboard
            updateErrorDashboard(logEntry);
            
            logger.info("Error alert processed for log: {} (Error #{} for this type)", 
                       logEntry.getLogId(), errorCount);
            
            
        } catch (Exception e) {
            logger.error("Error processing error log alert: {} - Error: {}", 
                        logEntry.getLogId(), e.getMessage());
            // Critical that error alerts are processed - throw to trigger retry
            throw new RuntimeException("Failed to process error log alert: " + logEntry.getLogId(), e);
        }
    }

    private void sendImmediateAlert(LogEntry logEntry) {
        logger.error("📧 SENDING IMMEDIATE ALERT - Service: {}", logEntry.getApplicationName());
        logger.error("   Error: {}", logEntry.getMessage());
        logger.error("   Source: {}", logEntry.getSource());
        logger.error("   Time: {}", logEntry.getTimestamp());
        
        // In production, this would:
        // 1. Send email to on-call engineers
        // 2. Send SMS/call for critical services
        // 3. Create PagerDuty incident
        // 4. Send to Slack #alerts channel
        // 5. Update status page if customer-facing
        
        sendSlackAlert(logEntry, "🚨 CRITICAL ERROR ALERT");
        sendEmailAlert("oncall@company.com", logEntry);
        
        // For payment/security related errors, send to security team
        if (isCriticalService(logEntry.getApplicationName())) {
            sendSecurityAlert(logEntry);
        }
    }

    private void sendEscalationAlert(LogEntry logEntry, int errorCount) {
        logger.error("⚠️ ERROR PATTERN DETECTED - {} errors of same type", errorCount);
        
        sendSlackAlert(logEntry, String.format("🔄 ESCALATION: %d similar errors detected", errorCount));
        sendEmailAlert("engineering-leads@company.com", logEntry);
        
        // Auto-scale or circuit breaker activation might be triggered here
        triggerAutoRemediation(logEntry);
    }

    private void createIncidentTicket(LogEntry logEntry) {
        logger.info("🎫 Creating incident ticket for error: {}", logEntry.getLogId());
        
        // Integration with ticketing systems like Jira, ServiceNow, etc.
        String ticketId = "INC-" + System.currentTimeMillis();
        
        logger.info("   Ticket ID: {}", ticketId);
        logger.info("   Priority: HIGH");
        logger.info("   Application: {}", logEntry.getApplicationName());
        logger.info("   Exception: {}", logEntry.getException());
    }

    private void updateErrorDashboard(LogEntry logEntry) {
        // Integration with monitoring systems like Grafana, DataDog, New Relic
        logger.debug("📊 Updating error dashboard with new error from: {}", 
                    logEntry.getApplicationName());
    }

    private void sendSlackAlert(LogEntry logEntry, String alertType) {
        logger.info("💬 SLACK ALERT: {}", alertType);
        logger.info("   Channel: #alerts");
        logger.info("   App: {}", logEntry.getApplicationName());
        logger.info("   Message: {}", truncateMessage(logEntry.getMessage(), 100));
    }

    private void sendEmailAlert(String recipient, LogEntry logEntry) {
        logger.info("📧 EMAIL ALERT to: {}", recipient);
        logger.info("   Subject: Critical Error - {}", logEntry.getApplicationName());
        logger.info("   Priority: High");
    }

    private void sendSecurityAlert(LogEntry logEntry) {
        logger.warn("🔒 SECURITY ALERT - Critical service error: {}", 
                   logEntry.getApplicationName());
        // Additional security team notifications
    }

    private void triggerAutoRemediation(LogEntry logEntry) {
        logger.info("🔧 Triggering auto-remediation for: {}", logEntry.getApplicationName());
        
        // Examples of auto-remediation:
        // 1. Restart unhealthy service instances
        // 2. Enable circuit breaker
        // 3. Scale up resources
        // 4. Switch to backup systems
        // 5. Clear caches
    }

    private boolean isCriticalService(String applicationName) {
        return applicationName.contains("payment") || 
               applicationName.contains("auth") || 
               applicationName.contains("security");
    }

    private String extractErrorType(String message) {
        // Simple error classification
        if (message.toLowerCase().contains("timeout")) return "TIMEOUT";
        if (message.toLowerCase().contains("connection")) return "CONNECTION";
        if (message.toLowerCase().contains("database")) return "DATABASE";
        if (message.toLowerCase().contains("memory")) return "MEMORY";
        return "GENERAL";
    }

    private String truncateMessage(String message, int maxLength) {
        return message != null && message.length() > maxLength 
            ? message.substring(0, maxLength) + "..." 
            : message;
    }

    // Public method to get error statistics
    public ConcurrentHashMap<String, AtomicInteger> getErrorCounts() {
        return new ConcurrentHashMap<>(errorCounts);
    }
}