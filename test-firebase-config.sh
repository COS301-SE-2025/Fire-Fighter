#!/bin/bash

# Quick test script for Firebase configuration
echo "🔥 Testing Firebase Development Configuration"
echo "============================================"

cd FF-API/firefighter-platform

echo "1. Checking if FirebaseDevConfig exists..."
if [ -f "src/main/java/com/apex/firefighter/config/FirebaseDevConfig.java" ]; then
    echo "✅ FirebaseDevConfig.java found"
else
    echo "❌ FirebaseDevConfig.java missing"
    exit 1
fi

echo ""
echo "2. Checking development application properties..."
if [ -f "src/main/resources/application-dev.properties" ]; then
    echo "✅ application-dev.properties found"
    echo "📋 Configuration preview:"
    grep -E "(server\.port|server\.ssl\.enabled|spring\.datasource\.url)" src/main/resources/application-dev.properties
else
    echo "❌ application-dev.properties missing"
    exit 1
fi

echo ""
echo "3. Testing compilation with development profile..."
mvn clean compile -Dspring.profiles.active=dev

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful with dev profile"
else
    echo "❌ Compilation failed"
    exit 1
fi

echo ""
echo "4. Starting backend in development mode..."
echo "   This will use:"
echo "   - Mock Firebase (no real authentication needed)"
echo "   - H2 in-memory database"
echo "   - HTTP on port 8080"
echo "   - No SSL requirements"
echo ""
echo "Press Ctrl+C to stop the server once it starts successfully"
echo ""

# Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
