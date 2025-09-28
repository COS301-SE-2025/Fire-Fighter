package com.apex.firefighter.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "DOLIBARR_FF_HR_GROUP_ID=7",
    "DOLIBARR_FF_FINANCIALS_GROUP_ID=6", 
    "DOLIBARR_FF_LOGISTICS_GROUP_ID=8",
    "DOLIBARR_FF_FMANAGER_GROUP_ID=9"
})
public class DoliGroupConfigTest {

    @Autowired
    private DoliGroupConfig doliGroupConfig;

    @Test
    void getGroups_ShouldReturnCorrectGroupMappings() {
        // Act
        Map<String, Integer> groups = doliGroupConfig.getGroups();
        
        // Assert
        assertThat(groups).isNotNull();
        assertThat(groups.get("hr")).isEqualTo(7);
        assertThat(groups.get("financials")).isEqualTo(6);
        assertThat(groups.get("logistics")).isEqualTo(8);
        assertThat(groups.get("fmanager")).isEqualTo(9);
        
        System.out.println("✅ GROUP CONFIG TEST: Groups loaded correctly: " + groups);
    }
}
