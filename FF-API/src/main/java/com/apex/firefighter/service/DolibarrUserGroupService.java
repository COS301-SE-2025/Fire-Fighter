package com.apex.firefighter.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
public class DolibarrUserGroupService {
    private final RestTemplate restTemplate;
    private final String dolibarrBaseUrl;
    private final String apiKey;
    private final Long firefighterGroupId;

    public DolibarrUserGroupService(
            RestTemplate restTemplate,
            @Value("${dolibarr.api.base-url}") String dolibarrBaseUrl,
            @Value("${dolibarr.api.key}") String apiKey,
            @Value("${dolibarr.ff.group.id}") Long firefighterGroupId) {
        this.restTemplate = restTemplate;
        this.dolibarrBaseUrl = dolibarrBaseUrl;
        this.apiKey = apiKey;
        this.firefighterGroupId = firefighterGroupId;
    }

    public void addUserToGroup(Long userId) {
        String url = dolibarrBaseUrl + "/users/" + userId + "/setGroup/" + firefighterGroupId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("DOLAPIKEY", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to add user to group: " + response.getStatusCode());
        }
    }

    public void removeUserFromGroup(Long userId) throws IOException, InterruptedException{
        ObjectMapper mapper = new ObjectMapper();
        
        String getURL = dolibarrBaseUrl + "/users/" + userId + "/groups";

        HttpHeaders headers = new HttpHeaders();
        headers.set("DOLAPIKEY", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(getURL, HttpMethod.GET, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch user groups: " + response.getStatusCode());
        }

        List<Map<String, Object>> groups = mapper.readValue(
            response.getBody(),
            new TypeReference<List<Map<String, Object>>>() {}
        );

        List<Integer> groupIds = new ArrayList<>();
        for (Map<String, Object> group : groups) {
            if (group.get("id").equals(firefighterGroupId)) {
                continue; // Skip the firefighter group
            }
            Object idValue = group.get("id");
            if (idValue instanceof Number) {
                groupIds.add(((Number) idValue).intValue());
            }
        }

        String groupsExlFF = mapper.writeValueAsString(Map.of("groups", groupIds));

        String putURL = dolibarrBaseUrl + "/users/" + userId + "?fetch_child=groups";
        HttpHeaders putHeaders = new HttpHeaders();
        putHeaders.set("DOLAPIKEY", apiKey);
        putHeaders.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"groups\":\"" + groupsExlFF + "\"}";
        HttpEntity<String> putRequest = new HttpEntity<>(requestBody, putHeaders);

        ResponseEntity<String> putResponse = restTemplate.exchange(putURL, HttpMethod.PUT, putRequest, String.class);
        if (!putResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to remove user from group: " + putResponse.getStatusCode());
        }
    }

}
