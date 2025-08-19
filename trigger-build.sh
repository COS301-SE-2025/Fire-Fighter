#!/bin/bash

# Fire-Fighter Manual Build Trigger Script
# Use this script to trigger Jenkins builds from command line

# Configuration
JENKINS_URL="http://localhost:9080"
JOB_NAME="fire-fighter-pipeline"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# Function to show usage
show_usage() {
    echo "🔥 Fire-Fighter Build Trigger"
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -q, --quick          Quick build (skip tests)"
    echo "  -m, --message TEXT   Custom build message"
    echo "  -s, --status         Show build status"
    echo "  -l, --logs           Show latest build logs"
    echo "  -h, --help           Show this help"
    echo ""
    echo "Examples:"
    echo "  $0                           # Normal build"
    echo "  $0 --quick                   # Quick build without tests"
    echo "  $0 -m \"Testing new feature\"  # Build with custom message"
    echo "  $0 --status                  # Check build status"
    echo "  $0 --logs                    # View latest build logs"
}

# Function to trigger build
trigger_build() {
    local skip_tests=$1
    local message=$2
    
    print_status "Triggering Fire-Fighter build..."
    echo "Jenkins URL: $JENKINS_URL"
    echo "Job: $JOB_NAME"
    echo "Skip Tests: $skip_tests"
    echo "Message: $message"
    echo ""
    
    # Check if Jenkins is accessible
    if ! curl -s "$JENKINS_URL" > /dev/null; then
        print_error "Cannot connect to Jenkins at $JENKINS_URL"
        print_error "Make sure Jenkins is running and accessible"
        exit 1
    fi
    
    # Trigger build with parameters
    local build_url="$JENKINS_URL/job/$JOB_NAME/buildWithParameters"
    local params="SKIP_TESTS=$skip_tests&BUILD_MESSAGE=$message"
    
    print_status "Sending build request..."
    
    # Use curl to trigger the build
    response=$(curl -s -w "%{http_code}" -X POST "$build_url" -d "$params" 2>/dev/null)
    http_code="${response: -3}"
    
    if [ "$http_code" = "201" ] || [ "$http_code" = "200" ]; then
        print_success "Build triggered successfully!"
        echo ""
        echo "🔗 Monitor your build at:"
        echo "   $JENKINS_URL/job/$JOB_NAME/"
        echo ""
        echo "⏳ Build should start within a few seconds..."
        
        # Wait a moment and show queue status
        sleep 3
        show_queue_status
        
    else
        print_error "Failed to trigger build (HTTP $http_code)"
        print_error "Check Jenkins job configuration and permissions"
        exit 1
    fi
}

# Function to show build status
show_build_status() {
    print_status "Checking build status..."
    
    # Get latest build info
    local api_url="$JENKINS_URL/job/$JOB_NAME/lastBuild/api/json"
    
    if build_info=$(curl -s "$api_url" 2>/dev/null); then
        # Parse JSON (basic parsing without jq)
        local build_number=$(echo "$build_info" | grep -o '"number":[0-9]*' | cut -d':' -f2)
        local result=$(echo "$build_info" | grep -o '"result":"[^"]*"' | cut -d'"' -f4)
        local building=$(echo "$build_info" | grep -o '"building":[^,]*' | cut -d':' -f2)
        
        echo "📊 Latest Build Status:"
        echo "   Build Number: #$build_number"
        
        if [ "$building" = "true" ]; then
            echo "   Status: 🔄 BUILDING"
        elif [ "$result" = "SUCCESS" ]; then
            echo "   Status: ✅ SUCCESS"
        elif [ "$result" = "FAILURE" ]; then
            echo "   Status: ❌ FAILURE"
        elif [ "$result" = "UNSTABLE" ]; then
            echo "   Status: ⚠️ UNSTABLE"
        else
            echo "   Status: ⏳ PENDING"
        fi
        
        echo "   URL: $JENKINS_URL/job/$JOB_NAME/$build_number/"
    else
        print_warning "Could not fetch build status"
    fi
}

# Function to show queue status
show_queue_status() {
    local queue_url="$JENKINS_URL/queue/api/json"
    
    if queue_info=$(curl -s "$queue_url" 2>/dev/null); then
        # Check if our job is in queue
        if echo "$queue_info" | grep -q "$JOB_NAME"; then
            print_status "Build is queued and will start soon..."
        else
            print_status "Build may have started already"
        fi
    fi
}

# Function to show latest logs
show_latest_logs() {
    print_status "Fetching latest build logs..."
    
    local console_url="$JENKINS_URL/job/$JOB_NAME/lastBuild/consoleText"
    
    if logs=$(curl -s "$console_url" 2>/dev/null); then
        echo "📋 Latest Build Logs (last 50 lines):"
        echo "=================================="
        echo "$logs" | tail -50
        echo "=================================="
        echo "🔗 Full logs: $JENKINS_URL/job/$JOB_NAME/lastBuild/console"
    else
        print_warning "Could not fetch build logs"
    fi
}

# Parse command line arguments
SKIP_TESTS="false"
BUILD_MESSAGE="Manual build triggered from command line"

while [[ $# -gt 0 ]]; do
    case $1 in
        -q|--quick)
            SKIP_TESTS="true"
            BUILD_MESSAGE="Quick build (tests skipped)"
            shift
            ;;
        -m|--message)
            BUILD_MESSAGE="$2"
            shift 2
            ;;
        -s|--status)
            show_build_status
            exit 0
            ;;
        -l|--logs)
            show_latest_logs
            exit 0
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Main execution
echo "🔥 Fire-Fighter Build Trigger Script"
echo "===================================="

# Trigger the build
trigger_build "$SKIP_TESTS" "$BUILD_MESSAGE"
