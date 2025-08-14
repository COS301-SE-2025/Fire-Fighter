# 🔥 FireFighter API - Docker Deployment

This repository contains the Docker deployment configuration for the FireFighter API service.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- PostgreSQL database running and accessible
- Required environment variables (see `.env.example`)

### Local Development
```bash
# Clone the repository
git clone <your-repo-url>
cd ff-api-docker

# Copy environment template
cp .env.example .env

# Edit .env with your actual values
nano .env

# Build and run
docker-compose up --build
```

### Portainer Deployment

1. **Create New Stack** in Portainer
2. **Repository Settings**:
   - Repository URL: `<your-repo-url>`
   - Branch: `main` (or your default branch)
   - Compose Path: `docker-compose.yml`

3. **Environment Variables** (set in Portainer):
   ```
   DB_HOST=your-db-host
   DB_PORT=5432
   DB_NAME=firefighter
   DB_USERNAME=ff_admin
   DB_PASSWORD=your-secure-password
   JWT_SECRET=your-jwt-secret
   GMAIL_USERNAME=your-email@gmail.com
   GMAIL_APP_PASSWORD=your-app-password
   GOOGLE_GEMINI_API_KEY=your-api-key
   ```

4. **Deploy Stack**

## 🔧 Configuration

### Environment Variables
See `.env.example` for all required environment variables.

### Health Check
The application includes a health check endpoint:
- URL: `http://localhost:8081/actuator/health`
- Interval: 30 seconds
- Timeout: 3 seconds
- Start Period: 60 seconds

### API Documentation
Once deployed, access Swagger UI at:
`http://localhost:8081/swagger-ui/index.html`

## 🐳 Docker Details

- **Base Image**: OpenJDK 17 JDK Slim
- **Build Tool**: Maven 3.9
- **Port**: 8080 (internal), 8081 (external)
- **User**: Non-root user for security
- **Health Check**: Built-in Spring Boot Actuator

## 🔍 Troubleshooting

### Check Logs
```bash
docker logs firefighter-api
```

### Test Health
```bash
curl http://localhost:8081/actuator/health
```

### Database Connection
Ensure your database is accessible from the Docker container and credentials are correct.

## 📝 Notes

- The application runs as a non-root user for security
- JVM is configured with memory limits (512MB max, 256MB initial)
- Build uses multi-stage approach for smaller final image
- Dependencies are cached for faster subsequent builds
