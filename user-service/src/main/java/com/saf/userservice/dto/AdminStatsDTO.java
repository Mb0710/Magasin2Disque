package com.saf.userservice.dto;

public class AdminStatsDTO {
    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long totalAnnonces;
    private long activeAnnonces;
    private long totalTransactions;
    private long pendingReports;
    private long adminActionsToday;

    // Constructeurs
    public AdminStatsDTO() {
    }

    public AdminStatsDTO(long totalUsers, long activeUsers, long bannedUsers,
            long totalAnnonces, long activeAnnonces, long totalTransactions,
            long pendingReports, long adminActionsToday) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.bannedUsers = bannedUsers;
        this.totalAnnonces = totalAnnonces;
        this.activeAnnonces = activeAnnonces;
        this.totalTransactions = totalTransactions;
        this.pendingReports = pendingReports;
        this.adminActionsToday = adminActionsToday;
    }

    // Getters et Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getBannedUsers() {
        return bannedUsers;
    }

    public void setBannedUsers(long bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public long getTotalAnnonces() {
        return totalAnnonces;
    }

    public void setTotalAnnonces(long totalAnnonces) {
        this.totalAnnonces = totalAnnonces;
    }

    public long getActiveAnnonces() {
        return activeAnnonces;
    }

    public void setActiveAnnonces(long activeAnnonces) {
        this.activeAnnonces = activeAnnonces;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public long getAdminActionsToday() {
        return adminActionsToday;
    }

    public void setAdminActionsToday(long adminActionsToday) {
        this.adminActionsToday = adminActionsToday;
    }
}
