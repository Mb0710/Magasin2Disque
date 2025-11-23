package com.saf.userservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId; // Celui qui donne l'avis

    @Column(name = "reviewer_username")
    private String reviewerUsername;

    @Column(name = "reviewed_user_id", nullable = false)
    private Long reviewedUserId; // Celui qui reçoit l'avis

    @Column(name = "transaction_id")
    private Long transactionId; // Transaction associée

    @Column(nullable = false)
    private Integer rating; // Note de 1 à 5

    @Column(length = 1000)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructeurs
    public Review() {
    }

    public Review(Long reviewerId, String reviewerUsername, Long reviewedUserId,
            Long transactionId, Integer rating, String comment) {
        this.reviewerId = reviewerId;
        this.reviewerUsername = reviewerUsername;
        this.reviewedUserId = reviewedUserId;
        this.transactionId = transactionId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }

    public Long getReviewedUserId() {
        return reviewedUserId;
    }

    public void setReviewedUserId(Long reviewedUserId) {
        this.reviewedUserId = reviewedUserId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
