# Development Dockerfile for Angular/Ionic application
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Install Ionic CLI globally
RUN npm install -g @ionic/cli

# Copy package files
COPY package*.json ./

# Install dependencies (including dev dependencies)
RUN npm ci

# Copy source code
COPY . .

# Expose port 8100 (default Ionic serve port)
EXPOSE 8100

# Start the development server
CMD ["ionic", "serve", "--host", "0.0.0.0", "--port", "8100", "--no-open"]
