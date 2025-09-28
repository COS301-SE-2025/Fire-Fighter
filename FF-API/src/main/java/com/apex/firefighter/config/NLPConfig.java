package com.apex.firefighter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.HashMap;

/**
 * Configuration class for Natural Language Processing service.
 * Manages NLP settings, parameters, and component configurations.
 */
@Configuration
public class NLPConfig {

    @Value("${nlp.intent.confidence.threshold:0.7}")
    private double intentConfidenceThreshold;

    @Value("${nlp.entity.confidence.threshold:0.6}")
    private double entityConfidenceThreshold;

    @Value("${nlp.response.max.length:1000}")
    private int maxResponseLength;

    @Value("${nlp.query.max.length:500}")
    private int maxQueryLength;

    @Value("${nlp.cache.enabled:true}")
    private boolean cacheEnabled;

    @Value("${nlp.cache.ttl:300}")
    private int cacheTtlSeconds;

    @Value("${nlp.debug.enabled:false}")
    private boolean debugEnabled;

    @PostConstruct
    public void validateConfiguration() {
        System.out.println("🧠 NLP Configuration initialized:");
        System.out.println("   Intent confidence threshold: " + intentConfidenceThreshold);
        System.out.println("   Entity confidence threshold: " + entityConfidenceThreshold);
        System.out.println("   Max response length: " + maxResponseLength);
        System.out.println("   Max query length: " + maxQueryLength);
        System.out.println("   Cache enabled: " + cacheEnabled);
        System.out.println("   Debug mode: " + debugEnabled);

        // Validate thresholds
        if (intentConfidenceThreshold < 0.0 || intentConfidenceThreshold > 1.0) {
            System.err.println("WARNING: Intent confidence threshold should be between 0.0 and 1.0");
        }
        if (entityConfidenceThreshold < 0.0 || entityConfidenceThreshold > 1.0) {
            System.err.println("WARNING: Entity confidence threshold should be between 0.0 and 1.0");
        }
    }

    /**
     * Configuration bean for intent recognition settings
     */
    @Bean
    public IntentRecognitionConfig intentRecognitionConfig() {
        IntentRecognitionConfig config = new IntentRecognitionConfig();
        config.setConfidenceThreshold(intentConfidenceThreshold);
        config.setDebugEnabled(debugEnabled);
        
        // TODO: Add more intent recognition specific configurations
        return config;
    }

    /**
     * Configuration bean for entity extraction settings
     */
    @Bean
    public EntityExtractionConfig entityExtractionConfig() {
        EntityExtractionConfig config = new EntityExtractionConfig();
        config.setConfidenceThreshold(entityConfidenceThreshold);
        config.setDebugEnabled(debugEnabled);
        
        // TODO: Add more entity extraction specific configurations
        return config;
    }

    /**
     * Configuration bean for response generation settings
     */
    @Bean
    public ResponseGenerationConfig responseGenerationConfig() {
        ResponseGenerationConfig config = new ResponseGenerationConfig();
        config.setMaxResponseLength(maxResponseLength);
        config.setDebugEnabled(debugEnabled);
        
        // TODO: Add more response generation specific configurations
        return config;
    }

    /**
     * Configuration bean for query processing settings
     */
    @Bean
    public QueryProcessingConfig queryProcessingConfig() {
        QueryProcessingConfig config = new QueryProcessingConfig();
        config.setMaxQueryLength(maxQueryLength);
        config.setCacheEnabled(cacheEnabled);
        config.setCacheTtlSeconds(cacheTtlSeconds);
        config.setDebugEnabled(debugEnabled);
        
        // TODO: Add more query processing specific configurations
        return config;
    }

    /**
     * Configuration for intent recognition component
     */
    public static class IntentRecognitionConfig {
        private double confidenceThreshold;
        private boolean debugEnabled;
        private Map<String, Object> modelParameters;

        public IntentRecognitionConfig() {
            this.modelParameters = new HashMap<>();
        }

        // Getters and setters
        public double getConfidenceThreshold() { return confidenceThreshold; }
        public void setConfidenceThreshold(double confidenceThreshold) { 
            this.confidenceThreshold = confidenceThreshold; 
        }
        
        public boolean isDebugEnabled() { return debugEnabled; }
        public void setDebugEnabled(boolean debugEnabled) { this.debugEnabled = debugEnabled; }
        
        public Map<String, Object> getModelParameters() { return modelParameters; }
        public void setModelParameters(Map<String, Object> modelParameters) { 
            this.modelParameters = modelParameters; 
        }
    }

    /**
     * Configuration for entity extraction component
     */
    public static class EntityExtractionConfig {
        private double confidenceThreshold;
        private boolean debugEnabled;
        private Map<String, Object> extractionRules;

        public EntityExtractionConfig() {
            this.extractionRules = new HashMap<>();
        }

        // Getters and setters
        public double getConfidenceThreshold() { return confidenceThreshold; }
        public void setConfidenceThreshold(double confidenceThreshold) { 
            this.confidenceThreshold = confidenceThreshold; 
        }
        
        public boolean isDebugEnabled() { return debugEnabled; }
        public void setDebugEnabled(boolean debugEnabled) { this.debugEnabled = debugEnabled; }
        
        public Map<String, Object> getExtractionRules() { return extractionRules; }
        public void setExtractionRules(Map<String, Object> extractionRules) { 
            this.extractionRules = extractionRules; 
        }
    }

    /**
     * Configuration for response generation component
     */
    public static class ResponseGenerationConfig {
        private int maxResponseLength;
        private boolean debugEnabled;
        private Map<String, String> responseTemplates;

        public ResponseGenerationConfig() {
            this.responseTemplates = new HashMap<>();
        }

        // Getters and setters
        public int getMaxResponseLength() { return maxResponseLength; }
        public void setMaxResponseLength(int maxResponseLength) { 
            this.maxResponseLength = maxResponseLength; 
        }
        
        public boolean isDebugEnabled() { return debugEnabled; }
        public void setDebugEnabled(boolean debugEnabled) { this.debugEnabled = debugEnabled; }
        
        public Map<String, String> getResponseTemplates() { return responseTemplates; }
        public void setResponseTemplates(Map<String, String> responseTemplates) { 
            this.responseTemplates = responseTemplates; 
        }
    }

    /**
     * Configuration for query processing component
     */
    public static class QueryProcessingConfig {
        private int maxQueryLength;
        private boolean cacheEnabled;
        private int cacheTtlSeconds;
        private boolean debugEnabled;
        private Map<String, Object> processingRules;

        public QueryProcessingConfig() {
            this.processingRules = new HashMap<>();
        }

        // Getters and setters
        public int getMaxQueryLength() { return maxQueryLength; }
        public void setMaxQueryLength(int maxQueryLength) { this.maxQueryLength = maxQueryLength; }
        
        public boolean isCacheEnabled() { return cacheEnabled; }
        public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
        
        public int getCacheTtlSeconds() { return cacheTtlSeconds; }
        public void setCacheTtlSeconds(int cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }
        
        public boolean isDebugEnabled() { return debugEnabled; }
        public void setDebugEnabled(boolean debugEnabled) { this.debugEnabled = debugEnabled; }
        
        public Map<String, Object> getProcessingRules() { return processingRules; }
        public void setProcessingRules(Map<String, Object> processingRules) { 
            this.processingRules = processingRules; 
        }
    }

    // Getters for configuration values
    public double getIntentConfidenceThreshold() { return intentConfidenceThreshold; }
    public double getEntityConfidenceThreshold() { return entityConfidenceThreshold; }
    public int getMaxResponseLength() { return maxResponseLength; }
    public int getMaxQueryLength() { return maxQueryLength; }
    public boolean isCacheEnabled() { return cacheEnabled; }
    public int getCacheTtlSeconds() { return cacheTtlSeconds; }
    public boolean isDebugEnabled() { return debugEnabled; }
}
