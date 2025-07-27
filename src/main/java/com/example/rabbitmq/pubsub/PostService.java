package com.example.rabbitmq.pubsub;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.SocialPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    
    private final RabbitTemplate rabbitTemplate;

    public PostService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public SocialPost publishPost(String userId, String username, String content, 
                                 List<String> imageUrls, List<String> hashtags, 
                                 List<String> mentions) {
        
        SocialPost post = new SocialPost(
                UUID.randomUUID().toString(),
                userId,
                username,
                content,
                imageUrls,
                hashtags,
                mentions
        );

        logger.info("Publishing new social post: {}", post);

        try {
            // Publish to fanout exchange - all bound queues will receive this message
            rabbitTemplate.convertAndSend(RabbitConfig.SOCIAL_FANOUT_EXCHANGE, "", post);
            logger.info("Post {} published to fanout exchange", post.getPostId());
        } catch (Exception e) {
            logger.error("Failed to publish post to exchange: {}", e.getMessage());
            throw new RuntimeException("Failed to publish post", e);
        }

        return post;
    }

    public SocialPost publishSamplePost() {
        return publishPost(
                "user-456",
                "jane_photographer",
                "Just captured this amazing sunset! 🌅 #photography #nature #beautiful",
                List.of("https://example.com/sunset1.jpg", "https://example.com/sunset2.jpg"),
                List.of("photography", "nature", "beautiful"),
                List.of("john_doe", "nature_lover")
        );
    }

    public SocialPost publishTextPost() {
        return publishPost(
                "user-789",
                "tech_blogger",
                "Excited to share my thoughts on the latest in AI technology! Check out my latest blog post. #AI #technology #innovation",
                null,
                List.of("AI", "technology", "innovation"),
                List.of()
        );
    }
}