#!/bin/bash

echo "================================"
echo "Quick Test Compilation Check"
echo "================================"

echo ""
echo "Compiling tests..."
./mvnw test-compile -q

if [ $? -eq 0 ]; then
    echo "✅ Test compilation successful!"
    echo ""
    echo "Running Registration Service tests..."
    ./mvnw test -Dtest=RegistrationServiceTest -q
    
    if [ $? -eq 0 ]; then
        echo "✅ RegistrationServiceTest passed!"
    else
        echo "❌ RegistrationServiceTest failed!"
        exit 1
    fi
else
    echo "❌ Test compilation failed!"
    echo ""
    echo "Running verbose compilation to see errors..."
    ./mvnw test-compile | grep -A 3 "\[ERROR\]"
    exit 1
fi
