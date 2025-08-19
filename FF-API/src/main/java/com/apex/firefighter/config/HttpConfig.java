package com.apex.firefighter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    /**
     * Configure HTTP-only server
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpServletContainer() {
        return server -> {
            System.out.println("🌐 HTTP Configuration:");
            System.out.println("   ✅ HTTP available on port: " + serverPort);
            System.out.println("   📋 HTTP-only mode (SSL disabled)");
        };
    }
}