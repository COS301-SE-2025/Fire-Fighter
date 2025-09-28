package com.apex.firefighter.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;

@Configuration
@Profile("!test") // Only activate this configuration when NOT in test profile
public class DatabaseConfig {

    @Value("${DB_HOST}")
    private String dbHost;

    @Value("${DB_PORT}")
    private String dbPort;

    @Value("${DB_NAME}")
    private String dbName;

    @Value("${DB_USERNAME}")
    private String dbUsername;

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    @Value("${DB_SSL_MODE}")
    private String dbSslMode;
    
    @Value("${FORCE_H2_DB:false}")
    private boolean forceH2Database;

    @PostConstruct
    public void debugDatabaseConfig() {
        System.out.println("🔍 DATABASE CONFIGURATION:");
        System.out.println("   - DB_HOST: " + dbHost);
        System.out.println("   - DB_PORT: " + dbPort);
        System.out.println("   - DB_NAME: " + dbName);
        System.out.println("   - DB_USERNAME: " + dbUsername);
        System.out.println("   - DB_PASSWORD: " + (dbPassword != null && !dbPassword.trim().isEmpty() ? "***CONFIGURED***" : "NOT_SET"));
        System.out.println("   - DB_SSL_MODE: " + dbSslMode);
        System.out.println("   - FORCE_H2_DB: " + forceH2Database);
        System.out.println("   - Will use H2: " + shouldUseH2Database());

        // Check if DB_PASSWORD environment variable exists but is empty
        String envDbPassword = System.getenv("DB_PASSWORD");
        if (envDbPassword != null && envDbPassword.trim().isEmpty()) {
            System.out.println("⚠️  WARNING: DB_PASSWORD environment variable is set but empty!");
        } else if (envDbPassword == null) {
            System.out.println("ℹ️  INFO: DB_PASSWORD environment variable not found in environment");
        }
    }
    
    /**
     * Determines whether to use H2 database based on configuration
     * Uses H2 if:
     * - FORCE_H2_DB is explicitly set to true
     * - DB_PASSWORD is not provided or empty (fallback to H2 for development)
     */
    private boolean shouldUseH2Database() {
        // If FORCE_H2_DB is explicitly set to true, use H2
        if (forceH2Database) {
            return true;
        }

        // If no password is provided, fallback to H2 for development
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            System.out.println("⚠️  No PostgreSQL password provided, falling back to H2 for development");
            return true;
        }

        // Otherwise use PostgreSQL
        return false;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println("🔍 Evaluating database configuration...");
        
        if (shouldUseH2Database()) {
            System.out.println("🔧 Development Mode: Using H2 in-memory database");
            return createH2DataSource();
        } else {
            System.out.println("🔧 Production Mode: Using PostgreSQL database");
            return createPostgreSQLDataSource();
        }
    }
    
    /**
     * Creates H2 in-memory database datasource for development
     */
    private DataSource createH2DataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:firefighterdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS FIREFIGHTER");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        
        // Connection pool settings for development
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        System.out.println("✅ H2 Database initialized for development with FIREFIGHTER schema");
        return new HikariDataSource(config);
    }
    
    /**
     * Creates PostgreSQL database datasource for production
     */
    private DataSource createPostgreSQLDataSource() {
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            String envDbPassword = System.getenv("DB_PASSWORD");
            String errorMsg = "❌ DB_PASSWORD is required for PostgreSQL connection but was not provided!\n" +
                            "   Environment variable DB_PASSWORD: " + (envDbPassword == null ? "NOT_FOUND" : "EMPTY") + "\n" +
                            "   Injected value: " + (dbPassword == null ? "NULL" : "EMPTY_STRING") + "\n" +
                            "   Suggestion: Ensure DB_PASSWORD is properly set in Jenkins credentials and injected into the environment.";
            throw new IllegalStateException(errorMsg);
        }
        
        HikariConfig config = new HikariConfig();
        
        // Build the JDBC URL
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=%s&sslcert=&sslkey=&sslrootcert=", 
                                     dbHost, dbPort, dbName, dbSslMode);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Connection pool settings for production
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        System.out.println("✅ Creating PostgreSQL DataSource with URL: " + jdbcUrl);
        System.out.println("✅ Using username: " + dbUsername);
        
        return new HikariDataSource(config);
    }
}
