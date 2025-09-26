#!/bin/bash

# Cleanup Test Data from FF-API Database
# Usage: ./cleanup-test-data.sh [database_name] [username] [password]

set -e

# Default values (adjust for your setup)
DB_NAME=${1:-firefighter_db}
DB_USER=${2:-your_db_user}
DB_PASS=${3:-your_db_password}
DB_HOST=${4:-localhost}
DB_PORT=${5:-5432}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧹 FF-API Test Data Cleanup${NC}"
echo -e "${BLUE}============================${NC}"
echo -e "Database: ${GREEN}$DB_NAME@$DB_HOST:$DB_PORT${NC}"
echo -e ""

# Warning prompt
echo -e "${YELLOW}⚠️  This will delete test data from your database!${NC}"
echo -e "${YELLOW}   Test tickets with userId starting with 'test-user-'${NC}"
echo -e "${YELLOW}   Test notifications related to test users${NC}"
read -p "Do you want to continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}Cleanup cancelled.${NC}"
    exit 0
fi

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo -e "${RED}❌ psql not found. Please install PostgreSQL client tools.${NC}"
    exit 1
fi

echo -e "${YELLOW}🔍 Checking test data...${NC}"

# Count test tickets
TEST_TICKETS=$(PGPASSWORD=$DB_PASS psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM tickets WHERE user_id LIKE 'test-user-%';" 2>/dev/null || echo "0")
TEST_TICKETS=$(echo $TEST_TICKETS | tr -d ' ')

echo -e "Found ${YELLOW}$TEST_TICKETS${NC} test tickets to delete"

if [ "$TEST_TICKETS" -eq 0 ]; then
    echo -e "${GREEN}✅ No test data found. Database is clean!${NC}"
    exit 0
fi

echo -e ""
echo -e "${YELLOW}🗑️  Deleting test data...${NC}"

# Delete test tickets
echo -e "Deleting test tickets..."
PGPASSWORD=$DB_PASS psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "DELETE FROM tickets WHERE user_id LIKE 'test-user-%';" > /dev/null

# Delete test notifications (if they exist)
echo -e "Deleting test notifications..."
PGPASSWORD=$DB_PASS psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "DELETE FROM notifications WHERE user_id LIKE 'test-user-%';" > /dev/null 2>&1 || true

# Delete test users (if they exist in your user table)
echo -e "Deleting test users..."
PGPASSWORD=$DB_PASS psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "DELETE FROM users WHERE firebase_uid LIKE 'test-user-%';" > /dev/null 2>&1 || true

echo -e ""
echo -e "${GREEN}✅ Test data cleanup completed!${NC}"
echo -e "${BLUE}Deleted $TEST_TICKETS test tickets and related data${NC}"
echo -e ""
echo -e "${YELLOW}💡 Your database is now clean and ready for production use.${NC}"
