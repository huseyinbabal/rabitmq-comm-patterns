package com.example.rabbitmq.direct;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LogAnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogAnalyticsService.class);
    
    private final ConcurrentHashMap<String, AtomicLong> serviceLogs = new ConcurrentHashMap<>();
    private final AtomicLong totalInfoLogs = new AtomicLong(0);

    @RabbitListener(queues = RabbitConfig.INFO_LOG_QUEUE)
    public void processInfoLog(LogEntry logEntry) {
        try {
            logger.info("ℹ️ INFO LOG - App: {}, Message: {}", 
                       logEntry.getApplicationName(), logEntry.getMessage());
            
            // Track info logs by service
            serviceLogs.computeIfAbsent(logEntry.getApplicationName(), k -> new AtomicLong(0))
                      .incrementAndGet();
            
            totalInfoLogs.incrementAndGet();
            
            // Store for analytics and reporting
            storeForAnalytics(logEntry);
            
            // Extract business metrics from info logs
            extractBusinessMetrics(logEntry);
            
            // Build usage patterns
            analyzeUsagePatterns(logEntry);
            
            // Generate insights
            generateInsights(logEntry);
            
            logger.debug("Info log processed: {} - Total info logs: {}", 
                        logEntry.getLogId(), totalInfoLogs.get());
            
            
        } catch (Exception e) {
            logger.error("Error processing info log: {} - Error: {}", 
                        logEntry.getLogId(), e.getMessage());
            throw new RuntimeException("Failed to process info log: " + logEntry.getLogId(), e);
        }
    }

    private void storeForAnalytics(LogEntry logEntry) {
        logger.debug("💾 Storing log for analytics: {}", logEntry.getLogId());
        
        // In production, this would store to analytics databases like:
        // - Elasticsearch for log search and analysis
        // - BigQuery for large-scale analytics
        // - InfluxDB for time-series analysis
        // - Apache Kafka for real-time streaming analytics
        
        indexInElasticsearch(logEntry);
        sendToDataWarehouse(logEntry);
    }

    private void extractBusinessMetrics(LogEntry logEntry) {
        String message = logEntry.getMessage().toLowerCase();
        
        // Extract business events from info logs
        if (message.contains("order created")) {
            recordBusinessEvent("order.created", logEntry);
        } else if (message.contains("user registered")) {
            recordBusinessEvent("user.registered", logEntry);
        } else if (message.contains("payment processed")) {
            recordBusinessEvent("payment.processed", logEntry);
        } else if (message.contains("login successful")) {
            recordBusinessEvent("user.login", logEntry);
        }
        
        // Extract performance metrics
        extractPerformanceMetrics(logEntry);
    }

    private void analyzeUsagePatterns(LogEntry logEntry) {
        logger.debug("📊 Analyzing usage patterns from: {}", logEntry.getApplicationName());
        
        // Analyze patterns like:
        // - Peak usage times
        // - Service interaction patterns
        // - User behavior patterns
        // - Feature usage analytics
        
        String service = logEntry.getApplicationName();
        long logCount = serviceLogs.get(service).get();
        
        if (logCount % 100 == 0) {
            logger.info("📈 Usage milestone: {} has processed {} operations", service, logCount);
            generateUsageReport(service, logCount);
        }
    }

    private void generateInsights(LogEntry logEntry) {
        // Generate actionable insights from log patterns
        analyzeServiceHealth(logEntry);
        identifyOptimizationOpportunities(logEntry);
        detectAnomalies(logEntry);
    }

    private void recordBusinessEvent(String eventType, LogEntry logEntry) {
        logger.info("📊 BUSINESS EVENT: {} from {}", eventType, logEntry.getApplicationName());
        
        // This would feed into business intelligence systems
        // - Revenue tracking
        // - User engagement metrics
        // - Conversion funnel analysis
        // - A/B testing results
        
        sendToBusinessIntelligence(eventType, logEntry);
    }

    private void extractPerformanceMetrics(LogEntry logEntry) {
        // Look for performance indicators in info logs
        String message = logEntry.getMessage();
        
        if (message.contains("completed in") || message.contains("took")) {
            logger.debug("⏱️ Performance metric detected in: {}", logEntry.getApplicationName());
            
            // Extract timing information
            // Parse and send to monitoring systems
        }
        
        if (message.contains("processed") && message.contains("records")) {
            logger.debug("📊 Throughput metric detected in: {}", logEntry.getApplicationName());
            
            // Extract throughput information
        }
    }

    private void analyzeServiceHealth(LogEntry logEntry) {
        String service = logEntry.getApplicationName();
        long logCount = serviceLogs.get(service).get();
        
        // Simple health indicator based on log volume
        if (logCount > 1000) {
            logger.info("✅ Service {} shows high activity - {} info logs", service, logCount);
        } else if (logCount < 10) {
            logger.warn("⚠️ Service {} shows low activity - only {} info logs", service, logCount);
        }
    }

    private void identifyOptimizationOpportunities(LogEntry logEntry) {
        // Identify potential optimizations from log patterns
        String message = logEntry.getMessage().toLowerCase();
        
        if (message.contains("cache miss")) {
            logger.info("💡 OPTIMIZATION OPPORTUNITY: Consider cache warming for {}", 
                       logEntry.getApplicationName());
        }
        
        if (message.contains("slow query") || message.contains("database query took")) {
            logger.info("💡 OPTIMIZATION OPPORTUNITY: Database query optimization needed in {}", 
                       logEntry.getApplicationName());
        }
    }

    private void detectAnomalies(LogEntry logEntry) {
        // Simple anomaly detection based on patterns
        String service = logEntry.getApplicationName();
        long currentCount = serviceLogs.get(service).get();
        
        // This is a simple example - production systems would use more sophisticated methods
        if (currentCount > 0 && currentCount % 500 == 0) {
            logger.info("🔍 ANOMALY CHECK: {} has unusual high activity - {} logs", 
                       service, currentCount);
        }
    }

    private void generateUsageReport(String service, long logCount) {
        logger.info("📋 USAGE REPORT for {}: {} operations processed", service, logCount);
        logger.info("   Trend: Steady activity");
        logger.info("   Health: Normal");
        logger.info("   Recommendations: Continue monitoring");
    }

    private void indexInElasticsearch(LogEntry logEntry) {
        logger.debug("🔍 Indexing in Elasticsearch: {}", logEntry.getLogId());
        // Integration with Elasticsearch for log indexing
    }

    private void sendToDataWarehouse(LogEntry logEntry) {
        logger.debug("🏢 Sending to data warehouse: {}", logEntry.getLogId());
        // Integration with data warehouse for long-term analytics
    }

    private void sendToBusinessIntelligence(String eventType, LogEntry logEntry) {
        logger.debug("📊 Sending to BI system: {} - {}", eventType, logEntry.getLogId());
        // Integration with business intelligence systems
    }

    // Public methods for getting analytics data
    public long getTotalInfoLogs() {
        return totalInfoLogs.get();
    }

    public long getLogsForService(String service) {
        return serviceLogs.getOrDefault(service, new AtomicLong(0)).get();
    }

    public ConcurrentHashMap<String, AtomicLong> getAllServiceLogs() {
        return new ConcurrentHashMap<>(serviceLogs);
    }
}