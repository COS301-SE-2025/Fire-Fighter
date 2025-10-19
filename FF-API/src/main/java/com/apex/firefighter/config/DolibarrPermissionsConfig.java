package com.apex.firefighter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * Configuration class for fine-grained Dolibarr permission groups
 * Maps permission categories to their Dolibarr group IDs
 */
@Configuration
@ConfigurationProperties(prefix = "dolibarr.permissions")
public class DolibarrPermissionsConfig {

    // User Management
    private Integer readUsersGroups;
    private Integer modifyUsersGroups;
    private Integer modifyUserPassword;
    private Integer deleteUsers;
    private Integer modifyOwnUser;
    private Integer modifyOwnPassword;
    private Integer exportUsers;

    // Third Parties
    private Integer readThirdParties;
    private Integer modifyThirdParties;
    private Integer deleteThirdParties;
    private Integer exportThirdParties;
    private Integer extendThirdPartiesAccess;

    // Contacts
    private Integer readContacts;
    private Integer modifyContacts;
    private Integer deleteContacts;
    private Integer exportContacts;

    // Customer Orders
    private Integer readCustomerOrders;
    private Integer modifyCustomerOrders;
    private Integer deleteCustomerOrders;
    private Integer exportSalesOrders;

    // Actions/Events (Own)
    private Integer readOwnActions;
    private Integer modifyOwnActions;
    private Integer deleteOwnActions;

    // Actions/Events (Others)
    private Integer readOthersActions;
    private Integer modifyOthersActions;
    private Integer deleteOthersActions;
    private Integer exportActions;

    // API Keys
    private Integer generateApiKey;

    // Products
    private Integer readProducts;
    private Integer modifyProducts;
    private Integer deleteProducts;
    private Integer exportProducts;

    // Warehouses/Stock
    private Integer readWarehousesStocks;
    private Integer modifyWarehouses;
    private Integer deleteWarehouses;
    private Integer readStockMovements;
    private Integer modifyStockMovements;

    // Shipping/Delivery
    private Integer readSendings;
    private Integer modifySendings;
    private Integer exportSendings;
    private Integer deleteSendings;
    private Integer readDeliveryReceipts;
    private Integer modifyDeliveryReceipts;
    private Integer deleteDeliveryReceipts;

    // HR - Skills/Jobs
    private Integer readSkillsJobs;
    private Integer modifySkillsJobs;
    private Integer deleteSkillsJobs;

    // HR - Evaluations
    private Integer readEvaluations;
    private Integer modifyEvaluations;
    private Integer deleteEvaluations;
    private Integer readAllEvaluations;

    // HR - Personal Info
    private Integer readPersonalInfo;
    private Integer writePersonalInfo;

    // HR - Salaries
    private Integer readSalaries;
    private Integer modifySalaries;
    private Integer deleteSalaries;
    private Integer readAllSalaries;
    private Integer exportSalaries;

    // Tickets
    private Integer seeTickets;
    private Integer modifyTickets;
    private Integer deleteTickets;
    private Integer manageTickets;
    private Integer exportTickets;

    // Manufacturing - BOM
    private Integer readBom;
    private Integer modifyBom;
    private Integer deleteBom;

    // Manufacturing - MO
    private Integer readMo;
    private Integer modifyMo;
    private Integer deleteMo;

    // Data Import/Export
    private Integer runMassImports;
    private Integer getExportResult;
    private Integer modifyExport;

    // Module Development
    private Integer generateModules;

    /**
     * Get permission groups for a specific department/category
     * @param category The category (hr, financial, logistics, management)
     * @return List of permission group IDs for that category
     */
    public List<Integer> getPermissionsForCategory(String category) {
        if (category == null) {
            return Collections.emptyList();
        }

        String categoryLower = category.toLowerCase();
        List<Integer> permissions = new ArrayList<>();

        switch (categoryLower) {
            case "hr":
            case "hr-emergency":
            case "human resources":
                // HR gets: skills, evaluations, personal info, salaries
                addIfNotNull(permissions, readSkillsJobs, modifySkillsJobs);
                addIfNotNull(permissions, readEvaluations, modifyEvaluations);
                addIfNotNull(permissions, readPersonalInfo, writePersonalInfo);
                addIfNotNull(permissions, readSalaries, modifySalaries, exportSalaries);
                addIfNotNull(permissions, readUsersGroups); // Can view users
                break;

            case "financial":
            case "financial-emergency":
            case "financials":
                // Financial gets: customer orders, third parties, contacts
                addIfNotNull(permissions, readCustomerOrders, modifyCustomerOrders, exportSalesOrders);
                addIfNotNull(permissions, readThirdParties, modifyThirdParties, exportThirdParties);
                addIfNotNull(permissions, readContacts, modifyContacts);
                break;

            case "logistics":
            case "logistics-emergency":
                // Logistics gets: products, warehouses, stock, shipping
                addIfNotNull(permissions, readProducts, modifyProducts);
                addIfNotNull(permissions, readWarehousesStocks, modifyWarehouses);
                addIfNotNull(permissions, readStockMovements, modifyStockMovements);
                addIfNotNull(permissions, readSendings, modifySendings);
                addIfNotNull(permissions, readDeliveryReceipts, modifyDeliveryReceipts);
                break;

            case "management":
            case "management-emergency":
            case "fmanager":
            case "manager":
                // Management gets: broader access including tickets, actions, some admin
                addIfNotNull(permissions, seeTickets, modifyTickets, manageTickets);
                addIfNotNull(permissions, readOthersActions, modifyOthersActions);
                addIfNotNull(permissions, readUsersGroups);
                addIfNotNull(permissions, readAllEvaluations);
                addIfNotNull(permissions, readAllSalaries);
                break;

            default:
                System.err.println("⚠️ Unknown category: " + category);
        }

        // Everyone gets basic own actions
        addIfNotNull(permissions, readOwnActions, modifyOwnActions);
        addIfNotNull(permissions, modifyOwnUser, modifyOwnPassword);

        return permissions;
    }

    /**
     * Helper method to add non-null integers to a list
     */
    private void addIfNotNull(List<Integer> list, Integer... values) {
        for (Integer value : values) {
            if (value != null) {
                list.add(value);
            }
        }
    }

    // Getters and Setters for all fields
    public Integer getReadUsersGroups() { return readUsersGroups; }
    public void setReadUsersGroups(Integer readUsersGroups) { this.readUsersGroups = readUsersGroups; }

    public Integer getModifyUsersGroups() { return modifyUsersGroups; }
    public void setModifyUsersGroups(Integer modifyUsersGroups) { this.modifyUsersGroups = modifyUsersGroups; }

    public Integer getModifyUserPassword() { return modifyUserPassword; }
    public void setModifyUserPassword(Integer modifyUserPassword) { this.modifyUserPassword = modifyUserPassword; }

    public Integer getDeleteUsers() { return deleteUsers; }
    public void setDeleteUsers(Integer deleteUsers) { this.deleteUsers = deleteUsers; }

    public Integer getModifyOwnUser() { return modifyOwnUser; }
    public void setModifyOwnUser(Integer modifyOwnUser) { this.modifyOwnUser = modifyOwnUser; }

    public Integer getModifyOwnPassword() { return modifyOwnPassword; }
    public void setModifyOwnPassword(Integer modifyOwnPassword) { this.modifyOwnPassword = modifyOwnPassword; }

    public Integer getExportUsers() { return exportUsers; }
    public void setExportUsers(Integer exportUsers) { this.exportUsers = exportUsers; }

    public Integer getReadThirdParties() { return readThirdParties; }
    public void setReadThirdParties(Integer readThirdParties) { this.readThirdParties = readThirdParties; }

    public Integer getModifyThirdParties() { return modifyThirdParties; }
    public void setModifyThirdParties(Integer modifyThirdParties) { this.modifyThirdParties = modifyThirdParties; }

    public Integer getDeleteThirdParties() { return deleteThirdParties; }
    public void setDeleteThirdParties(Integer deleteThirdParties) { this.deleteThirdParties = deleteThirdParties; }

    public Integer getExportThirdParties() { return exportThirdParties; }
    public void setExportThirdParties(Integer exportThirdParties) { this.exportThirdParties = exportThirdParties; }

    public Integer getExtendThirdPartiesAccess() { return extendThirdPartiesAccess; }
    public void setExtendThirdPartiesAccess(Integer extendThirdPartiesAccess) { this.extendThirdPartiesAccess = extendThirdPartiesAccess; }

    public Integer getReadContacts() { return readContacts; }
    public void setReadContacts(Integer readContacts) { this.readContacts = readContacts; }

    public Integer getModifyContacts() { return modifyContacts; }
    public void setModifyContacts(Integer modifyContacts) { this.modifyContacts = modifyContacts; }

    public Integer getDeleteContacts() { return deleteContacts; }
    public void setDeleteContacts(Integer deleteContacts) { this.deleteContacts = deleteContacts; }

    public Integer getExportContacts() { return exportContacts; }
    public void setExportContacts(Integer exportContacts) { this.exportContacts = exportContacts; }

    public Integer getReadCustomerOrders() { return readCustomerOrders; }
    public void setReadCustomerOrders(Integer readCustomerOrders) { this.readCustomerOrders = readCustomerOrders; }

    public Integer getModifyCustomerOrders() { return modifyCustomerOrders; }
    public void setModifyCustomerOrders(Integer modifyCustomerOrders) { this.modifyCustomerOrders = modifyCustomerOrders; }

    public Integer getDeleteCustomerOrders() { return deleteCustomerOrders; }
    public void setDeleteCustomerOrders(Integer deleteCustomerOrders) { this.deleteCustomerOrders = deleteCustomerOrders; }

    public Integer getExportSalesOrders() { return exportSalesOrders; }
    public void setExportSalesOrders(Integer exportSalesOrders) { this.exportSalesOrders = exportSalesOrders; }

    public Integer getReadOwnActions() { return readOwnActions; }
    public void setReadOwnActions(Integer readOwnActions) { this.readOwnActions = readOwnActions; }

    public Integer getModifyOwnActions() { return modifyOwnActions; }
    public void setModifyOwnActions(Integer modifyOwnActions) { this.modifyOwnActions = modifyOwnActions; }

    public Integer getDeleteOwnActions() { return deleteOwnActions; }
    public void setDeleteOwnActions(Integer deleteOwnActions) { this.deleteOwnActions = deleteOwnActions; }

    public Integer getReadOthersActions() { return readOthersActions; }
    public void setReadOthersActions(Integer readOthersActions) { this.readOthersActions = readOthersActions; }

    public Integer getModifyOthersActions() { return modifyOthersActions; }
    public void setModifyOthersActions(Integer modifyOthersActions) { this.modifyOthersActions = modifyOthersActions; }

    public Integer getDeleteOthersActions() { return deleteOthersActions; }
    public void setDeleteOthersActions(Integer deleteOthersActions) { this.deleteOthersActions = deleteOthersActions; }

    public Integer getExportActions() { return exportActions; }
    public void setExportActions(Integer exportActions) { this.exportActions = exportActions; }

    public Integer getGenerateApiKey() { return generateApiKey; }
    public void setGenerateApiKey(Integer generateApiKey) { this.generateApiKey = generateApiKey; }

    public Integer getReadProducts() { return readProducts; }
    public void setReadProducts(Integer readProducts) { this.readProducts = readProducts; }

    public Integer getModifyProducts() { return modifyProducts; }
    public void setModifyProducts(Integer modifyProducts) { this.modifyProducts = modifyProducts; }

    public Integer getDeleteProducts() { return deleteProducts; }
    public void setDeleteProducts(Integer deleteProducts) { this.deleteProducts = deleteProducts; }

    public Integer getExportProducts() { return exportProducts; }
    public void setExportProducts(Integer exportProducts) { this.exportProducts = exportProducts; }

    public Integer getReadWarehousesStocks() { return readWarehousesStocks; }
    public void setReadWarehousesStocks(Integer readWarehousesStocks) { this.readWarehousesStocks = readWarehousesStocks; }

    public Integer getModifyWarehouses() { return modifyWarehouses; }
    public void setModifyWarehouses(Integer modifyWarehouses) { this.modifyWarehouses = modifyWarehouses; }

    public Integer getDeleteWarehouses() { return deleteWarehouses; }
    public void setDeleteWarehouses(Integer deleteWarehouses) { this.deleteWarehouses = deleteWarehouses; }

    public Integer getReadStockMovements() { return readStockMovements; }
    public void setReadStockMovements(Integer readStockMovements) { this.readStockMovements = readStockMovements; }

    public Integer getModifyStockMovements() { return modifyStockMovements; }
    public void setModifyStockMovements(Integer modifyStockMovements) { this.modifyStockMovements = modifyStockMovements; }

    public Integer getReadSendings() { return readSendings; }
    public void setReadSendings(Integer readSendings) { this.readSendings = readSendings; }

    public Integer getModifySendings() { return modifySendings; }
    public void setModifySendings(Integer modifySendings) { this.modifySendings = modifySendings; }

    public Integer getExportSendings() { return exportSendings; }
    public void setExportSendings(Integer exportSendings) { this.exportSendings = exportSendings; }

    public Integer getDeleteSendings() { return deleteSendings; }
    public void setDeleteSendings(Integer deleteSendings) { this.deleteSendings = deleteSendings; }

    public Integer getReadDeliveryReceipts() { return readDeliveryReceipts; }
    public void setReadDeliveryReceipts(Integer readDeliveryReceipts) { this.readDeliveryReceipts = readDeliveryReceipts; }

    public Integer getModifyDeliveryReceipts() { return modifyDeliveryReceipts; }
    public void setModifyDeliveryReceipts(Integer modifyDeliveryReceipts) { this.modifyDeliveryReceipts = modifyDeliveryReceipts; }

    public Integer getDeleteDeliveryReceipts() { return deleteDeliveryReceipts; }
    public void setDeleteDeliveryReceipts(Integer deleteDeliveryReceipts) { this.deleteDeliveryReceipts = deleteDeliveryReceipts; }

    public Integer getReadSkillsJobs() { return readSkillsJobs; }
    public void setReadSkillsJobs(Integer readSkillsJobs) { this.readSkillsJobs = readSkillsJobs; }

    public Integer getModifySkillsJobs() { return modifySkillsJobs; }
    public void setModifySkillsJobs(Integer modifySkillsJobs) { this.modifySkillsJobs = modifySkillsJobs; }

    public Integer getDeleteSkillsJobs() { return deleteSkillsJobs; }
    public void setDeleteSkillsJobs(Integer deleteSkillsJobs) { this.deleteSkillsJobs = deleteSkillsJobs; }

    public Integer getReadEvaluations() { return readEvaluations; }
    public void setReadEvaluations(Integer readEvaluations) { this.readEvaluations = readEvaluations; }

    public Integer getModifyEvaluations() { return modifyEvaluations; }
    public void setModifyEvaluations(Integer modifyEvaluations) { this.modifyEvaluations = modifyEvaluations; }

    public Integer getDeleteEvaluations() { return deleteEvaluations; }
    public void setDeleteEvaluations(Integer deleteEvaluations) { this.deleteEvaluations = deleteEvaluations; }

    public Integer getReadAllEvaluations() { return readAllEvaluations; }
    public void setReadAllEvaluations(Integer readAllEvaluations) { this.readAllEvaluations = readAllEvaluations; }

    public Integer getReadPersonalInfo() { return readPersonalInfo; }
    public void setReadPersonalInfo(Integer readPersonalInfo) { this.readPersonalInfo = readPersonalInfo; }

    public Integer getWritePersonalInfo() { return writePersonalInfo; }
    public void setWritePersonalInfo(Integer writePersonalInfo) { this.writePersonalInfo = writePersonalInfo; }

    public Integer getReadSalaries() { return readSalaries; }
    public void setReadSalaries(Integer readSalaries) { this.readSalaries = readSalaries; }

    public Integer getModifySalaries() { return modifySalaries; }
    public void setModifySalaries(Integer modifySalaries) { this.modifySalaries = modifySalaries; }

    public Integer getDeleteSalaries() { return deleteSalaries; }
    public void setDeleteSalaries(Integer deleteSalaries) { this.deleteSalaries = deleteSalaries; }

    public Integer getReadAllSalaries() { return readAllSalaries; }
    public void setReadAllSalaries(Integer readAllSalaries) { this.readAllSalaries = readAllSalaries; }

    public Integer getExportSalaries() { return exportSalaries; }
    public void setExportSalaries(Integer exportSalaries) { this.exportSalaries = exportSalaries; }

    public Integer getSeeTickets() { return seeTickets; }
    public void setSeeTickets(Integer seeTickets) { this.seeTickets = seeTickets; }

    public Integer getModifyTickets() { return modifyTickets; }
    public void setModifyTickets(Integer modifyTickets) { this.modifyTickets = modifyTickets; }

    public Integer getDeleteTickets() { return deleteTickets; }
    public void setDeleteTickets(Integer deleteTickets) { this.deleteTickets = deleteTickets; }

    public Integer getManageTickets() { return manageTickets; }
    public void setManageTickets(Integer manageTickets) { this.manageTickets = manageTickets; }

    public Integer getExportTickets() { return exportTickets; }
    public void setExportTickets(Integer exportTickets) { this.exportTickets = exportTickets; }

    public Integer getReadBom() { return readBom; }
    public void setReadBom(Integer readBom) { this.readBom = readBom; }

    public Integer getModifyBom() { return modifyBom; }
    public void setModifyBom(Integer modifyBom) { this.modifyBom = modifyBom; }

    public Integer getDeleteBom() { return deleteBom; }
    public void setDeleteBom(Integer deleteBom) { this.deleteBom = deleteBom; }

    public Integer getReadMo() { return readMo; }
    public void setReadMo(Integer readMo) { this.readMo = readMo; }

    public Integer getModifyMo() { return modifyMo; }
    public void setModifyMo(Integer modifyMo) { this.modifyMo = modifyMo; }

    public Integer getDeleteMo() { return deleteMo; }
    public void setDeleteMo(Integer deleteMo) { this.deleteMo = deleteMo; }

    public Integer getRunMassImports() { return runMassImports; }
    public void setRunMassImports(Integer runMassImports) { this.runMassImports = runMassImports; }

    public Integer getGetExportResult() { return getExportResult; }
    public void setGetExportResult(Integer getExportResult) { this.getExportResult = getExportResult; }

    public Integer getModifyExport() { return modifyExport; }
    public void setModifyExport(Integer modifyExport) { this.modifyExport = modifyExport; }

    public Integer getGenerateModules() { return generateModules; }
    public void setGenerateModules(Integer generateModules) { this.generateModules = generateModules; }
}
