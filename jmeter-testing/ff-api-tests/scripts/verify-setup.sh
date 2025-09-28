#!/bin/bash

# Verify JMeter Setup for FF-API Testing
# Usage: ./verify-setup.sh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 FireFighter API JMeter Setup Verification${NC}"
echo -e "${BLUE}==============================================${NC}"
echo -e ""

# Check JMeter installation
echo -e "${YELLOW}1. Checking JMeter installation...${NC}"
JMETER_BIN="../../apache-jmeter-5.6.3/bin/jmeter"
if [ -f "$JMETER_BIN" ]; then
    echo -e "   ${GREEN}✅ JMeter found at $JMETER_BIN${NC}"
    JMETER_VERSION=$("$JMETER_BIN" --version 2>&1 | head -1 || echo "Unknown version")
    echo -e "   ${BLUE}📋 $JMETER_VERSION${NC}"
else
    echo -e "   ${RED}❌ JMeter not found at $JMETER_BIN${NC}"
    echo -e "   ${YELLOW}💡 Please ensure JMeter is properly extracted${NC}"
fi

# Check Java
echo -e ""
echo -e "${YELLOW}2. Checking Java installation...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo -e "   ${GREEN}✅ Java found${NC}"
    echo -e "   ${BLUE}📋 $JAVA_VERSION${NC}"
else
    echo -e "   ${RED}❌ Java not found${NC}"
    echo -e "   ${YELLOW}💡 Please install Java 8 or higher${NC}"
fi

# Check test plans
echo -e ""
echo -e "${YELLOW}3. Checking test plans...${NC}"
test_plans=(
    "../test-plans/FF-API-Load-Test.jmx"
    "../test-plans/FF-API-Stress-Test.jmx"
)

for plan in "${test_plans[@]}"; do
    if [ -f "$plan" ]; then
        echo -e "   ${GREEN}✅ $(basename "$plan")${NC}"
    else
        echo -e "   ${RED}❌ $(basename "$plan") not found${NC}"
    fi
done

# Check test data
echo -e ""
echo -e "${YELLOW}4. Checking test data files...${NC}"
test_data=(
    "../test-data/users.csv"
    "../test-data/tickets.csv"
    "../test-data/nlp-queries.csv"
)

for data in "${test_data[@]}"; do
    if [ -f "$data" ]; then
        lines=$(wc -l < "$data")
        echo -e "   ${GREEN}✅ $(basename "$data") ($lines lines)${NC}"
    else
        echo -e "   ${RED}❌ $(basename "$data") not found${NC}"
    fi
done

# Check scripts
echo -e ""
echo -e "${YELLOW}5. Checking test scripts...${NC}"
scripts=(
    "./run-load-test.sh"
    "./run-stress-test.sh"
    "./quick-health-check.sh"
)

for script in "${scripts[@]}"; do
    if [ -f "$script" ] && [ -x "$script" ]; then
        echo -e "   ${GREEN}✅ $(basename "$script") (executable)${NC}"
    elif [ -f "$script" ]; then
        echo -e "   ${YELLOW}⚠️  $(basename "$script") (not executable)${NC}"
        echo -e "      ${BLUE}Run: chmod +x $script${NC}"
    else
        echo -e "   ${RED}❌ $(basename "$script") not found${NC}"
    fi
done

# Check directories
echo -e ""
echo -e "${YELLOW}6. Checking directory structure...${NC}"
directories=(
    "../reports"
    "../test-plans"
    "../test-data"
    "."
)

for dir in "${directories[@]}"; do
    if [ -d "$dir" ]; then
        echo -e "   ${GREEN}✅ $(basename "$dir")/ directory exists${NC}"
    else
        echo -e "   ${YELLOW}⚠️  $(basename "$dir")/ directory missing${NC}"
        mkdir -p "$dir"
        echo -e "      ${BLUE}Created directory${NC}"
    fi
done

# Summary
echo -e ""
echo -e "${BLUE}📋 Setup Summary${NC}"
echo -e "${BLUE}=================${NC}"

if [ -f "$JMETER_BIN" ] && command -v java &> /dev/null; then
    echo -e "${GREEN}✅ Core requirements met${NC}"
    echo -e ""
    echo -e "${YELLOW}🚀 Ready to run tests!${NC}"
    echo -e ""
    echo -e "${BLUE}Next steps:${NC}"
    echo -e "1. Start your FF-API server: ${GREEN}cd ../../FF-API && mvn spring-boot:run${NC}"
    echo -e "2. Run health check: ${GREEN}./quick-health-check.sh${NC}"
    echo -e "3. Run load test: ${GREEN}./run-load-test.sh${NC}"
else
    echo -e "${RED}❌ Setup incomplete${NC}"
    echo -e "${YELLOW}Please address the issues above before running tests${NC}"
fi

echo -e ""
