package com.apex.firefighter.repository;

import com.apex.firefighter.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {
    
    // Find user preferences by Firebase UID (primary key)
    Optional<UserPreferences> findByUserId(String userId);
    
    // Check if user preferences exist by Firebase UID
    boolean existsByUserId(String userId);
    
    // Find users with email notifications enabled
    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotificationsEnabled = true")
    java.util.List<UserPreferences> findUsersWithEmailNotificationsEnabled();
    
    // Find users with specific email notification type enabled
    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotificationsEnabled = true AND up.emailTicketCreation = true")
    java.util.List<UserPreferences> findUsersWithTicketCreationEmailEnabled();
    
    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotificationsEnabled = true AND up.emailTicketCompletion = true")
    java.util.List<UserPreferences> findUsersWithTicketCompletionEmailEnabled();
    
    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotificationsEnabled = true AND up.emailTicketRevocation = true")
    java.util.List<UserPreferences> findUsersWithTicketRevocationEmailEnabled();
    
    @Query("SELECT up FROM UserPreferences up WHERE up.emailNotificationsEnabled = true AND up.emailFiveMinuteWarning = true")
    java.util.List<UserPreferences> findUsersWithFiveMinuteWarningEmailEnabled();
    
    // Check if user has specific email notification enabled
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPreferences up WHERE up.userId = :userId AND up.emailNotificationsEnabled = true AND up.emailTicketCreation = true")
    boolean hasTicketCreationEmailEnabled(@Param("userId") String userId);
    
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPreferences up WHERE up.userId = :userId AND up.emailNotificationsEnabled = true AND up.emailTicketCompletion = true")
    boolean hasTicketCompletionEmailEnabled(@Param("userId") String userId);
    
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPreferences up WHERE up.userId = :userId AND up.emailNotificationsEnabled = true AND up.emailTicketRevocation = true")
    boolean hasTicketRevocationEmailEnabled(@Param("userId") String userId);
    
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPreferences up WHERE up.userId = :userId AND up.emailNotificationsEnabled = true AND up.emailFiveMinuteWarning = true")
    boolean hasFiveMinuteWarningEmailEnabled(@Param("userId") String userId);
}
