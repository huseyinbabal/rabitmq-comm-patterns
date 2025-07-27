package com.example.rabbitmq.pubsub;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.SocialPost;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Service
public class TimelineService {
    
    private static final Logger logger = LoggerFactory.getLogger(TimelineService.class);
    
    // In-memory storage for demonstration (use database in production)
    private final ConcurrentHashMap<String, List<SocialPost>> userTimelines = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.TIMELINE_QUEUE)
    public void updateTimeline(SocialPost post, Channel channel, Message message) {
        try {
            logger.info("Updating timeline for post: {} by user: {}", post.getPostId(), post.getUsername());
            
            // Simulate getting followers of the user who posted
            List<String> followers = getFollowers(post.getUserId());
            
            // Update timeline for each follower
            for (String followerId : followers) {
                userTimelines.computeIfAbsent(followerId, k -> new CopyOnWriteArrayList<>())
                           .add(0, post); // Add to beginning of timeline
                
                // Keep only latest 100 posts per timeline
                List<SocialPost> timeline = userTimelines.get(followerId);
                if (timeline.size() > 100) {
                    timeline.subList(100, timeline.size()).clear();
                }
            }
            
            logger.info("Timeline updated for {} followers of user: {}", 
                       followers.size(), post.getUsername());
            
            // Acknowledge successful processing
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            
        } catch (Exception e) {
            logger.error("Error updating timeline for post: {} - Error: {}", 
                        post.getPostId(), e.getMessage());
            try {
                // Requeue for retry
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                logger.error("Failed to nack message", ioException);
            }
        }
    }

    private List<String> getFollowers(String userId) {
        // Mock followers data - in production, this would come from user service/database
        return switch (userId) {
            case "user-456" -> List.of("follower-1", "follower-2", "follower-3", "follower-4", "follower-5");
            case "user-789" -> List.of("follower-2", "follower-6", "follower-7", "follower-8");
            default -> List.of("follower-1", "follower-2");
        };
    }

    public List<SocialPost> getTimelineForUser(String userId) {
        return userTimelines.getOrDefault(userId, List.of());
    }

    public int getTimelineCount() {
        return userTimelines.size();
    }
}