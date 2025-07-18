package com.apex.firefighter.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiAIService {

    @Value("${GOOGLE_GEMINI_API_KEY}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public GeminiAIService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate AI response using Gemini Pro
     */
    public String generateResponse(String prompt) {
        try {
            Map<String, Object> requestBody = createRequestBody(prompt);
            
            String response = webClient.post()
                    .uri(GEMINI_API_URL)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromResponse(response);
            
        } catch (WebClientResponseException e) {
            System.err.println("Gemini API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "I'm sorry, I'm having trouble processing your request right now. Please try again later.";
        } catch (Exception e) {
            System.err.println("Unexpected error calling Gemini API: " + e.getMessage());
            return "I'm experiencing technical difficulties. Please try again later.";
        }
    }

    /**
     * Generate AI response with context about the user's role
     */
    public String generateResponseWithContext(String prompt, String userRole, String context) {
        String enhancedPrompt = buildContextualPrompt(prompt, userRole, context);
        return generateResponse(enhancedPrompt);
    }

    /**
     * Create request body for Gemini API
     */
    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();

        // Create contents array - simplified format matching the example
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        return requestBody;
    }



    /**
     * Extract text response from Gemini API response
     */
    private String extractTextFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode candidates = jsonNode.get("candidates");
            
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.get("content");
                
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode firstPart = parts.get(0);
                        JsonNode text = firstPart.get("text");
                        if (text != null) {
                            return text.asText().trim();
                        }
                    }
                }
            }
            
            return "I couldn't generate a proper response. Please try rephrasing your question.";
            
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            return "I had trouble understanding the response. Please try again.";
        }
    }

    /**
     * Build contextual prompt with user role and system context
     */
    private String buildContextualPrompt(String userPrompt, String userRole, String context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an AI assistant for the FireFighter Emergency Response Platform. ");
        prompt.append("You help emergency responders and administrators manage tickets and operations.\n\n");
        
        prompt.append("User Role: ").append(userRole).append("\n");
        
        if (context != null && !context.trim().isEmpty()) {
            prompt.append("Context: ").append(context).append("\n");
        }
        
        prompt.append("\nUser Question: ").append(userPrompt).append("\n\n");
        
        prompt.append("Please provide a helpful, accurate, and professional response. ");
        prompt.append("If the question is about specific ticket data, use the provided context. ");
        prompt.append("If you need more information, ask clarifying questions. ");
        prompt.append("Keep responses concise but informative.");
        
        return prompt.toString();
    }

    /**
     * Check if API key is configured
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("${GOOGLE_GEMINI_API_KEY}");
    }
}
