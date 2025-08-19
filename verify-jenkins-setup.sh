#!/bin/bash

# Fire-Fighter Jenkins Setup Verification Script
# Run this script on your Jenkins server to verify the setup

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

echo "=== Fire-Fighter Jenkins Setup Verification ==="
echo

# Check Jenkins is running
print_status "Checking Jenkins service..."
if systemctl is-active --quiet jenkins; then
    print_success "Jenkins service is running"
else
    print_error "Jenkins service is not running"
    echo "Try: sudo systemctl start jenkins"
fi

# Check Jenkins port
print_status "Checking Jenkins port 9080..."
if netstat -tuln | grep -q ":9080 "; then
    print_success "Jenkins is listening on port 9080"
else
    print_warning "Jenkins may not be listening on port 9080"
    echo "Check Jenkins configuration: sudo nano /etc/default/jenkins"
fi

# Check Java installation
print_status "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    if [[ $JAVA_VERSION == 17* ]]; then
        print_success "Java 17 is installed: $JAVA_VERSION"
    else
        print_warning "Java version is $JAVA_VERSION (expected 17.x)"
    fi
    echo "JAVA_HOME: ${JAVA_HOME:-Not set}"
    echo "Java location: $(which java)"
else
    print_error "Java is not installed or not in PATH"
fi

# Check Maven installation
print_status "Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
    print_success "Maven is installed: $MAVEN_VERSION"
    echo "Maven location: $(which mvn)"
    mvn -version | grep "Maven home"
else
    print_error "Maven is not installed or not in PATH"
fi

# Check Node.js installation
print_status "Checking Node.js installation..."
if command -v node &> /dev/null; then
    NODE_VERSION=$(node --version)
    if [[ $NODE_VERSION == v22* ]]; then
        print_success "Node.js 22 is installed: $NODE_VERSION"
    else
        print_warning "Node.js version is $NODE_VERSION (expected v22.x)"
    fi
    echo "Node.js location: $(which node)"
    echo "NPM version: $(npm --version)"
else
    print_error "Node.js is not installed or not in PATH"
fi

# Check Angular CLI
print_status "Checking Angular CLI..."
if command -v ng &> /dev/null; then
    NG_VERSION=$(ng version --skip-git 2>/dev/null | grep "Angular CLI" | cut -d' ' -f3 || echo "Unknown")
    print_success "Angular CLI is installed: $NG_VERSION"
else
    print_warning "Angular CLI is not installed globally"
    echo "Install with: sudo npm install -g @angular/cli"
fi

# Check Ionic CLI
print_status "Checking Ionic CLI..."
if command -v ionic &> /dev/null; then
    IONIC_VERSION=$(ionic --version 2>/dev/null || echo "Unknown")
    print_success "Ionic CLI is installed: $IONIC_VERSION"
else
    print_warning "Ionic CLI is not installed globally"
    echo "Install with: sudo npm install -g @ionic/cli"
fi

# Check Git installation
print_status "Checking Git installation..."
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | cut -d' ' -f3)
    print_success "Git is installed: $GIT_VERSION"
else
    print_error "Git is not installed or not in PATH"
fi

# Check database connectivity
print_status "Checking database connectivity..."
if command -v nc &> /dev/null; then
    if nc -z 100.83.111.92 5432 2>/dev/null; then
        print_success "Database server is reachable on 100.83.111.92:5432"
    else
        print_warning "Cannot reach database server on 100.83.111.92:5432"
        echo "Check network connectivity and firewall rules"
    fi
else
    print_warning "netcat (nc) not available for database connectivity test"
    echo "Install with: sudo apt-get install netcat"
fi

# Check disk space
print_status "Checking disk space..."
DISK_USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -lt 80 ]; then
    print_success "Disk usage is ${DISK_USAGE}% (sufficient space available)"
else
    print_warning "Disk usage is ${DISK_USAGE}% (consider cleaning up)"
fi

# Check memory
print_status "Checking memory..."
MEMORY_TOTAL=$(free -m | grep "Mem:" | awk '{print $2}')
MEMORY_AVAILABLE=$(free -m | grep "Mem:" | awk '{print $7}')
if [ $MEMORY_TOTAL -gt 2048 ]; then
    print_success "Total memory: ${MEMORY_TOTAL}MB (sufficient for Jenkins)"
else
    print_warning "Total memory: ${MEMORY_TOTAL}MB (consider upgrading for better performance)"
fi
echo "Available memory: ${MEMORY_AVAILABLE}MB"

# Check Jenkins user permissions
print_status "Checking Jenkins user permissions..."
JENKINS_USER=$(ps aux | grep jenkins | grep -v grep | awk '{print $1}' | head -1)
if [ ! -z "$JENKINS_USER" ]; then
    print_success "Jenkins is running as user: $JENKINS_USER"
    
    # Check if Jenkins user can access tools
    if sudo -u $JENKINS_USER which java &> /dev/null; then
        print_success "Jenkins user can access Java"
    else
        print_warning "Jenkins user cannot access Java"
    fi
    
    if sudo -u $JENKINS_USER which mvn &> /dev/null; then
        print_success "Jenkins user can access Maven"
    else
        print_warning "Jenkins user cannot access Maven"
    fi
    
    if sudo -u $JENKINS_USER which node &> /dev/null; then
        print_success "Jenkins user can access Node.js"
    else
        print_warning "Jenkins user cannot access Node.js"
    fi
else
    print_warning "Could not determine Jenkins user"
fi

# Check port conflicts
print_status "Checking for port conflicts..."
if netstat -tuln | grep -q ":8080 "; then
    print_success "Port 8080 is in use (ERP system as expected)"
else
    print_warning "Port 8080 is not in use (ERP system may not be running)"
fi

if netstat -tuln | grep -q ":8081 "; then
    print_warning "Port 8081 is in use (may conflict with test server)"
else
    print_success "Port 8081 is available for test server"
fi

echo
echo "=== Verification Summary ==="
echo "✅ Review the results above"
echo "✅ Address any warnings or errors"
echo "✅ Proceed with Jenkins configuration if all checks pass"
echo
echo "=== Next Steps ==="
echo "1. Open Jenkins at: http://$(hostname -I | awk '{print $1}'):9080"
echo "2. Follow the setup guide: JENKINS_EXISTING_SERVER_SETUP.md"
echo "3. Configure global tools with the paths shown above"
echo "4. Set up credentials for your Fire-Fighter project"
echo "5. Create your pipeline job"
echo
echo "=== Useful Commands ==="
echo "- Check Jenkins logs: sudo journalctl -u jenkins -f"
echo "- Restart Jenkins: sudo systemctl restart jenkins"
echo "- Check Jenkins status: sudo systemctl status jenkins"
echo "- View Jenkins config: sudo nano /etc/default/jenkins"
