package com.apex.firefighter.service.nlp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import com.apex.firefighter.config.NLPConfig;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.ticket.TicketService;
import com.apex.firefighter.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

    // Map of entity type to list of patterns
    private final Map<EntityType, List<EntityPattern>> ENTITY_PATTERNS = new HashMap<>();

    // Common stop words to ignore during entity extraction
    private static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "from", "in", "is",
        "it", "of", "on", "or", "that", "the", "this", "to", "was", "were", "which", "with"
    );

    private boolean patternsInitialized = false;

    @PostConstruct
    public void init() {
        initializeEntityPatterns();
    }

    /**
     * Initialize entity patterns for all supported entity types
     */
    private void initializeEntityPatterns() {
        if (patternsInitialized) {
            return;
        }
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

        // EMERGENCY_TYPE: Matches exact Dolibarr emergency types from frontend
        ENTITY_PATTERNS.put(EntityType.EMERGENCY_TYPE, Arrays.asList(
            new EntityPattern(1.0,
                Arrays.asList("hr-emergency", "financial-emergency", "management-emergency", "logistics-emergency"),
                Arrays.asList("hr", "financial", "management", "logistics", "hr-emergency", "financial-emergency", "management-emergency", "logistics-emergency"),
                Arrays.asList(
                    Pattern.compile("\\b(hr|financial|management|logistics)-emergency\\b"),
                    Pattern.compile("\\b(hr|financial|management|logistics)\\s+emergency\\b"),
                    Pattern.compile("\\b(hr|human\\s+resources|financial|payment|management|admin|logistics|supply)\\s+emergency\\b")
                ))
        ));

        // USER_NAME: Matches names from database
        List<User> users = userService.getAuthorizedUsers(); // Fetch authorized users
        List<String> userNames = new ArrayList<>();
        for (User user : users) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                userNames.add(user.getUsername().toLowerCase());
            }
        }
                
        ENTITY_PATTERNS.put(EntityType.USER_NAME, Arrays.asList(
            new EntityPattern(0.8, Collections.emptyList(),
                //Arrays.asList("john", "jane", "admin"), // Populate with names from user database
                userNames,
                Arrays.asList(Pattern.compile("\\b[A-Z][a-z]+\\b")))
        ));

        // DESCRIPTION: Matches descriptive text after common trigger words
        ENTITY_PATTERNS.put(EntityType.DESCRIPTION, Arrays.asList(
            new EntityPattern(0.9, Arrays.asList("create ticket", "new ticket", "report"),
                Arrays.asList("issue", "problem", "emergency", "help", "assistance"),
                Arrays.asList(
                    // Pattern for emergency-type-specific tickets: "create hr-emergency ticket for [description]"
                    Pattern.compile("(?:create|new)\\s+(?:hr-emergency|financial-emergency|management-emergency|logistics-emergency)\\s+ticket\\s+for\\s+(.+?)(?:\\s*,\\s*(?:duration|contact|phone)|\\s+(?:duration|contact|phone))", Pattern.CASE_INSENSITIVE),
                    // Pattern for general tickets: "create ticket for [description]"
                    Pattern.compile("(?:create|new)\\s+(?:emergency\\s+)?ticket\\s+for\\s+(.+?)(?:\\s*,\\s*(?:duration|contact|phone)|\\s+(?:duration|contact|phone))", Pattern.CASE_INSENSITIVE),
                    // Pattern for simple format: "create ticket [description]"
                    Pattern.compile("(?:create|new)\\s+ticket\\s+(.+?)(?:\\s*,\\s*(?:duration|contact|phone)|\\s+(?:duration|contact|phone))", Pattern.CASE_INSENSITIVE),
                    // Pattern for issue/problem format
                    Pattern.compile("(?:issue|problem|emergency)\\s*:?\\s*([^,]+?)(?:\\s*,\\s*(?:duration|contact|phone)|$)", Pattern.CASE_INSENSITIVE),
                    // Pattern for help format
                    Pattern.compile("help\\s+(?:with|for)\\s+([^,]+?)(?:\\s*,\\s*(?:duration|contact|phone)|$)", Pattern.CASE_INSENSITIVE)
                ))
        ));

        // DURATION: Matches time duration expressions
        ENTITY_PATTERNS.put(EntityType.DURATION, Arrays.asList(
            new EntityPattern(1.0,
                Arrays.asList("15 minutes", "30 minutes", "45 minutes", "60 minutes", "90 minutes", "120 minutes"),
                Arrays.asList("minutes", "mins", "min", "hour", "hours"),
                Arrays.asList(
                    Pattern.compile("\\b(\\d+)\\s*(?:minutes?|mins?)\\b"),
                    Pattern.compile("\\b(\\d+)\\s*(?:hours?)\\b"),
                    Pattern.compile("\\bduration\\s+(\\d+)\\s*(?:minutes?|mins?|hours?)\\b"),
                    Pattern.compile("\\b(\\d+)\\s*(?:min|hr)\\b")
                ))
        ));

        // PHONE: Matches phone number patterns
        ENTITY_PATTERNS.put(EntityType.PHONE, Arrays.asList(
            new EntityPattern(1.0,
                Arrays.asList("contact", "phone", "number"),
                Arrays.asList("contact", "phone", "call"),
                Arrays.asList(
                    Pattern.compile("\\b(?:contact|phone)\\s+(\\d{10,15})\\b"),
                    Pattern.compile("\\b(\\d{10})\\b"), // 10-digit phone numbers
                    Pattern.compile("\\b(\\+?\\d{1,3}[-\\s]?\\d{3,4}[-\\s]?\\d{3,4}[-\\s]?\\d{3,4})\\b"), // International format
                    Pattern.compile("\\b(\\d{3,4}[-\\s]?\\d{6,7})\\b") // Local format
                ))
        ));

        patternsInitialized = true;
    }

    private String normalizeQuery(String query) {
        String normalized = query.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s-#]"," ") // Remove special chars except hyphens and #
                .replaceAll("\\s+", " ") // Normalize whitespace
                .trim();
        if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
            System.out.println("Debug: Normalized query: " + normalized);
        }
        return normalized;
    }

    //Normalization logic goes here. Add more later as needed.
    private String normalizeEntityValue(EntityType type, String value) {
        if (type == EntityType.DATE) {
            switch (value.toLowerCase()) {
                case "today":
                    return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                case "yesterday":
                    return LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
                case "tomorrow":
                    return LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
                default:
                    try {
                        // Try parsing MM/DD/YYYY or YYYY-MM-DD
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[MM/dd/yyyy][yyyy-MM-dd]");
                        return LocalDate.parse(value, formatter).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        return value; // Fallback to raw value
                    }
            }
        } else if (type == EntityType.TICKET_ID) {
            return value.replaceAll("[^\\d]", ""); // Extract digits (e.g., "#123" -> "123")
        }
        return value; // Default: return as-is
    }

    private void extractPatternEntities(String query, EntityPattern pattern, EntityType type, List<Entity> entities) {
        // Input validation
        if (query == null || pattern == null || type == null || entities == null) {
            return;
        }

        double threshold = nlpConfig != null ? nlpConfig.getEntityConfidenceThreshold() : 0.7;
        boolean matched = false;

        // Check exact phrases with error handling
        try {
            if (pattern.getRegexPatterns() != null) {
                for (Pattern regex : pattern.getRegexPatterns()) {
                    if (regex != null) {
                        try {
                            Matcher matcher = regex.matcher(query);
                            while (matcher.find()) {
                                try {
                                    // For DESCRIPTION patterns, use the captured group (group 1) instead of the full match
                                    String value;
                                    if (type == EntityType.DESCRIPTION && matcher.groupCount() > 0) {
                                        value = matcher.group(1); // Get the captured group (the description part)
                                        System.out.println("🔵 DESCRIPTION REGEX: Full match: '" + matcher.group() + "', Captured group: '" + value + "'");
                                    } else {
                                        value = matcher.group(); // Get the full match for other entity types
                                    }

                                    if (value != null && !value.trim().isEmpty()) {
                                        Entity entity = new Entity(type, value, matcher.start(), matcher.end());
                                        entity.setConfidence(pattern.getWeight() * 0.9); // Slightly lower confidence for regex matches
                                        if (entity.getConfidence() >= threshold) {
                                            entity.setNormalizedValue(normalizeEntityValue(type, value));
                                            entities.add(entity);
                                            matched = true;
                                        }
                                    }
                                } catch (Exception e) {
                                    // Log error but continue with next match
                                    if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
                                        System.out.println("DEBUG: Error processing regex match: " + e.getMessage());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Log error but continue with next regex
                            if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
                                System.out.println("DEBUG: Error with regex pattern: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log error but continue with exact phrases
            if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
                System.out.println("DEBUG: Error in regex pattern processing: " + e.getMessage());
            }
        }

        for (String phrase : pattern.getExactPhrases()) {
            if (query.contains(phrase.toLowerCase())) {
                int start = query.indexOf(phrase.toLowerCase());
                Entity entity = new Entity(type, phrase, start, start + phrase.length());
                entity.setConfidence(pattern.getWeight());
                if (entity.getConfidence() >= threshold) {
                    entity.setNormalizedValue(normalizeEntityValue(type, phrase));
                    entities.add(entity);
                    matched = true;
                }
            }
        }

        if (!matched && !pattern.getKeywords().isEmpty()) {
            String[] words = query.split("\\s+");
            for (String word : pattern.getKeywords()) {
                for (int i = 0; i < words.length; i++) {
                    if (!STOP_WORDS.contains(words[i]) && words[i].equals(word.toLowerCase())){
                        Entity entity = new Entity(type, word, query.indexOf(word), query.indexOf(word) + word.length());
                        entity.setConfidence(pattern.getWeight() * 0.6);
                        if (entity.getConfidence() >= threshold) {
                            entity.setNormalizedValue(normalizeEntityValue(type, word));
                            entities.add(entity);
                        }
                    }
                }
            }
        }
    }

    public ExtractedEntities extractEntities(String query) {
        // Ensure patterns are initialized
        if (!patternsInitialized) {
            initializeEntityPatterns();
        }

        if (query == null || query.trim().isEmpty()) {
           if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
               System.out.println("Debug: Empty or null query provided for entity extraction.");
            }
            return new ExtractedEntities();
        }

        String normalizedQuery = normalizeQuery(query);
        ExtractedEntities entities = new ExtractedEntities();

        entities.setTicketIds(new ArrayList<>());
        entities.setStatuses(new ArrayList<>());
        entities.setDates(new ArrayList<>());
        entities.setUserNames(new ArrayList<>());
        entities.setEmergencyTypes(new ArrayList<>());
        entities.setNumbers(new ArrayList<>());
        entities.setTimeExpressions(new ArrayList<>());
        entities.setDurations(new ArrayList<>());
        entities.setPhones(new ArrayList<>());
        entities.setDescriptions(new ArrayList<>());
        entities.setPriorities(new ArrayList<>());
        entities.setLocations(new ArrayList<>());
        entities.setEmails(new ArrayList<>());
        entities.setAllEntities(new HashMap<>());

        for (EntityType type : ENTITY_PATTERNS.keySet()) {
            List<EntityPattern> patterns = ENTITY_PATTERNS.get(type);
            List<Entity> typeEntities = new ArrayList<>();
            for (EntityPattern pattern : patterns) {
                extractPatternEntities(normalizedQuery, pattern, type, typeEntities);
            }

            entities.getAllEntities().put(type, typeEntities);
            // Set specific lists for easy access
            switch (type) {
                case TICKET_ID -> entities.setTicketIds(typeEntities);
                case STATUS -> entities.setStatuses(typeEntities);
                case DATE -> entities.setDates(typeEntities);
                case USER_NAME -> entities.setUserNames(typeEntities);
                case EMERGENCY_TYPE -> entities.setEmergencyTypes(typeEntities);
                case NUMBER -> entities.setNumbers(typeEntities);
                case TIME -> entities.setTimeExpressions(typeEntities);
                case DURATION -> entities.setDurations(typeEntities);
                case PHONE -> entities.setPhones(typeEntities);
                case DESCRIPTION -> entities.setDescriptions(typeEntities);
                case PRIORITY -> entities.setPriorities(typeEntities);
                case LOCATION -> entities.setLocations(typeEntities);
                case EMAIL -> entities.setEmails(typeEntities);
                default -> {}
            }
        }



        if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
            System.out.println("Debug: Extracted entities: " +
                entities
                .getAllEntities()
                .entrySet()
                .stream()
                .map(
                    e -> e.getKey() + ": " +
                    e.getValue().stream()
                    .map(
                            Entity::getValue
                    )
                    .collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining("; "))
            );
        }

        return entities;
    }

    /**
     * Extract specific entity types from a query
     * 
     * @param query The user's natural language input
     * @param entityTypes The specific entity types to extract
     * @return Map of entity type to list of found entities
     */
    public Map<EntityType, List<Entity>> extractSpecificEntities(String query, List<EntityType> entityTypes) {
        if (query == null || query.trim().isEmpty() || entityTypes == null || entityTypes.isEmpty()) {
            if (nlpConfig.isDebugEnabled()) {
                System.out.println("DEBUG: Invalid input for extractSpecificEntities");
            }
            return new HashMap<>();
        }

        ExtractedEntities allEntities = extractEntities(query);
        Map<EntityType, List<Entity>> result = new HashMap<>();
        for (EntityType type : entityTypes) {
            List<Entity> entities = allEntities.getAllEntities().getOrDefault(type, Collections.emptyList());
            result.put(type, entities);
        }



        if (nlpConfig.isDebugEnabled()) {
            System.out.println("DEBUG: Specific entities extracted: " +
                result.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue().stream()
                        .map(Entity::getValue).collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("; ")));
        }

        return result;
    }

    /**
     * Validate extracted entities against system data
     * 
     * @param entities The extracted entities to validate
     * @return ValidationResult containing validation status and errors
     */
    public ValidationResult validateEntities(ExtractedEntities entities) {
        ValidationResult result = new ValidationResult(true);
        result.setErrors(new ArrayList<>());
        result.setWarnings(new ArrayList<>());
        result.setEntityValidation(new HashMap<>());

        // Validate TICKET_ID
        for (Entity ticket : entities.getTicketIds()) {
            String ticketId = ticket.getNormalizedValue();
            Optional<Ticket> ticketOpt = ticketService.getTicketByTicketId(ticketId);
            boolean valid = ticketOpt.isPresent();
            result.getEntityValidation().put(EntityType.TICKET_ID, valid);
            if (!valid) {
                result.setValid(false);
                result.getErrors().add("Invalid ticket ID: " + ticketId);
            }
        }

        // Validate STATUS
        List<String> validStatuses = Arrays.asList("open", "closed", "in progress"); // Fetch from TicketService if dynamic
        for (Entity status : entities.getStatuses()) {
            boolean valid = validStatuses.contains(status.getValue().toLowerCase());
            result.getEntityValidation().put(EntityType.STATUS, valid);
            if (!valid) {
                result.setValid(false);
                result.getErrors().add("Invalid status: " + status.getValue());
            }
        }

        // Validate DATE
        for (Entity date : entities.getDates()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDate.parse(date.getNormalizedValue(), formatter);
                result.getEntityValidation().put(EntityType.DATE, true);
            } catch (DateTimeParseException e) {
                result.setValid(false);
                result.getErrors().add("Invalid date format: " + date.getValue());
            }
        }

        // Validate EMERGENCY_TYPE - must match exact frontend values
        List<String> validEmergencyTypes = Arrays.asList("hr-emergency", "financial-emergency", "management-emergency", "logistics-emergency");
        for (Entity emergency : entities.getEmergencyTypes()) {
            String type = emergency.getNormalizedValue();
            if (type == null) {
                type = emergency.getValue();
            }
            type = type.toLowerCase().trim();

            // Normalize common variations to the correct format
            if (type.equals("hr") || type.equals("hr emergency")) {
                type = "hr-emergency";
            } else if (type.equals("financial") || type.equals("financial emergency")) {
                type = "financial-emergency";
            } else if (type.equals("management") || type.equals("management emergency")) {
                type = "management-emergency";
            } else if (type.equals("logistics") || type.equals("logistics emergency")) {
                type = "logistics-emergency";
            }

            boolean valid = validEmergencyTypes.contains(type);
            result.getEntityValidation().put(EntityType.EMERGENCY_TYPE, valid);
            if (!valid) {
                result.setValid(false);
                result.getErrors().add("Invalid emergency type: " + emergency.getValue() + ". Must be one of: hr-emergency, financial-emergency, management-emergency, logistics-emergency");
            }
        }

        // Validate DURATION
        for (Entity duration : entities.getDurations()) {
            String durationStr = duration.getNormalizedValue();
            if (durationStr == null) {
                durationStr = duration.getValue();
            }

            try {
                int durationValue = Integer.parseInt(durationStr.replaceAll("[^0-9]", ""));
                boolean valid = durationValue >= 15 && durationValue <= 120;
                result.getEntityValidation().put(EntityType.DURATION, valid);
                if (!valid) {
                    result.setValid(false);
                    result.getErrors().add("Duration must be between 15 and 120 minutes. Found: " + durationValue + " minutes");
                }
            } catch (NumberFormatException e) {
                result.setValid(false);
                result.getErrors().add("Invalid duration format: " + duration.getValue());
            }
        }

        // Validate PHONE
        for (Entity phone : entities.getPhones()) {
            String phoneStr = phone.getNormalizedValue();
            if (phoneStr == null) {
                phoneStr = phone.getValue();
            }

            // Basic phone validation - must be 10+ digits
            String digitsOnly = phoneStr.replaceAll("[^0-9]", "");
            boolean valid = digitsOnly.length() >= 10 && digitsOnly.length() <= 15;
            result.getEntityValidation().put(EntityType.PHONE, valid);
            if (!valid) {
                result.setValid(false);
                result.getErrors().add("Invalid phone number format: " + phone.getValue());
            }
        }

        // temporary USER_NAME validation - replace with real user lookup
        for (Entity user : entities.getUserNames()) {
            Optional<Ticket> ticketOpt = ticketService.getTicketByTicketId(user.getValue());
            boolean valid = ticketOpt.isPresent();
            result.getEntityValidation().put(EntityType.USER_NAME, valid);
            if (!valid) {
                result.getWarnings().add("Unknown user name: " + user.getValue());
            }
        }



        if (nlpConfig.isDebugEnabled()) {
            System.out.println("DEBUG: Validation result: valid=" + result.isValid() +
                ", errors=" + result.getErrors() + ", warnings=" + result.getWarnings());
        }

        return result;
    }

    /**
     * Get supported entity types for the NLP system
     * 
     * @return List of supported entity types
     */
    public List<EntityType> getSupportedEntityTypes() {
        List<EntityType> types = Arrays.asList(EntityType.values());
        if (nlpConfig.isDebugEnabled()) {
            System.out.println("DEBUG: Supported entity types: " +
                types.stream().map(EntityType::getCode).collect(Collectors.joining(", ")));
        }
        return types;
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
        private List<Entity> durations;
        private List<Entity> phones;
        private List<Entity> descriptions;
        private List<Entity> priorities;
        private List<Entity> locations;
        private List<Entity> emails;
        private Map<EntityType, List<Entity>> allEntities;

        public ExtractedEntities() {
            this.allEntities = new HashMap<>();
        }

        // Getters and setters
        public List<Entity> getTicketIds() { return ticketIds; }
        public void setTicketIds(List<Entity> ticketIds) {
            this.ticketIds = ticketIds;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.TICKET_ID, ticketIds);
        }

        public List<Entity> getStatuses() { return statuses; }
        public void setStatuses(List<Entity> statuses) {
            this.statuses = statuses;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.STATUS, statuses);
        }
        
        public List<Entity> getDates() { return dates; }
        public void setDates(List<Entity> dates) {
            this.dates = dates;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.DATE, dates);
        }

        public List<Entity> getUserNames() { return userNames; }
        public void setUserNames(List<Entity> userNames) {
            this.userNames = userNames;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.USER_NAME, userNames);
        }

        public List<Entity> getEmergencyTypes() { return emergencyTypes; }
        public void setEmergencyTypes(List<Entity> emergencyTypes) {
            this.emergencyTypes = emergencyTypes;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.EMERGENCY_TYPE, emergencyTypes);
        }

        public List<Entity> getNumbers() { return numbers; }
        public void setNumbers(List<Entity> numbers) {
            this.numbers = numbers;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.NUMBER, numbers);
        }

        public List<Entity> getTimeExpressions() { return timeExpressions; }
        public void setTimeExpressions(List<Entity> timeExpressions) {
            this.timeExpressions = timeExpressions;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.TIME, timeExpressions);
        }

        public List<Entity> getDurations() { return durations; }
        public void setDurations(List<Entity> durations) {
            this.durations = durations;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.DURATION, durations);
        }

        public List<Entity> getPhones() { return phones; }
        public void setPhones(List<Entity> phones) {
            this.phones = phones;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.PHONE, phones);
        }

        public List<Entity> getDescriptions() { return descriptions; }
        public void setDescriptions(List<Entity> descriptions) {
            this.descriptions = descriptions;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.DESCRIPTION, descriptions);
        }

        public List<Entity> getPriorities() { return priorities; }
        public void setPriorities(List<Entity> priorities) {
            this.priorities = priorities;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.PRIORITY, priorities);
        }

        public List<Entity> getLocations() { return locations; }
        public void setLocations(List<Entity> locations) {
            this.locations = locations;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.LOCATION, locations);
        }

        public List<Entity> getEmails() { return emails; }
        public void setEmails(List<Entity> emails) {
            this.emails = emails;
            if (this.allEntities == null) this.allEntities = new HashMap<>();
            this.allEntities.put(EntityType.EMAIL, emails);
        }

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
        PHONE("phone", "Phone number"),
        DESCRIPTION("description", "Ticket description text");

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

    /**
     * Pattern class for entity extraction (similar to IntentPattern)
     */
    public static class EntityPattern {
        private final double weight;
        private final List<String> exactPhrases;
        private final List<String> keywords;
        private final List<Pattern> regexPatterns;

        public EntityPattern(double weight, List<String> exactPhrases, List<String> keywords, List<Pattern> regexPatterns) {
            this.weight = weight;
            this.exactPhrases = exactPhrases != null ? exactPhrases : new ArrayList<>();
            this.keywords = keywords != null ? keywords : new ArrayList<>();
            this.regexPatterns = regexPatterns != null ? regexPatterns : new ArrayList<>();
        }

        public double getWeight() { return weight; }
        public List<String> getExactPhrases() { return exactPhrases; }
        public List<String> getKeywords() { return keywords; }
        public List<Pattern> getRegexPatterns() { return regexPatterns; }
    }
}
