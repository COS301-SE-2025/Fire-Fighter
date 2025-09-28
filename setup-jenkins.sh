#!/bin/bash

# Fire-Fighter Jenkins Setup Script
# This script sets up a complete Jenkins CI/CD environment using Docker

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

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    print_success "Docker and Docker Compose are installed"
}

# Create necessary directories
create_directories() {
    print_status "Creating necessary directories..."
    
    mkdir -p jenkins-config
    mkdir -p jenkins-data
    mkdir -p sonar-data
    mkdir -p nexus-data
    
    # Set proper permissions
    sudo chown -R 1000:1000 jenkins-data
    sudo chown -R 999:999 sonar-data
    sudo chown -R 200:200 nexus-data
    
    print_success "Directories created and permissions set"
}

# Install E2E testing dependencies
install_e2e_dependencies() {
    print_status "Installing E2E testing dependencies..."

    # Install Chrome for Cypress
    if ! command -v google-chrome &> /dev/null; then
        print_status "Installing Google Chrome..."
        wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add -
        echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list
        apt-get update
        apt-get install -y google-chrome-stable
    else
        print_success "Google Chrome already installed"
    fi

    # Install Xvfb for headless display
    if ! command -v xvfb-run &> /dev/null; then
        print_status "Installing Xvfb for headless display..."
        apt-get install -y xvfb
    else
        print_success "Xvfb already installed"
    fi

    # Install additional dependencies for Cypress
    apt-get install -y libgtk2.0-0 libgtk-3-0 libgbm-dev libnotify-dev libgconf-2-4 libnss3 libxss1 libasound2 libxtst6 xauth xvfb
}

# Generate environment file
create_env_file() {
    print_status "Creating environment configuration..."

    cat > .env << EOF
# Jenkins Configuration
JENKINS_AGENT_SECRET=your-agent-secret-here

# Database Configuration (for your application)
DB_HOST=your-db-host
DB_PORT=5432
DB_NAME=firefighter
DB_USERNAME=ff_admin
DB_PASSWORD=your-db-password

# JWT Configuration
JWT_SECRET=your-jwt-secret-key-here

# SonarQube Configuration
SONAR_JDBC_URL=jdbc:postgresql://sonar-db:5432/sonar
SONAR_JDBC_USERNAME=sonar
SONAR_JDBC_PASSWORD=sonar_password

# E2E Testing Configuration
CYPRESS_baseUrl=http://localhost:4200
CYPRESS_video=false
CYPRESS_screenshotOnRunFailure=true
EOF

    print_warning "Please edit .env file with your actual configuration values"
}

# Start Jenkins services
start_services() {
    print_status "Starting Jenkins services..."
    
    # Start core services first
    docker-compose -f docker-compose.jenkins.yml up -d jenkins
    
    print_status "Waiting for Jenkins to start..."
    sleep 30
    
    # Check if Jenkins is running
    if curl -f http://localhost:8080 &> /dev/null; then
        print_success "Jenkins is running at http://localhost:8080"
    else
        print_warning "Jenkins may still be starting. Please wait a few more minutes."
    fi
    
    # Start optional services
    read -p "Do you want to start SonarQube for code quality analysis? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose -f docker-compose.jenkins.yml up -d sonarqube sonar-db
        print_success "SonarQube will be available at http://localhost:9000"
        print_status "Default SonarQube credentials: admin/admin"
    fi
    
    read -p "Do you want to start Nexus Repository for artifact management? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose -f docker-compose.jenkins.yml up -d nexus
        print_success "Nexus Repository will be available at http://localhost:8081"
        print_status "Check Nexus logs for initial admin password: docker logs fire-fighter-nexus"
    fi
}

# Display initial credentials and next steps
show_next_steps() {
    print_success "Jenkins setup completed!"
    echo
    echo "=== NEXT STEPS ==="
    echo "1. Open Jenkins at: http://localhost:8080"
    echo "2. Login with: admin/admin123"
    echo "3. Change the admin password immediately"
    echo "4. Configure your Git repository credentials"
    echo "5. Create a new Pipeline job pointing to your Jenkinsfile"
    echo
    echo "=== CONFIGURATION FILES ==="
    echo "- Jenkinsfile: Main pipeline configuration"
    echo "- JENKINS_SETUP_GUIDE.md: Detailed setup instructions"
    echo "- .env: Environment variables (edit with your values)"
    echo
    echo "=== USEFUL COMMANDS ==="
    echo "- View logs: docker-compose -f docker-compose.jenkins.yml logs -f"
    echo "- Stop services: docker-compose -f docker-compose.jenkins.yml down"
    echo "- Restart Jenkins: docker-compose -f docker-compose.jenkins.yml restart jenkins"
    echo
    if [[ -f .env ]]; then
        print_warning "Don't forget to edit .env file with your actual configuration!"
    fi
}

# Main execution
main() {
    echo "=== Fire-Fighter Jenkins Setup ==="
    echo

    check_docker
    create_directories
    install_e2e_dependencies
    create_env_file
    start_services
    show_next_steps
}

# Run main function
main "$@"
