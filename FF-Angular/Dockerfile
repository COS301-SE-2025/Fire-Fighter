# Multi-stage build for Angular application
# Stage 1: Build the Angular app
FROM node:18-alpine AS build

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Build the application for production
RUN npm run build

# Stage 2: Serve the app with nginx
FROM nginx:alpine

# Copy the built app from the previous stage
COPY --from=build /app/www /usr/share/nginx/html

# Copy custom nginx configuration (optional)
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
