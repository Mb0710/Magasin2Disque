package com.saf.userservice.dto;

public class BanUserRequest {
    private String reason;
    private String duration; // PERMANENT, 7_DAYS, 30_DAYS

    // Constructeurs
    public BanUserRequest() {
    }

    public BanUserRequest(String reason, String duration) {
        this.reason = reason;
        this.duration = duration;
    }

    // Getters et Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
