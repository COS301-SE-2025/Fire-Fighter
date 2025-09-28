package com.apex.firefighter.service;

import com.apex.firefighter.config.DoliGroupConfig;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DolibarrGroupAllocater {
    private final DoliGroupConfig properties;

    public DolibarrGroupAllocater(DoliGroupConfig properties) {
        this.properties = properties;
    }

    /*
        For the case where the frontend sends the group description along with the ticket.
        Add, modify, remove group allocation logic here as required.
    */
    public Integer allocateByDescription(String description) throws IllegalArgumentException {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        String descLower = description.toLowerCase();
        Map<String, Integer> groups = properties.getGroups();

        System.out.println("🔍 GROUP ALLOCATOR: Processing description: '" + description + "'");
        System.out.println("🔍 GROUP ALLOCATOR: Available groups: " + groups);

        // Check for emergency type values first (from dropdown selection)
        if (descLower.contains("hr-emergency") || descLower.contains("hr") || descLower.contains("human resources")) {
            Integer groupId = groups.get("hr");
            if (groupId == null) {
                throw new IllegalArgumentException("HR group ID not configured in application.properties (dolibarr.ff.hr)");
            }
            return groupId;
        } else if (descLower.contains("financial-emergency") || descLower.contains("financial") || descLower.contains("payment") || descLower.contains("invoice")) {
            Integer groupId = groups.get("financials");
            if (groupId == null) {
                throw new IllegalArgumentException("Financials group ID not configured in application.properties (dolibarr.ff.financials)");
            }
            return groupId;
        } else if (descLower.contains("management-emergency") || descLower.contains("manager") || descLower.contains("management") || descLower.contains("admin")) {
            Integer groupId = groups.get("fmanager");
            if (groupId == null) {
                throw new IllegalArgumentException("Manager group ID not configured in application.properties (dolibarr.ff.fmanager)");
            }
            return groupId;
        } else if (descLower.contains("logistics-emergency") || descLower.contains("logistics") || descLower.contains("supply") || descLower.contains("inventory")) {
            Integer groupId = groups.get("logistics");
            if (groupId == null) {
                throw new IllegalArgumentException("Logistics group ID not configured in application.properties (dolibarr.ff.logistics)");
            }
            return groupId;
        } else {
            throw new IllegalArgumentException("No matching group found for description: " + description);
        }
    }

    /*
        For the case where the frontend provides a list of reasons to choose from and the request contains the list number.
        Add, modify, remove group allocation logic here according to frontend logic.
    */
    public Integer allocateByListNumber(int listNumber) throws IllegalArgumentException {
        Map<String, Integer> groups = properties.getGroups();
        return switch (listNumber) {
            case 1 -> Integer.valueOf(groups.get("hr"));
            case 2 -> Integer.valueOf(groups.get("financials"));
            case 3 -> Integer.valueOf(groups.get("fmanager"));
            case 4 -> Integer.valueOf(groups.get("logistics"));
            default -> throw new IllegalArgumentException("Invalid group list number: " + listNumber);
        };
    }
}
