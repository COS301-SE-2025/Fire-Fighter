#!/bin/bash

# Fire-Fighter Develop Branch Build Monitor
# Run this script to check the status of your develop branch builds

JENKINS_URL="http://localhost:9080"
JOB_NAME="fire-fighter-develop"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "🔥 Fire-Fighter Develop Branch Build Monitor"
echo "=============================================="

# Get latest build info
echo -e "${BLUE}📊 Latest Build Status:${NC}"
curl -s "${JENKINS_URL}/job/${JOB_NAME}/lastBuild/api/json" | \
    jq -r '"Build #" + (.number|tostring) + " - " + .result + " (" + .timestamp + ")"' 2>/dev/null || \
    echo "Unable to fetch build status (check if Jenkins is running and jq is installed)"

echo ""

# Get build queue
echo -e "${BLUE}⏳ Build Queue:${NC}"
curl -s "${JENKINS_URL}/queue/api/json" | \
    jq -r '.items[] | select(.task.name=="'${JOB_NAME}'") | "Queued: " + .why' 2>/dev/null || \
    echo "No builds in queue"

echo ""

# Get recent builds
echo -e "${BLUE}📈 Recent Builds:${NC}"
curl -s "${JENKINS_URL}/job/${JOB_NAME}/api/json" | \
    jq -r '.builds[0:5][] | "#" + (.number|tostring) + " - " + (.result // "RUNNING")' 2>/dev/null || \
    echo "Unable to fetch recent builds"

echo ""

# Build trends
echo -e "${BLUE}📊 Build Statistics:${NC}"
echo "Job URL: ${JENKINS_URL}/job/${JOB_NAME}/"
echo "Console: ${JENKINS_URL}/job/${JOB_NAME}/lastBuild/console"
echo "Test Results: ${JENKINS_URL}/job/${JOB_NAME}/lastBuild/testReport/"

echo ""
echo -e "${GREEN}✅ Monitor script completed${NC}"
echo "Run this script regularly to monitor your develop branch builds"
