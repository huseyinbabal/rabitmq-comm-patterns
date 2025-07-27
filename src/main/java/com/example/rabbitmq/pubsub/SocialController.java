package com.example.rabbitmq.pubsub;

import com.example.rabbitmq.model.SocialPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/social")
@Tag(name = "Publish/Subscribe Pattern", description = "Social media posts using fanout exchange")
public class SocialController {

    private final PostService postService;
    private final TimelineService timelineService;
    private final SocialAnalyticsService analyticsService;

    public SocialController(PostService postService, TimelineService timelineService, 
                           SocialAnalyticsService analyticsService) {
        this.postService = postService;
        this.timelineService = timelineService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/posts")
    @Operation(summary = "Create a new social post", 
               description = "Publishes a post that will be broadcasted to all subscribers via fanout exchange")
    public ResponseEntity<SocialPost> createPost(@RequestBody CreatePostRequest request) {
        SocialPost post = postService.publishPost(
                request.getUserId(),
                request.getUsername(),
                request.getContent(),
                request.getImageUrls(),
                request.getHashtags(),
                request.getMentions()
        );
        return ResponseEntity.ok(post);
    }

    @PostMapping("/posts/sample")
    @Operation(summary = "Create a sample social post", 
               description = "Creates a predefined sample post for demonstration")
    public ResponseEntity<SocialPost> createSamplePost() {
        SocialPost post = postService.publishSamplePost();
        return ResponseEntity.ok(post);
    }

    @PostMapping("/posts/text-sample")
    @Operation(summary = "Create a sample text post", 
               description = "Creates a sample text-only post")
    public ResponseEntity<SocialPost> createTextPost() {
        SocialPost post = postService.publishTextPost();
        return ResponseEntity.ok(post);
    }

    @GetMapping("/timeline/{userId}")
    @Operation(summary = "Get user timeline", 
               description = "Retrieves the timeline for a specific user")
    public ResponseEntity<List<SocialPost>> getTimeline(@PathVariable String userId) {
        List<SocialPost> timeline = timelineService.getTimelineForUser(userId);
        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/analytics/summary")
    @Operation(summary = "Get analytics summary", 
               description = "Retrieves overall analytics data")
    public ResponseEntity<AnalyticsSummary> getAnalyticsSummary() {
        AnalyticsSummary summary = new AnalyticsSummary();
        summary.setTotalPosts(analyticsService.getTotalPosts());
        summary.setTotalTimelines(timelineService.getTimelineCount());
        summary.setImagePosts(analyticsService.getPostTypeCount(SocialPost.PostType.IMAGE));
        summary.setTextPosts(analyticsService.getPostTypeCount(SocialPost.PostType.TEXT));
        summary.setTopHashtags(analyticsService.getTopHashtags());
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/analytics/user/{userId}")
    @Operation(summary = "Get user analytics", 
               description = "Retrieves analytics for a specific user")
    public ResponseEntity<UserAnalytics> getUserAnalytics(@PathVariable String userId) {
        UserAnalytics analytics = new UserAnalytics();
        analytics.setUserId(userId);
        analytics.setPostCount(analyticsService.getPostCountForUser(userId));
        analytics.setTimelinePosts(timelineService.getTimelineForUser(userId).size());
        
        return ResponseEntity.ok(analytics);
    }

    // Request/Response DTOs
    public static class CreatePostRequest {
        private String userId;
        private String username;
        private String content;
        private List<String> imageUrls;
        private List<String> hashtags;
        private List<String> mentions;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public List<String> getImageUrls() { return imageUrls; }
        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

        public List<String> getHashtags() { return hashtags; }
        public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }

        public List<String> getMentions() { return mentions; }
        public void setMentions(List<String> mentions) { this.mentions = mentions; }
    }

    public static class AnalyticsSummary {
        private long totalPosts;
        private int totalTimelines;
        private long imagePosts;
        private long textPosts;
        private Map<String, AtomicLong> topHashtags;

        public long getTotalPosts() { return totalPosts; }
        public void setTotalPosts(long totalPosts) { this.totalPosts = totalPosts; }

        public int getTotalTimelines() { return totalTimelines; }
        public void setTotalTimelines(int totalTimelines) { this.totalTimelines = totalTimelines; }

        public long getImagePosts() { return imagePosts; }
        public void setImagePosts(long imagePosts) { this.imagePosts = imagePosts; }

        public long getTextPosts() { return textPosts; }
        public void setTextPosts(long textPosts) { this.textPosts = textPosts; }

        public Map<String, AtomicLong> getTopHashtags() { return topHashtags; }
        public void setTopHashtags(Map<String, AtomicLong> topHashtags) { this.topHashtags = topHashtags; }
    }

    public static class UserAnalytics {
        private String userId;
        private long postCount;
        private int timelinePosts;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public long getPostCount() { return postCount; }
        public void setPostCount(long postCount) { this.postCount = postCount; }

        public int getTimelinePosts() { return timelinePosts; }
        public void setTimelinePosts(int timelinePosts) { this.timelinePosts = timelinePosts; }
    }
}