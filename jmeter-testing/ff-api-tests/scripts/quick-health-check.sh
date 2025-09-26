#!/bin/bash

# Quick Health Check for FF-API
# Usage: ./quick-health-check.sh [server] [port]

set -e

# Default values
SERVER=${1:-localhost}
PORT=${2:-8080}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚒 FireFighter API Health Check${NC}"
echo -e "${BLUE}================================${NC}"
echo -e "🔍 Testing server: ${GREEN}$SERVER:$PORT${NC}"
echo -e ""

# Test endpoints
endpoints=(
    "/api/health"
    "/api/auth/simple-test"
    "/api/nlp/health"
    "/api/chatbot/health"
)

for endpoint in "${endpoints[@]}"; do
    echo -n "Testing $endpoint... "
    
    response=$(curl -s -w "%{http_code}" -o /dev/null "http://$SERVER:$PORT$endpoint" || echo "000")
    
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ OK${NC}"
    elif [ "$response" = "000" ]; then
        echo -e "${RED}❌ Connection Failed${NC}"
    else
        echo -e "${YELLOW}⚠️  HTTP $response${NC}"
    fi
done

echo -e ""
echo -e "${BLUE}🔍 Detailed health check:${NC}"
curl -s "http://$SERVER:$PORT/api/health" | jq . 2>/dev/null || echo "Could not parse JSON response"

echo -e ""
echo -e "${GREEN}Health check completed!${NC}"
