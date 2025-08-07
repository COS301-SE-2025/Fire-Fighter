#!/bin/bash

echo "=== Fire Fighter JWT Security Test Suite ==="
echo "Running comprehensive JWT and security tests..."
echo ""

# Test 1: JWT Service Tests
echo "1. Running JWT Service Tests..."
mvn test -Dtest=JwtServiceTest -q
if [ $? -eq 0 ]; then
    echo "✅ JWT Service Tests: PASSED"
else
    echo "❌ JWT Service Tests: FAILED"
fi
echo ""

# Test 2: JWT Authentication Filter Tests
echo "2. Running JWT Authentication Filter Tests..."
mvn test -Dtest=JwtAuthenticationFilterTest -q
if [ $? -eq 0 ]; then
    echo "✅ JWT Authentication Filter Tests: PASSED"
else
    echo "❌ JWT Authentication Filter Tests: FAILED"
fi
echo ""

# Test 3: Authentication Service Tests
echo "3. Running Authentication Service Tests..."
mvn test -Dtest=AuthenticationServiceTest -q
if [ $? -eq 0 ]; then
    echo "✅ Authentication Service Tests: PASSED"
else
    echo "❌ Authentication Service Tests: FAILED"
fi
echo ""

# Test 4: Auth Controller Integration Tests
echo "4. Running Auth Controller Integration Tests..."
mvn test -Dtest=AuthControllerIntegrationTest -q
if [ $? -eq 0 ]; then
    echo "✅ Auth Controller Integration Tests: PASSED"
else
    echo "❌ Auth Controller Integration Tests: FAILED"
fi
echo ""

# Test 5: User Repository Tests
echo "5. Running User Repository Tests..."
mvn test -Dtest=UserRepositoryTest -q
if [ $? -eq 0 ]; then
    echo "✅ User Repository Tests: PASSED"
else
    echo "❌ User Repository Tests: FAILED"
fi
echo ""

# Test 6: Role Repository Tests
echo "6. Running Role Repository Tests..."
mvn test -Dtest=RoleRepositoryTest -q
if [ $? -eq 0 ]; then
    echo "✅ Role Repository Tests: PASSED"
else
    echo "❌ Role Repository Tests: FAILED"
fi
echo ""

echo "=== JWT Security Test Suite Complete ==="
echo ""
echo "Summary:"
echo "- These tests validate JWT token generation, validation, and expiration"
echo "- Authentication filter ensures proper JWT handling in requests"
echo "- Firebase integration is properly tested"
echo "- User and role management supports the security framework"
echo ""
echo "If all tests pass, your JWT security implementation is working correctly!"
