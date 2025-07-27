package com.example.rabbitmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class ImageProcessingTask {
    @JsonProperty("taskId") private String taskId;
    @JsonProperty("imageUrl") private String imageUrl;
    @JsonProperty("userId") private String userId;
    @JsonProperty("operations") private List<String> operations;
    @JsonProperty("status") private TaskStatus status;
    @JsonProperty("createdAt") private LocalDateTime createdAt;
    @JsonProperty("processedAt") private LocalDateTime processedAt;
    @JsonProperty("workerNode") private String workerNode;
    @JsonProperty("processingTimeMs") private long processingTimeMs;

    public ImageProcessingTask() {}

    public ImageProcessingTask(String taskId, String imageUrl, String userId, List<String> operations) {
        this.taskId = taskId;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.operations = operations;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<String> getOperations() { return operations; }
    public void setOperations(List<String> operations) { this.operations = operations; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public String getWorkerNode() { return workerNode; }
    public void setWorkerNode(String workerNode) { this.workerNode = workerNode; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public enum TaskStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    @Override
    public String toString() {
        return "ImageProcessingTask{" + "taskId='" + taskId + '\'' + ", imageUrl='" + imageUrl + '\'' + 
               ", operations=" + operations + ", status=" + status + '}';
    }
}