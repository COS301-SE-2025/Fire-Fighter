#!/bin/bash

#################################################################
# 🧪 Registration Backend Test Runner
# Purpose: Run all registration-related tests with detailed reporting
# Author: GitHub Copilot
# Date: October 19, 2025
#################################################################

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print header
print_header() {
    echo ""
    echo "=================================================================="
    echo -e "${BLUE}$1${NC}"
    echo "=================================================================="
    echo ""
}

# Print section
print_section() {
    echo ""
    echo -e "${YELLOW}▶ $1${NC}"
    echo "------------------------------------------------------------------"
}

# Print success
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Print error
print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Print info
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Navigate to project directory
cd "$(dirname "$0")"

print_header "🧪 REGISTRATION BACKEND TEST SUITE"

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    print_error "Maven wrapper not found!"
    print_info "Please run this script from the FF-API directory"
    exit 1
fi

# Make mvnw executable
chmod +x ./mvnw

# Function to run tests
run_tests() {
    local test_type=$1
    local test_pattern=$2
    local description=$3
    
    print_section "$description"
    
    if ./mvnw test -Dtest="$test_pattern" -q; then
        print_success "$description passed!"
        return 0
    else
        print_error "$description failed!"
        return 1
    fi
}

# Track failures
FAILED_TESTS=()

# ================================================================
# UNIT TESTS
# ================================================================

print_header "1️⃣  UNIT TESTS"

# Registration Service Tests
if ! run_tests "unit" "RegistrationServiceTest" "Registration Service Unit Tests"; then
    FAILED_TESTS+=("Registration Service Unit Tests")
fi

# Access Group Service Tests
if ! run_tests "unit" "AccessGroupServiceTest" "Access Group Service Unit Tests"; then
    FAILED_TESTS+=("Access Group Service Unit Tests")
fi

# Cleanup Service Tests
if ! run_tests "unit" "RegistrationCleanupServiceTest" "Cleanup Service Unit Tests"; then
    FAILED_TESTS+=("Cleanup Service Unit Tests")
fi

# ================================================================
# INTEGRATION TESTS
# ================================================================

print_header "2️⃣  INTEGRATION TESTS"

# Registration Controller Integration Tests
if ! run_tests "integration" "RegistrationControllerIntegrationTest" "Registration Controller Integration Tests"; then
    FAILED_TESTS+=("Registration Controller Integration Tests")
fi

# ================================================================
# END-TO-END TESTS
# ================================================================

print_header "3️⃣  END-TO-END TESTS"

# Complete Registration Workflows
if ! run_tests "e2e" "RegistrationE2ETest" "Registration End-to-End Tests"; then
    FAILED_TESTS+=("Registration E2E Tests")
fi

# ================================================================
# ALL REGISTRATION TESTS
# ================================================================

print_header "4️⃣  ALL REGISTRATION TESTS (COMBINED)"

print_section "Running all registration tests together..."

if ./mvnw test -Dtest="*Registration*Test,*AccessGroup*Test,*Cleanup*Test" > /tmp/test-output.log 2>&1; then
    print_success "All registration tests passed!"
    
    # Show summary
    echo ""
    print_info "Test Summary:"
    grep "Tests run:" /tmp/test-output.log | tail -1
    
else
    print_error "Some tests failed!"
    
    # Show failures
    echo ""
    print_info "Failed Test Details:"
    grep -A 5 "FAILURE" /tmp/test-output.log || echo "See /tmp/test-output.log for details"
fi

# ================================================================
# COVERAGE REPORT
# ================================================================

print_header "5️⃣  CODE COVERAGE REPORT"

print_section "Generating JaCoCo coverage report..."

if ./mvnw jacoco:report -q; then
    print_success "Coverage report generated!"
    print_info "Open target/site/jacoco/index.html to view coverage"
    
    # Try to extract coverage percentage
    if [ -f "target/site/jacoco/index.html" ]; then
        COVERAGE=$(grep -oP 'Total.*?(\d+)%' target/site/jacoco/index.html | head -1 || echo "N/A")
        print_info "Overall Coverage: $COVERAGE"
    fi
else
    print_error "Failed to generate coverage report"
fi

# ================================================================
# FINAL SUMMARY
# ================================================================

print_header "📊 TEST EXECUTION SUMMARY"

if [ ${#FAILED_TESTS[@]} -eq 0 ]; then
    print_success "ALL TESTS PASSED! 🎉"
    echo ""
    echo "  ✅ Unit Tests: PASSED"
    echo "  ✅ Integration Tests: PASSED"
    echo "  ✅ End-to-End Tests: PASSED"
    echo ""
    print_info "Total test suites: 5"
    print_info "Total test methods: ~50"
    echo ""
    exit 0
else
    print_error "SOME TESTS FAILED!"
    echo ""
    echo "Failed test suites:"
    for test in "${FAILED_TESTS[@]}"; do
        echo "  ❌ $test"
    done
    echo ""
    print_info "Check /tmp/test-output.log for details"
    print_info "Run individual tests with: ./mvnw test -Dtest=<TestClassName>"
    echo ""
    exit 1
fi
