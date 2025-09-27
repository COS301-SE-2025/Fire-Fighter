package com.apex.firefighter.service.anomaly;

import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.AccessLogRepository;
import com.apex.firefighter.repository.AccessSessionRepository;
import com.apex.firefighter.model.AccessLog;
import com.apex.firefighter.model.AccessSession;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyDetectionService {

    private final TicketRepository ticketRepository;
    private final AccessLogRepository accessLogRepository;
    private final AccessSessionRepository accessSessionRepository;

    //configuration for frequent request detection
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_REQUESTS_PER_DAY = 20;
    
    //configuration for dormant user detection
    private static final int DORMANT_THRESHOLD_DAYS = 5;
    private static final int QUICK_ACTION_THRESHOLD_MINUTES = 15;

    //configuration for off-hours request detection
    private static final int EARLIEST_WORKING_HOURS = 7;
    private static final int LATEST_WORKING_HOURS = 17;

    public AnomalyDetectionService(TicketRepository ticketRepository, 
                                 AccessLogRepository accessLogRepository,
                                 AccessSessionRepository accessSessionRepository){

        this.ticketRepository = ticketRepository;
        this.accessLogRepository = accessLogRepository;
        this.accessSessionRepository = accessSessionRepository;

    }


    //CHANGE DOCUMENTATION WHEN MAKE CHANGES (SINCE IT WILL CHANGE WHAT IS BEGIN RETURNED)
    /**
     * Checks if a user's ticket creation shows anomalous behavior
     * 
     * @param userId The user ID to check
     * @return true if anomalous behavior is detected, false otherwise
     */
    public boolean checkForAnomalousTicketCreation(String userId){

        //check for frequent requests
        boolean frequentRequestAnomaly = isFrequentRequestAnomaly(userId);
        
        //check for dormant user login anomaly
        boolean dormantUserAnomaly = isDormantUserLoginAnomaly(userId);

        return frequentRequestAnomaly || dormantUserAnomaly;

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
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

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

    /**
     * Checks if a user shows dormant user login anomaly behavior
     * Detects when a user has been inactive for 30+ days and suddenly logs in and makes actions within 15 minutes
     * 
     * @param userId The user ID to check
     * @return true if dormant user anomaly is detected, false otherwise
     */
    private boolean isDormantUserLoginAnomaly(String userId) {
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime quickActionThreshold = now.minusMinutes(QUICK_ACTION_THRESHOLD_MINUTES);
        LocalDateTime dormantThreshold = now.minusDays(DORMANT_THRESHOLD_DAYS);
        
        // 1. Find most recent login within the quick action threshold
        List<AccessLog> recentLogins = accessLogRepository.findLoginEventsByUserSince(userId, quickActionThreshold);
        
        if (recentLogins.isEmpty()) {
            return false; // No recent login, so no anomaly
        }
        
        AccessLog mostRecentLogin = recentLogins.get(0);
        
        // 2. Check for previous activity before the dormant threshold period
        List<AccessSession> previousSessions = accessSessionRepository.findByUserIdBeforeDate(userId, dormantThreshold);
        List<AccessLog> previousLogins = accessLogRepository.findLoginEventsByUserSince(userId, dormantThreshold);
        
        // Remove any logins that are after the dormant threshold (we only want truly old activity)
        previousLogins.removeIf(log -> log.getTimestamp().isAfter(dormantThreshold));
        
        // 3. Determine if user was truly dormant (no sessions/logins for 30+ days)
        boolean wasDormant = previousSessions.isEmpty() && previousLogins.isEmpty();
        
        if (!wasDormant) {
            return false; // User was not dormant, so no anomaly
        }
        
        // 4. Check if user made any actions/tickets after the recent login
        long actionsAfterLogin = accessLogRepository.countActionsByUserSince(userId, mostRecentLogin.getTimestamp());
        
        // 5. Flag as anomaly if dormant user made quick actions after login
        if (actionsAfterLogin > 0) {
            System.out.println("🚨 DORMANT USER ANOMALY DETECTED: User " + userId + 
                " was dormant for " + DORMANT_THRESHOLD_DAYS + "+ days, " +
                "logged in at " + mostRecentLogin.getTimestamp() + 
                " and made " + actionsAfterLogin + " actions within " + 
                QUICK_ACTION_THRESHOLD_MINUTES + " minutes");
            
            return true;
        }
        
        return false;
    }

    /**
     * Gets details about the detected dormant user anomaly for logging/notification purposes
     * 
     * @param userId The user ID to check
     * @return A string describing the dormant user anomaly, or null if no anomaly is detected
     */
    public String getDormantUserAnomalyDetails(String userId) {
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime quickActionThreshold = now.minusMinutes(QUICK_ACTION_THRESHOLD_MINUTES);
        LocalDateTime dormantThreshold = now.minusDays(DORMANT_THRESHOLD_DAYS);
        
        // Find most recent login within the quick action threshold
        List<AccessLog> recentLogins = accessLogRepository.findLoginEventsByUserSince(userId, quickActionThreshold);
        
        if (recentLogins.isEmpty()) {
            return null; // No recent login
        }
        
        AccessLog mostRecentLogin = recentLogins.get(0);
        
        // Check for previous activity before the dormant threshold period
        List<AccessSession> previousSessions = accessSessionRepository.findByUserIdBeforeDate(userId, dormantThreshold);
        List<AccessLog> previousLogins = new ArrayList<>(accessLogRepository.findLoginEventsByUserSince(userId, dormantThreshold));
        previousLogins.removeIf(log -> log.getTimestamp().isAfter(dormantThreshold));
        
        boolean wasDormant = previousSessions.isEmpty() && previousLogins.isEmpty();
        
        if (!wasDormant) {
            return null; // User was not dormant
        }
        
        // Count actions after the recent login
        long actionsAfterLogin = accessLogRepository.countActionsByUserSince(userId, mostRecentLogin.getTimestamp());
        
        if (actionsAfterLogin > 0) {
            return String.format("User was dormant for %d+ days, logged in at %s and made %d actions within %d minutes", 
                DORMANT_THRESHOLD_DAYS, mostRecentLogin.getTimestamp(), actionsAfterLogin, QUICK_ACTION_THRESHOLD_MINUTES);
        }
        
        return null;
    }


    /**
     * Checks if a user makes a request outside of regular working hours.
     * Working hours are defined as 7 AM to 5 PM (17:00) on weekdays only.
     * Requests made outside these hours or on weekends are considered anomalous.
     * 
     * @param userID The user ID to check
     * @return true if a user made a request outside of working hours, false otherwise
     */
    public boolean isOffHoursAnomaly(String userID){

        LocalDateTime now = LocalDateTime.now();
        int hourOfDay = now.getHour();

        //hourOfDay (0-23) 
        //valid hours (7am to 5pm - 7th hour to 17th hour)
        if(hourOfDay < EARLIEST_WORKING_HOURS || hourOfDay > LATEST_WORKING_HOURS){

            System.out.println("🚨 ANOMALY DETECTED: Request made outside of regular working hours!"
            + " User "+ userID 
            + " made a request at " + now.toString());

            return true;

        }

        DayOfWeek day = now.getDayOfWeek();

        //request made on a weekend (which is also an off-time anomaly)
        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){

            System.out.println("🚨 ANOMALY DETECTED: Request made outside of regular working hours!"
            + " User "+ userID 
            + " made a request at " + now.toString()
            + ", which is a " + day.toString());

            return true;

        }

        return false;

    }

    /**
     * Gets details about detected off-hours anomaly for logging/notification purposes.
     * Returns specific information about why the request was flagged as off-hours:
     * - Time-based: Request made outside 7 AM - 5 PM window
     * - Day-based: Request made on weekend (Saturday/Sunday)
     * 
     * @param userID The user ID to get anomaly details for
     * @return A String describing the off-hours anomaly, or null if no anomaly is detected 
     */
    public String getOffHoursAnomalyDetails(String userID){

        LocalDateTime now = LocalDateTime.now();
        int hourOfDay = now.getHour();

        //hourOfDay (0-23) 
        //valid hours (7am to 5pm - 7th hour to 17th hour)
        if(hourOfDay < EARLIEST_WORKING_HOURS || hourOfDay > LATEST_WORKING_HOURS){

            return String.format("User made a request at %d:00 which is outside of regular work hours! (allowed: %d:00 AM - %d:00 PM)", 
                hourOfDay, EARLIEST_WORKING_HOURS, LATEST_WORKING_HOURS);

        }

        DayOfWeek day = now.getDayOfWeek();

        //request made on a weekend (which is also an off-time anomaly)
        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){

            return String.format("User made a request on a %s which is outside of regular work hours! (allowed: weekdays only)", 
                day.toString());

        }

        return null;

    }

    /**
     * Gets details about any detected anomaly for logging/notification purposes
     * This method combines both frequent request and dormant user anomaly details
     * 
     * @param userId The user ID to check
     * @return A string describing the anomaly, or null if no anomaly is detected
     */
    public String getFrequencyAnomalyDetails(String userId) {
        
        // Check for frequent request anomaly details
        String frequencyDetails = getRequestFrequencyDetails(userId);
        if (frequencyDetails != null) {
            return "Frequent Request Anomaly: " + frequencyDetails;
        }
        
        // Check for dormant user anomaly details
        String dormantDetails = getDormantUserAnomalyDetails(userId);
        if (dormantDetails != null) {
            return "Dormant User Anomaly: " + dormantDetails;
        }
        
        return null;
    }

}