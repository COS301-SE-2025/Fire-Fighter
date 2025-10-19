#!/bin/bash

# FireFighter Registration API Testing Script
# Run this script after the application has fully started

echo "🚀 FireFighter Registration API Testing"
echo "========================================="
echo ""

# Configuration
BASE_URL="http://localhost:8080"
ADMIN_UID="admin-test-uid"  # Replace with actual admin Firebase UID
TEST_UID="test-user-$(date +%s)"  # Unique test UID

echo "📋 Test Configuration:"
echo "  Base URL: $BASE_URL"
echo "  Admin UID: $ADMIN_UID"
echo "  Test UID: $TEST_UID"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print test headers
print_test() {
    echo ""
    echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo "${BLUE}TEST $1: $2${NC}"
    echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

# Function to check response
check_response() {
    if [ $1 -eq 0 ]; then
        echo "${GREEN}✅ SUCCESS${NC}"
    else
        echo "${RED}❌ FAILED${NC}"
    fi
}

# Wait for application to start
echo "${YELLOW}⏳ Waiting for application to start...${NC}"
sleep 5

# Test health check
print_test "0" "Health Check"
echo "GET $BASE_URL/actuator/health"
curl -s "$BASE_URL/actuator/health"
echo ""
check_response $?

# ============================================
# REGISTRATION FLOW TESTS
# ============================================

# Test 1: Submit Registration
print_test "1" "Submit New Registration"
echo "POST $BASE_URL/api/registration/submit"
echo "Body:"
cat << EOF
{
  "firebaseUid": "$TEST_UID",
  "username": "Test User",
  "email": "testuser@example.com",
  "department": "Engineering",
  "contactNumber": "+27123456789",
  "registrationMethod": "EMAIL",
  "requestedAccessGroups": ["FINANCIAL", "LOGISTICS"],
  "businessJustification": "Need access for testing purposes",
  "priorityLevel": "MEDIUM",
  "dolibarrId": "999"
}
EOF
echo ""
echo "Response:"
curl -X POST "$BASE_URL/api/registration/submit" \
  -H "Content-Type: application/json" \
  -d "{
    \"firebaseUid\": \"$TEST_UID\",
    \"username\": \"Test User\",
    \"email\": \"testuser@example.com\",
    \"department\": \"Engineering\",
    \"contactNumber\": \"+27123456789\",
    \"registrationMethod\": \"EMAIL\",
    \"requestedAccessGroups\": [\"FINANCIAL\", \"LOGISTICS\"],
    \"businessJustification\": \"Need access for testing purposes\",
    \"priorityLevel\": \"MEDIUM\",
    \"dolibarrId\": \"999\"
  }" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 2: Check Registration Status
print_test "2" "Check Registration Status"
echo "GET $BASE_URL/api/registration/status/$TEST_UID"
echo "Response:"
curl -s "$BASE_URL/api/registration/status/$TEST_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 3: Get Pending Approvals (Admin)
print_test "3" "Get Pending Approvals (Admin)"
echo "GET $BASE_URL/api/registration/admin/pending"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -s "$BASE_URL/api/registration/admin/pending" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 4: Get Statistics (Admin)
print_test "4" "Get Registration Statistics (Admin)"
echo "GET $BASE_URL/api/registration/admin/statistics"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -s "$BASE_URL/api/registration/admin/statistics" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 5: Approve User (Admin)
print_test "5" "Approve User Registration (Admin)"
echo "PUT $BASE_URL/api/registration/admin/approve"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Body:"
cat << EOF
{
  "targetUid": "$TEST_UID",
  "assignedAccessGroups": ["FINANCIAL", "LOGISTICS"],
  "department": "Engineering",
  "dolibarrId": "999"
}
EOF
echo ""
echo "Response:"
curl -X PUT "$BASE_URL/api/registration/admin/approve" \
  -H "Content-Type: application/json" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -d "{
    \"targetUid\": \"$TEST_UID\",
    \"assignedAccessGroups\": [\"FINANCIAL\", \"LOGISTICS\"],
    \"department\": \"Engineering\",
    \"dolibarrId\": \"999\"
  }" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 6: Verify User Created
print_test "6" "Verify User Account Created"
echo "GET $BASE_URL/api/users/$TEST_UID"
echo "Response:"
curl -s "$BASE_URL/api/users/$TEST_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# ============================================
# ACCESS GROUPS TESTS
# ============================================

# Test 7: Get All Access Groups
print_test "7" "Get All Access Groups (Admin)"
echo "GET $BASE_URL/api/access-groups"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -s "$BASE_URL/api/access-groups" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 8: Get User's Access Groups
print_test "8" "Get User's Access Groups"
echo "GET $BASE_URL/api/users/$TEST_UID/access-groups"
echo "Response:"
curl -s "$BASE_URL/api/users/$TEST_UID/access-groups" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 9: Create New Access Group (Super Admin)
TEST_GROUP_ID="TEST_GROUP_$(date +%s)"
print_test "9" "Create New Access Group (Super Admin)"
echo "POST $BASE_URL/api/access-groups"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Body:"
cat << EOF
{
  "groupId": "$TEST_GROUP_ID",
  "name": "Test Group",
  "description": "Testing access group creation"
}
EOF
echo ""
echo "${YELLOW}⚠️  Note: This requires SUPER_ADMIN role. May fail with 403 if admin doesn't have super admin privileges.${NC}"
echo "Response:"
curl -X POST "$BASE_URL/api/access-groups" \
  -H "Content-Type: application/json" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -d "{
    \"groupId\": \"$TEST_GROUP_ID\",
    \"name\": \"Test Group\",
    \"description\": \"Testing access group creation\"
  }" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""

# Test 10: Add User to Group Manually
print_test "10" "Add User to Group (Admin)"
echo "POST $BASE_URL/api/users/$TEST_UID/access-groups?groupId=HR&assignedBy=$ADMIN_UID"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -X POST "$BASE_URL/api/users/$TEST_UID/access-groups?groupId=HR&assignedBy=$ADMIN_UID" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 11: Get User's Updated Access Groups
print_test "11" "Get User's Updated Access Groups"
echo "GET $BASE_URL/api/users/$TEST_UID/access-groups"
echo "Response:"
curl -s "$BASE_URL/api/users/$TEST_UID/access-groups" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 12: Remove User from Group
print_test "12" "Remove User from Group (Admin)"
echo "DELETE $BASE_URL/api/users/$TEST_UID/access-groups/HR"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -X DELETE "$BASE_URL/api/users/$TEST_UID/access-groups/HR" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# ============================================
# USER MANAGEMENT TESTS
# ============================================

# Test 13: Update User Department
print_test "13" "Update User Department (Admin)"
echo "PUT $BASE_URL/api/users/$TEST_UID/admin/department?department=Marketing"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -X PUT "$BASE_URL/api/users/$TEST_UID/admin/department?department=Marketing" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 14: Get All Users with Statistics
print_test "14" "Get All Users with Statistics (Admin)"
echo "GET $BASE_URL/api/users/admin/all"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -s "$BASE_URL/api/users/admin/all" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 15: Disable User Account
print_test "15" "Disable User Account (Admin)"
echo "PUT $BASE_URL/api/users/$TEST_UID/admin/status?isAuthorized=false"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -X PUT "$BASE_URL/api/users/$TEST_UID/admin/status?isAuthorized=false" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# Test 16: Re-enable User Account
print_test "16" "Re-enable User Account (Admin)"
echo "PUT $BASE_URL/api/users/$TEST_UID/admin/status?isAuthorized=true"
echo "Header: X-Firebase-UID: $ADMIN_UID"
echo "Response:"
curl -X PUT "$BASE_URL/api/users/$TEST_UID/admin/status?isAuthorized=true" \
  -H "X-Firebase-UID: $ADMIN_UID" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
check_response $?

# ============================================
# ERROR HANDLING TESTS
# ============================================

# Test 17: Duplicate Registration (Should Fail)
print_test "17" "Duplicate Registration (Should Return 409)"
echo "POST $BASE_URL/api/registration/submit"
echo "Response:"
curl -X POST "$BASE_URL/api/registration/submit" \
  -H "Content-Type: application/json" \
  -d "{
    \"firebaseUid\": \"$TEST_UID\",
    \"username\": \"Test User\",
    \"email\": \"testuser@example.com\",
    \"registrationMethod\": \"EMAIL\"
  }" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
echo "${YELLOW}Expected: 409 Conflict${NC}"

# Test 18: Non-Admin Access (Should Fail)
print_test "18" "Non-Admin Accessing Admin Endpoint (Should Return 403)"
echo "GET $BASE_URL/api/registration/admin/pending"
echo "Header: X-Firebase-UID: non-admin-uid"
echo "Response:"
curl -s "$BASE_URL/api/registration/admin/pending" \
  -H "X-Firebase-UID: non-admin-uid" \
  -w "\nHTTP Status: %{http_code}\n"
echo ""
echo "${YELLOW}Expected: 403 Forbidden or 500${NC}"

# ============================================
# CLEANUP & SUMMARY
# ============================================

echo ""
echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "${GREEN}✅ TESTING COMPLETE${NC}"
echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "📊 Summary:"
echo "  - Test User UID: $TEST_UID"
echo "  - Test Group ID: $TEST_GROUP_ID"
echo ""
echo "${YELLOW}📧 Email Notifications:${NC}"
echo "  Check your email for:"
echo "    1. New registration notification (to admin)"
echo "    2. Approval notification (to user)"
echo "    3. Department change notification (to user)"
echo "    4. Status change notifications (to user)"
echo ""
echo "${YELLOW}🗄️  Database Cleanup:${NC}"
echo "  You may want to delete the test user from the database:"
echo "    DELETE FROM firefighter.users WHERE user_id = '$TEST_UID';"
echo "    DELETE FROM firefighter.pending_user_approvals WHERE firebase_uid = '$TEST_UID';"
echo ""
echo "${GREEN}🎉 All API tests completed!${NC}"
