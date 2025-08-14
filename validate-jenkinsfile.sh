#!/bin/bash

# Jenkinsfile Syntax Validation Script
# Use this to validate your Jenkinsfile syntax before committing

JENKINS_URL="http://localhost:9080"
JENKINSFILE="Jenkinsfile"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

echo "🔍 Jenkinsfile Syntax Validator"
echo "==============================="

# Check if Jenkinsfile exists
if [ ! -f "$JENKINSFILE" ]; then
    print_error "Jenkinsfile not found in current directory"
    exit 1
fi

print_status "Found Jenkinsfile: $JENKINSFILE"

# Basic syntax checks
print_status "Running basic syntax checks..."

# Check for common syntax issues
errors=0

# Check for proper pipeline structure
if ! grep -q "^pipeline {" "$JENKINSFILE"; then
    print_error "Missing 'pipeline {' declaration"
    ((errors++))
fi

# Check for agent declaration
if ! grep -q "agent " "$JENKINSFILE"; then
    print_error "Missing 'agent' declaration"
    ((errors++))
fi

# Check for stages block
if ! grep -q "stages {" "$JENKINSFILE"; then
    print_error "Missing 'stages {' block"
    ((errors++))
fi

# Check for balanced braces
open_braces=$(grep -o "{" "$JENKINSFILE" | wc -l)
close_braces=$(grep -o "}" "$JENKINSFILE" | wc -l)

if [ "$open_braces" -ne "$close_braces" ]; then
    print_error "Unbalanced braces: $open_braces opening, $close_braces closing"
    ((errors++))
fi

# Check for proper string quoting in environment block
if grep -n "environment {" "$JENKINSFILE" > /dev/null; then
    print_status "Checking environment block syntax..."
    
    # Extract environment block and check for unquoted values
    awk '/environment {/,/}/' "$JENKINSFILE" | grep -n "=" | while read line; do
        if echo "$line" | grep -E "=.*[^'\"].*\?" > /dev/null; then
            print_warning "Possible unquoted ternary operator in environment block: $line"
        fi
    done
fi

# Check for proper when condition syntax
if grep -n "when {" "$JENKINSFILE" > /dev/null; then
    print_status "Checking when conditions..."
    
    # Look for common when condition issues
    if grep -A 5 "when {" "$JENKINSFILE" | grep -E "not \{ params\." > /dev/null; then
        if ! grep -A 5 "when {" "$JENKINSFILE" | grep -E "not \{ \s*params\..*== " > /dev/null; then
            print_warning "Check 'not' condition syntax - may need explicit comparison"
        fi
    fi
fi

# Check for proper parameter definitions
if grep -n "parameters {" "$JENKINSFILE" > /dev/null; then
    print_status "Checking parameters block..."
    
    # Check for proper parameter syntax
    param_count=$(awk '/parameters {/,/}/' "$JENKINSFILE" | grep -c "Param(")
    if [ "$param_count" -gt 0 ]; then
        print_success "Found $param_count parameter(s)"
    fi
fi

# Try Jenkins API validation if available
print_status "Attempting Jenkins API validation..."

if curl -s "$JENKINS_URL" > /dev/null 2>&1; then
    print_status "Jenkins is accessible, attempting API validation..."
    
    # Use Jenkins API to validate pipeline syntax
    validation_url="$JENKINS_URL/pipeline-model-converter/validate"
    
    if response=$(curl -s -X POST -F "jenkinsfile=<$JENKINSFILE" "$validation_url" 2>/dev/null); then
        if echo "$response" | grep -q "Jenkinsfile successfully validated"; then
            print_success "Jenkins API validation passed!"
        else
            print_error "Jenkins API validation failed:"
            echo "$response"
            ((errors++))
        fi
    else
        print_warning "Could not validate via Jenkins API"
    fi
else
    print_warning "Jenkins not accessible for API validation"
    print_warning "Start Jenkins to enable full validation"
fi

# Summary
echo ""
echo "================================="
if [ $errors -eq 0 ]; then
    print_success "Jenkinsfile validation completed successfully!"
    echo "✅ No syntax errors detected"
    echo "✅ Basic structure looks good"
    echo ""
    echo "You can now:"
    echo "1. Commit your Jenkinsfile changes"
    echo "2. Trigger a build in Jenkins"
    echo "3. Monitor the build results"
else
    print_error "Found $errors potential issue(s)"
    echo "❌ Please fix the issues above before committing"
    echo ""
    echo "Common fixes:"
    echo "1. Check brace balancing"
    echo "2. Quote environment variable values"
    echo "3. Use proper when condition syntax"
    echo "4. Verify parameter definitions"
fi

echo ""
echo "🔗 Useful links:"
echo "   Jenkins Pipeline Syntax: https://www.jenkins.io/doc/book/pipeline/syntax/"
echo "   Pipeline Examples: https://www.jenkins.io/doc/pipeline/examples/"

exit $errors
