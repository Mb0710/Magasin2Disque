package com.saf.userservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Destinataire de la notification

    @Column(nullable = false, length = 50)
    private String type; // NEW_OFFER, OFFER_ACCEPTED, OFFER_REFUSED, NEW_MESSAGE, NEW_REVIEW, etc.

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "related_id")
    private Long relatedId; // ID de l'offre, transaction, message, etc.

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructeurs
    public Notification() {
    }

    public Notification(Long userId, String type, String message, Long relatedId) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
