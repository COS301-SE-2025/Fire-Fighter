# Health Monitoring System

This document explains how the health monitoring system works in the FireFighter frontend application.

## Overview

The health monitoring system automatically checks the backend service health and displays the service-down page when the service becomes unavailable. It integrates with the Spring Boot health endpoint (`/api/health`) to provide real-time service status.

## Components

### 1. HealthService (`src/app/services/health.service.ts`)
- Performs HTTP requests to the `/api/health` endpoint
- Provides continuous health monitoring with configurable intervals (default: 30 seconds)
- Exposes an observable stream of health status updates
- Handles error cases and provides user-friendly error messages

### 2. HealthMonitorService (`src/app/services/health-monitor.service.ts`)
- Global service that monitors health status changes
- Automatically navigates to `/service-down` when service becomes unhealthy
- Automatically navigates back to `/dashboard` when service recovers
- Prevents unnecessary navigation when already on the correct page

### 3. ServiceDownPage (`src/app/pages/service-down/service-down.page.ts`)
- Displays real-time service status based on health endpoint responses
- Shows detailed health information including service version, components, etc.
- Provides manual retry functionality using actual health checks
- Automatically redirects to dashboard when service recovers

## Health Endpoint Integration

The system integrates with the Spring Boot health endpoints:

### Basic Health Check
- **Endpoint**: `GET /api/health`
- **Response**: 
  ```json
  {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00",
    "service": "FireFighter Backend",
    "version": "1.0.0"
  }
  ```

### Detailed Health Check
- **Endpoint**: `GET /api/health/detailed`
- **Response**:
  ```json
  {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00",
    "service": "FireFighter Backend",
    "version": "1.0.0",
    "components": {
      "database": "UP",
      "authentication": "UP",
      "api": "UP"
    },
    "system": {
      "java.version": "17.0.1",
      "spring.profiles.active": "default"
    }
  }
  ```

## Status Indicators

The service-down page displays different status indicators:

- **🟢 Online**: Service is healthy and responding
- **🔴 Offline**: Service is not responding or returning errors
- **🟡 Checking**: Health check is in progress

## Automatic Behavior

1. **App Startup**: Health monitoring starts automatically when the app initializes
2. **Service Failure**: When health checks fail, users are automatically redirected to the service-down page
3. **Service Recovery**: When health checks succeed again, users are automatically redirected back to the dashboard
4. **Manual Retry**: Users can manually trigger health checks from the service-down page

## Configuration

Health monitoring can be configured in the `HealthService`:

```typescript
private monitoringInterval = 30000; // 30 seconds between health checks
```

## Error Handling

The system handles various error scenarios:

- **Connection Errors** (status 0): "Service Offline (Connection Failed)"
- **Not Found** (status 404): "Health endpoint not found"
- **Server Errors** (status 5xx): "Service Error"
- **Other Errors**: Uses error message from response or "Service Unavailable"

## Testing

To test the health monitoring system:

1. Start the frontend application
2. Stop the backend service
3. Observe automatic redirection to service-down page
4. Restart the backend service
5. Observe automatic redirection back to dashboard

## Integration with Existing Systems

The health monitoring integrates with:

- **HTTP Interceptor**: Basic error handling for API requests
- **Auth Service**: Connection error handling during authentication
- **Router**: Automatic navigation based on service status
