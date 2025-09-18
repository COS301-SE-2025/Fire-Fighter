 package com.apex.firefighter.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

        prompt.append("You are an AI assistant for the FireFighter Emergency Access Management Platform. ");
        prompt.append("FireFighter is a system that allows users to request temporary elevated access to critical systems ");
        prompt.append("(like ERP systems, databases, production environments) during system emergencies without waiting for administrator approval. ");
        prompt.append("This enables rapid incident response and system fixes.\n\n");

        prompt.append("Key FireFighter Concepts:\n");
        prompt.append("- Tickets = Access requests for elevated permissions in integrated systems\n");
        prompt.append("- Emergency Types: Critical System Failure, Security Incident, Data Recovery, Network Outage, User Lockout, Other Emergency\n");
        prompt.append("- Users can create tickets that immediately grant temporary elevated access\n");
        prompt.append("- Administrators review access logs and can revoke access if needed\n");
        prompt.append("- This is for SYSTEM emergencies, not physical emergencies\n\n");

        prompt.append("FireFighter Application Structure:\n");
        prompt.append("- Dashboard: Main overview page with key metrics and recent activity\n");
        prompt.append("- My Requests: User's personal access tickets and request history\n");
        prompt.append("- Gemini: AI chatbot interface (this current conversation)\n");
        prompt.append("- Admin Console: System-wide access logs and management (Administrators Only)\n");
        prompt.append("- Notifications: Alerts about access approvals, revocations, and system updates\n");
        prompt.append("- Help and Support: Documentation, guides, and support resources\n");
        prompt.append("- Account: User profile and authentication settings\n");
        prompt.append("- Settings: Application preferences and configuration\n\n");

        prompt.append("User Role: ").append(userRole).append("\n");

        if (context != null && !context.trim().isEmpty()) {
            prompt.append("TICKET DATA CONTEXT (USE THIS EXACT DATA): ").append(context).append("\n");
        } else {
            prompt.append("TICKET DATA CONTEXT: No ticket data provided - user may have no tickets.\n");
        }

        prompt.append("\nUser Question: ").append(userPrompt).append("\n\n");

        prompt.append("CRITICAL: USE THE TICKET DATA CONTEXT ABOVE. ");
        prompt.append("When users ask about tickets, access, or activity, COPY THE EXACT TEXT from the 'TICKET DATA CONTEXT' section. ");
        prompt.append("If the context says 'You have no active tickets. Here are your past X tickets:', COPY THAT EXACTLY. ");
        prompt.append("If the context shows ticket lists, COPY THOSE EXACTLY. ");
        prompt.append("DO NOT create your own response - USE THE PROVIDED CONTEXT. ");
        prompt.append("DO NOT mention navigation, pages, or UI elements. ");
        prompt.append("DO NOT say 'I need to access' or 'navigate to'. ");
        prompt.append("ONLY use the ticket data from the context section above. ");
        prompt.append("If context is empty or says no tickets, respond with exactly what the context says.");

        return prompt.toString();
    }

    /**
     * Check if API key is configured
     */
    public boolean isConfigured() {
        boolean configured = apiKey != null && !apiKey.trim().isEmpty() && 
               !apiKey.equals("${GOOGLE_GEMINI_API_KEY}") && 
               !apiKey.equals("disabled-for-dev");
        
        System.out.println("🤖 GEMINI AI SERVICE: Configuration check");
        System.out.println("🤖 GEMINI AI SERVICE: API Key present: " + (apiKey != null));
        System.out.println("🤖 GEMINI AI SERVICE: API Key value: " + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "null"));
        System.out.println("🤖 GEMINI AI SERVICE: Is configured: " + configured);
        
        return configured;
    }
}
