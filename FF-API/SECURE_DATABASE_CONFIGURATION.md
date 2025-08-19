# Secure Database Configuration Guide

This guide explains how to securely configure the database connection for the FireFighter API.

## Overview

The `DatabaseConfig.java` class now supports secure database configuration using environment variables instead of hardcoded passwords. It automatically switches between H2 (for development) and PostgreSQL (for production) based on configuration.

## Security Improvements Made

### 1. Removed Hardcoded Passwords
- ❌ **Before**: Passwords were hardcoded in the Java source code
- ✅ **After**: All sensitive data is loaded from environment variables

### 2. Eliminated Password Logging
- ❌ **Before**: Actual passwords were printed to console logs
- ✅ **After**: Only shows if password is configured (***CONFIGURED*** vs NOT_SET)

### 3. Environment-Based Configuration
- Uses `FORCE_H2_DB` flag to control database type
- Requires `DB_PASSWORD` to be set for PostgreSQL connections
- Fails fast if PostgreSQL is attempted without proper credentials

## Configuration Options

### Development Mode (H2 Database)
```properties
FORCE_H2_DB=true
# OR leave DB_PASSWORD empty
DB_PASSWORD=
```

### Production Mode (PostgreSQL)
```properties
FORCE_H2_DB=false
DB_HOST=your-database-host
DB_PORT=5432
DB_NAME=firefighter
DB_USERNAME=your-username
DB_PASSWORD=your-secure-password
DB_SSL_MODE=require
```

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_HOST` | Production | `100.83.111.92` | Database host address |
| `DB_PORT` | No | `5432` | Database port |
| `DB_NAME` | No | `firefighter` | Database name |
| `DB_USERNAME` | Production | `ff_admin` | Database username |
| `DB_PASSWORD` | Production | *(empty)* | Database password |
| `DB_SSL_MODE` | No | `require` | SSL mode for PostgreSQL |
| `FORCE_H2_DB` | No | `true` | Force H2 usage in development |

## Security Best Practices

### 1. Use Environment Variables
```bash
# Set environment variables
export DB_PASSWORD="your-secure-password"
export JWT_SECRET="your-256-bit-secret"
```

### 2. Use .env Files (Local Development)
Create a `.env` file in the FF-API directory:
```properties
DB_PASSWORD=your-local-dev-password
JWT_SECRET=your-local-dev-secret
```

### 3. Production Deployment
For production deployments:
1. Copy `application-prod.properties.template` to `application-prod.properties`
2. Fill in actual values
3. Ensure the file is not committed to version control
4. Set the active profile: `spring.profiles.active=prod`

### 4. CI/CD Pipelines
Use secure environment variable injection:
```yaml
# Example for GitHub Actions
env:
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
  JWT_SECRET: ${{ secrets.JWT_SECRET }}
```

## Database Selection Logic

The system uses H2 if:
- `FORCE_H2_DB` is `true` (default for development)
- `DB_PASSWORD` is not provided or empty

The system uses PostgreSQL if:
- `FORCE_H2_DB` is `false` AND
- `DB_PASSWORD` is provided and not empty

## Files to Keep Secure

Never commit these files to version control:
- `.env`
- `application-prod.properties`
- `firebase-service-account.json`
- Any file containing actual passwords or API keys

## Migration from Old Configuration

If you're migrating from the old configuration:

1. Remove any hardcoded passwords from code
2. Set environment variables for your deployment
3. Update your deployment scripts to use the new environment variables
4. Test in a development environment first

## Troubleshooting

### Common Issues

1. **"DB_PASSWORD is required" Error**
   - Ensure `DB_PASSWORD` environment variable is set for production
   - Or set `FORCE_H2_DB=true` for development

2. **Database Connection Fails**
   - Check that all database environment variables are correctly set
   - Verify database host is accessible
   - Ensure SSL mode matches your database configuration

3. **Wrong Database Used**
   - Check the `FORCE_H2_DB` setting
   - Verify `DB_PASSWORD` is set if you want PostgreSQL

### Debug Information

The application logs database configuration on startup:
```
🔍 DATABASE CONFIGURATION:
   - DB_HOST: your-host
   - DB_PORT: 5432
   - DB_NAME: firefighter
   - DB_USERNAME: your-username
   - DB_PASSWORD: ***CONFIGURED***
   - DB_SSL_MODE: require
   - FORCE_H2_DB: false
   - Will use H2: false
```

Look for these logs to verify your configuration is correct.
