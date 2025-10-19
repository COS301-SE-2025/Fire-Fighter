package com.apex.firefighter.service;

import com.apex.firefighter.config.DoliGroupConfig;
import com.apex.firefighter.config.DolibarrPermissionsConfig;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DolibarrGroupAllocater {
    private final DoliGroupConfig properties;
    private final DolibarrPermissionsConfig permissionsConfig;

    public DolibarrGroupAllocater(DoliGroupConfig properties, DolibarrPermissionsConfig permissionsConfig) {
        this.properties = properties;
        this.permissionsConfig = permissionsConfig;
    }

    /**
     * Allocates a user to the appropriate Dolibarr group based on the permission selection.
     * The description parameter should contain the fine-grained permission value from the frontend
     * (e.g., "user-read-users-groups", "hr-salary-read", etc.), optionally followed by additional text.
     * 
     * This method extracts the permission value and delegates to getPermissionGroupId().
     * 
     * @param description The permission value (possibly with additional description text)
     * @return The Dolibarr group ID for that permission
     * @throws IllegalArgumentException if the permission is invalid or not configured
     */
    public Integer allocateByDescription(String description) throws IllegalArgumentException {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        System.out.println("🔍 GROUP ALLOCATOR: Processing description: '" + description + "'");

        // Extract the permission value from the description (first word before any space)
        // This handles cases where the description is like "user-read-users-groups Need to view team"
        String permissionValue = description.trim().split("\\s+")[0];
        
        System.out.println("🔍 GROUP ALLOCATOR: Extracted permission value: '" + permissionValue + "'");

        // Use the new fine-grained permission mapper
        return getPermissionGroupId(permissionValue);
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

    /**
     * Get the specific fine-grained permission group ID based on the permission selection
     * from the frontend dropdown. This maps the exact permission value to its Dolibarr group ID.
     * 
     * @param permissionValue The permission value from the frontend (e.g., "user-read-users-groups")
     * @return The Dolibarr group ID for that specific permission
     * @throws IllegalArgumentException if the permission value is invalid or not configured
     */
    public Integer getPermissionGroupId(String permissionValue) {
        if (permissionValue == null || permissionValue.isEmpty()) {
            throw new IllegalArgumentException("Permission value cannot be null or empty");
        }

        String permValue = permissionValue.trim().toLowerCase();
        
        System.out.println("🔍 PERMISSION MAPPER: Looking up group ID for permission: '" + permissionValue + "'");

        // Map frontend permission values to their corresponding getter methods in DolibarrPermissionsConfig
        Integer groupId = switch (permValue) {
            // User Management (IDs 12-18)
            case "user-read-users-groups" -> permissionsConfig.getReadUsersGroups();
            case "user-modify-users-groups" -> permissionsConfig.getModifyUsersGroups();
            case "user-modify-password" -> permissionsConfig.getModifyUserPassword();
            case "user-delete-users" -> permissionsConfig.getDeleteUsers();
            case "user-modify-own" -> permissionsConfig.getModifyOwnUser();
            case "user-modify-own-password" -> permissionsConfig.getModifyOwnPassword();
            case "user-export-users" -> permissionsConfig.getExportUsers();
            
            // Third Parties (IDs 19-23)
            case "third-party-read" -> permissionsConfig.getReadThirdParties();
            case "third-party-modify" -> permissionsConfig.getModifyThirdParties();
            case "third-party-delete" -> permissionsConfig.getDeleteThirdParties();
            case "third-party-export" -> permissionsConfig.getExportThirdParties();
            case "third-party-extend-access" -> permissionsConfig.getExtendThirdPartiesAccess();
            
            // Contacts (IDs 24-27)
            case "contacts-read" -> permissionsConfig.getReadContacts();
            case "contacts-modify" -> permissionsConfig.getModifyContacts();
            case "contacts-delete" -> permissionsConfig.getDeleteContacts();
            case "contacts-export" -> permissionsConfig.getExportContacts();
            
            // Customer Orders (IDs 28-31)
            case "orders-read" -> permissionsConfig.getReadCustomerOrders();
            case "orders-modify" -> permissionsConfig.getModifyCustomerOrders();
            case "orders-delete" -> permissionsConfig.getDeleteCustomerOrders();
            case "orders-export" -> permissionsConfig.getExportSalesOrders();
            
            // Actions/Events - Own (IDs 32-34)
            case "actions-own-read" -> permissionsConfig.getReadOwnActions();
            case "actions-own-modify" -> permissionsConfig.getModifyOwnActions();
            case "actions-own-delete" -> permissionsConfig.getDeleteOwnActions();
            
            // Actions/Events - Others (IDs 35-38)
            case "actions-others-read" -> permissionsConfig.getReadOthersActions();
            case "actions-others-modify" -> permissionsConfig.getModifyOthersActions();
            case "actions-others-delete" -> permissionsConfig.getDeleteOthersActions();
            case "actions-export" -> permissionsConfig.getExportActions();
            
            // API Keys (ID 39)
            case "api-generate-key" -> permissionsConfig.getGenerateApiKey();
            
            // Products (IDs 40-43)
            case "products-read" -> permissionsConfig.getReadProducts();
            case "products-modify" -> permissionsConfig.getModifyProducts();
            case "products-delete" -> permissionsConfig.getDeleteProducts();
            case "products-export" -> permissionsConfig.getExportProducts();
            
            // Warehouses/Stock (IDs 44-48)
            case "warehouse-read" -> permissionsConfig.getReadWarehousesStocks();
            case "warehouse-modify" -> permissionsConfig.getModifyWarehouses();
            case "warehouse-delete" -> permissionsConfig.getDeleteWarehouses();
            case "stock-read" -> permissionsConfig.getReadStockMovements();
            case "stock-modify" -> permissionsConfig.getModifyStockMovements();
            
            // Shipping/Delivery (IDs 49-55)
            case "shipping-read" -> permissionsConfig.getReadSendings();
            case "shipping-modify" -> permissionsConfig.getModifySendings();
            case "shipping-export" -> permissionsConfig.getExportSendings();
            case "shipping-delete" -> permissionsConfig.getDeleteSendings();
            case "delivery-read" -> permissionsConfig.getReadDeliveryReceipts();
            case "delivery-modify" -> permissionsConfig.getModifyDeliveryReceipts();
            case "delivery-delete" -> permissionsConfig.getDeleteDeliveryReceipts();
            
            // HR - Skills/Jobs (IDs 56-58)
            case "hr-skills-read" -> permissionsConfig.getReadSkillsJobs();
            case "hr-skills-modify" -> permissionsConfig.getModifySkillsJobs();
            case "hr-skills-delete" -> permissionsConfig.getDeleteSkillsJobs();
            
            // HR - Evaluations (IDs 59-62)
            case "hr-eval-read" -> permissionsConfig.getReadEvaluations();
            case "hr-eval-modify" -> permissionsConfig.getModifyEvaluations();
            case "hr-eval-delete" -> permissionsConfig.getDeleteEvaluations();
            case "hr-eval-read-all" -> permissionsConfig.getReadAllEvaluations();
            
            // HR - Personal Info (IDs 63-64)
            case "hr-personal-read" -> permissionsConfig.getReadPersonalInfo();
            case "hr-personal-write" -> permissionsConfig.getWritePersonalInfo();
            
            // HR - Salaries (IDs 65-69)
            case "hr-salary-read" -> permissionsConfig.getReadSalaries();
            case "hr-salary-modify" -> permissionsConfig.getModifySalaries();
            case "hr-salary-delete" -> permissionsConfig.getDeleteSalaries();
            case "hr-salary-read-all" -> permissionsConfig.getReadAllSalaries();
            case "hr-salary-export" -> permissionsConfig.getExportSalaries();
            
            // Tickets (IDs 70-74)
            case "tickets-see" -> permissionsConfig.getSeeTickets();
            case "tickets-modify" -> permissionsConfig.getModifyTickets();
            case "tickets-delete" -> permissionsConfig.getDeleteTickets();
            case "tickets-manage" -> permissionsConfig.getManageTickets();
            case "tickets-export" -> permissionsConfig.getExportTickets();
            
            // Manufacturing - BOM (IDs 75-77)
            case "bom-read" -> permissionsConfig.getReadBom();
            case "bom-modify" -> permissionsConfig.getModifyBom();
            case "bom-delete" -> permissionsConfig.getDeleteBom();
            
            // Manufacturing - MO (IDs 78-80)
            case "mo-read" -> permissionsConfig.getReadMo();
            case "mo-modify" -> permissionsConfig.getModifyMo();
            case "mo-delete" -> permissionsConfig.getDeleteMo();
            
            // Data Import/Export (IDs 81-83)
            case "data-import" -> permissionsConfig.getRunMassImports();
            case "data-export-get" -> permissionsConfig.getGetExportResult();
            case "data-export-modify" -> permissionsConfig.getModifyExport();
            
            // Module Development (ID 84)
            case "system-generate-modules" -> permissionsConfig.getGenerateModules();
            
            default -> null;
        };

        if (groupId == null) {
            throw new IllegalArgumentException(
                "Invalid or unconfigured permission: '" + permissionValue + "'. " +
                "Please ensure this permission is mapped in DolibarrGroupAllocater and configured in application.properties");
        }

        System.out.println("✅ PERMISSION MAPPER: Mapped '" + permissionValue + "' -> Dolibarr group ID " + groupId);
        return groupId;
    }

    /**
     * Get fine-grained permission group IDs for a specific category
     * This returns multiple permission groups instead of a single legacy group
     * 
     * @param description The emergency description/category
     * @return List of permission group IDs to assign
     * @deprecated Use getPermissionGroupId() for specific permission mapping
     */
    @Deprecated
    public List<Integer> getFineGrainedPermissions(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        List<Integer> permissions = permissionsConfig.getPermissionsForCategory(description);
        
        System.out.println("🔍 FINE-GRAINED ALLOCATOR: Category '" + description + "' -> " + permissions.size() + " permission groups");
        
        return permissions;
    }
}
