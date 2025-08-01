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
public class MonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    
    private final ConcurrentHashMap<String, AtomicInteger> warningCounts = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.WARNING_LOG_QUEUE)
    public void processWarningLog(LogEntry logEntry) {
        try {
            logger.warn("⚠️ WARNING DETECTED - App: {}, Message: {}", 
                       logEntry.getApplicationName(), logEntry.getMessage());
            
            // Track warning frequency
            String warningKey = logEntry.getApplicationName() + ":" + extractWarningType(logEntry.getMessage());
            int warningCount = warningCounts.computeIfAbsent(warningKey, k -> new AtomicInteger(0))
                                           .incrementAndGet();
            
            // Update monitoring metrics
            updateMonitoringMetrics(logEntry);
            
            // Check for warning patterns that might indicate problems
            analyzeWarningPatterns(logEntry, warningCount);
            
            // Send to monitoring dashboard
            sendToMonitoringDashboard(logEntry);
            
            // For performance-related warnings, check thresholds
            if (isPerformanceWarning(logEntry)) {
                checkPerformanceThresholds(logEntry);
            }
            
            logger.info("Warning processed for log: {} (Warning #{} for this type)", 
                       logEntry.getLogId(), warningCount);
            
            
        } catch (Exception e) {
            logger.error("Error processing warning log: {} - Error: {}", 
                        logEntry.getLogId(), e.getMessage());
            throw new RuntimeException("Failed to process warning log: " + logEntry.getLogId(), e);
        }
    }

    private void updateMonitoringMetrics(LogEntry logEntry) {
        logger.debug("📈 Updating monitoring metrics for: {}", logEntry.getApplicationName());
        
        // In production, this would send metrics to:
        // - Prometheus
        // - CloudWatch
        // - DataDog
        // - New Relic
        // - Grafana
        
        recordMetric("warnings.total", 1, logEntry.getApplicationName());
        recordMetric("warnings.by_type." + extractWarningType(logEntry.getMessage()), 1, 
                    logEntry.getApplicationName());
    }

    private void analyzeWarningPatterns(LogEntry logEntry, int warningCount) {
        if (warningCount >= 10) {
            logger.warn("🔍 WARNING PATTERN DETECTED: {} similar warnings for {}", 
                       warningCount, logEntry.getApplicationName());
            
            // This could escalate to alert if pattern indicates degrading performance
            if (warningCount >= 20) {
                sendPreventiveAlert(logEntry, warningCount);
            }
        }
        
        // Analyze time-based patterns
        analyzeTimeBasedPatterns(logEntry);
    }

    private void sendToMonitoringDashboard(LogEntry logEntry) {
        logger.info("📊 Sending warning to monitoring dashboard: {}", logEntry.getApplicationName());
        
        // Dashboard widgets that would be updated:
        // - Warning count by service
        // - Warning trends over time
        // - Top warning sources
        // - Warning severity distribution
        
        logger.debug("   Dashboard Widget: warning-count-by-service");
        logger.debug("   Dashboard Widget: warning-trends-timeline");
        logger.debug("   Dashboard Widget: top-warning-sources");
    }

    private void checkPerformanceThresholds(LogEntry logEntry) {
        logger.info("⏱️ Checking performance thresholds for: {}", logEntry.getApplicationName());
        
        // Extract performance metrics from log message
        if (logEntry.getMessage().toLowerCase().contains("slow")) {
            logger.warn("   Performance degradation detected");
            sendPerformanceAlert(logEntry);
        }
        
        if (logEntry.getMessage().toLowerCase().contains("timeout")) {
            logger.warn("   Timeout issues detected");
            checkTimeoutPatterns(logEntry);
        }
        
        if (logEntry.getMessage().toLowerCase().contains("memory")) {
            logger.warn("   Memory pressure detected");
            checkMemoryUsage(logEntry);
        }
    }

    private void sendPreventiveAlert(LogEntry logEntry, int warningCount) {
        logger.warn("🛡️ PREVENTIVE ALERT: {} warnings may indicate developing issue", warningCount);
        
        // Send to operations team before it becomes critical
        logger.info("   Notifying operations team");
        logger.info("   Suggested actions: Investigate {}", logEntry.getApplicationName());
        
        // Could trigger automated scaling or other preventive measures
        suggestPreventiveActions(logEntry, warningCount);
    }

    private void analyzeTimeBasedPatterns(LogEntry logEntry) {
        // This would analyze warning frequency over time windows
        logger.debug("🕒 Analyzing time-based warning patterns for: {}", 
                    logEntry.getApplicationName());
        
        // Check for patterns like:
        // - Warnings increasing over time
        // - Cyclical warning patterns
        // - Warnings correlated with specific times/days
    }

    private void sendPerformanceAlert(LogEntry logEntry) {
        logger.warn("🐌 PERFORMANCE ALERT: Slow operation detected in {}", 
                   logEntry.getApplicationName());
        
        // Would integrate with APM tools to get detailed performance metrics
    }

    private void checkTimeoutPatterns(LogEntry logEntry) {
        logger.warn("⏰ Analyzing timeout patterns for: {}", logEntry.getApplicationName());
        
        // Could trigger:
        // - Connection pool analysis
        // - Network latency checks
        // - Downstream service health checks
    }

    private void checkMemoryUsage(LogEntry logEntry) {
        logger.warn("💾 Memory pressure warning for: {}", logEntry.getApplicationName());
        
        // Could trigger:
        // - Memory profiling
        // - Garbage collection analysis
        // - Auto-scaling decisions
    }

    private void suggestPreventiveActions(LogEntry logEntry, int warningCount) {
        logger.info("💡 Suggested preventive actions:");
        logger.info("   1. Scale up {} if needed", logEntry.getApplicationName());
        logger.info("   2. Check downstream dependencies");
        logger.info("   3. Review recent deployments");
        logger.info("   4. Analyze resource utilization");
    }

    private void recordMetric(String metricName, double value, String service) {
        logger.debug("📏 Recording metric: {} = {} for service: {}", metricName, value, service);
        
        // In production, this would send to metrics collection systems
    }

    private boolean isPerformanceWarning(LogEntry logEntry) {
        String message = logEntry.getMessage().toLowerCase();
        return message.contains("slow") || 
               message.contains("timeout") || 
               message.contains("latency") ||
               message.contains("memory") ||
               message.contains("cpu");
    }

    private String extractWarningType(String message) {
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("performance") || lowerMessage.contains("slow")) return "PERFORMANCE";
        if (lowerMessage.contains("authentication") || lowerMessage.contains("auth")) return "AUTH";
        if (lowerMessage.contains("database") || lowerMessage.contains("db")) return "DATABASE";
        if (lowerMessage.contains("network") || lowerMessage.contains("connection")) return "NETWORK";
        if (lowerMessage.contains("memory") || lowerMessage.contains("cpu")) return "RESOURCE";
        return "GENERAL";
    }

    public ConcurrentHashMap<String, AtomicInteger> getWarningCounts() {
        return new ConcurrentHashMap<>(warningCounts);
    }
}