package com.apex.firefighter.unit.config;

import java.io.IOException;

import com.apex.firefighter.config.SwaggerAutoOpenConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwaggerAutoOpenConfigTest {

    private SwaggerAutoOpenConfig swaggerAutoOpenConfig;
    private ByteArrayOutputStream outputStreamCaptor;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        swaggerAutoOpenConfig = new SwaggerAutoOpenConfig();
        
        // Capture console output for testing
        outputStreamCaptor = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    void tearDown() {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // ==================== AUTO-OPEN ENABLED TESTS ====================

    @Test
    void openSwaggerUI_WithAutoOpenEnabled_ShouldAttemptToOpenBrowser() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
                doNothing().when(mockDesktop).browse(any(URI.class));

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                verify(mockDesktop).browse(any(URI.class));
                
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("🚀 FireFighter Platform Started Successfully!");
                assertThat(output).contains("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
                assertThat(output).contains("🤖 AI Chatbot: http://localhost:8080/api/chatbot/health");
                assertThat(output).contains("📧 Email Service: Configured and ready");
                assertThat(output).contains("🌐 Opening Swagger UI in your default browser...");
                assertThat(output).contains("✅ Browser opened successfully!");
            }
        } finally {
            tearDown();
        }
    }

    @Test
    void openSwaggerUI_WithCustomPort_ShouldUseCustomPort() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "9090");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
                doNothing().when(mockDesktop).browse(any(URI.class));

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                verify(mockDesktop).browse(argThat(uri -> 
                    uri.toString().equals("http://localhost:9090/swagger-ui.html")));
                
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("📚 Swagger UI: http://localhost:9090/swagger-ui.html");
                assertThat(output).contains("🤖 AI Chatbot: http://localhost:9090/api/chatbot/health");
            }
        } finally {
            tearDown();
        }
    }

    // ==================== AUTO-OPEN DISABLED TESTS ====================

    @Test
    void openSwaggerUI_WithAutoOpenDisabled_ShouldNotOpenBrowser() {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", false);

            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                mockedDesktop.verifyNoInteractions();
                
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("Swagger UI auto-open is disabled");
                assertThat(output).doesNotContain("🚀 FireFighter Platform Started Successfully!");
            }
        } finally {
            tearDown();
        }
    }

    // ==================== DESKTOP NOT SUPPORTED TESTS ====================

    @Test
    void openSwaggerUI_WithDesktopNotSupported_ShouldShowManualInstructions() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(false);

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("🚀 FireFighter Platform Started Successfully!");
                assertThat(output).contains("⚠️  Desktop browsing not supported on this system");
                assertThat(output).contains("📋 Please manually open: http://localhost:8080/swagger-ui.html");
            }
        } finally {
            tearDown();
        }
    }

    @Test
    void openSwaggerUI_WithBrowseActionNotSupported_ShouldShowManualInstructions() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(false);

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                verify(mockDesktop, never()).browse(any(URI.class));
                
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("🚀 FireFighter Platform Started Successfully!");
                assertThat(output).contains("⚠️  Desktop browsing not supported on this system");
                assertThat(output).contains("📋 Please manually open: http://localhost:8080/swagger-ui.html");
            }
        } finally {
            tearDown();
        }
    }

    // ==================== EXCEPTION HANDLING TESTS ====================

    @Test
    void openSwaggerUI_WithIOException_ShouldHandleGracefully() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
                doThrow(new IOException("Browser launch failed")).when(mockDesktop).browse(any(URI.class));

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                verify(mockDesktop).browse(any(URI.class));
                
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("❌ Failed to auto-open Swagger UI: Browser launch failed");
                assertThat(output).contains("📋 Please manually open: http://localhost:8080/swagger-ui.html");
            }
        } finally {
            tearDown();
        }
    }

    @Test
    void openSwaggerUI_WithURIException_ShouldHandleGracefully() {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "invalid:port");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("❌ Failed to auto-open Swagger UI:");
                assertThat(output).contains("📋 Please manually open: http://localhost:invalid:port/swagger-ui.html");
            }
        } finally {
            tearDown();
        }
    }

    // ==================== OUTPUT FORMAT TESTS ====================

    @Test
    void openSwaggerUI_ShouldDisplayCorrectStartupMessage() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
                doNothing().when(mockDesktop).browse(any(URI.class));

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                String output = outputStreamCaptor.toString();
                
                // Check for proper formatting
                assertThat(output).contains("=" + "=".repeat(59)); // 60 equals signs
                assertThat(output).contains("🚀 FireFighter Platform Started Successfully!");
                assertThat(output).contains("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
                assertThat(output).contains("🤖 AI Chatbot: http://localhost:8080/api/chatbot/health");
                assertThat(output).contains("📧 Email Service: Configured and ready");
                
                // Check that all required sections are present
                String[] lines = output.split("\n");
                boolean hasStartupBanner = false;
                boolean hasSwaggerUrl = false;
                boolean hasChatbotUrl = false;
                boolean hasEmailService = false;
                
                for (String line : lines) {
                    if (line.contains("🚀 FireFighter Platform Started Successfully!")) hasStartupBanner = true;
                    if (line.contains("📚 Swagger UI:")) hasSwaggerUrl = true;
                    if (line.contains("🤖 AI Chatbot:")) hasChatbotUrl = true;
                    if (line.contains("📧 Email Service:")) hasEmailService = true;
                }
                
                assertThat(hasStartupBanner).isTrue();
                assertThat(hasSwaggerUrl).isTrue();
                assertThat(hasChatbotUrl).isTrue();
                assertThat(hasEmailService).isTrue();
            }
        } finally {
            tearDown();
        }
    }

    // ==================== EDGE CASES AND BOUNDARY TESTS ====================

    @Test
    void openSwaggerUI_WithNullPort_ShouldHandleGracefully() {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", null);
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("❌ Failed to auto-open Swagger UI:");
                assertThat(output).contains("📋 Please manually open: http://localhost:null/swagger-ui.html");
            }
        } finally {
            tearDown();
        }
    }

    @Test
    void openSwaggerUI_WithEmptyPort_ShouldUseEmptyPort() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
                doNothing().when(mockDesktop).browse(any(URI.class));

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert
                String output = outputStreamCaptor.toString();
                assertThat(output).contains("📚 Swagger UI: http://localhost:/swagger-ui.html");
                assertThat(output).contains("🤖 AI Chatbot: http://localhost:/api/chatbot/health");
            }
        } finally {
            tearDown();
        }
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    void openSwaggerUI_FullSuccessWorkflow_ShouldCompleteAllSteps() throws IOException {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            Desktop mockDesktop = mock(Desktop.class);
            
            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(true);
                mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
                when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
                doNothing().when(mockDesktop).browse(any(URI.class));

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert - Verify complete workflow
                verify(mockDesktop).browse(argThat(uri -> 
                    uri.toString().equals("http://localhost:8080/swagger-ui.html")));
                
                String output = outputStreamCaptor.toString();
                
                // Verify startup banner
                assertThat(output).contains("🚀 FireFighter Platform Started Successfully!");
                
                // Verify all service URLs
                assertThat(output).contains("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
                assertThat(output).contains("🤖 AI Chatbot: http://localhost:8080/api/chatbot/health");
                assertThat(output).contains("📧 Email Service: Configured and ready");
                
                // Verify browser opening messages
                assertThat(output).contains("🌐 Opening Swagger UI in your default browser...");
                assertThat(output).contains("✅ Browser opened successfully!");
                
                // Verify no error messages
                assertThat(output).doesNotContain("❌");
                assertThat(output).doesNotContain("⚠️");
            }
        } finally {
            tearDown();
        }
    }

    @Test
    void openSwaggerUI_FullFailureWorkflow_ShouldHandleGracefully() {
        try {
            // Arrange
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "serverPort", "8080");
            ReflectionTestUtils.setField(swaggerAutoOpenConfig, "autoOpenEnabled", true);

            try (MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {
                mockedDesktop.when(Desktop::isDesktopSupported).thenReturn(false);

                // Act
                swaggerAutoOpenConfig.openSwaggerUI();

                // Assert - Verify graceful failure handling
                String output = outputStreamCaptor.toString();
                
                // Verify startup banner still shows
                assertThat(output).contains("🚀 FireFighter Platform Started Successfully!");
                
                // Verify all service URLs still show
                assertThat(output).contains("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
                assertThat(output).contains("🤖 AI Chatbot: http://localhost:8080/api/chatbot/health");
                assertThat(output).contains("📧 Email Service: Configured and ready");
                
                // Verify appropriate warning and fallback
                assertThat(output).contains("⚠️  Desktop browsing not supported on this system");
                assertThat(output).contains("📋 Please manually open: http://localhost:8080/swagger-ui.html");
                
                // Verify no success messages
                assertThat(output).doesNotContain("🌐 Opening Swagger UI in your default browser...");
                assertThat(output).doesNotContain("✅ Browser opened successfully!");
            }
        } finally {
            tearDown();
        }
    }
}
