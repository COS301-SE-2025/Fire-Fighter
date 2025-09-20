package com.apex.firefighter.service;

import com.apex.firefighter.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnomalyDetectionService {

    private final TicketRepository ticketRepository;

    //configuration for frequent request detection
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_REQUESTS_PER_DAY = 20;

    public AnomalyDetectionService(TicketRepository ticketRepository){

        this.ticketRepository = ticketRepository;

    }


    //CHANGE DOCUMENTATION WHEN MAKE CHANGES (SINCE IT WILL CHANGE WHAT IS BEGIN RETURNED)
    /**
     * Checks if a user's ticket creation shows anomalous behavior
     * 
     * @param userId The user ID to check
     * @return true if anomalous behavior is detected, false otherwise
     */
    public boolean checkForAnomalousTicketCreation(String userId){

        //currently only checking for frequent requests
        //add other anomaly checks here as needed

        return isFrequentRequestAnomaly(userId);

    }

    /**
     * Checks if a user has made too many requests in a short time period
     * 
     * @param userId The user ID to check
     * @return true if the user has made suspicious number of requests
     */
    private boolean isFrequentRequestAnomaly(String userId){

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        //count requests in the last hour
        long requestsLastHour = ticketRepository.countTicketsByUserSince(userId, oneHourAgo);
        
        //count requests in the last day
        long requestsLastDay = ticketRepository.countTicketsByUserSince(userId, oneDayAgo);

        //check if either threshold is exceeded
        boolean hourlyThresholdExceeded = requestsLastHour >= MAX_REQUESTS_PER_HOUR;
        boolean dailyThresholdExceeded = requestsLastDay >= MAX_REQUESTS_PER_DAY;

        if(hourlyThresholdExceeded || dailyThresholdExceeded){

            System.out.println("🚨 ANOMALY DETECTED: User " + userId + 
                " - Requests last hour: " + requestsLastHour + 
                " (max: " + MAX_REQUESTS_PER_HOUR + ")" +
                " - Requests last day: " + requestsLastDay + 
                " (max: " + MAX_REQUESTS_PER_DAY + ")");

            return true;

        }

        return false;

    }

    /**
     * Gets details about the detected anomaly for logging/notification purposes
     * 
     * @param userID
     * @return A string describing the anomaly, or null if no anomaly is deteceted
     */
    public String getRequestFrequencyDetails(String userID){

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime oneDayAgo = LocalDateTime.nmow().minusDays(1);

        long requestsLastHour = ticketRepository.countTicketsByUserSince(userID, oneHourAgo);
        long requestsLastDay = ticketRepository.countTicketsByUserSince(userID, oneDayAgo);

        if(requestsLastHour >= MAX_REQUESTS_PER_HOUR){

            return String.format("User has made %d requests in the last hour (threshold: %d)", requestsLastHour, MAX_REQUESTS_PER_HOUR);

        }

        if(requestsLastDay >= MAX_REQUESTS_PER_DAY){

            return String.format("User has made %d requests in the last 24 hours (threshold: %d)", requestsLastDay, MAX_REQUESTS_PER_DAY);

        }

        return null;

    }

}