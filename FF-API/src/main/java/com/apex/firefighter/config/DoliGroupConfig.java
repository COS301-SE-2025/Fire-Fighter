package com.apex.firefighter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DoliGroupConfig {

    @Value("${DOLIBARR_FF_HR_GROUP_ID}")
    private Integer hrGroupId;

    @Value("${DOLIBARR_FF_FINANCIALS_GROUP_ID}")
    private Integer financialsGroupId;

    @Value("${DOLIBARR_FF_LOGISTICS_GROUP_ID}")
    private Integer logisticsGroupId;

    @Value("${DOLIBARR_FF_FMANAGER_GROUP_ID}")
    private Integer fmanagerGroupId;

    public Map<String, Integer> getGroups() {
        Map<String, Integer> groups = new HashMap<>();
        groups.put("hr", hrGroupId);
        groups.put("financials", financialsGroupId);
        groups.put("logistics", logisticsGroupId);
        groups.put("fmanager", fmanagerGroupId);
        return groups;
    }

}
