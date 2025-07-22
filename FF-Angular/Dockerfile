# Development Dockerfile for Angular/Ionic application
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Install both Angular CLI and Ionic CLI globally
RUN npm install -g @angular/cli @ionic/cli

# Copy package files first for better caching
COPY package*.json ./

# Copy ionic configuration files
COPY ionic.config.json ./
COPY angular.json ./
COPY tsconfig.json ./

# Install dependencies (including dev dependencies)
RUN npm ci

# Copy all source code
COPY . .

# Debug: List files to verify structure
RUN ls -la && echo "=== Checking ionic.config.json ===" && cat ionic.config.json

# Initialize ionic if needed and verify project
RUN ionic info || (echo "Ionic info failed, trying to fix..." && ionic init --type=angular-standalone --name="FireFighter" --force)

# Expose port 8100 (Ionic serve port)
EXPOSE 8100

# Start the development server using Ionic CLI
CMD ["ionic", "serve", "--host", "0.0.0.0", "--port", "8100", "--no-open", "--external"]
