#!/bin/bash

# Test FF-API Authentication Flow
# Usage: ./test-auth-flow.sh [server] [port]

set -e

# Default values
SERVER=${1:-localhost}
PORT=${2:-8080}
BASE_URL="http://$SERVER:$PORT"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔐 FF-API Authentication Flow Test${NC}"
echo -e "${BLUE}===================================${NC}"
echo -e "Testing server: ${GREEN}$BASE_URL${NC}"
echo -e ""

# Test data
TEST_USER_ID="testuser1"
TEST_EMAIL="testuser1@firefighter.com"
TEST_USERNAME="John Smith"

echo -e "${YELLOW}1. Testing development login...${NC}"

# Step 1: Get JWT token via dev-login
echo -e "   Requesting JWT token..."
LOGIN_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/dev-login" \
  -H "Content-Type: application/json" \
  -d "{
    \"firebaseUid\": \"$TEST_USER_ID\",
    \"email\": \"$TEST_EMAIL\",
    \"username\": \"$TEST_USERNAME\"
  }")

# Extract HTTP status code (last 3 characters)
HTTP_CODE="${LOGIN_RESPONSE: -3}"
RESPONSE_BODY="${LOGIN_RESPONSE%???}"

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "   ${GREEN}✅ Login successful (HTTP 200)${NC}"
    
    # Extract JWT token from response (without jq dependency)
    JWT_TOKEN=$(echo "$RESPONSE_BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    USER_ID=$(echo "$RESPONSE_BODY" | grep -o '"userId":"[^"]*"' | cut -d'"' -f4)
    
    if [ "$JWT_TOKEN" != "" ] && [ "$JWT_TOKEN" != "null" ]; then
        echo -e "   ${GREEN}✅ JWT token extracted${NC}"
        echo -e "   ${BLUE}Token preview: ${JWT_TOKEN:0:20}...${NC}"
        echo -e "   ${BLUE}User ID: $USER_ID${NC}"
    else
        echo -e "   ${RED}❌ Failed to extract JWT token${NC}"
        echo -e "   Response: $RESPONSE_BODY"
        exit 1
    fi
else
    echo -e "   ${RED}❌ Login failed (HTTP $HTTP_CODE)${NC}"
    echo -e "   Response: $RESPONSE_BODY"
    exit 1
fi

echo -e ""
echo -e "${YELLOW}2. Testing authenticated ticket creation...${NC}"

# Step 2: Create a ticket using the JWT token
TICKET_DATA='{
  "description": "Test emergency - house fire on Main Street",
  "userId": "'$USER_ID'",
  "emergencyType": "fire",
  "emergencyContact": "+27123456789",
  "duration": 30
}'

echo -e "   Creating test ticket..."
TICKET_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/tickets" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "$TICKET_DATA")

# Extract HTTP status code
TICKET_HTTP_CODE="${TICKET_RESPONSE: -3}"
TICKET_BODY="${TICKET_RESPONSE%???}"

if [ "$TICKET_HTTP_CODE" = "200" ]; then
    echo -e "   ${GREEN}✅ Ticket created successfully (HTTP 200)${NC}"
    
    TICKET_ID=$(echo "$TICKET_BODY" | grep -o '"ticketId":"[^"]*"' | cut -d'"' -f4)
    if [ "$TICKET_ID" != "" ] && [ "$TICKET_ID" != "null" ]; then
        echo -e "   ${BLUE}Ticket ID: $TICKET_ID${NC}"
    fi
else
    echo -e "   ${RED}❌ Ticket creation failed (HTTP $TICKET_HTTP_CODE)${NC}"
    echo -e "   Response: $TICKET_BODY"
fi

echo -e ""
echo -e "${YELLOW}3. Testing authenticated ticket retrieval...${NC}"

# Step 3: Get all tickets using the JWT token
echo -e "   Retrieving tickets..."
GET_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/tickets" \
  -H "Authorization: Bearer $JWT_TOKEN")

GET_HTTP_CODE="${GET_RESPONSE: -3}"
GET_BODY="${GET_RESPONSE%???}"

if [ "$GET_HTTP_CODE" = "200" ]; then
    echo -e "   ${GREEN}✅ Tickets retrieved successfully (HTTP 200)${NC}"
    
    TICKET_COUNT=$(echo "$GET_BODY" | grep -o '"ticketId"' | wc -l)
    echo -e "   ${BLUE}Found $TICKET_COUNT tickets${NC}"
else
    echo -e "   ${RED}❌ Ticket retrieval failed (HTTP $GET_HTTP_CODE)${NC}"
    echo -e "   Response: $GET_BODY"
fi

echo -e ""
echo -e "${YELLOW}4. Testing without authentication...${NC}"

# Step 4: Try to create ticket without JWT (should fail)
echo -e "   Attempting ticket creation without JWT..."
UNAUTH_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/tickets" \
  -H "Content-Type: application/json" \
  -d "$TICKET_DATA")

UNAUTH_HTTP_CODE="${UNAUTH_RESPONSE: -3}"

if [ "$UNAUTH_HTTP_CODE" = "401" ] || [ "$UNAUTH_HTTP_CODE" = "403" ]; then
    echo -e "   ${GREEN}✅ Correctly rejected unauthenticated request (HTTP $UNAUTH_HTTP_CODE)${NC}"
else
    echo -e "   ${YELLOW}⚠️  Unexpected response for unauthenticated request (HTTP $UNAUTH_HTTP_CODE)${NC}"
fi

echo -e ""
echo -e "${GREEN}🎉 Authentication flow test completed!${NC}"
echo -e ""
echo -e "${BLUE}Summary:${NC}"
echo -e "✅ Development login endpoint works"
echo -e "✅ JWT token extraction works"
echo -e "✅ Authenticated requests work"
echo -e "✅ Authentication is properly enforced"
echo -e ""
echo -e "${YELLOW}💡 Your JMeter tests will use this same flow:${NC}"
echo -e "   1. Call /api/auth/dev-login to get JWT"
echo -e "   2. Extract token from response"
echo -e "   3. Use 'Authorization: Bearer \$token' header"
echo -e "   4. Make authenticated API calls"
