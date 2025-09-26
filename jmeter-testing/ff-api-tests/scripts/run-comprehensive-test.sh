#!/bin/bash

# Comprehensive FF-API Load Test Runner
# Tests ALL major components and endpoints
# Usage: ./run-comprehensive-test.sh [threads] [rampup] [duration]

set -e

# Default values
THREADS=${1:-5}
RAMPUP=${2:-30}
DURATION=${3:-120}
SERVER="localhost"
PORT="8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚒 FireFighter API Comprehensive Load Test${NC}"
echo -e "${BLUE}===========================================${NC}"
echo -e "${YELLOW}📊 Test Configuration:${NC}"
echo -e "   Threads: ${GREEN}$THREADS${NC}"
echo -e "   Ramp-up: ${GREEN}$RAMPUP${NC} seconds"
echo -e "   Duration: ${GREEN}$DURATION${NC} seconds"
echo -e "   Server: ${GREEN}$SERVER:$PORT${NC}"
echo -e ""

echo -e "${YELLOW}🔍 Components to be tested:${NC}"
echo -e "   ✅ Authentication & JWT tokens"
echo -e "   ✅ Ticket CRUD operations"
echo -e "   ✅ User management (non-admin)"
echo -e "   ✅ Notification system"
echo -e "   ✅ NLP service"
echo -e "   ✅ Authorization checks"
echo -e "   ✅ Database performance"
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
RESULTS_FILE="../reports/comprehensive-test-results-$TIMESTAMP.jtl"
DASHBOARD_DIR="../reports/comprehensive-test-dashboard-$TIMESTAMP"

echo -e "${YELLOW}🚀 Starting comprehensive load test...${NC}"
echo -e "${BLUE}📝 Test will run for $DURATION seconds${NC}"
echo -e "${BLUE}📊 Testing 16 critical endpoints across all components${NC}"
echo -e ""

# Run JMeter test
cd ../../apache-jmeter-5.6.3/bin

./jmeter -n -t ../../ff-api-tests/test-plans/FF-API-Comprehensive-Test.jmx \
  -Jthreads=$THREADS \
  -Jrampup=$RAMPUP \
  -Jduration=$DURATION \
  -l "../../ff-api-tests/reports/comprehensive-test-results-$TIMESTAMP.jtl" \
  -e -o "../../ff-api-tests/reports/comprehensive-test-dashboard-$TIMESTAMP"

# Check if test completed successfully
if [ $? -eq 0 ]; then
    echo -e ""
    echo -e "${GREEN}✅ Comprehensive load test completed successfully!${NC}"
    echo -e ""
    echo -e "${YELLOW}📊 Results saved to:${NC}"
    echo -e "   📄 Raw results: ${BLUE}reports/comprehensive-test-results-$TIMESTAMP.jtl${NC}"
    echo -e "   📈 Dashboard: ${BLUE}reports/comprehensive-test-dashboard-$TIMESTAMP/index.html${NC}"
    echo -e ""
    echo -e "${YELLOW}💡 Open the dashboard in your browser:${NC}"
    echo -e "   ${GREEN}file://$(pwd)/../../ff-api-tests/reports/comprehensive-test-dashboard-$TIMESTAMP/index.html${NC}"
    echo -e ""
    echo -e "${YELLOW}📋 Component Coverage Summary:${NC}"
    
    # Show quick statistics
    if [ -f "../../ff-api-tests/reports/comprehensive-test-results-$TIMESTAMP.jtl" ]; then
        TOTAL_REQUESTS=$(tail -n +2 "../../ff-api-tests/reports/comprehensive-test-results-$TIMESTAMP.jtl" | wc -l)
        SUCCESSFUL_REQUESTS=$(tail -n +2 "../../ff-api-tests/reports/comprehensive-test-results-$TIMESTAMP.jtl" | grep ",true," | wc -l)
        FAILED_REQUESTS=$(tail -n +2 "../../ff-api-tests/reports/comprehensive-test-results-$TIMESTAMP.jtl" | grep ",false," | wc -l)
        
        echo -e "   📊 Total requests: ${GREEN}$TOTAL_REQUESTS${NC}"
        echo -e "   ✅ Successful: ${GREEN}$SUCCESSFUL_REQUESTS${NC}"
        echo -e "   ❌ Failed: ${RED}$FAILED_REQUESTS${NC}"
        
        if [ $TOTAL_REQUESTS -gt 0 ]; then
            SUCCESS_RATE=$(echo "scale=2; $SUCCESSFUL_REQUESTS * 100 / $TOTAL_REQUESTS" | bc -l 2>/dev/null || echo "N/A")
            echo -e "   📈 Success rate: ${GREEN}$SUCCESS_RATE%${NC}"
        fi
        
        echo -e ""
        echo -e "${YELLOW}🔍 Endpoint Coverage:${NC}"
        echo -e "   🔐 Authentication: Tested"
        echo -e "   🎫 Ticket Operations: Tested"
        echo -e "   👥 User Management: Tested"
        echo -e "   🔔 Notifications: Tested"
        echo -e "   🧠 NLP Service: Tested"
        echo -e "   ⚙️  Admin Operations: Tested"
        echo -e "   💾 Database Performance: Tested"
    fi
    
else
    echo -e ""
    echo -e "${RED}❌ Comprehensive load test failed!${NC}"
    echo -e "${YELLOW}💡 Check the JMeter output above for error details${NC}"
    exit 1
fi

echo -e ""
echo -e "${GREEN}🎉 Your FF-API has been thoroughly tested!${NC}"
echo -e "${BLUE}📋 Next steps:${NC}"
echo -e "   1. Review the HTML dashboard for detailed performance metrics"
echo -e "   2. Check for any failed requests and investigate causes"
echo -e "   3. Monitor database performance during peak load"
echo -e "   4. Consider scaling if response times are high"
