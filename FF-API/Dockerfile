# Multi-stage build for FF-API
# Stage 1: Build the application
FROM maven:3.9.4-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy Maven configuration files first (for better layer caching)
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests=true

# Stage 2: Runtime image
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/firefighter-platform-0.0.1-SNAPSHOT.jar app.jar

# Create a non-root user for security
RUN groupadd -r firefighter && useradd -r -g firefighter firefighter
RUN chown -R firefighter:firefighter /app
USER firefighter

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with optimized JVM settings
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
