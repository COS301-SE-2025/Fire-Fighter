package com.apex.firefighter.service.nlp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ResponseGenerationServiceTest {

    private ResponseGenerationService service;
    private ResponseGenerationService.QueryContext context;
    private ResponseGenerationService.ResponsePreferences prefs;

    @BeforeEach
    void setUp() {
        service = new ResponseGenerationService();

        context = new ResponseGenerationService.QueryContext();
        context.setOriginalQuery("show my active tickets");
        context.setIntent(IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS);
        Map<String,Object> filters = new HashMap<>();
        filters.put("status", "open");
        context.setFilters(filters);
        context.setTimeframe("last 7 days");
        context.setIncludeDetails(false);

        prefs = new ResponseGenerationService.ResponsePreferences();
        prefs.setIncludeEmojis(false);   // keep stable for assertions
        prefs.setVerboseMode(false);
        prefs.setStyle(ResponseGenerationService.ResponseStyle.PROFESSIONAL);
        prefs.setMaxResponseLength(500);
    }

}
