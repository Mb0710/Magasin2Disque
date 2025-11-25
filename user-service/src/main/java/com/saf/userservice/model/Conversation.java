package com.saf.userservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sentAt DESC")
    private List<Message> messages = new ArrayList<>();
    
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public Conversation() {
        this.createdAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
    }
    
    public Conversation(User user1, User user2) {
        this();
        this.user1 = user1;
        this.user2 = user2;
    }
    
    // Méthode pour obtenir l'autre utilisateur de la conversation
    public User getOtherUser(Long currentUserId) {
        if (user1.getId().equals(currentUserId)) {
            return user2;
        }
        return user1;
    }
    
    // Méthode pour compter les messages non lus pour un utilisateur
    public long getUnreadCount(Long userId) {
        return messages.stream()
            .filter(m -> m.getReceiver().getId().equals(userId) && !m.isRead())
            .count();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser1() {
        return user1;
    }
    
    public void setUser1(User user1) {
        this.user1 = user1;
    }
    
    public User getUser2() {
        return user2;
    }
    
    public void setUser2(User user2) {
        this.user2 = user2;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
    
    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
