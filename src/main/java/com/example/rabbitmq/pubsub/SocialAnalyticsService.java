package com.example.rabbitmq.pubsub;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.SocialPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SocialAnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SocialAnalyticsService.class);
    
    // In-memory analytics storage (use time-series database like InfluxDB in production)
    private final ConcurrentHashMap<String, AtomicLong> userPostCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> hashtagCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SocialPost.PostType, AtomicLong> postTypeCounts = new ConcurrentHashMap<>();
    private final AtomicLong totalPosts = new AtomicLong(0);

    @RabbitListener(queues = RabbitConfig.ANALYTICS_QUEUE)
    public void analyzePost(SocialPost post) {
        try {
            logger.info("Analyzing post: {} by user: {}", post.getPostId(), post.getUsername());
            
            // Track overall post count
            totalPosts.incrementAndGet();
            
            // Track posts per user
            userPostCounts.computeIfAbsent(post.getUserId(), k -> new AtomicLong(0))
                          .incrementAndGet();
            
            // Track hashtag usage
            if (post.getHashtags() != null) {
                for (String hashtag : post.getHashtags()) {
                    hashtagCounts.computeIfAbsent(hashtag.toLowerCase(), k -> new AtomicLong(0))
                               .incrementAndGet();
                }
            }
            
            // Track post types
            postTypeCounts.computeIfAbsent(post.getPostType(), k -> new AtomicLong(0))
                         .incrementAndGet();
            
            // Generate engagement predictions
            generateEngagementPrediction(post);
            
            // Update trending analysis
            updateTrendingAnalysis(post);
            
            // Store detailed analytics
            storeDetailedAnalytics(post);
            
            logger.info("Analytics completed for post: {}. Total posts tracked: {}", 
                       post.getPostId(), totalPosts.get());
            
            
        } catch (Exception e) {
            logger.error("Error analyzing post: {} - Error: {}", post.getPostId(), e.getMessage());
            throw new RuntimeException("Failed to analyze post: " + post.getPostId(), e);
        }
    }

    private void generateEngagementPrediction(SocialPost post) {
        int engagementScore = 0;
        
        // Factor in hashtags (popular hashtags get higher scores)
        if (post.getHashtags() != null) {
            for (String hashtag : post.getHashtags()) {
                Long count = hashtagCounts.getOrDefault(hashtag.toLowerCase(), new AtomicLong(0)).get();
                engagementScore += Math.min(count.intValue() * 2, 20); // Cap at 20 per hashtag
            }
        }
        
        // Factor in mentions
        if (post.getMentions() != null && !post.getMentions().isEmpty()) {
            engagementScore += post.getMentions().size() * 5;
        }
        
        // Factor in post type
        engagementScore += switch (post.getPostType()) {
            case IMAGE -> 15;
            case VIDEO -> 25;
            case TEXT -> 5;
            case STORY -> 10;
        };
        
        // Factor in content length (optimal length gets bonus)
        int contentLength = post.getContent() != null ? post.getContent().length() : 0;
        if (contentLength >= 50 && contentLength <= 200) {
            engagementScore += 10; // Sweet spot for engagement
        }
        
        logger.info("📊 Engagement prediction for post {}: {} points", 
                   post.getPostId(), engagementScore);
    }

    private void updateTrendingAnalysis(SocialPost post) {
        if (post.getHashtags() != null) {
            for (String hashtag : post.getHashtags()) {
                Long count = hashtagCounts.get(hashtag.toLowerCase()).get();
                if (count != null && count > 10) { // Trending threshold
                    logger.info("🔥 TRENDING: #{} with {} posts", hashtag, count);
                }
            }
        }
    }

    private void storeDetailedAnalytics(SocialPost post) {
        // In production, this would store to a time-series database
        logger.debug("Storing detailed analytics for post: {} - User: {}, Type: {}, Hashtags: {}", 
                    post.getPostId(), post.getUserId(), post.getPostType(), 
                    post.getHashtags() != null ? post.getHashtags().size() : 0);
    }

    // Public methods to get analytics data
    public long getTotalPosts() {
        return totalPosts.get();
    }

    public long getPostCountForUser(String userId) {
        return userPostCounts.getOrDefault(userId, new AtomicLong(0)).get();
    }

    public long getHashtagCount(String hashtag) {
        return hashtagCounts.getOrDefault(hashtag.toLowerCase(), new AtomicLong(0)).get();
    }

    public long getPostTypeCount(SocialPost.PostType postType) {
        return postTypeCounts.getOrDefault(postType, new AtomicLong(0)).get();
    }

    public ConcurrentHashMap<String, AtomicLong> getTopHashtags() {
        return new ConcurrentHashMap<>(hashtagCounts);
    }
}