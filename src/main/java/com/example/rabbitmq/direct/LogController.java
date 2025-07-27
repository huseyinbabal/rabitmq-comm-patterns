package com.example.rabbitmq.direct;

import com.example.rabbitmq.model.LogEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Direct Exchange Pattern", description = "Log processing using direct exchange routing")
public class LogController {

    private final LogService logService;
    private final AlertService alertService;
    private final MonitoringService monitoringService;
    private final LogAnalyticsService analyticsService;

    public LogController(LogService logService, AlertService alertService,
                        MonitoringService monitoringService, LogAnalyticsService analyticsService) {
        this.logService = logService;
        this.alertService = alertService;
        this.monitoringService = monitoringService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/error")
    @Operation(summary = "Log an error", 
               description = "Creates an error log entry that will be routed to the alert service")
    public ResponseEntity<LogEntry> logError(@RequestBody ErrorLogRequest request) {
        LogEntry logEntry = logService.logError(
                request.getApplicationName(),
                request.getMessage(),
                request.getSource(),
                request.getException()
        );
        return ResponseEntity.ok(logEntry);
    }

    @PostMapping("/warning")
    @Operation(summary = "Log a warning", 
               description = "Creates a warning log entry that will be routed to the monitoring service")
    public ResponseEntity<LogEntry> logWarning(@RequestBody WarningLogRequest request) {
        LogEntry logEntry = logService.logWarning(
                request.getApplicationName(),
                request.getMessage(),
                request.getSource()
        );
        return ResponseEntity.ok(logEntry);
    }

    @PostMapping("/info")
    @Operation(summary = "Log information", 
               description = "Creates an info log entry that will be routed to the analytics service")
    public ResponseEntity<LogEntry> logInfo(@RequestBody InfoLogRequest request) {
        LogEntry logEntry = logService.logInfo(
                request.getApplicationName(),
                request.getMessage(),
                request.getSource()
        );
        return ResponseEntity.ok(logEntry);
    }

    @PostMapping("/debug")
    @Operation(summary = "Log debug information", 
               description = "Creates a debug log entry for development purposes")
    public ResponseEntity<LogEntry> logDebug(@RequestBody DebugLogRequest request) {
        LogEntry logEntry = logService.logDebug(
                request.getApplicationName(),
                request.getMessage(),
                request.getSource()
        );
        return ResponseEntity.ok(logEntry);
    }

    @PostMapping("/sample-logs")
    @Operation(summary = "Generate sample logs", 
               description = "Generates sample logs of all levels for demonstration")
    public ResponseEntity<String> generateSampleLogs() {
        logService.generateSampleLogs();
        return ResponseEntity.ok("Sample logs generated successfully");
    }

    @GetMapping("/analytics/summary")
    @Operation(summary = "Get log analytics summary", 
               description = "Retrieves overall log processing statistics")
    public ResponseEntity<LogAnalyticsSummary> getAnalyticsSummary() {
        LogAnalyticsSummary summary = new LogAnalyticsSummary();
        summary.setTotalInfoLogs(analyticsService.getTotalInfoLogs());
        summary.setErrorCounts(alertService.getErrorCounts());
        summary.setWarningCounts(monitoringService.getWarningCounts());
        summary.setServiceLogs(analyticsService.getAllServiceLogs());
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/analytics/service/{serviceName}")
    @Operation(summary = "Get service-specific analytics", 
               description = "Retrieves log analytics for a specific service")
    public ResponseEntity<ServiceAnalytics> getServiceAnalytics(@PathVariable String serviceName) {
        ServiceAnalytics analytics = new ServiceAnalytics();
        analytics.setServiceName(serviceName);
        analytics.setInfoLogCount(analyticsService.getLogsForService(serviceName));
        
        // Count errors and warnings for this service
        long errorCount = alertService.getErrorCounts().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(serviceName + ":"))
                .mapToInt(entry -> entry.getValue().get())
                .sum();
        
        long warningCount = monitoringService.getWarningCounts().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(serviceName + ":"))
                .mapToInt(entry -> entry.getValue().get())
                .sum();
        
        analytics.setErrorCount(errorCount);
        analytics.setWarningCount(warningCount);
        
        return ResponseEntity.ok(analytics);
    }

    // Request DTOs
    public static class ErrorLogRequest {
        private String applicationName;
        private String message;
        private String source;
        private String exception;

        public String getApplicationName() { return applicationName; }
        public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getException() { return exception; }
        public void setException(String exception) { this.exception = exception; }
    }

    public static class WarningLogRequest {
        private String applicationName;
        private String message;
        private String source;

        public String getApplicationName() { return applicationName; }
        public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    public static class InfoLogRequest {
        private String applicationName;
        private String message;
        private String source;

        public String getApplicationName() { return applicationName; }
        public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    public static class DebugLogRequest {
        private String applicationName;
        private String message;
        private String source;

        public String getApplicationName() { return applicationName; }
        public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    // Response DTOs
    public static class LogAnalyticsSummary {
        private long totalInfoLogs;
        private Map<String, AtomicInteger> errorCounts;
        private Map<String, AtomicInteger> warningCounts;
        private Map<String, AtomicLong> serviceLogs;

        public long getTotalInfoLogs() { return totalInfoLogs; }
        public void setTotalInfoLogs(long totalInfoLogs) { this.totalInfoLogs = totalInfoLogs; }

        public Map<String, AtomicInteger> getErrorCounts() { return errorCounts; }
        public void setErrorCounts(Map<String, AtomicInteger> errorCounts) { this.errorCounts = errorCounts; }

        public Map<String, AtomicInteger> getWarningCounts() { return warningCounts; }
        public void setWarningCounts(Map<String, AtomicInteger> warningCounts) { this.warningCounts = warningCounts; }

        public Map<String, AtomicLong> getServiceLogs() { return serviceLogs; }
        public void setServiceLogs(Map<String, AtomicLong> serviceLogs) { this.serviceLogs = serviceLogs; }
    }

    public static class ServiceAnalytics {
        private String serviceName;
        private long infoLogCount;
        private long errorCount;
        private long warningCount;

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public long getInfoLogCount() { return infoLogCount; }
        public void setInfoLogCount(long infoLogCount) { this.infoLogCount = infoLogCount; }

        public long getErrorCount() { return errorCount; }
        public void setErrorCount(long errorCount) { this.errorCount = errorCount; }

        public long getWarningCount() { return warningCount; }
        public void setWarningCount(long warningCount) { this.warningCount = warningCount; }
    }
}