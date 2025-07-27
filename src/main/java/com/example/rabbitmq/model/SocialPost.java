package com.example.rabbitmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class SocialPost {
    
    @JsonProperty("postId")
    private String postId;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("imageUrls")
    private List<String> imageUrls;
    
    @JsonProperty("hashtags")
    private List<String> hashtags;
    
    @JsonProperty("mentions")
    private List<String> mentions;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("postType")
    private PostType postType;

    public SocialPost() {}

    public SocialPost(String postId, String userId, String username, String content, 
                     List<String> imageUrls, List<String> hashtags, List<String> mentions) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.imageUrls = imageUrls;
        this.hashtags = hashtags;
        this.mentions = mentions;
        this.timestamp = LocalDateTime.now();
        this.postType = imageUrls != null && !imageUrls.isEmpty() ? PostType.IMAGE : PostType.TEXT;
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

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

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }

    @Override
    public String toString() {
        return "SocialPost{" +
                "postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", postType=" + postType +
                '}';
    }

    public enum PostType {
        TEXT, IMAGE, VIDEO, STORY
    }
}