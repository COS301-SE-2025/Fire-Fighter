#!/bin/bash

# Setup script for E2E testing in CI environments
# This script checks for required dependencies and provides guidance

set -e

echo "🔧 Setting up E2E testing environment..."

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Node.js
if command_exists node; then
    echo "✅ Node.js $(node --version) is available"
else
    echo "❌ Node.js is required but not found"
    exit 1
fi

# Check npm
if command_exists npm; then
    echo "✅ npm $(npm --version) is available"
else
    echo "❌ npm is required but not found"
    exit 1
fi

# Check for Xvfb
if command_exists xvfb-run; then
    echo "✅ Xvfb is available"
    XVFB_AVAILABLE=true
else
    echo "⚠️  Xvfb not found - will try to run without virtual display"
    XVFB_AVAILABLE=false
fi

# Check for browsers
BROWSER_AVAILABLE=false
if command_exists google-chrome; then
    echo "✅ Google Chrome is available"
    BROWSER_AVAILABLE=true
elif command_exists chromium-browser; then
    echo "✅ Chromium is available"
    BROWSER_AVAILABLE=true
elif command_exists chromium; then
    echo "✅ Chromium is available"
    BROWSER_AVAILABLE=true
else
    echo "⚠️  No Chrome/Chromium browser found"
fi

# Set environment variables for Cypress
export CYPRESS_CACHE_FOLDER="$HOME/.cache/Cypress"
export DISPLAY=":99"

# Create cache directory if it doesn't exist
mkdir -p "$CYPRESS_CACHE_FOLDER"

echo "🎯 Environment setup complete!"
echo "   - Xvfb available: $XVFB_AVAILABLE"
echo "   - Browser available: $BROWSER_AVAILABLE"
echo "   - Cypress cache: $CYPRESS_CACHE_FOLDER"

# If we have both Xvfb and a browser, we're good to go
if [ "$XVFB_AVAILABLE" = true ] && [ "$BROWSER_AVAILABLE" = true ]; then
    echo "✅ Ready for E2E testing!"
    exit 0
else
    echo "⚠️  Some dependencies are missing, but will attempt to run tests anyway"
    exit 0
fi
