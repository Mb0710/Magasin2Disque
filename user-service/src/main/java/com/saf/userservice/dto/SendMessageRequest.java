package com.saf.userservice.dto;

public class SendMessageRequest {
    private Long receiverId;
    private String content;
    
    public SendMessageRequest() {}
    
    public SendMessageRequest(Long receiverId, String content) {
        this.receiverId = receiverId;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
