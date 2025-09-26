#!/bin/bash

# Simple FF-API Load Test Runner
# Usage: ./run-simple-test.sh [threads] [rampup] [duration]

set -e

# Default values
THREADS=${1:-3}
RAMPUP=${2:-15}
DURATION=${3:-60}
SERVER="localhost"
PORT="8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚒 FireFighter API Simple Load Test${NC}"
echo -e "${BLUE}====================================${NC}"
echo -e "${YELLOW}📊 Test Configuration:${NC}"
echo -e "   Threads: ${GREEN}$THREADS${NC}"
echo -e "   Ramp-up: ${GREEN}$RAMPUP${NC} seconds"
echo -e "   Duration: ${GREEN}$DURATION${NC} seconds"
echo -e "   Server: ${GREEN}$SERVER:$PORT${NC}"
echo -e ""

# Check if FF-API is running
echo -e "${YELLOW}🔍 Checking if FF-API is running...${NC}"
if curl -s "http://$SERVER:$PORT/api/health" > /dev/null; then
    echo -e "${GREEN}✅ FF-API is responding${NC}"
else
    echo -e "${RED}❌ FF-API is not responding at http://$SERVER:$PORT${NC}"
    echo -e "${YELLOW}💡 Please start your FF-API server first:${NC}"
    echo -e "   cd ../../FF-API && mvn spring-boot:run"
    exit 1
fi

# Create reports directory
mkdir -p ../reports

# Generate timestamp for unique filenames
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_FILE="../reports/simple-test-results-$TIMESTAMP.jtl"
DASHBOARD_DIR="../reports/simple-test-dashboard-$TIMESTAMP"

echo -e "${YELLOW}🚀 Starting simple load test...${NC}"
echo -e "${BLUE}📝 Test will run for $DURATION seconds${NC}"
echo -e ""

# Run JMeter test
cd ../../apache-jmeter-5.6.3/bin

./jmeter -n -t ../../ff-api-tests/test-plans/FF-API-Simple-Test.jmx \
  -Jthreads=$THREADS \
  -Jrampup=$RAMPUP \
  -Jduration=$DURATION \
  -l "../../ff-api-tests/reports/simple-test-results-$TIMESTAMP.jtl" \
  -e -o "../../ff-api-tests/reports/simple-test-dashboard-$TIMESTAMP"

# Check if test completed successfully
if [ $? -eq 0 ]; then
    echo -e ""
    echo -e "${GREEN}✅ Simple load test completed successfully!${NC}"
    echo -e ""
    echo -e "${YELLOW}📊 Results saved to:${NC}"
    echo -e "   📄 Raw results: ${BLUE}reports/simple-test-results-$TIMESTAMP.jtl${NC}"
    echo -e "   📈 Dashboard: ${BLUE}reports/simple-test-dashboard-$TIMESTAMP/index.html${NC}"
    echo -e ""
    echo -e "${YELLOW}💡 Open the dashboard in your browser:${NC}"
    echo -e "   ${GREEN}file://$(pwd)/../../ff-api-tests/reports/simple-test-dashboard-$TIMESTAMP/index.html${NC}"
    echo -e ""
    echo -e "${YELLOW}📋 Quick stats:${NC}"
    
    # Show quick statistics
    if [ -f "../../ff-api-tests/reports/simple-test-results-$TIMESTAMP.jtl" ]; then
        TOTAL_REQUESTS=$(tail -n +2 "../../ff-api-tests/reports/simple-test-results-$TIMESTAMP.jtl" | wc -l)
        SUCCESSFUL_REQUESTS=$(tail -n +2 "../../ff-api-tests/reports/simple-test-results-$TIMESTAMP.jtl" | grep ",true," | wc -l)
        FAILED_REQUESTS=$(tail -n +2 "../../ff-api-tests/reports/simple-test-results-$TIMESTAMP.jtl" | grep ",false," | wc -l)
        
        echo -e "   📊 Total requests: ${GREEN}$TOTAL_REQUESTS${NC}"
        echo -e "   ✅ Successful: ${GREEN}$SUCCESSFUL_REQUESTS${NC}"
        echo -e "   ❌ Failed: ${RED}$FAILED_REQUESTS${NC}"
        
        if [ $TOTAL_REQUESTS -gt 0 ]; then
            SUCCESS_RATE=$(echo "scale=2; $SUCCESSFUL_REQUESTS * 100 / $TOTAL_REQUESTS" | bc -l 2>/dev/null || echo "N/A")
            echo -e "   📈 Success rate: ${GREEN}$SUCCESS_RATE%${NC}"
        fi
    fi
    
else
    echo -e ""
    echo -e "${RED}❌ Load test failed!${NC}"
    echo -e "${YELLOW}💡 Check the JMeter output above for error details${NC}"
    exit 1
fi
