package com.saf.userservice.dto;

import java.time.LocalDateTime;

public class AdminActionDTO {
    private Long id;
    private String adminUsername;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String reason;
    private LocalDateTime createdAt;
    private String details;

    // Constructeurs
    public AdminActionDTO() {
    }

    public AdminActionDTO(Long id, String adminUsername, String actionType, String targetType,
            Long targetId, String targetName, String reason, LocalDateTime createdAt, String details) {
        this.id = id;
        this.adminUsername = adminUsername;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetName = targetName;
        this.reason = reason;
        this.createdAt = createdAt;
        this.details = details;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
