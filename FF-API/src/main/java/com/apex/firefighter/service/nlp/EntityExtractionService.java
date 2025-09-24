package com.apex.firefighter.service.nlp;

import org.springframework.stereotype.Service;

import com.apex.firefighter.config.NLPConfig;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service responsible for extracting entities from natural language queries.
 * Identifies ticket IDs, statuses, dates, user names, and other relevant data.
 */
@Service
public class EntityExtractionService {

    /**
     * Extract all entities from a natural language query
     * 
     * @param query The user's natural language input
     * @return ExtractedEntities object containing all found entities
     */

    @Autowired 
    private NLPConfig nlpConfig;

    // For Validation
    @Autowired
    private TicketService ticketService;

    // Common stop words to ignore during entity extraction
    private static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "from", "in", "is",
        "it", "of", "on", "or", "that", "the", "this", "to", "was", "were", "which", "with"
    );

    static {
        initializeEntityPatterns();
    }

    /**
     * Initialize entity patterns for all supported entity types
     */
    private static void initializeEntityPatterns() {
        // TICKET_ID: Matches #123, ticket123
        ENTITY_PATTERNS.put(EntityType.TICKET_ID, Arrays.asList(
            new EntityPattern(1.0, Arrays.asList("#\\d+", "ticket\\d+"),
                Arrays.asList("ticket"),
                Arrays.asList(Pattern.compile("\\b#\\d+\\b"), Pattern.compile("\\bticket\\d+\\b")))
        ));

        // STATUS: Matches open, closed, in progress
        ENTITY_PATTERNS.put(EntityType.STATUS, Arrays.asList(
            new EntityPattern(1.0, Arrays.asList("open", "closed", "in progress"),
                Arrays.asList("open", "closed", "progress"),
                Arrays.asList(Pattern.compile("\\b(open|closed|in\\s+progress)\\b")))
        ));

        // DATE: Matches MM/DD/YYYY, YYYY-MM-DD, or relative terms like "today"
        ENTITY_PATTERNS.put(EntityType.DATE, Arrays.asList(
            new EntityPattern(1.0, Arrays.asList("today", "yesterday", "tomorrow"),
                Arrays.asList("today", "yesterday", "tomorrow"),
                Arrays.asList(Pattern.compile("\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b"),
                             Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b"),
                             Pattern.compile("\\b(today|yesterday|tomorrow)\\b")))
        ));

        // EMERGENCY_TYPE: Matches Dolibarr-specific types
        ENTITY_PATTERNS.put(EntityType.EMERGENCY_TYPE, Arrays.asList(
            new EntityPattern(1.0, Arrays.asList("hr emergency", "financial emergency", "management emergency"),
                Arrays.asList("hr", "financial", "management", "logistics"),
                Arrays.asList(Pattern.compile("\\b(hr|financial|management|logistics)\\s+emergency\\b")))
        ));

        // USER_NAME: Matches simple names (simplified for now)
        ENTITY_PATTERNS.put(EntityType.USER_NAME, Arrays.asList(
            new EntityPattern(0.8, Collections.emptyList(),
                Arrays.asList("john", "jane", "admin"), // Populate with names from user database
                Arrays.asList(Pattern.compile("\\b[A-Z][a-z]+\\b")))
        ));

        // Add more patterns later (example NUMBER, TIME, etc.) as needed
    }



    public ExtractedEntities extractEntities(String query) {
        // TODO: Implement entity extraction logic
        return null;
    }

    /**
     * Extract specific entity types from a query
     * 
     * @param query The user's natural language input
     * @param entityTypes The specific entity types to extract
     * @return Map of entity type to list of found entities
     */
    public Map<EntityType, List<Entity>> extractSpecificEntities(String query, List<EntityType> entityTypes) {
        // TODO: Implement specific entity extraction logic
        return null;
    }

    /**
     * Validate extracted entities against system data
     * 
     * @param entities The extracted entities to validate
     * @return ValidationResult containing validation status and errors
     */
    public ValidationResult validateEntities(ExtractedEntities entities) {
        // TODO: Implement entity validation logic
        return null;
    }

    /**
     * Get supported entity types for the NLP system
     * 
     * @return List of supported entity types
     */
    public List<EntityType> getSupportedEntityTypes() {
        // TODO: Implement supported entity types logic
        return null;
    }

    /**
     * Container for all extracted entities from a query
     */
    public static class ExtractedEntities {
        private List<Entity> ticketIds;
        private List<Entity> statuses;
        private List<Entity> dates;
        private List<Entity> userNames;
        private List<Entity> emergencyTypes;
        private List<Entity> numbers;
        private List<Entity> timeExpressions;
        private Map<EntityType, List<Entity>> allEntities;

        public ExtractedEntities() {}

        // Getters and setters
        public List<Entity> getTicketIds() { return ticketIds; }
        public void setTicketIds(List<Entity> ticketIds) { this.ticketIds = ticketIds; }
        
        public List<Entity> getStatuses() { return statuses; }
        public void setStatuses(List<Entity> statuses) { this.statuses = statuses; }
        
        public List<Entity> getDates() { return dates; }
        public void setDates(List<Entity> dates) { this.dates = dates; }
        
        public List<Entity> getUserNames() { return userNames; }
        public void setUserNames(List<Entity> userNames) { this.userNames = userNames; }
        
        public List<Entity> getEmergencyTypes() { return emergencyTypes; }
        public void setEmergencyTypes(List<Entity> emergencyTypes) { this.emergencyTypes = emergencyTypes; }
        
        public List<Entity> getNumbers() { return numbers; }
        public void setNumbers(List<Entity> numbers) { this.numbers = numbers; }
        
        public List<Entity> getTimeExpressions() { return timeExpressions; }
        public void setTimeExpressions(List<Entity> timeExpressions) { this.timeExpressions = timeExpressions; }
        
        public Map<EntityType, List<Entity>> getAllEntities() { return allEntities; }
        public void setAllEntities(Map<EntityType, List<Entity>> allEntities) { this.allEntities = allEntities; }
    }

    /**
     * Represents a single extracted entity
     */
    public static class Entity {
        private EntityType type;
        private String value;
        private String normalizedValue;
        private int startPosition;
        private int endPosition;
        private double confidence;
        private Map<String, Object> metadata;

        public Entity() {}

        public Entity(EntityType type, String value, int startPosition, int endPosition) {
            this.type = type;
            this.value = value;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        // Getters and setters
        public EntityType getType() { return type; }
        public void setType(EntityType type) { this.type = type; }
        
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        
        public String getNormalizedValue() { return normalizedValue; }
        public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }
        
        public int getStartPosition() { return startPosition; }
        public void setStartPosition(int startPosition) { this.startPosition = startPosition; }
        
        public int getEndPosition() { return endPosition; }
        public void setEndPosition(int endPosition) { this.endPosition = endPosition; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * Enumeration of entity types that can be extracted
     */
    public enum EntityType {
        TICKET_ID("ticket_id", "Ticket identifier"),
        STATUS("status", "Ticket status"),
        DATE("date", "Date or date range"),
        TIME("time", "Time expression"),
        USER_NAME("user_name", "User name or identifier"),
        EMERGENCY_TYPE("emergency_type", "Type of emergency"),
        NUMBER("number", "Numeric value"),
        DURATION("duration", "Time duration"),
        PRIORITY("priority", "Priority level"),
        LOCATION("location", "Geographic location"),
        EMAIL("email", "Email address"),
        PHONE("phone", "Phone number");

        private final String code;
        private final String description;

        EntityType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Result of entity validation
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;
        private Map<EntityType, Boolean> entityValidation;

        public ValidationResult() {}

        public ValidationResult(boolean valid) {
            this.valid = valid;
        }

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
        
        public Map<EntityType, Boolean> getEntityValidation() { return entityValidation; }
        public void setEntityValidation(Map<EntityType, Boolean> entityValidation) { this.entityValidation = entityValidation; }
    }
}
