#!/bin/bash

# FireFighter API Stress Test Runner
# Usage: ./run-stress-test.sh [threads] [ramp-up] [duration] [server] [port]

set -e

# Default values for stress testing
THREADS=${1:-100}
RAMP_UP=${2:-120}
DURATION=${3:-600}
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
TEST_PLAN="../test-plans/FF-API-Stress-Test.jmx"

# Reports directory
REPORTS_DIR="../reports"
mkdir -p "$REPORTS_DIR"

echo -e "${RED}🔥 FireFighter API Stress Test${NC}"
echo -e "${RED}================================${NC}"
echo -e "⚠️  ${YELLOW}WARNING: This is a high-load stress test!${NC}"
echo -e "📊 Test Configuration:"
echo -e "   Threads: ${RED}$THREADS${NC}"
echo -e "   Ramp-up: ${RED}$RAMP_UP seconds${NC}"
echo -e "   Duration: ${RED}$DURATION seconds${NC}"
echo -e "   Server: ${RED}$SERVER:$PORT${NC}"
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

# Warning prompt
echo -e "${YELLOW}⚠️  This stress test will generate high load on your system.${NC}"
echo -e "${YELLOW}   Make sure you have sufficient resources available.${NC}"
read -p "Do you want to continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}Test cancelled.${NC}"
    exit 0
fi

# Check if JMeter exists
if [ ! -f "$JMETER_BIN" ]; then
    echo -e "${RED}❌ JMeter not found at $JMETER_BIN${NC}"
    exit 1
fi

# Generate timestamp for report files
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

echo -e "${RED}🚀 Starting stress test...${NC}"
echo -e "${YELLOW}📝 Test will run for $DURATION seconds with $THREADS concurrent users${NC}"
echo -e ""

# Run JMeter stress test
"$JMETER_BIN" -n -t "$TEST_PLAN" \
    -JSTRESS_THREADS="$THREADS" \
    -JSTRESS_RAMP_UP="$RAMP_UP" \
    -JSTRESS_DURATION="$DURATION" \
    -Jserver="$SERVER" \
    -Jport="$PORT" \
    -l "$REPORTS_DIR/stress-test-results-$TIMESTAMP.jtl" \
    -e -o "$REPORTS_DIR/stress-test-dashboard-$TIMESTAMP"

echo -e ""
echo -e "${GREEN}✅ Stress test completed!${NC}"
echo -e "${BLUE}📊 Results saved to:${NC}"
echo -e "   📄 Raw results: ${GREEN}$REPORTS_DIR/stress-test-results-$TIMESTAMP.jtl${NC}"
echo -e "   📈 Dashboard: ${GREEN}$REPORTS_DIR/stress-test-dashboard-$TIMESTAMP/index.html${NC}"
echo -e ""
echo -e "${YELLOW}💡 Open the dashboard in your browser:${NC}"
echo -e "   ${BLUE}file://$(pwd)/$REPORTS_DIR/stress-test-dashboard-$TIMESTAMP/index.html${NC}"
