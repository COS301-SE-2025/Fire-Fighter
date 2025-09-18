package com.apex.firefighter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "dolibarr.ff")
public class DoliGroupConfig {
    private Map<String, Integer> groups = new HashMap<>();

    public Map<String, Integer> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Integer> groups) {
        this.groups = groups;
    }
    
}
