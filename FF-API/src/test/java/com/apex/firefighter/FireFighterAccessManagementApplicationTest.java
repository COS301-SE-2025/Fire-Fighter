package com.apex.firefighter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FireFighterAccessManagementApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
        // If the context fails to load, this test will fail
        assertThat(true).isTrue();
    }

    @Test
    void main_ShouldPrintStartupMessages() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Act
            FireFighterAccessManagementApplication.main(new String[]{});

            // Assert
            String output = outputStream.toString();
            assertThat(output).contains("🚒 STARTING FIREFIGHTER PLATFORM");
            assertThat(output).contains("🔧 Loading configurations...");
            assertThat(output).contains("📧 Initializing email service...");
            assertThat(output).contains("🤖 Setting up AI chatbot...");
            assertThat(output).contains("📚 Preparing Swagger documentation...");
            assertThat(output).contains("=".repeat(70));

            // Verify SpringApplication.run was called
            springApplicationMock.verify(() -> 
                SpringApplication.run(FireFighterAccessManagementApplication.class, new String[]{})
            );
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void main_WithArguments_ShouldPassArgumentsToSpringApplication() {
        // Arrange
        String[] args = {"--server.port=8081", "--spring.profiles.active=test"};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Act
            FireFighterAccessManagementApplication.main(args);

            // Assert
            springApplicationMock.verify(() -> 
                SpringApplication.run(FireFighterAccessManagementApplication.class, args)
            );
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void main_WithEmptyArguments_ShouldWork() {
        // Arrange
        String[] args = {};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Act
            FireFighterAccessManagementApplication.main(args);

            // Assert
            springApplicationMock.verify(() -> 
                SpringApplication.run(FireFighterAccessManagementApplication.class, args)
            );
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void main_WithNullArguments_ShouldWork() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Act
            FireFighterAccessManagementApplication.main(null);

            // Assert
            springApplicationMock.verify(() -> 
                SpringApplication.run(FireFighterAccessManagementApplication.class, (String[]) null)
            );
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void applicationClass_ShouldHaveCorrectAnnotations() {
        // Assert
        assertThat(FireFighterAccessManagementApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class)).isTrue();
        assertThat(FireFighterAccessManagementApplication.class.isAnnotationPresent(
            org.springframework.scheduling.annotation.EnableScheduling.class)).isTrue();
    }

    @Test
    void startupMessages_ShouldContainAllExpectedElements() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Act
            FireFighterAccessManagementApplication.main(new String[]{});

            // Assert
            String output = outputStream.toString();
            
            // Check for specific startup messages
            assertThat(output).contains("STARTING FIREFIGHTER PLATFORM");
            assertThat(output).contains("Loading configurations");
            assertThat(output).contains("Initializing email service");
            assertThat(output).contains("Setting up AI chatbot");
            assertThat(output).contains("Preparing Swagger documentation");
            
            // Check for emojis
            assertThat(output).contains("🚒");
            assertThat(output).contains("🔧");
            assertThat(output).contains("📧");
            assertThat(output).contains("🤖");
            assertThat(output).contains("📚");
            
            // Check for formatting
            assertThat(output).contains("=".repeat(70));
            assertThat(output).contains("\n");
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void main_ShouldPrintMessagesInCorrectOrder() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Act
            FireFighterAccessManagementApplication.main(new String[]{});

            // Assert
            String output = outputStream.toString();
            String[] lines = output.split("\n");
            
            // Find the indices of key messages
            int platformIndex = -1;
            int configIndex = -1;
            int emailIndex = -1;
            int aiIndex = -1;
            int swaggerIndex = -1;
            
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains("STARTING FIREFIGHTER PLATFORM")) {
                    platformIndex = i;
                } else if (lines[i].contains("Loading configurations")) {
                    configIndex = i;
                } else if (lines[i].contains("Initializing email service")) {
                    emailIndex = i;
                } else if (lines[i].contains("Setting up AI chatbot")) {
                    aiIndex = i;
                } else if (lines[i].contains("Preparing Swagger documentation")) {
                    swaggerIndex = i;
                }
            }
            
            // Verify order
            assertThat(platformIndex).isGreaterThan(-1);
            assertThat(configIndex).isGreaterThan(platformIndex);
            assertThat(emailIndex).isGreaterThan(configIndex);
            assertThat(aiIndex).isGreaterThan(emailIndex);
            assertThat(swaggerIndex).isGreaterThan(aiIndex);
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void applicationClass_ShouldBePublic() {
        // Assert
        assertThat(java.lang.reflect.Modifier.isPublic(
            FireFighterAccessManagementApplication.class.getModifiers())).isTrue();
    }

    @Test
    void mainMethod_ShouldBePublicStatic() throws NoSuchMethodException {
        // Act
        java.lang.reflect.Method mainMethod = FireFighterAccessManagementApplication.class
            .getMethod("main", String[].class);

        // Assert
        assertThat(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())).isTrue();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
    }
}
