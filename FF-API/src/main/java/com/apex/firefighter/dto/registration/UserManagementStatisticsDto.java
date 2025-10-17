package com.apex.firefighter.dto.registration;

/**
 * DTO for user management statistics
 */
public class UserManagementStatisticsDto {

    private long totalUsers;
    private long adminUsers;
    private long regularUsers;
    private long pendingApprovals;
    private long activeUsers;
    private long inactiveUsers;

    // Constructors
    public UserManagementStatisticsDto() {}

    public UserManagementStatisticsDto(long totalUsers, long adminUsers, long regularUsers, 
                                      long pendingApprovals, long activeUsers, long inactiveUsers) {
        this.totalUsers = totalUsers;
        this.adminUsers = adminUsers;
        this.regularUsers = regularUsers;
        this.pendingApprovals = pendingApprovals;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
    }

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(long adminUsers) {
        this.adminUsers = adminUsers;
    }

    public long getRegularUsers() {
        return regularUsers;
    }

    public void setRegularUsers(long regularUsers) {
        this.regularUsers = regularUsers;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }
}
