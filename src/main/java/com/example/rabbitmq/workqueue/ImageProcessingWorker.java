package com.example.rabbitmq.workqueue;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.ImageProcessingTask;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ImageProcessingWorker {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingWorker.class);
    private final Random random = new Random();
    private final String workerNodeId = "worker-" + random.nextInt(1000);

    @RabbitListener(queues = RabbitConfig.IMAGE_PROCESSING_QUEUE, concurrency = "3-5")
    public void processImage(ImageProcessingTask task, Channel channel, Message message) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("🖼️ {} starting image processing: {}", workerNodeId, task.getTaskId());
            
            task.setStatus(ImageProcessingTask.TaskStatus.PROCESSING);
            task.setWorkerNode(workerNodeId);
            
            // Simulate image processing operations
            for (String operation : task.getOperations()) {
                processOperation(task, operation);
                
                // Simulate processing time
                Thread.sleep(1000 + random.nextInt(2000));
            }
            
            // Mark as completed
            task.setStatus(ImageProcessingTask.TaskStatus.COMPLETED);
            task.setProcessedAt(LocalDateTime.now());
            task.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            
            logger.info("✅ {} completed image processing: {} in {}ms", 
                       workerNodeId, task.getTaskId(), task.getProcessingTimeMs());
            
            // Send completion notification (could be another queue)
            notifyCompletion(task);
            
            // Acknowledge successful processing
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            
        } catch (InterruptedException e) {
            logger.error("❌ {} processing interrupted: {}", workerNodeId, task.getTaskId());
            Thread.currentThread().interrupt();
            task.setStatus(ImageProcessingTask.TaskStatus.FAILED);
            handleFailure(task, channel, message, e);
            
        } catch (Exception e) {
            logger.error("❌ {} processing error: {} - {}", workerNodeId, task.getTaskId(), e.getMessage());
            task.setStatus(ImageProcessingTask.TaskStatus.FAILED);
            handleFailure(task, channel, message, e);
        }
    }

    private void processOperation(ImageProcessingTask task, String operation) {
        logger.info("   {} processing '{}' on image: {}", workerNodeId, operation, task.getImageUrl());
        
        switch (operation.toUpperCase()) {
            case "RESIZE":
                logger.info("   📏 Resizing image to multiple dimensions");
                break;
            case "THUMBNAIL":
                logger.info("   🖼️ Generating thumbnail versions");
                break;
            case "WATERMARK":
                logger.info("   🏷️ Adding watermark to image");
                break;
            case "FILTER":
                logger.info("   🎨 Applying image filters");
                break;
            case "COMPRESS":
                logger.info("   🗜️ Compressing image for web");
                break;
            case "METADATA":
                logger.info("   📊 Extracting image metadata");
                break;
            default:
                logger.info("   ⚙️ Performing generic operation: {}", operation);
        }
    }

    private void notifyCompletion(ImageProcessingTask task) {
        logger.info("📨 Sending completion notification for task: {} to user: {}", 
                   task.getTaskId(), task.getUserId());
        
        // In production, this would:
        // 1. Send email/push notification to user
        // 2. Update database with processed image URLs
        // 3. Trigger other workflows (e.g., ML analysis)
        // 4. Update user dashboard
        // 5. Send webhook to client applications
    }

    private void handleFailure(ImageProcessingTask task, Channel channel, Message message, Exception e) {
        try {
            logger.error("🚫 Task failed, sending to DLX: {}", task.getTaskId());
            
            // Check retry count from message headers
            Integer retryCount = (Integer) message.getMessageProperties().getHeaders().get("x-retry-count");
            if (retryCount == null) retryCount = 0;
            
            if (retryCount < 3) {
                // Retry with delay
                logger.info("🔄 Retrying task: {} (attempt {})", task.getTaskId(), retryCount + 1);
                // Could implement exponential backoff here
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } else {
                // Send to dead letter queue
                logger.error("💀 Max retries exceeded, sending to DLX: {}", task.getTaskId());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
            
        } catch (Exception nackException) {
            logger.error("Failed to handle failure for task: {}", task.getTaskId(), nackException);
        }
    }
}