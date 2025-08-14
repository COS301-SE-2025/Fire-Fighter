# 🚀 Jenkins Pipeline Setup Guide for Fire-Fighter Project

## 📋 Overview

This guide helps you set up Jenkins CI/CD pipeline for the Fire-Fighter monorepo project containing:
- **FF-API**: Spring Boot backend (Java 17, Maven)
- **FF-Angular**: Angular/Ionic frontend with Capacitor mobile support

## 🛠️ Prerequisites

### Jenkins Server Requirements
- Jenkins 2.400+ with Pipeline plugin
- Minimum 4GB RAM, 2 CPU cores
- 20GB+ disk space for builds and artifacts

### Required Jenkins Plugins
Install these plugins via Jenkins Plugin Manager:

```
- Pipeline
- Git
- Maven Integration
- NodeJS
- HTML Publisher
- JUnit
- Jacoco
- OWASP Dependency Check
- Credentials Binding
- Build Timeout
- Timestamper
- Workspace Cleanup
```

## 🔧 Jenkins Configuration

### 1. Global Tool Configuration

Navigate to **Manage Jenkins > Global Tool Configuration**:

#### Java (JDK)
- **Name**: `jdk-17`
- **JAVA_HOME**: `/usr/lib/jvm/java-17-openjdk` (or your Java 17 path)
- **Install automatically**: ✅ (if needed)

#### Maven
- **Name**: `maven-3.9`
- **MAVEN_HOME**: `/opt/maven` (or auto-install)
- **Version**: 3.9.x

#### NodeJS
- **Name**: `nodejs-18`
- **Version**: 18.x.x
- **Global npm packages**: `@angular/cli@latest @ionic/cli@latest`

### 2. Credentials Setup

Navigate to **Manage Jenkins > Credentials > System > Global credentials**:

#### Database Credentials
Create these **Secret text** credentials:
- **ID**: `DB_HOST` | **Secret**: `your-db-host`
- **ID**: `DB_PORT` | **Secret**: `5432`
- **ID**: `DB_NAME` | **Secret**: `firefighter`
- **ID**: `DB_USERNAME` | **Secret**: `ff_admin`
- **ID**: `DB_PASSWORD` | **Secret**: `your-db-password`

#### JWT Configuration
- **ID**: `JWT_SECRET` | **Secret**: `your-jwt-secret-key`

#### Git Repository (if private)
- **Kind**: Username with password
- **ID**: `git-credentials`
- **Username**: Your Git username
- **Password**: Your Git token/password

### 3. Pipeline Job Creation

1. **New Item** > **Pipeline**
2. **Name**: `fire-fighter-pipeline`
3. **Pipeline Definition**: Pipeline script from SCM
4. **SCM**: Git
5. **Repository URL**: `https://github.com/your-username/Fire-Fighter.git`
6. **Credentials**: Select your Git credentials
7. **Branch**: `*/main` (or your default branch)
8. **Script Path**: `Jenkinsfile`

## 🔄 Pipeline Stages Explained

### 1. **Checkout**
- Clones the repository
- Sets build version with commit hash

### 2. **Environment Setup**
- Verifies Java and Node.js installations
- Displays version information

### 3. **Install Dependencies**
- **Backend**: Maven dependency resolution
- **Frontend**: NPM package installation

### 4. **Code Quality & Linting**
- **Backend**: Maven validation and compilation
- **Frontend**: ESLint code quality checks

### 5. **Unit Tests**
- **Backend**: Spring Boot tests with JaCoCo coverage
- **Frontend**: Angular/Jasmine tests with coverage

### 6. **Build Applications**
- **Backend**: Maven package (JAR file)
- **Frontend**: Angular production build

### 7. **Integration Tests**
- Starts backend service
- Runs API integration tests
- Stops services after testing

### 8. **Security Scan**
- **Backend**: OWASP dependency vulnerability check
- **Frontend**: NPM audit for security issues

### 9. **Build Mobile App** (main/release branches only)
- Capacitor Android APK generation

### 10. **Deploy to Staging** (develop branch)
- Automated staging deployment

### 11. **Deploy to Production** (main branch)
- Manual approval required
- Production deployment

## 📊 Reports and Artifacts

The pipeline generates several reports:

### Test Reports
- **Backend**: JUnit test results
- **Frontend**: Karma/Jasmine test results

### Coverage Reports
- **Backend**: JaCoCo HTML report
- **Frontend**: Istanbul coverage report

### Build Artifacts
- **Backend**: JAR file (`firefighter-platform-0.0.1-SNAPSHOT.jar`)
- **Frontend**: Built web assets (`www/` directory)
- **Mobile**: Android APK (debug build)

### Security Reports
- OWASP dependency check results
- NPM audit security findings

## 🚀 Running the Pipeline

### Automatic Triggers
- **Push to main**: Full pipeline with production deployment option
- **Push to develop**: Full pipeline with staging deployment
- **Pull Requests**: Build and test only (no deployment)

### Manual Triggers
1. Go to your Jenkins job
2. Click **"Build Now"**
3. Monitor progress in **Console Output**

## 🔧 Customization Options

### Environment Variables
Add custom environment variables in the `environment` block:

```groovy
environment {
    CUSTOM_VAR = 'value'
    API_URL = credentials('api-url')
}
```

### Additional Test Stages
Add custom test stages:

```groovy
stage('E2E Tests') {
    steps {
        dir(env.ANGULAR_PATH) {
            sh 'npm run e2e'
        }
    }
}
```

### Deployment Customization
Modify deployment stages for your infrastructure:

```groovy
stage('Deploy to AWS') {
    steps {
        sh 'aws s3 sync ${ANGULAR_PATH}/www s3://your-bucket'
        sh 'aws ecs update-service --service your-service'
    }
}
```

## 🐛 Troubleshooting

### Common Issues

#### 1. **Java Version Mismatch**
```bash
Error: JAVA_HOME is not set correctly
```
**Solution**: Verify JDK 17 installation and JAVA_HOME in Global Tool Configuration

#### 2. **Node.js Module Not Found**
```bash
Error: Cannot find module '@angular/cli'
```
**Solution**: Add `@angular/cli` to Global npm packages in NodeJS configuration

#### 3. **Database Connection Failed**
```bash
Error: Connection refused to database
```
**Solution**: Verify database credentials and ensure PostgreSQL is accessible

#### 4. **Permission Denied on Gradlew**
```bash
Permission denied: ./gradlew
```
**Solution**: Add execution permissions:
```groovy
sh 'chmod +x android/gradlew'
```

### Log Analysis
- Check **Console Output** for detailed error messages
- Review **Test Results** for failing tests
- Examine **Workspace** for build artifacts

## 📈 Performance Optimization

### Build Caching
- Enable Maven local repository caching
- Use NPM cache for faster dependency installation
- Consider Jenkins build cache plugins

### Parallel Execution
- Backend and frontend builds run in parallel
- Test stages execute simultaneously
- Adjust based on Jenkins agent capacity

### Resource Management
- Set build timeouts to prevent hanging builds
- Clean workspace after builds to save disk space
- Monitor Jenkins agent resource usage

## 🔒 Security Best Practices

1. **Credentials Management**
   - Never hardcode secrets in Jenkinsfile
   - Use Jenkins Credentials Store
   - Rotate credentials regularly

2. **Access Control**
   - Implement role-based access control
   - Restrict pipeline modification permissions
   - Audit pipeline changes

3. **Dependency Security**
   - Regular OWASP dependency checks
   - NPM audit for frontend vulnerabilities
   - Update dependencies promptly

## 📞 Support

For issues with this pipeline:
1. Check Jenkins logs and console output
2. Verify all prerequisites are met
3. Review credential configuration
4. Test individual stages manually

Remember to adapt the deployment stages to match your specific infrastructure and requirements!
