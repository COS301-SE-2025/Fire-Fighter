#!/bin/bash

# FireFighter API Load Test Runner
# Usage: ./run-load-test.sh [threads] [ramp-up] [duration] [server] [port]

set -e

# Default values
THREADS=${1:-10}
RAMP_UP=${2:-30}
DURATION=${3:-300}
SERVER=${4:-localhost}
PORT=${5:-8080}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# JMeter path
JMETER_HOME="../../apache-jmeter-5.6.3"
JMETER_BIN="$JMETER_HOME/bin/jmeter"

# Test plan path
TEST_PLAN="../test-plans/FF-API-Load-Test.jmx"

# Reports directory
REPORTS_DIR="../reports"
mkdir -p "$REPORTS_DIR"

echo -e "${BLUE}🚒 FireFighter API Load Test${NC}"
echo -e "${BLUE}================================${NC}"
echo -e "📊 Test Configuration:"
echo -e "   Threads: ${GREEN}$THREADS${NC}"
echo -e "   Ramp-up: ${GREEN}$RAMP_UP seconds${NC}"
echo -e "   Duration: ${GREEN}$DURATION seconds${NC}"
echo -e "   Server: ${GREEN}$SERVER:$PORT${NC}"
echo -e ""

# Check if FF-API is running
echo -e "${YELLOW}🔍 Checking if FF-API is running...${NC}"
if curl -s "http://$SERVER:$PORT/api/health" > /dev/null; then
    echo -e "${GREEN}✅ FF-API is responding${NC}"
else
    echo -e "${RED}❌ FF-API is not responding at http://$SERVER:$PORT${NC}"
    echo -e "${YELLOW}💡 Please start your FF-API server first${NC}"
    exit 1
fi

# Check if JMeter exists
if [ ! -f "$JMETER_BIN" ]; then
    echo -e "${RED}❌ JMeter not found at $JMETER_BIN${NC}"
    exit 1
fi

# Generate timestamp for report files
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

echo -e "${YELLOW}🚀 Starting load test...${NC}"
echo -e "${BLUE}📝 Test will run for $DURATION seconds${NC}"
echo -e ""

# Run JMeter test
"$JMETER_BIN" -n -t "$TEST_PLAN" \
    -JTHREADS="$THREADS" \
    -JRAMP_UP="$RAMP_UP" \
    -JDURATION="$DURATION" \
    -Jserver="$SERVER" \
    -Jport="$PORT" \
    -l "$REPORTS_DIR/load-test-results-$TIMESTAMP.jtl" \
    -e -o "$REPORTS_DIR/load-test-dashboard-$TIMESTAMP"

echo -e ""
echo -e "${GREEN}✅ Load test completed!${NC}"
echo -e "${BLUE}📊 Results saved to:${NC}"
echo -e "   📄 Raw results: ${GREEN}$REPORTS_DIR/load-test-results-$TIMESTAMP.jtl${NC}"
echo -e "   📈 Dashboard: ${GREEN}$REPORTS_DIR/load-test-dashboard-$TIMESTAMP/index.html${NC}"
echo -e ""
echo -e "${YELLOW}💡 Open the dashboard in your browser:${NC}"
echo -e "   ${BLUE}file://$(pwd)/$REPORTS_DIR/load-test-dashboard-$TIMESTAMP/index.html${NC}"
