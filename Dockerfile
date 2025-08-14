# Multi-stage build for FireFighter Spring Boot API
# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

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
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks and timezone data
RUN apk add --no-cache curl tzdata

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/firefighter-platform-0.0.1-SNAPSHOT.jar app.jar

# Create a non-root user for security
RUN addgroup -g 1001 -S firefighter && \
    adduser -S firefighter -u 1001 -G firefighter

# Change ownership of the app directory
RUN chown -R firefighter:firefighter /app

# Switch to non-root user
USER firefighter

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with optimized JVM settings for containers
ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Xms256m", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
