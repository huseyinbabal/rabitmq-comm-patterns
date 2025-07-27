package com.example.rabbitmq.workqueue;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.ImageProcessingTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Work Queue Pattern", description = "Image processing using work queue for load distribution")
public class ImageProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingController.class);
    private final RabbitTemplate rabbitTemplate;

    public ImageProcessingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/process")
    @Operation(summary = "Submit image for processing")
    public ResponseEntity<ImageProcessingTask> processImage(@RequestBody ImageProcessingRequest request) {
        ImageProcessingTask task = new ImageProcessingTask(
            UUID.randomUUID().toString(),
            request.getImageUrl(),
            request.getUserId(),
            request.getOperations()
        );

        rabbitTemplate.convertAndSend(RabbitConfig.IMAGE_PROCESSING_QUEUE, task);
        logger.info("Image processing task submitted: {} - Operations: {}", 
                   task.getTaskId(), task.getOperations());
        
        return ResponseEntity.ok(task);
    }

    @PostMapping("/batch-process")
    @Operation(summary = "Submit multiple images for processing")
    public ResponseEntity<String> processBatchImages() {
        // Submit multiple tasks to demonstrate work queue load balancing
        for (int i = 1; i <= 10; i++) {
            ImageProcessingTask task = new ImageProcessingTask(
                UUID.randomUUID().toString(),
                "https://example.com/image" + i + ".jpg",
                "user-batch-" + i,
                List.of("RESIZE", "THUMBNAIL", "WATERMARK")
            );
            rabbitTemplate.convertAndSend(RabbitConfig.IMAGE_PROCESSING_QUEUE, task);
        }
        
        logger.info("Batch of 10 image processing tasks submitted");
        return ResponseEntity.ok("10 image processing tasks submitted successfully");
    }

    public static class ImageProcessingRequest {
        private String imageUrl;
        private String userId;
        private List<String> operations;

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public List<String> getOperations() { return operations; }
        public void setOperations(List<String> operations) { this.operations = operations; }
    }
}