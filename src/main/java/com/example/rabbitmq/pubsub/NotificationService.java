package com.example.rabbitmq.pubsub;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.SocialPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void sendNotifications(SocialPost post) {
        try {
            logger.info("Processing notifications for post: {} by user: {}", 
                       post.getPostId(), post.getUsername());
            
            // Send push notifications to mentioned users
            if (post.getMentions() != null && !post.getMentions().isEmpty()) {
                sendMentionNotifications(post);
            }
            
            // Send hashtag notifications to users following hashtags
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                sendHashtagNotifications(post);
            }
            
            // Send notifications to close friends/family (special followers)
            sendCloseFriendsNotifications(post);
            
            logger.info("Notifications sent for post: {}", post.getPostId());
            
            
        } catch (Exception e) {
            logger.error("Error sending notifications for post: {} - Error: {}", 
                        post.getPostId(), e.getMessage());
            throw new RuntimeException("Failed to send notifications for post: " + post.getPostId(), e);
        }
    }

    private void sendMentionNotifications(SocialPost post) {
        for (String mentionedUser : post.getMentions()) {
            logger.info("Sending mention notification to: {} for post: {}", 
                       mentionedUser, post.getPostId());
            
            // In real implementation, this would:
            // 1. Get user's notification preferences
            // 2. Send push notification via FCM/APNS
            // 3. Send in-app notification
            // 4. Optionally send email notification
            
            sendPushNotification(mentionedUser, 
                String.format("%s mentioned you in a post", post.getUsername()),
                post.getContent());
        }
    }

    private void sendHashtagNotifications(SocialPost post) {
        for (String hashtag : post.getHashtags()) {
            List<String> hashtagFollowers = getHashtagFollowers(hashtag);
            
            for (String follower : hashtagFollowers) {
                logger.info("Sending hashtag notification to: {} for hashtag: #{}", 
                           follower, hashtag);
                
                sendPushNotification(follower,
                    String.format("New post with #%s", hashtag),
                    String.format("%s posted: %s", post.getUsername(), post.getContent()));
            }
        }
    }

    private void sendCloseFriendsNotifications(SocialPost post) {
        List<String> closeFriends = getCloseFriends(post.getUserId());
        
        for (String friend : closeFriends) {
            logger.info("Sending close friend notification to: {} for post: {}", 
                       friend, post.getPostId());
            
            sendPushNotification(friend,
                String.format("%s shared a new post", post.getUsername()),
                post.getContent());
        }
    }

    private void sendPushNotification(String userId, String title, String body) {
        // Mock push notification sending
        logger.info("📱 PUSH NOTIFICATION - User: {}, Title: {}, Body: {}", 
                   userId, title, truncateText(body, 50));
        
        // In real implementation, this would integrate with:
        // - Firebase Cloud Messaging (FCM) for Android
        // - Apple Push Notification Service (APNS) for iOS
        // - Web Push API for web browsers
    }

    private List<String> getHashtagFollowers(String hashtag) {
        // Mock hashtag followers - in production, this would come from database
        return switch (hashtag.toLowerCase()) {
            case "photography" -> List.of("photo-lover-1", "photo-lover-2", "camera-enthusiast");
            case "nature" -> List.of("nature-lover", "outdoor-explorer", "hiking-fan");
            case "technology", "ai" -> List.of("tech-follower", "ai-researcher", "developer-123");
            default -> List.of("general-user-1");
        };
    }

    private List<String> getCloseFriends(String userId) {
        // Mock close friends data
        return switch (userId) {
            case "user-456" -> List.of("best-friend-1", "family-member-1", "close-friend-2");
            case "user-789" -> List.of("colleague-1", "mentor-1", "study-buddy");
            default -> List.of("friend-1");
        };
    }

    private String truncateText(String text, int maxLength) {
        return text != null && text.length() > maxLength 
            ? text.substring(0, maxLength) + "..." 
            : text;
    }
}