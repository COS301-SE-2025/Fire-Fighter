# 🚀 Manual Build Guide for Fire-Fighter

## 📋 Quick Setup Summary

Since you're working with the university repository `https://github.com/COS301-SE-2025/Fire-Fighter` and may not have webhook permissions, here's how to trigger builds manually.

## 🔧 Jenkins Pipeline Job Setup

### Step 1: Create Pipeline Job
1. **Open Jenkins**: `http://your-server-ip:9080`
2. **New Item** → **Pipeline**
3. **Name**: `fire-fighter-pipeline`
4. **Description**: `Fire-Fighter CI/CD Pipeline - COS301-SE-2025`

### Step 2: Configure Job
**Pipeline Configuration:**
- **Definition**: `Pipeline script from SCM`
- **SCM**: `Git`
- **Repository URL**: `https://github.com/COS301-SE-2025/Fire-Fighter.git`
- **Credentials**: `github-credentials` (your zander-van-schoor token)
- **Branches to build**: `*/develop` (or `*/main` for main branch)
- **Script Path**: `Jenkinsfile`

**Build Triggers:**
- **✅ This project is parameterized** (automatically enabled with parameters in Jenkinsfile)
- **Poll SCM**: `H/10 * * * *` (optional - checks every 10 minutes)

## 🎯 Manual Build Options

### Option 1: Simple Build Now
1. **Go to your job**: `fire-fighter-pipeline`
2. **Click**: **"Build Now"**
3. **Monitor**: Click build number → **Console Output**

### Option 2: Build with Parameters
1. **Go to your job**: `fire-fighter-pipeline`
2. **Click**: **"Build with Parameters"**
3. **Configure options**:
   - **Skip Tests**: ✅ Check for faster builds (skips unit tests)
   - **Build Message**: Enter custom message like "Testing new feature"
4. **Click**: **"Build"**

## ⚡ Build Types

### Full Build (Default)
- ✅ Code quality checks
- ✅ Unit tests (backend + frontend)
- ✅ Build applications
- ✅ Integration tests
- ✅ Security scans
- ⏱️ Duration: ~10-15 minutes

### Quick Build (Skip Tests)
- ✅ Code quality checks
- ❌ Unit tests (skipped)
- ✅ Build applications
- ❌ Integration tests (skipped)
- ✅ Security scans
- ⏱️ Duration: ~5-8 minutes

## 📊 Monitoring Your Builds

### Build Status
- **🔵 Blue**: Success
- **🔴 Red**: Failed
- **🟡 Yellow**: Unstable (tests failed but build succeeded)
- **⚪ Gray**: Not built yet
- **🔄 Blinking**: Currently building

### Build Information
Each build shows:
- **Build Number**: #1, #2, #3, etc.
- **Duration**: How long it took
- **Commit**: Git commit hash
- **Branch**: Which branch was built
- **Parameters**: What options were selected

### Useful Links
- **Console Output**: See detailed build logs
- **Test Results**: View test reports
- **Coverage Reports**: Code coverage statistics
- **Artifacts**: Download build outputs (JAR files, etc.)

## 🔄 Regular Build Workflow

### For Development Work:
1. **Make code changes** in your local repository
2. **Commit and push** to develop branch:
   ```bash
   git add .
   git commit -m "Your commit message"
   git push origin develop
   ```
3. **Go to Jenkins** and trigger manual build
4. **Monitor results** and fix any issues

### For Quick Testing:
1. **Use "Build with Parameters"**
2. **Check "Skip Tests"** for faster feedback
3. **Add descriptive message** like "Testing login fix"

## 🛠️ Troubleshooting

### Build Fails to Start
- **Check**: Repository URL is correct
- **Verify**: GitHub credentials are working
- **Ensure**: Branch name exists (develop/main)

### Build Fails During Execution
- **Check**: Console Output for error details
- **Common issues**:
  - Maven dependency problems
  - NPM installation failures
  - Test failures
  - Database connection issues

### Slow Builds
- **Use**: "Skip Tests" option for quick builds
- **Check**: Server resources (CPU, memory, disk)
- **Consider**: Running tests separately

## 📈 Build Optimization Tips

### Faster Builds:
1. **Skip tests** during development iterations
2. **Run full builds** before merging to main
3. **Use incremental builds** when possible

### Better Monitoring:
1. **Set up email notifications** for build results
2. **Check builds regularly** during development
3. **Fix failing builds immediately**

## 🔔 Alternative Triggering Methods

### Option 1: Jenkins CLI (Advanced)
```bash
# Download Jenkins CLI
wget http://your-server-ip:9080/jnlpJars/jenkins-cli.jar

# Trigger build
java -jar jenkins-cli.jar -s http://your-server-ip:9080 build fire-fighter-pipeline
```

### Option 2: REST API (Advanced)
```bash
# Trigger build via API
curl -X POST http://your-server-ip:9080/job/fire-fighter-pipeline/build \
  --user your-username:your-api-token
```

### Option 3: Scheduled Builds
Add to your job configuration:
- **Build Triggers** → **Build periodically**
- **Schedule**: `H 2 * * *` (daily at 2 AM)

## 📞 Getting Help

### If builds fail:
1. **Check Console Output** for detailed error messages
2. **Verify all credentials** are correctly configured
3. **Test individual components** (Java, Maven, Node.js)
4. **Run the verification script**: `./verify-jenkins-setup.sh`

### Common Solutions:
- **Restart Jenkins**: `sudo systemctl restart jenkins`
- **Clear workspace**: Delete workspace in job configuration
- **Update tools**: Verify Global Tool Configuration
- **Check permissions**: Ensure Jenkins user has access to tools

## 🎯 Success Indicators

A successful build will show:
- ✅ **All stages green** in pipeline view
- ✅ **Test reports** generated
- ✅ **Build artifacts** created (JAR files, web assets)
- ✅ **No security vulnerabilities** found
- ✅ **Email notification** sent (if configured)

## 📋 Quick Reference

### Essential Commands:
- **Start build**: Click "Build Now" or "Build with Parameters"
- **View logs**: Click build number → Console Output
- **Check tests**: Click build number → Test Results
- **Download artifacts**: Click build number → Build Artifacts

### Build Parameters:
- **SKIP_TESTS**: `true/false` - Skip unit tests for faster builds
- **BUILD_MESSAGE**: `string` - Custom message for this build

### Typical Build Flow:
1. **Checkout** → Downloads code
2. **Environment Setup** → Verifies tools
3. **Install Dependencies** → Maven + NPM
4. **Code Quality** → Linting
5. **Unit Tests** → Backend + Frontend (if not skipped)
6. **Build** → JAR + Angular build
7. **Integration Tests** → API testing
8. **Security Scan** → Vulnerability check

Your manual build setup is now ready! 🔥
