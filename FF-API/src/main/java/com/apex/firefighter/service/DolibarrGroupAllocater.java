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

        if (descLower.contains("hr") || descLower.contains("human resources")) {
            return groups.get("hr");
        } else if (descLower.contains("financial") || descLower.contains("payment") || descLower.contains("invoice")) {
            return groups.get("financials");
        } else if (descLower.contains("manager") || descLower.contains("management") || descLower.contains("admin")) {
            return groups.get("fmanager");
        } else if (descLower.contains("logistics") || descLower.contains("supply") || descLower.contains("inventory")) {
            return groups.get("logistics");
        } else {
            throw new IllegalArgumentException("No matching group found for description: " + description);
        }
    }

    /* 
        For the case where the frontend provides a list of reasons to choose from and the request contains the list number.
        Add, modify, remove group allocation logic here according to frontend logic.
    */
    public Integer allocateByListNumber(int listNumber) throws IllegalArgumentException {
        return switch (listNumber) {
            case 1 -> Integer.valueOf(FFHR);
            case 2 -> Integer.valueOf(FFFIN);
            case 3 -> Integer.valueOf(FFMNG);
            case 4 -> Integer.valueOf(FFLOG);
            default -> throw new IllegalArgumentException("Invalid group list number: " + listNumber);
        };
    }
}
