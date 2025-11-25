package com.saf.userservice.dto;

import java.time.LocalDateTime;

public class ConversationDTO {
    private Long id;
    private Long otherUserId;
    private String otherUsername;
    private String otherUserEmail;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
    
    public ConversationDTO() {}
    
    public ConversationDTO(Long id, Long otherUserId, String otherUsername, 
                          String otherUserEmail, String lastMessage, 
                          LocalDateTime lastMessageAt, long unreadCount) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
        this.otherUserEmail = otherUserEmail;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOtherUserId() {
        return otherUserId;
    }
    
    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }
    
    public String getOtherUsername() {
        return otherUsername;
    }
    
    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }
    
    public String getOtherUserEmail() {
        return otherUserEmail;
    }
    
    public void setOtherUserEmail(String otherUserEmail) {
        this.otherUserEmail = otherUserEmail;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
    
    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    
    public long getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
