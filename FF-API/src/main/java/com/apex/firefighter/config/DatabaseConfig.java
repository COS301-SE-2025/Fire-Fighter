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

    @Value("${DB_HOST:100.83.111.92}")
    private String dbHost;

    @Value("${DB_PORT:5432}")
    private String dbPort;

    @Value("${DB_NAME:firefighter}")
    private String dbName;

    @Value("${DB_USERNAME:ff_admin}")
    private String dbUsername;

    @Value("${DB_PASSWORD:dev_password}")
    private String dbPassword;

    @Value("${DB_SSL_MODE:require}")
    private String dbSslMode;

    @PostConstruct
    public void debugDatabaseConfig() {
        System.out.println("🔍 DATABASE CONFIGURATION:");
        System.out.println("   - DB_HOST: " + dbHost);
        System.out.println("   - DB_PORT: " + dbPort);
        System.out.println("   - DB_NAME: " + dbName);
        System.out.println("   - DB_USERNAME: " + dbUsername);
        System.out.println("   - DB_PASSWORD: " + (dbPassword != null ? "***SET*** (length: " + dbPassword.length() + ")" : "NULL"));
        System.out.println("   - DB_PASSWORD actual value: '" + dbPassword + "'");
        System.out.println("   - DB_SSL_MODE: " + dbSslMode);
        System.out.println("   - Should use H2: " + (dbPassword == null || dbPassword.trim().isEmpty() || "dev_password".equals(dbPassword)));
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println("🔍 Evaluating database configuration...");
        System.out.println("   - Password check: " + (dbPassword != null ? "SET" : "NULL"));
        System.out.println("   - Password actual value: '" + dbPassword + "'");
        System.out.println("   - Password equals dev_password: " + "dev_password".equals(dbPassword));
        
        // Force H2 for development - check for null, empty, or dev_password
        boolean useH2 = dbPassword == null || 
                       dbPassword.trim().isEmpty() || 
                       "dev_password".equals(dbPassword) ||
                       dbPassword.equals("${DB_PASSWORD:dev_password}"); // In case env var isn't resolved
        
        System.out.println("   - Use H2 decision: " + useH2);
        
        if (useH2) {
            System.out.println("🔧 Development Mode: Using H2 in-memory database");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:firefighterdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS FIREFIGHTER");
            config.setUsername("sa");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            System.out.println("✅ H2 Database initialized for development with FIREFIGHTER schema");
            return new HikariDataSource(config);
        }

        System.out.println("🔧 Production Mode: Attempting PostgreSQL connection");
        // Production PostgreSQL configuration
        HikariConfig config = new HikariConfig();
        
        // Build the JDBC URL
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=%s&sslcert=&sslkey=&sslrootcert=", 
                                     dbHost, dbPort, dbName, dbSslMode);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Connection pool settings
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
