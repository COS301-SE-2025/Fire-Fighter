#!/bin/bash

# Fire-Fighter Pipeline Validation Script
# Run this to validate your Jenkins pipeline setup

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo "=== Fire-Fighter Pipeline Validation ==="
echo

# Check if we're in the right directory
if [[ ! -f "Jenkinsfile" ]]; then
    print_error "Jenkinsfile not found. Please run this script from the Fire-Fighter repository root."
    exit 1
fi

print_success "Found Jenkinsfile in current directory"

# Validate Jenkinsfile syntax
print_status "Validating Jenkinsfile syntax..."
if command -v jenkins-cli.jar &> /dev/null; then
    java -jar jenkins-cli.jar -s http://localhost:9080 declarative-linter < Jenkinsfile
    print_success "Jenkinsfile syntax is valid"
else
    print_warning "Jenkins CLI not available for syntax validation"
    print_status "You can validate syntax in Jenkins UI: Pipeline Syntax → Declarative Directive Generator"
fi

# Check project structure
print_status "Validating project structure..."

if [[ -d "FF-API" && -f "FF-API/pom.xml" ]]; then
    print_success "Backend project structure is correct"
else
    print_error "Backend project (FF-API) structure is invalid"
fi

if [[ -d "FF-Angular" && -f "FF-Angular/package.json" ]]; then
    print_success "Frontend project structure is correct"
else
    print_error "Frontend project (FF-Angular) structure is invalid"
fi

# Check for required configuration files
print_status "Checking configuration files..."

required_files=(
    "FF-API/src/main/resources/application.properties"
    "FF-API/src/main/resources/application-test.properties"
    "FF-Angular/angular.json"
    "FF-Angular/capacitor.config.ts"
)

for file in "${required_files[@]}"; do
    if [[ -f "$file" ]]; then
        print_success "Found: $file"
    else
        print_warning "Missing: $file"
    fi
done

# Validate Maven configuration
print_status "Validating Maven configuration..."
cd FF-API
if mvn validate -q; then
    print_success "Maven configuration is valid"
else
    print_error "Maven configuration has issues"
fi
cd ..

# Validate NPM configuration
print_status "Validating NPM configuration..."
cd FF-Angular
if npm ls --depth=0 &> /dev/null; then
    print_success "NPM configuration is valid"
else
    print_warning "NPM configuration may have issues (run 'npm install' to fix)"
fi
cd ..

# Check for environment variables in Jenkinsfile
print_status "Checking environment variables in Jenkinsfile..."

required_env_vars=(
    "DB_HOST"
    "DB_PASSWORD"
    "JWT_SECRET"
    "GMAIL_APP_PASSWORD"
    "GOOGLE_GEMINI_API_KEY"
)

for var in "${required_env_vars[@]}"; do
    if grep -q "credentials('$var')" Jenkinsfile; then
        print_success "Environment variable configured: $var"
    else
        print_error "Missing environment variable: $var"
    fi
done

# Check tool configurations
print_status "Checking tool configurations in Jenkinsfile..."

if grep -q 'jdk "jdk-17"' Jenkinsfile; then
    print_success "JDK 17 tool configured"
else
    print_error "JDK 17 tool not configured"
fi

if grep -q 'maven.*maven-3.8' Jenkinsfile; then
    print_success "Maven 3.8 tool configured"
else
    print_error "Maven 3.8 tool not configured"
fi

if grep -q 'nodejs.*nodejs-22' Jenkinsfile; then
    print_success "Node.js 22 tool configured"
else
    print_error "Node.js 22 tool not configured"
fi

# Check for security configurations
print_status "Checking security configurations..."

if grep -q "OWASP" Jenkinsfile; then
    print_success "OWASP security scanning configured"
else
    print_warning "OWASP security scanning not found"
fi

if grep -q "npm audit" Jenkinsfile; then
    print_success "NPM security audit configured"
else
    print_warning "NPM security audit not found"
fi

# Validate pipeline stages
print_status "Validating pipeline stages..."

expected_stages=(
    "Checkout"
    "Environment Setup"
    "Install Dependencies"
    "Code Quality"
    "Unit Tests"
    "Build Applications"
    "Integration Tests"
    "Security Scan"
)

for stage in "${expected_stages[@]}"; do
    if grep -q "stage('$stage" Jenkinsfile; then
        print_success "Stage found: $stage"
    else
        print_warning "Stage missing or renamed: $stage"
    fi
done

# Check for parallel execution
if grep -q "parallel {" Jenkinsfile; then
    print_success "Parallel execution configured for faster builds"
else
    print_warning "No parallel execution found (builds may be slower)"
fi

# Check for proper error handling
if grep -q "post {" Jenkinsfile; then
    print_success "Post-build actions configured"
else
    print_warning "No post-build actions found"
fi

# Generate pipeline summary
echo
echo "=== Pipeline Summary ==="
echo "📁 Project Type: Monorepo (Spring Boot + Angular/Ionic)"
echo "🔧 Build Tools: Maven 3.8 + Node.js 22"
echo "☕ Java Version: 17"
echo "🗄️ Database: PostgreSQL (external)"
echo "📱 Mobile: Android APK generation"
echo "🔒 Security: OWASP + NPM audit"
echo "🚀 Deployment: Staging (develop) + Production (main)"

echo
echo "=== Next Steps ==="
echo "1. Ensure all credentials are configured in Jenkins"
echo "2. Verify global tools are properly set up"
echo "3. Test the pipeline with a manual build"
echo "4. Set up GitHub webhooks for automatic builds"
echo "5. Configure email notifications"

echo
echo "=== Jenkins Pipeline URLs ==="
echo "🔗 Pipeline Job: http://your-server-ip:9080/job/fire-fighter-pipeline/"
echo "🔗 Build History: http://your-server-ip:9080/job/fire-fighter-pipeline/builds"
echo "🔗 Console Output: http://your-server-ip:9080/job/fire-fighter-pipeline/lastBuild/console"
echo "🔗 Test Results: http://your-server-ip:9080/job/fire-fighter-pipeline/lastBuild/testReport"

echo
print_success "Pipeline validation completed!"
print_status "Review any warnings or errors above before running your first build."
