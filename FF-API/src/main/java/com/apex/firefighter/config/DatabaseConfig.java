package com.apex.firefighter.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;

@Configuration
public class DatabaseConfig {

    @Value("${DB_HOST:100.83.111.92}")
    private String dbHost;

    @Value("${DB_PORT:5432}")
    private String dbPort;

    @Value("${DB_NAME:firefighter}")
    private String dbName;

    @Value("${DB_USERNAME:ff_admin}")
    private String dbUsername;

    @Value("${DB_PASSWORD}")
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
        System.out.println("   - DB_SSL_MODE: " + dbSslMode);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            throw new RuntimeException("DB_PASSWORD environment variable is required but not set!");
        }

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
        
        System.out.println("✅ Creating DataSource with URL: " + jdbcUrl);
        System.out.println("✅ Using username: " + dbUsername);
        
        return new HikariDataSource(config);
    }
}
