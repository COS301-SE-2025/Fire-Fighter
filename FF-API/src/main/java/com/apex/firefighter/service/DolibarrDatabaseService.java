package com.apex.firefighter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Service for direct database operations on the Dolibarr database.
 * This service is used for operations that require direct database access
 * rather than going through the Dolibarr API.
 */
@Service
public class DolibarrDatabaseService {
    
    private final DataSource dolibarrDataSource;

    public DolibarrDatabaseService(
            @Value("${DB_HOST}") String dbHost,
            @Value("${DB_PORT}") String dbPort,
            @Value("${DOLIBARR_DB_NAME}") String dolibarrDbName,
            @Value("${DB_USERNAME}") String dbUsername,
            @Value("${DB_PASSWORD}") String dbPassword,
            @Value("${DB_SSL_MODE}") String dbSslMode) {

        this.dolibarrDataSource = createDolibarrDataSource(dbHost, dbPort, dolibarrDbName, dbUsername, dbPassword, dbSslMode);

        System.out.println("✅ DolibarrDatabaseService initialized.");
    }

    /**
     * Creates a dedicated DataSource for the Dolibarr database
     */
    protected DataSource createDolibarrDataSource(String dbHost, String dbPort, String dolibarrDbName, 
                                               String dbUsername, String dbPassword, String dbSslMode) {
        
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            String envDbPassword = System.getenv("DB_PASSWORD");
            String errorMsg = "❌ DB_PASSWORD is required for Dolibarr database connection but was not provided!\n" +
                            "   Environment variable DB_PASSWORD: " + (envDbPassword == null ? "NOT_FOUND" : "EMPTY") + "\n" +
                            "   Injected value: " + (dbPassword == null ? "NULL" : "EMPTY_STRING");
            throw new IllegalStateException(errorMsg);
        }
        
        HikariConfig config = new HikariConfig();
        
        // Build the JDBC URL for Dolibarr database
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=%s&sslcert=&sslkey=&sslrootcert=", 
                                     dbHost, dbPort, dolibarrDbName, dbSslMode);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Connection pool settings - smaller pool for this specific service
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        System.out.println("✅ Creating Dolibarr PostgreSQL DataSource with URL: " + jdbcUrl);
        System.out.println("✅ Using username: " + dbUsername);
        
        return new HikariDataSource(config);
    }

    /**
     * Removes a user from the firefighter group by directly deleting the entry
     * from the llx_usergroup_user table in the Dolibarr database.
     * 
     * This method only removes the specific user-group association for the firefighter
     * group and does not modify any other user group memberships.
     * 
     * @param dolibarrUserId The Dolibarr user ID (fk_user in the table)
     * @throws SQLException if database operation fails
     */
    public void removeUserFromFirefighterGroup(String dolibarrUserId, Integer firefighterGroupId) throws SQLException {
        String sql = "DELETE FROM llx_usergroup_user WHERE fk_user = ? AND fk_usergroup = ?";
        
        try (Connection connection = dolibarrDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            // Convert string IDs to integers for the database
            int userId = Integer.parseInt(dolibarrUserId);
            //int groupId = Integer.parseInt(firefighterGroupId);
            
            statement.setInt(1, userId);
            //statement.setInt(2, groupId);
            statement.setInt(2, firefighterGroupId);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ DOLIBARR DB: Successfully removed user " + dolibarrUserId + 
                                 " from firefighter group " + firefighterGroupId + 
                                 " (" + rowsAffected + " row(s) deleted)");
            } else {
                System.out.println("ℹ️ DOLIBARR DB: No rows affected - user " + dolibarrUserId + 
                                 " was not in firefighter group " + firefighterGroupId + 
                                 " or association did not exist");
            }
            
        } catch (NumberFormatException e) {
            String errorMsg = "❌ DOLIBARR DB: Invalid ID format - dolibarrUserId: " + dolibarrUserId + 
                            ", firefighterGroupId: " + firefighterGroupId;
            System.err.println(errorMsg);
            throw new IllegalArgumentException(errorMsg, e);
        } catch (SQLException e) {
            String errorMsg = "❌ DOLIBARR DB: Failed to remove user " + dolibarrUserId + 
                            " from firefighter group " + firefighterGroupId + ": " + e.getMessage();
            System.err.println(errorMsg);
            throw e;
        }
    }

    /**
     * Adds a user to the firefighter group by directly inserting an entry
     * into the llx_usergroup_user table in the Dolibarr database.
     *
     * This method only adds the specific user-group association for the firefighter
     * group and does not modify any other user group memberships.
     * The entity ID is set to 1 as requested.
     *
     * @param dolibarrUserId The Dolibarr user ID (fk_user in the table)
     * @throws SQLException if database operation fails
     */
    public void addUserToFirefighterGroup(String dolibarrUserId, Integer firefighterGroupId) throws SQLException {
        // First check if the user is already in the group
        String checkSql = "SELECT COUNT(*) FROM llx_usergroup_user WHERE fk_user = ? AND fk_usergroup = ?";
        String insertSql = "INSERT INTO llx_usergroup_user (entity, fk_user, fk_usergroup) VALUES (?, ?, ?)";

        try (Connection connection = dolibarrDataSource.getConnection()) {

            // Convert string IDs to integers for the database
            int userId = Integer.parseInt(dolibarrUserId);
            //int groupId = Integer.parseInt(firefighterGroupId);

            // Check if user is already in the group
            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                checkStatement.setInt(1, userId);
                checkStatement.setInt(2, firefighterGroupId);

                var resultSet = checkStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("ℹ️ DOLIBARR DB: User " + dolibarrUserId +
                                     " is already in firefighter group " + firefighterGroupId +
                                     " (duplicate entry ignored)");
                    return;
                }
            }

            // Insert the new user-group association
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setInt(1, 1); // entity = 1 as requested
                insertStatement.setInt(2, userId);
                //insertStatement.setInt(3, groupId);
                insertStatement.setInt(3, firefighterGroupId);

                int rowsAffected = insertStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("✅ DOLIBARR DB: Successfully added user " + dolibarrUserId +
                                     " to firefighter group " + firefighterGroupId +
                                     " with entity=1 (" + rowsAffected + " row(s) inserted)");
                } else {
                    System.out.println("⚠️ DOLIBARR DB: No rows affected when adding user " + dolibarrUserId +
                                     " to firefighter group " + firefighterGroupId);
                }
            }

        } catch (NumberFormatException e) {
            String errorMsg = "❌ DOLIBARR DB: Invalid ID format for add operation - dolibarrUserId: " + dolibarrUserId +
                            ", firefighterGroupId: " + firefighterGroupId;
            System.err.println(errorMsg);
            throw new IllegalArgumentException(errorMsg, e);
        } catch (SQLException e) {
            String errorMsg = "❌ DOLIBARR DB: Failed to add user " + dolibarrUserId +
                            " to firefighter group " + firefighterGroupId + ": " + e.getMessage();
            System.err.println(errorMsg);
            throw e;
        }
    }

    /**
     * Test method to verify database connectivity
     */
    public boolean testConnection() {
        try (Connection connection = dolibarrDataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            System.err.println("❌ DOLIBARR DB: Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
