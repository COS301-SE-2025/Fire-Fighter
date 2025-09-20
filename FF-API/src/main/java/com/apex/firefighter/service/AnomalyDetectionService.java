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

    public boolean checkForAnomalousTicketCreation(String userId){

        //currently only checking for frequent requests
        //add other anomaly checks here as needed

        return isFrequentRequestAnomaly(userId);

    }

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

}