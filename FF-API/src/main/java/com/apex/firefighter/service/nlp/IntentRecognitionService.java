package com.apex.firefighter.service.nlp;

import com.apex.firefighter.config.NLPConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service responsible for recognizing user intents from natural language queries.
 * Determines what action the user wants to perform (show tickets, update status, etc.)
 */
@Service
public class IntentRecognitionService {

    @Autowired
    NLPConfig nlpConfig; // Package-private for testing

    // Intent patterns - each intent has multiple patterns with different weights
    private static final Map<IntentType, List<IntentPattern>> INTENT_PATTERNS = new HashMap<>();

    // Common words that should be ignored during intent recognition
    private static final Set<String> STOP_WORDS = Set.of(
        "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
        "please", "can", "could", "would", "should", "will", "i", "me", "my", "mine"
    );

    static {
        initializeIntentPatterns();
    }

    /**
     * Initialize intent patterns for all supported intent types
     */
    private static void initializeIntentPatterns() {
        // Query intents - Order matters! More specific patterns first
        INTENT_PATTERNS.put(IntentType.SHOW_ACTIVE_TICKETS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("show active tickets", "active tickets", "current tickets"),
                Arrays.asList("active", "current", "running", "open"),
                Arrays.asList(Pattern.compile("\\bactive\\s+tickets\\b"), Pattern.compile("\\bcurrent\\s+tickets\\b"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.SHOW_COMPLETED_TICKETS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("show completed tickets", "completed tickets", "finished tickets"),
                Arrays.asList("completed", "finished", "done", "closed"),
                Arrays.asList(Pattern.compile("\\bcompleted\\s+tickets\\b"), Pattern.compile("\\bfinished\\s+tickets\\b"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.SHOW_ALL_TICKETS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("show all tickets", "all tickets", "system tickets"),
                Arrays.asList("all", "system", "everyone"),
                Arrays.asList(Pattern.compile("\\ball\\s+tickets\\b"), Pattern.compile("\\bsystem\\s+tickets\\b"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.SHOW_TICKETS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("show my tickets", "my tickets", "list my tickets"),
                Arrays.asList("my"),
                Arrays.asList(Pattern.compile("\\bmy\\s+tickets\\b"), Pattern.compile("\\blist\\s+my\\s+tickets\\b"))
            ),
            new IntentPattern(0.8,
                Arrays.asList("what tickets do i have", "tickets for me"),
                Arrays.asList("what", "have", "me"),
                Arrays.asList(Pattern.compile("\\btickets\\s+for\\s+me\\b"), Pattern.compile("\\bwhat\\s+tickets\\s+do\\s+i\\s+have\\b"))
            ),
            new IntentPattern(0.5,
                Arrays.asList("tickets"),
                Arrays.asList(),
                Arrays.asList(Pattern.compile("^tickets$"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.SEARCH_TICKETS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("search for tickets", "find tickets", "search tickets"),
                Arrays.asList("search", "find", "lookup"),
                Arrays.asList(Pattern.compile("search.*tickets"), Pattern.compile("find.*tickets"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.GET_TICKET_DETAILS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("show ticket details", "ticket details", "details of ticket"),
                Arrays.asList("details", "info", "information"),
                Arrays.asList(Pattern.compile("\\bdetails.*ticket\\b"), Pattern.compile("\\binfo.*ticket\\b"), Pattern.compile("\\bticket.*details\\b"))
            )
        ));

        // Management intents
        INTENT_PATTERNS.put(IntentType.UPDATE_TICKET_STATUS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("update ticket status", "change status", "set status"),
                Arrays.asList("update", "change", "set", "status"),
                Arrays.asList(Pattern.compile("update.*status"), Pattern.compile("change.*status"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.CREATE_TICKET, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("create ticket", "new ticket", "create emergency ticket", "new emergency request"),
                Arrays.asList("create", "new", "emergency", "request"),
                Arrays.asList(Pattern.compile("\\bcreate.*ticket\\b"), Pattern.compile("\\bnew.*emergency\\b"))
            ),
            // Dolibarr emergency type patterns
            new IntentPattern(0.9,
                Arrays.asList("hr emergency", "financial emergency", "management emergency", "logistics emergency"),
                Arrays.asList("hr", "financial", "management", "logistics"),
                Arrays.asList(Pattern.compile("\\bhr.*emergency\\b"), Pattern.compile("\\bfinancial.*emergency\\b"),
                             Pattern.compile("\\bmanagement.*emergency\\b"), Pattern.compile("\\blogistics.*emergency\\b"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.CLOSE_TICKET, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("close ticket", "end ticket", "finish ticket"),
                Arrays.asList("close", "end", "finish", "complete"),
                Arrays.asList(Pattern.compile("close.*ticket"), Pattern.compile("end.*ticket"))
            )
        ));

        // Admin intents
        INTENT_PATTERNS.put(IntentType.GET_SYSTEM_STATS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("system statistics", "stats", "system stats"),
                Arrays.asList("statistics", "stats", "metrics", "analytics"),
                Arrays.asList(Pattern.compile("system.*stats"), Pattern.compile("statistics"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.EXPORT_TICKETS, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("export tickets", "download tickets", "export data"),
                Arrays.asList("export", "download", "csv", "excel"),
                Arrays.asList(Pattern.compile("export.*tickets"), Pattern.compile("download.*data"))
            )
        ));

        // Help intents - Order matters! More specific patterns first
        INTENT_PATTERNS.put(IntentType.SHOW_CAPABILITIES, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("what can you do", "capabilities", "features", "show capabilities"),
                Arrays.asList("capabilities", "features", "functions"),
                Arrays.asList(Pattern.compile("\\bcapabilities\\b"), Pattern.compile("\\bfeatures\\b"), Pattern.compile("\\bwhat\\s+can\\s+you\\s+do\\b"))
            )
        ));

        INTENT_PATTERNS.put(IntentType.GET_HELP, Arrays.asList(
            new IntentPattern(1.0,
                Arrays.asList("help", "how to", "need help"),
                Arrays.asList("help", "how"),
                Arrays.asList(Pattern.compile("\\bhelp\\b"), Pattern.compile("\\bhow\\s+to\\b"))
            )
        ));
    }

    /**
     * Recognize the primary intent from a natural language query
     *
     * @param query The user's natural language input
     * @return Intent object containing the recognized intent and confidence score
     */
    public Intent recognizeIntent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new Intent(IntentType.UNKNOWN, 0.0, "Empty query");
        }

        // Normalize the query
        String normalizedQuery = normalizeQuery(query);

        // Calculate scores for each intent type
        Map<IntentType, Double> intentScores = calculateIntentScores(normalizedQuery);

        // Find the best matching intent
        IntentType bestIntent = IntentType.UNKNOWN;
        double bestScore = 0.0;

        for (Map.Entry<IntentType, Double> entry : intentScores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestIntent = entry.getKey();
            }
        }

        // Apply confidence threshold
        double threshold = nlpConfig != null ? nlpConfig.getIntentConfidenceThreshold() : 0.7;
        if (bestScore < threshold) {
            return new Intent(IntentType.UNKNOWN, bestScore,
                "Confidence below threshold (" + threshold + ")");
        }

        return new Intent(bestIntent, bestScore, "Intent recognized successfully");
    }

    /**
     * Normalize query text for better pattern matching
     */
    private String normalizeQuery(String query) {
        return query.toLowerCase()
                   .replaceAll("[^a-zA-Z0-9\\s-]", " ") // Remove special chars except hyphens
                   .replaceAll("\\s+", " ") // Normalize whitespace
                   .trim();
    }

    /**
     * Calculate confidence scores for each intent type
     */
    private Map<IntentType, Double> calculateIntentScores(String normalizedQuery) {
        Map<IntentType, Double> scores = new HashMap<>();

        for (IntentType intentType : IntentType.values()) {
            if (intentType == IntentType.UNKNOWN) continue;

            List<IntentPattern> patterns = INTENT_PATTERNS.get(intentType);
            if (patterns == null) continue;

            double maxScore = 0.0;
            for (IntentPattern pattern : patterns) {
                double score = calculatePatternScore(normalizedQuery, pattern);
                maxScore = Math.max(maxScore, score);
            }

            scores.put(intentType, maxScore);
        }

        return scores;
    }

    /**
     * Calculate how well a query matches a specific pattern
     */
    private double calculatePatternScore(String query, IntentPattern pattern) {
        double score = 0.0;
        boolean hasExactMatch = false;

        // Check for exact phrase matches (highest priority)
        for (String phrase : pattern.getExactPhrases()) {
            if (query.equals(phrase.toLowerCase())) {
                return pattern.getWeight(); // Perfect match, return immediately
            }
            if (query.contains(phrase.toLowerCase())) {
                score += pattern.getWeight() * 0.9; // High weight for exact phrase matches
                hasExactMatch = true;
            }
        }

        // Check for regex pattern matches (second priority)
        for (Pattern regex : pattern.getRegexPatterns()) {
            if (regex.matcher(query).find()) {
                score += pattern.getWeight() * 0.8; // High weight for regex matches
                hasExactMatch = true;
            }
        }

        // Only check keywords if no exact matches found (to avoid over-scoring)
        if (!hasExactMatch && !pattern.getKeywords().isEmpty()) {
            String[] queryWords = query.split("\\s+");
            Set<String> queryWordSet = Arrays.stream(queryWords)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toSet());

            int keywordMatches = 0;
            for (String keyword : pattern.getKeywords()) {
                if (queryWordSet.contains(keyword.toLowerCase())) {
                    keywordMatches++;
                }
            }

            if (keywordMatches > 0) {
                // Score based on percentage of keywords matched
                double keywordScore = (double) keywordMatches / pattern.getKeywords().size();
                score += pattern.getWeight() * keywordScore * 0.6; // Lower weight for keyword matches
            }
        }

        // Normalize score to 0-1 range
        return Math.min(score, 1.0);
    }

    /**
     * Recognize multiple possible intents with confidence scores
     * 
     * @param query The user's natural language input
     * @return List of Intent objects ranked by confidence
     */
    public List<Intent> recognizeMultipleIntents(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
                System.out.println("Debug: Empty query received for multiple intent recognition");
            }
            return Collections.singletonList(new Intent(IntentType.UNKNOWN, 0, "Empty query"));
        }

        // Normalize the query
        String normalizedQuery = normalizeQuery(query);
        if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
            System.out.println("Debug: Normalized query for multiple intent recognition: " + normalizedQuery);
        }

        // Calculate scores for each intent type
        Map<IntentType, Double> intentScores = calculateIntentScores(normalizedQuery);
        double threshold = nlpConfig != null ? nlpConfig.getIntentConfidenceThreshold() : 0.7;


        // Filter intents above threshold and sort by confidence
        List<Intent> intents = intentScores.entrySet().stream()
            .filter(entry -> entry.getValue() >= threshold)
            .map(entry -> {
                Intent intent = new Intent(entry.getKey(), entry.getValue(), query);
                if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
                    System.out.println("Debug: Recognized intent " + intent.getType().getCode() + " with confidence " + entry.getValue()); 
                }
                return intent;
            })
            .sorted((i1, i2) -> Double.compare(i2.getConfidence(), i1.getConfidence()))
            .collect(Collectors.toList());

        // If no intents above threshold, return UNKNOWN
        if (intents.isEmpty()) {
            if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
                System.out.println("Debug: No intents recognized above threshold (" + threshold + ")");
            }
            return Collections.singletonList(new Intent(IntentType.UNKNOWN, 0, 
                "No intents recognized above threshold (" + threshold + ")"));
        }
        return intents;
    }

    /**
     * Get all supported intents for a given user role
     * 
     * @param userRole The role of the user (admin, user, etc.)
     * @return List of supported intent types
     */
    public List<IntentType> getSupportedIntents(String userRole) {
        if (userRole == null || userRole.trim().isEmpty()) {
            if (nlpConfig.isDebugEnabled()) {
                System.out.println("DEBUG: Null or empty user role provided");
            }
            return Collections.emptyList();
        }

        // Define role-based intent access
        Map<String, List<IntentType>> roleIntents = new HashMap<>();
        roleIntents.put("USER", Arrays.asList(
            IntentType.SHOW_TICKETS,
            IntentType.SHOW_ACTIVE_TICKETS,
            IntentType.SHOW_COMPLETED_TICKETS,
            IntentType.SEARCH_TICKETS,
            IntentType.GET_TICKET_DETAILS,
            IntentType.CREATE_TICKET,
            IntentType.UPDATE_TICKET_STATUS,
            IntentType.CLOSE_TICKET,
            IntentType.GET_HELP,
            IntentType.SHOW_CAPABILITIES
        ));
        // For simplicity, admins can access all intents
        roleIntents.put("ADMIN", Arrays.asList(
            IntentType.values()
        ));

        String normalizedRole = userRole.trim().toUpperCase();
        List<IntentType> supportedIntents = roleIntents.getOrDefault(normalizedRole, Collections.emptyList());
    
        if (nlpConfig != null && nlpConfig.isDebugEnabled()) {
            System.out.println("Debug: Supported intents for role " + normalizedRole + ": " +
                supportedIntents.stream().map(IntentType::getCode).collect(Collectors.joining(", ")));
        }
        return supportedIntents;
    }

    /**
     * Validate if an intent is allowed for a specific user role
     * 
     * @param intent The intent to validate
     * @param userRole The user's role
     * @return true if intent is allowed, false otherwise
     */
    public boolean isIntentAllowed(IntentType intent, String userRole) {
        if (intent == null || userRole == null || userRole.trim().isEmpty()) {
            if (nlpConfig.isDebugEnabled()) {
                System.out.println("DEBUG: Invalid intent or user role provided");
            }
            return false;
        }

        List<IntentType> supportedIntents = getSupportedIntents(userRole);
        boolean allowed = supportedIntents.contains(intent);

        if (nlpConfig.isDebugEnabled()) {
            System.out.println("DEBUG: Intent " + intent.getCode() + " allowed for role " + userRole + ": " + allowed);
        }

        return allowed;
    }

    /**
     * Represents a recognized intent with confidence score
     */
    public static class Intent {
        private IntentType type;
        private double confidence;
        private Map<String, Object> parameters;
        private String originalQuery;

        public Intent() {}

        public Intent(IntentType type, double confidence) {
            this.type = type;
            this.confidence = confidence;
        }

        public Intent(IntentType type, double confidence, String originalQuery) {
            this.type = type;
            this.confidence = confidence;
            this.originalQuery = originalQuery;
        }

        // Getters and setters
        public IntentType getType() { return type; }
        public void setType(IntentType type) { this.type = type; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }

        /**
         * Check if the intent recognition was successful
         * @return true if intent is not UNKNOWN and confidence is above threshold
         */
        public boolean isSuccess() {
            return type != IntentType.UNKNOWN && confidence > 0.0;
        }
    }

    /**
     * Enumeration of supported intent types
     */
    public enum IntentType {
        // Query intents
        SHOW_TICKETS("show_tickets", "Display user's tickets"),
        SHOW_ACTIVE_TICKETS("show_active_tickets", "Display active tickets"),
        SHOW_COMPLETED_TICKETS("show_completed_tickets", "Display completed tickets"),
        SEARCH_TICKETS("search_tickets", "Search for specific tickets"),
        GET_TICKET_DETAILS("get_ticket_details", "Get details of a specific ticket"),
        
        // Management intents
        UPDATE_TICKET_STATUS("update_ticket_status", "Update the status of a ticket"),
        ASSIGN_TICKET("assign_ticket", "Assign a ticket to a user"),
        CREATE_TICKET("create_ticket", "Create a new ticket"),
        CLOSE_TICKET("close_ticket", "Close an existing ticket"),
        
        // Admin intents
        SHOW_ALL_TICKETS("show_all_tickets", "Display all tickets in system"),
        GET_SYSTEM_STATS("get_system_stats", "Get system statistics"),
        EXPORT_TICKETS("export_tickets", "Export tickets to file"),
        MANAGE_USERS("manage_users", "User management operations"),
        
        // Help and information
        GET_HELP("get_help", "Get help information"),
        SHOW_CAPABILITIES("show_capabilities", "Show available capabilities"),
        
        // Unknown or unclear intent
        UNKNOWN("unknown", "Intent could not be determined");

        private final String code;
        private final String description;

        IntentType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Pattern class for intent recognition
     */
    public static class IntentPattern {
        private final double weight;
        private final List<String> exactPhrases;
        private final List<String> keywords;
        private final List<Pattern> regexPatterns;

        public IntentPattern(double weight, List<String> exactPhrases, List<String> keywords, List<Pattern> regexPatterns) {
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
