# Environment Variables Migration Guide

## Overview
Database connection details and sensitive configuration have been moved from application properties files to environment variables for better security.

## Changes Made

### 1. Created Environment Files
- **`.env.example`** - Template file with all required environment variables
- **`.env`** - Actual environment file with current values (already existed, updated with database config)

### 2. Updated Application Properties
The following files were updated to use environment variables:

#### `application.properties`
- Database URL, username, password now use `${DB_HOST}`, `${DB_USERNAME}`, `${DB_PASSWORD}` etc.
- JWT secret now uses `${JWT_SECRET}`
- Gmail configuration already used environment variables

#### `application-dev.properties`
- Database configuration updated to use environment variables
- SSL mode configurable via `${DB_SSL_MODE}`

### 3. Removed SSL/HTTPS Configuration
- Removed all SSL certificate references
- Updated to HTTP-only configuration
- Updated CORS settings to remove HTTPS origins
- Updated Postman collection to use HTTP by default

## Environment Variables

### Required Variables
```bash
# Database Configuration
DB_HOST=100.83.111.92
DB_PORT=5432
DB_NAME=firefighter
DB_USERNAME=ff_admin
DB_PASSWORD=your_database_password

# JWT Configuration
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=3600000

# Gmail SMTP (for email functionality)
GMAIL_USERNAME=your-email@gmail.com
GMAIL_APP_PASSWORD=your-app-password

# Google Gemini AI (for chatbot)
GOOGLE_GEMINI_API_KEY=your-api-key
```

### Optional Variables
```bash
# Database SSL Mode (default: require)
DB_SSL_MODE=require

# Server Configuration (defaults provided)
SERVER_PORT=8080
SERVER_ADDRESS=0.0.0.0

# Gmail Sender Name
GMAIL_SENDER_NAME=FireFighter Platform
```

## Security Benefits

1. **No Hardcoded Credentials** - Database passwords and API keys are no longer in source code
2. **Environment-Specific Config** - Different environments can use different values
3. **Git Ignored** - `.env` file is in `.gitignore` to prevent accidental commits
4. **Easy Deployment** - Environment variables can be set in deployment environments

## Usage

### Development
1. Copy `.env.example` to `.env`
2. Fill in your actual values in `.env`
3. Run the application normally

### Production
Set environment variables in your deployment environment:
```bash
export DB_PASSWORD=your_production_password
export JWT_SECRET=your_production_jwt_secret
# etc.
```

## Fallback Values
The application includes fallback values for most settings, so it will still work if some environment variables are not set (except for sensitive ones like `DB_PASSWORD`).

## Files Modified
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-test.properties`
- `src/test/resources/application-test.properties`
- `src/main/java/com/apex/firefighter/config/HttpConfig.java` (renamed from HttpsConfig.java)
- `src/main/java/com/apex/firefighter/config/SwaggerAutoOpenConfig.java`
- `src/main/java/com/apex/firefighter/config/SecurityConfig.java`
- `src/main/java/com/apex/firefighter/controller/HealthController.java`
- `postman/FireFighter_Chatbot_Collection.json`
- `postman/SETUP_GUIDE.md`
- `.env` (updated)
- `.env.example` (created)
