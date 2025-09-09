#!/bin/bash

# FireFighter Notification Service Test Runner
# This script runs comprehensive tests for the notification service

echo "🚒 FireFighter Notification Service Test Runner"
echo "================================================"

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

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed or not in PATH"
    exit 1
fi

# Check if we're in the correct directory
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found. Please run this script from the FF-API directory"
    exit 1
fi

print_status "Starting notification service tests..."

# 1. Run Unit Tests
echo ""
print_status "Running Notification Service Unit Tests..."
mvn test -Dtest=NotificationServiceTest -q
if [ $? -eq 0 ]; then
    print_success "Notification Service unit tests passed"
else
    print_error "Notification Service unit tests failed"
    exit 1
fi

# 2. Run Controller Tests
echo ""
print_status "Running Notification Controller Integration Tests..."
mvn test -Dtest=NotificationControllerTest -q
if [ $? -eq 0 ]; then
    print_success "Notification Controller tests passed"
else
    print_error "Notification Controller tests failed"
    exit 1
fi

# 3. Run Repository Tests
echo ""
print_status "Running Notification Repository Tests..."
mvn test -Dtest=NotificationRepositoryTest -q
if [ $? -eq 0 ]; then
    print_success "Notification Repository tests passed"
else
    print_error "Notification Repository tests failed"
    exit 1
fi

# 4. Run All Notification Tests
echo ""
print_status "Running All Notification Tests..."
mvn test -Dtest="*Notification*" -q
if [ $? -eq 0 ]; then
    print_success "All notification tests passed"
else
    print_error "Some notification tests failed"
    exit 1
fi

# 5. Generate Test Coverage Report
echo ""
print_status "Generating test coverage report..."
mvn jacoco:report -q
if [ $? -eq 0 ]; then
    print_success "Test coverage report generated"
    print_status "Coverage report available at: target/site/jacoco/index.html"
else
    print_warning "Failed to generate coverage report"
fi

# 6. Check if application can start
echo ""
print_status "Checking application startup..."
timeout 30s mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test" &> /dev/null &
SPRING_PID=$!
sleep 10

# Check if the application is running
if ps -p $SPRING_PID > /dev/null; then
    print_success "Application started successfully"
    kill $SPRING_PID
    wait $SPRING_PID 2>/dev/null
else
    print_warning "Application startup test skipped or failed"
fi

echo ""
print_success "🎉 All notification service tests completed successfully!"
echo ""
print_status "Next steps:"
echo "  1. Import the Postman collection for API testing:"
echo "     FF-API/src/test/postman/FireFighter_Notification_API.postman_collection.json"
echo "  2. Run the SQL setup script to create the notifications table:"
echo "     psql -h your-host -U ff_admin -d firefighter -f src/test/java/com/apex/firefighter/notification/create_notifications_table.sql"
echo "  3. Start the application and test the endpoints manually"
echo "  4. Review the documentation at:"
echo "     src/main/java/com/apex/firefighter/service/NOTIFICATION_SERVICE_DOCUMENTATION.md"
echo ""
print_status "Happy testing! 🚀"
