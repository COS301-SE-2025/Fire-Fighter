package com.apex.firefighter.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsConfig {

    @Value("${http.port:8080}")
    private int httpPort;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    /**
     * Configure dual HTTP/HTTPS support when SSL is enabled
     * This allows the server to accept requests on both HTTP (8080) and HTTPS (8443) ports
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpsServletContainer() {
        return server -> {
            if (sslEnabled) {
                // Add HTTP connector when HTTPS is enabled
                // This allows both HTTP and HTTPS to work simultaneously
                Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                connector.setScheme("http");
                connector.setPort(httpPort);
                connector.setSecure(false);
                server.addAdditionalTomcatConnectors(connector);
                
                System.out.println("🔒 HTTPS Configuration:");
                System.out.println("   ✅ HTTP available on port: " + httpPort);
                System.out.println("   ✅ HTTPS available on port: 8443");
                System.out.println("   📋 Both protocols are supported simultaneously");
            } else {
                System.out.println("🌐 HTTP Configuration:");
                System.out.println("   ✅ HTTP available on port: " + httpPort);
                System.out.println("   ⚠️  HTTPS not configured (SSL disabled)");
                System.out.println("   💡 To enable HTTPS, configure SSL certificates in application.properties");
            }
        };
    }
} 