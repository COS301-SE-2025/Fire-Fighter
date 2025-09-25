package com.apex.firefighter.service;

import com.apex.firefighter.config.DoliGroupConfig;
import com.apex.firefighter.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "DOLIBARR_FF_HR_GROUP_ID=7",
    "DOLIBARR_FF_FINANCIALS_GROUP_ID=6", 
    "DOLIBARR_FF_LOGISTICS_GROUP_ID=8",
    "DOLIBARR_FF_FMANAGER_GROUP_ID=9"
})
public class DolibarrGroupAllocaterIntegrationTest {

    @Autowired
    private DolibarrGroupAllocater dolibarrGroupAllocater;

    @Test
    void allocateByDescription_WithHrEmergency_ShouldReturnHrGroupId() {
        // Act & Assert
        Integer groupId = dolibarrGroupAllocater.allocateByDescription("hr-emergency Need urgent access to employee records");
        assertThat(groupId).isEqualTo(7);
        
        System.out.println("✅ HR Emergency Test: hr-emergency → Group " + groupId);
    }

    @Test
    void allocateByDescription_WithFinancialEmergency_ShouldReturnFinancialsGroupId() {
        // Act & Assert
        Integer groupId = dolibarrGroupAllocater.allocateByDescription("financial-emergency Need to process urgent payment");
        assertThat(groupId).isEqualTo(6);
        
        System.out.println("✅ Financial Emergency Test: financial-emergency → Group " + groupId);
    }

    @Test
    void allocateByDescription_WithManagementEmergency_ShouldReturnManagerGroupId() {
        // Act & Assert
        Integer groupId = dolibarrGroupAllocater.allocateByDescription("management-emergency Need admin access for critical decision");
        assertThat(groupId).isEqualTo(9);
        
        System.out.println("✅ Management Emergency Test: management-emergency → Group " + groupId);
    }

    @Test
    void allocateByDescription_WithLogisticsEmergency_ShouldReturnLogisticsGroupId() {
        // Act & Assert
        Integer groupId = dolibarrGroupAllocater.allocateByDescription("logistics-emergency Need access to supply chain system");
        assertThat(groupId).isEqualTo(8);
        
        System.out.println("✅ Logistics Emergency Test: logistics-emergency → Group " + groupId);
    }

    @Test
    void allocateByDescription_WithKeywordMatching_ShouldStillWork() {
        // Test backward compatibility with keyword matching
        Integer hrGroup = dolibarrGroupAllocater.allocateByDescription("Need access to HR system for employee data");
        assertThat(hrGroup).isEqualTo(7);
        
        Integer financialGroup = dolibarrGroupAllocater.allocateByDescription("Need to process financial transaction");
        assertThat(financialGroup).isEqualTo(6);
        
        Integer managerGroup = dolibarrGroupAllocater.allocateByDescription("Need manager access for approval");
        assertThat(managerGroup).isEqualTo(9);
        
        Integer logisticsGroup = dolibarrGroupAllocater.allocateByDescription("Need logistics access for inventory");
        assertThat(logisticsGroup).isEqualTo(8);
        
        System.out.println("✅ Keyword Matching Test: All groups allocated correctly");
    }

    @Test
    void allocateByDescription_WithUnknownDescription_ShouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> {
            dolibarrGroupAllocater.allocateByDescription("unknown emergency type");
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("No matching group found for description");
        
        System.out.println("✅ Unknown Description Test: Exception thrown correctly");
    }
}
