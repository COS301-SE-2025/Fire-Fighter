package com.apex.firefighter.dto;

import java.util.Map;

/**
 * DTO for Emergency Response Statistics
 * Contains system-wide statistics calculated from all tickets in the database
 */
public class EmergencyStatisticsResponse {
    
    private Map<String, Integer> emergencyTypeBreakdown;
    private String mostCommonEmergencyType;
    private Integer systemHealthScore;
    private Integer averageResponseTime;
    private Double completionRate;
    private Integer currentMonthTickets;
    private Integer totalTickets;
    private Integer activeTickets;
    
    public EmergencyStatisticsResponse() {
    }
    
    public EmergencyStatisticsResponse(
            Map<String, Integer> emergencyTypeBreakdown,
            String mostCommonEmergencyType,
            Integer systemHealthScore,
            Integer averageResponseTime,
            Double completionRate,
            Integer currentMonthTickets,
            Integer totalTickets,
            Integer activeTickets) {
        this.emergencyTypeBreakdown = emergencyTypeBreakdown;
        this.mostCommonEmergencyType = mostCommonEmergencyType;
        this.systemHealthScore = systemHealthScore;
        this.averageResponseTime = averageResponseTime;
        this.completionRate = completionRate;
        this.currentMonthTickets = currentMonthTickets;
        this.totalTickets = totalTickets;
        this.activeTickets = activeTickets;
    }
    
    // Getters and Setters
    public Map<String, Integer> getEmergencyTypeBreakdown() {
        return emergencyTypeBreakdown;
    }
    
    public void setEmergencyTypeBreakdown(Map<String, Integer> emergencyTypeBreakdown) {
        this.emergencyTypeBreakdown = emergencyTypeBreakdown;
    }
    
    public String getMostCommonEmergencyType() {
        return mostCommonEmergencyType;
    }
    
    public void setMostCommonEmergencyType(String mostCommonEmergencyType) {
        this.mostCommonEmergencyType = mostCommonEmergencyType;
    }
    
    public Integer getSystemHealthScore() {
        return systemHealthScore;
    }
    
    public void setSystemHealthScore(Integer systemHealthScore) {
        this.systemHealthScore = systemHealthScore;
    }
    
    public Integer getAverageResponseTime() {
        return averageResponseTime;
    }
    
    public void setAverageResponseTime(Integer averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }
    
    public Double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }
    
    public Integer getCurrentMonthTickets() {
        return currentMonthTickets;
    }
    
    public void setCurrentMonthTickets(Integer currentMonthTickets) {
        this.currentMonthTickets = currentMonthTickets;
    }
    
    public Integer getTotalTickets() {
        return totalTickets;
    }
    
    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }
    
    public Integer getActiveTickets() {
        return activeTickets;
    }
    
    public void setActiveTickets(Integer activeTickets) {
        this.activeTickets = activeTickets;
    }
}
