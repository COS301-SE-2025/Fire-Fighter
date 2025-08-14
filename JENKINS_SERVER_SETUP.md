# 🔧 Jenkins Server Setup Guide for Fire-Fighter

## 🎯 Server Configuration Summary

Your current setup:
- ✅ Jenkins running on port 9080
- ✅ OpenJDK 17 installed
- ✅ Maven installed
- ✅ ERP system on port 8080 (avoided in pipeline)

## 📋 Pre-Setup Checklist

### System Requirements
Verify your server meets these requirements:

```bash
# Check Java version
java -version
# Should show OpenJDK 17

# Check Maven version
mvn -version
# Should show Maven 3.x

# Check available memory (recommended: 4GB+)
free -h

# Check disk space (recommended: 20GB+ free)
df -h

# Check Jenkins is running
curl http://localhost:9080
```

### Required System Packages
Install these if not already present:

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y curl wget git unzip

# CentOS/RHEL
sudo yum install -y curl wget git unzip
```

## 🔧 Jenkins Configuration Steps

### Step 1: Access Jenkins
1. Open browser: `http://your-server-ip:9080`
2. If first time, get initial admin password:
   ```bash
   sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   ```

### Step 2: Install Suggested Plugins
- Choose "Install suggested plugins"
- Wait for installation to complete

### Step 3: Create Admin User
- Create your admin user account
- **Important**: Use a strong password

## 🛠️ Tool Configuration Details

### Java Configuration
In **Manage Jenkins** → **Global Tool Configuration**:

**Find your Java path:**
```bash
# Method 1
sudo update-alternatives --display java

# Method 2
echo $JAVA_HOME

# Method 3
readlink -f $(which java)
```

Common paths:
- Ubuntu: `/usr/lib/jvm/java-17-openjdk-amd64`
- CentOS: `/usr/lib/jvm/java-17-openjdk`

### Maven Configuration
**Find your Maven path:**
```bash
# Check Maven installation
mvn -version
# Look for "Maven home:" in output

# Common paths to check
ls -la /usr/share/maven
ls -la /opt/maven
ls -la /usr/local/maven
```

### Node.js Installation
Since Node.js will be auto-installed by Jenkins:
- Version: 18.x (latest stable)
- Global packages: `@angular/cli@latest @ionic/cli@latest`

## 🔐 Security Configuration

### Firewall Settings
Ensure these ports are accessible:

```bash
# Jenkins (internal access)
sudo ufw allow 9080/tcp

# If using external Git repositories
sudo ufw allow out 443/tcp
sudo ufw allow out 80/tcp

# SSH for Git (if needed)
sudo ufw allow out 22/tcp
```

### Jenkins Security
1. **Enable CSRF Protection**: Manage Jenkins → Configure Global Security
2. **Set Authorization**: "Logged-in users can do anything"
3. **Disable CLI**: For security (unless needed)

## 📊 Performance Optimization

### Memory Settings
Edit Jenkins startup configuration:

```bash
# Find Jenkins service file
sudo systemctl status jenkins

# Edit Java options (example for systemd)
sudo systemctl edit jenkins
```

Add these JVM options:
```ini
[Service]
Environment="JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC"
```

### Disk Space Management
```bash
# Set up log rotation
sudo nano /etc/logrotate.d/jenkins

# Content:
/var/log/jenkins/jenkins.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
}
```

## 🔄 Pipeline Port Configuration

The Jenkinsfile has been customized for your environment:

### Port Usage
- **Jenkins**: 9080 (your current setup)
- **ERP System**: 8080 (avoided)
- **Test Backend**: 8081 (for integration tests)
- **Frontend Dev**: 4200 (Angular default)

### Integration Test Configuration
The pipeline starts the backend on port 8081 during integration tests to avoid conflicts with your ERP system.

## 🚀 Creating Your First Pipeline

### Step 1: Create New Job
1. Jenkins Dashboard → **New Item**
2. **Name**: `fire-fighter-pipeline`
3. **Type**: Pipeline
4. Click **OK**

### Step 2: Configure Pipeline
**Pipeline Section:**
- **Definition**: Pipeline script from SCM
- **SCM**: Git
- **Repository URL**: `https://github.com/your-username/Fire-Fighter.git`
- **Credentials**: Select your Git credentials (if private repo)
- **Branch Specifier**: `*/main` (or your default branch)
- **Script Path**: `Jenkinsfile`

### Step 3: Build Triggers (Optional)
- **GitHub hook trigger**: For automatic builds on push
- **Poll SCM**: `H/5 * * * *` (check every 5 minutes)
- **Build periodically**: `H 2 * * *` (nightly builds)

## 🐛 Troubleshooting Common Issues

### Issue 1: Java Not Found
```bash
# Error: JAVA_HOME not set
# Solution: Set JAVA_HOME in Jenkins
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### Issue 2: Maven Not Found
```bash
# Error: mvn command not found
# Solution: Check Maven installation
which mvn
sudo ln -s /usr/share/maven/bin/mvn /usr/local/bin/mvn
```

### Issue 3: Permission Denied
```bash
# Error: Permission denied accessing workspace
# Solution: Fix Jenkins user permissions
sudo chown -R jenkins:jenkins /var/lib/jenkins
```

### Issue 4: Port Already in Use
```bash
# Error: Port 8081 already in use
# Solution: Check what's using the port
sudo netstat -tlnp | grep 8081
sudo lsof -i :8081
```

### Issue 5: Out of Memory
```bash
# Error: Java heap space
# Solution: Increase Jenkins memory
# Edit /etc/default/jenkins or systemd service
JAVA_ARGS="-Xmx2g -Xms1g"
```

## 📈 Monitoring Your Pipeline

### Build Logs
- **Console Output**: Real-time build logs
- **Blue Ocean**: Modern pipeline visualization
- **Build History**: Track build trends

### System Monitoring
```bash
# Monitor Jenkins process
top -p $(pgrep -f jenkins)

# Monitor disk usage
watch df -h

# Monitor memory usage
watch free -h
```

### Log Locations
```bash
# Jenkins logs
tail -f /var/log/jenkins/jenkins.log

# Build workspace
ls -la /var/lib/jenkins/workspace/

# Build artifacts
ls -la /var/lib/jenkins/jobs/fire-fighter-pipeline/builds/
```

## 🔧 Maintenance Tasks

### Weekly Tasks
- Check disk space usage
- Review failed builds
- Update plugins (if stable)

### Monthly Tasks
- Backup Jenkins configuration
- Clean old build artifacts
- Review security settings

### Backup Commands
```bash
# Backup Jenkins home
sudo tar -czf jenkins-backup-$(date +%Y%m%d).tar.gz /var/lib/jenkins

# Backup specific job
sudo tar -czf job-backup.tar.gz /var/lib/jenkins/jobs/fire-fighter-pipeline
```

## 📞 Getting Help

### Log Analysis
1. Check Jenkins system logs
2. Review build console output
3. Examine workspace files
4. Verify tool configurations

### Common Commands
```bash
# Restart Jenkins
sudo systemctl restart jenkins

# Check Jenkins status
sudo systemctl status jenkins

# View Jenkins logs
sudo journalctl -u jenkins -f
```

### Support Resources
- Jenkins Documentation: https://www.jenkins.io/doc/
- Plugin Documentation: Available in Jenkins Plugin Manager
- Community Forums: https://community.jenkins.io/

---

**🎉 Your Jenkins server is now ready for the Fire-Fighter pipeline!**
