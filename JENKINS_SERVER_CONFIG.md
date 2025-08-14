# 🔧 Jenkins Server Configuration for Fire-Fighter

## 📋 Your Server Setup
- **Jenkins**: Port 9080
- **ERP System**: Port 8080 (protected)
- **Java**: OpenJDK 17
- **Maven**: 3.8.7
- **Node.js**: 22

## 🎯 Port Configuration
The Jenkinsfile has been configured to avoid conflicts:
- **Jenkins**: 9080 (your current setup)
- **Backend Tests**: 8081 (avoids ERP system on 8080)
- **Frontend Dev**: 4200 (Angular default)

## 🛠️ Tool Paths to Verify

### Find Your Tool Paths
Run these commands on your server to confirm paths:

```bash
# Java path
which java
readlink -f $(which java)
# Usually: /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Maven path
mvn -version
# Look for "Maven home:" in output
# Usually: /usr/share/maven

# Node.js path
which node
# Usually: /usr/bin/node
```

## 🔧 Jenkins Global Tool Configuration

Use these exact values in Jenkins:

### JDK Configuration
- **Name**: `jdk-17`
- **JAVA_HOME**: `/usr/lib/jvm/java-17-openjdk-amd64`
- **Install automatically**: ❌ (unchecked)

### Maven Configuration
- **Name**: `maven-3.8`
- **MAVEN_HOME**: `/usr/share/maven`
- **Install automatically**: ❌ (unchecked)

### NodeJS Configuration
- **Name**: `nodejs-22`
- **Installation directory**: `/usr/bin`
- **Install automatically**: ❌ (unchecked)
- **Global npm packages**: `@angular/cli@latest @ionic/cli@latest`

## 🔐 Required Credentials

Set these up in Jenkins Credentials Store:

| Credential ID | Type | Value | Description |
|---------------|------|-------|-------------|
| `DB_HOST` | Secret text | Your DB host IP | Database server |
| `DB_PORT` | Secret text | `5432` | PostgreSQL port |
| `DB_NAME` | Secret text | `firefighter` | Database name |
| `DB_USERNAME` | Secret text | `ff_admin` | DB username |
| `DB_PASSWORD` | Secret text | Your DB password | DB password |
| `JWT_SECRET` | Secret text | Your JWT secret | JWT signing key |
| `git-credentials` | Username/Password | Your Git creds | Repository access |

## 🚀 Pipeline Job Creation

1. **Create New Job**:
   - Jenkins Dashboard → **New Item**
   - **Name**: `fire-fighter-pipeline`
   - **Type**: Pipeline
   - Click **OK**

2. **Configure Pipeline**:
   - **Pipeline Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: `https://github.com/your-username/Fire-Fighter.git`
   - **Credentials**: Select your git credentials
   - **Branch Specifier**: `*/main` (or your default branch)
   - **Script Path**: `Jenkinsfile`

3. **Save** the configuration

## 🔍 Testing Your Setup

### Test Tool Installations
Run these in Jenkins **Manage Jenkins** → **Script Console**:

```groovy
// Test Java
def javaHome = System.getProperty("java.home")
println "Java Home: ${javaHome}"
println "Java Version: ${System.getProperty("java.version")}"

// Test Maven (if in PATH)
def proc = "mvn -version".execute()
proc.waitFor()
println "Maven: ${proc.text}"

// Test Node.js (if in PATH)
def nodeProc = "node --version".execute()
nodeProc.waitFor()
println "Node.js: ${nodeProc.text}"
```

### Test Database Connection
Create a simple test job to verify DB connectivity:

```groovy
pipeline {
    agent any
    environment {
        DB_HOST = credentials('DB_HOST')
        DB_PASSWORD = credentials('DB_PASSWORD')
    }
    stages {
        stage('Test DB Connection') {
            steps {
                sh '''
                    echo "Testing database connection..."
                    # Add your DB test command here
                    echo "DB Host: ${DB_HOST}"
                '''
            }
        }
    }
}
```

## 🐛 Common Issues & Solutions

### Issue: Java Not Found
```
Error: JAVA_HOME is not set
```
**Solution**: 
- Verify Java path: `which java`
- Set correct JAVA_HOME in Global Tool Configuration
- Ensure Jenkins user has access to Java

### Issue: Maven Not Found
```
Error: mvn command not found
```
**Solution**:
- Check Maven installation: `mvn -version`
- Verify MAVEN_HOME path
- Add Maven to system PATH if needed

### Issue: Node.js Modules Missing
```
Error: @angular/cli not found
```
**Solution**:
- Install globally: `npm install -g @angular/cli @ionic/cli`
- Add to NodeJS global packages in Jenkins
- Verify npm permissions

### Issue: Permission Denied
```
Error: Permission denied
```
**Solution**:
- Check Jenkins user permissions
- Ensure Jenkins can access tool directories
- Fix file permissions: `chmod +x gradlew`

### Issue: Port Already in Use
```
Error: Port 8080 already in use
```
**Solution**:
- Pipeline uses port 8081 for tests (already configured)
- Verify no other services on 8081
- Check firewall settings

## 📊 Expected Pipeline Behavior

### Successful Run Timeline:
1. **Checkout** (30s) - Clone repository
2. **Environment Setup** (1min) - Verify tools
3. **Install Dependencies** (3-5min) - Maven + NPM
4. **Code Quality** (1min) - Linting
5. **Unit Tests** (2-4min) - Backend + Frontend tests
6. **Build Applications** (2-3min) - JAR + Angular build
7. **Integration Tests** (2min) - API testing
8. **Security Scan** (1-2min) - Vulnerability checks

**Total Expected Time**: 12-18 minutes

### Build Artifacts Generated:
- `FF-API/target/firefighter-platform-0.0.1-SNAPSHOT.jar`
- `FF-Angular/www/` (Angular build output)
- Test reports and coverage data
- Security scan reports

## 🔄 Next Steps After Setup

1. **Run First Build**:
   - Go to your pipeline job
   - Click **Build Now**
   - Monitor **Console Output**

2. **Verify Reports**:
   - Check test results
   - Review coverage reports
   - Examine security findings

3. **Configure Webhooks** (optional):
   - Set up Git webhooks for automatic builds
   - Configure branch-specific triggers

4. **Set Up Notifications** (optional):
   - Email notifications for build failures
   - Slack/Teams integration

## 📞 Support

If you encounter issues:
1. Check Jenkins **Console Output** for detailed errors
2. Verify all tool paths are correct
3. Test credentials and database connectivity
4. Review firewall and port configurations

Remember: Your ERP system on port 8080 is protected - the pipeline uses port 8081 for testing!
