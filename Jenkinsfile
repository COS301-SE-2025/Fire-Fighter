// Helper function to update GitHub status using GitHub API directly
def updateGitHubStatus(status, description, context) {
    try {
        // Get repository info from SCM
        def repoUrl = scm.getUserRemoteConfigs()[0].getUrl()
        def repoName = repoUrl.tokenize('/').last().replace('.git', '')
        def repoOwner = repoUrl.tokenize('/')[-2]

        // Use GitHub API to update status
        withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
            def apiUrl = "https://api.github.com/repos/${repoOwner}/${repoName}/statuses/${env.GIT_COMMIT}"
            def payload = """
            {
                "state": "${status}",
                "description": "${description}",
                "context": "${context}",
                "target_url": "${env.BUILD_URL}"
            }
            """

            def response = sh(
                script: """
                curl -s -w "HTTPSTATUS:%{http_code}" \\
                -X POST \\
                -H "Authorization: token \${GITHUB_TOKEN}" \\
                -H "Content-Type: application/json" \\
                -H "Accept: application/vnd.github.v3+json" \\
                -d '${payload}' \\
                "${apiUrl}"
                """,
                returnStdout: true
            ).trim()

            def httpStatus = response.tokenize("HTTPSTATUS:")[1]
            if (httpStatus == "201") {
                echo "✅ GitHub status updated: ${context} - ${status}"
            } else {
                echo "⚠️ GitHub API returned status: ${httpStatus}"
            }
        }
    } catch (Exception e) {
        echo "⚠️ Could not update GitHub status for ${context}: ${e.getMessage()}"
        // Don't fail the build if GitHub status update fails
    }
}

pipeline {
    agent any

    tools {
        jdk "jdk-17"
        nodejs "nodejs-22"
        maven 'maven-3.8'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "🔥 Fire-Fighter Build Started"

                // Set GitHub status to pending
                script {
                    updateGitHubStatus('pending', 'Fire-Fighter build started', 'jenkins/build')
                }
            }
        }

        stage('Install Dependencies') {
            parallel {
                stage('Backend Dependencies') {
                    steps {
                        dir('FF-API') {
                            sh 'mvn clean compile -DskipTests=true'
                        }
                    }
                }
                stage('Frontend Dependencies') {
                    steps {
                        dir('FF-Angular') {
                            sh 'npm install'
                        }
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    updateGitHubStatus('pending', 'Running unit tests', 'jenkins/tests')
                }

                dir('FF-API') {
                    echo "🧪 Running backend unit tests (unit folder)..."
                    // Copy Firebase service account key from Jenkins secret to workspace
                    withCredentials([
                        file(credentialsId: 'firebase-service-account-key', variable: 'FIREBASE_KEY_FILE'),
                        string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                        string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                        string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                        string(credentialsId: 'DOLIBARR_DB_NAME', variable: 'DOLIBARR_DB_NAME'),
                        string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'JWT_EXPIRATION', variable: 'JWT_EXPIRATION'),
                        string(credentialsId: 'GMAIL_ENABLED', variable: 'GMAIL_ENABLED'),
                        string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                        string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                        string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                        string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY'),
                        string(credentialsId: 'DOLIBARR_API_BASE_URL', variable: 'DOLIBARR_API_BASE_URL'),
                        string(credentialsId: 'DOLIBARR_API_KEY', variable: 'DOLIBARR_API_KEY'),
                        string(credentialsId: 'DOLIBARR_FF_FINANCIALS_GROUP_ID', variable: 'DOLIBARR_FF_FINANCIALS_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_HR_GROUP_ID', variable: 'DOLIBARR_FF_HR_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_LOGISTICS_GROUP_ID', variable: 'DOLIBARR_FF_LOGISTICS_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_FMANAGER_GROUP_ID', variable: 'DOLIBARR_FF_FMANAGER_GROUP_ID')
                    ]) {
                        sh 'cp $FIREBASE_KEY_FILE src/main/resources/firebase-service-account.json'
                        sh 'mvn -Dtest=com.apex.firefighter.unit.**.*Test test -Dspring.profiles.active=test'
                        // Clean up Firebase key for security
                        sh 'rm -f src/main/resources/firebase-service-account.json'
                    }
                }
            }
            post {
                always {
                    dir('FF-API') {
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
        }

        stage('JWT Token Tests') {
            steps {
                dir('FF-API') {
                    echo "🔐 Running JWT token tests..."
                    // Copy Firebase service account key from Jenkins secret to workspace
                    withCredentials([
                        file(credentialsId: 'firebase-service-account-key', variable: 'FIREBASE_KEY_FILE'),
                        string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                        string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                        string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                        string(credentialsId: 'DOLIBARR_DB_NAME', variable: 'DOLIBARR_DB_NAME'),
                        string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'JWT_EXPIRATION', variable: 'JWT_EXPIRATION'),
                        string(credentialsId: 'GMAIL_ENABLED', variable: 'GMAIL_ENABLED'),
                        string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                        string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                        string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                        string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY'),
                        string(credentialsId: 'DOLIBARR_API_BASE_URL', variable: 'DOLIBARR_API_BASE_URL'),
                        string(credentialsId: 'DOLIBARR_API_KEY', variable: 'DOLIBARR_API_KEY'),
                        string(credentialsId: 'DOLIBARR_FF_FINANCIALS_GROUP_ID', variable: 'DOLIBARR_FF_FINANCIALS_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_HR_GROUP_ID', variable: 'DOLIBARR_FF_HR_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_LOGISTICS_GROUP_ID', variable: 'DOLIBARR_FF_LOGISTICS_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_FMANAGER_GROUP_ID', variable: 'DOLIBARR_FF_FMANAGER_GROUP_ID')
                    ]) {
                        sh 'cp $FIREBASE_KEY_FILE src/main/resources/firebase-service-account.json'
                        sh 'mvn -Dtest=**/JwtServiceTest,**/JwtAuthenticationFilterTest,**/JwtAuthenticationFilterExpirationTest,**/AuthControllerTest test -Dspring.profiles.active=test'
                        // Clean up Firebase key for security
                        sh 'rm -f src/main/resources/firebase-service-account.json'
                    }
                }
            }
            post {
                always {
                    dir('FF-API') {
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                dir('FF-API') {
                    echo "🔗 Running backend integration tests (integration folder)..."
                    // Copy Firebase service account key from Jenkins secret to workspace
                    withCredentials([
                        file(credentialsId: 'firebase-service-account-key', variable: 'FIREBASE_KEY_FILE'),
                        string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                        string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                        string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                        string(credentialsId: 'DOLIBARR_DB_NAME', variable: 'DOLIBARR_DB_NAME'),
                        string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'JWT_EXPIRATION', variable: 'JWT_EXPIRATION'),
                        string(credentialsId: 'GMAIL_ENABLED', variable: 'GMAIL_ENABLED'),
                        string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                        string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                        string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                        string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY'),
                        string(credentialsId: 'DOLIBARR_API_BASE_URL', variable: 'DOLIBARR_API_BASE_URL'),
                        string(credentialsId: 'DOLIBARR_API_KEY', variable: 'DOLIBARR_API_KEY'),
                        string(credentialsId: 'DOLIBARR_FF_FINANCIALS_GROUP_ID', variable: 'DOLIBARR_FF_FINANCIALS_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_HR_GROUP_ID', variable: 'DOLIBARR_FF_HR_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_LOGISTICS_GROUP_ID', variable: 'DOLIBARR_FF_LOGISTICS_GROUP_ID'),
                        string(credentialsId: 'DOLIBARR_FF_FMANAGER_GROUP_ID', variable: 'DOLIBARR_FF_FMANAGER_GROUP_ID')
                    ]) {
                        sh 'cp $FIREBASE_KEY_FILE src/main/resources/firebase-service-account.json'
                        sh 'mvn -Dtest=com.apex.firefighter.integration.**.*Test test -Dspring.profiles.active=test'
                        // Clean up Firebase key for security
                        sh 'rm -f src/main/resources/firebase-service-account.json'
                    }
                }
            }
            post {
                always {
                    dir('FF-API') {
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
        }

        stage('Test Frontend') {
            steps {
                dir('FF-Angular') {
                    echo "🧪 Running frontend unit tests..."
                    sh 'npm test --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        stage('E2E Tests') {
            steps {
                dir('FF-Angular') {
                    script {
                        try {
                            echo "🔧 Verifying E2E testing environment..."
                            sh '''
                                # Verify required dependencies are available
                                echo "Checking for Xvfb..."
                                if command -v xvfb-run &> /dev/null; then
                                    echo "✅ Xvfb is available"
                                else
                                    echo "❌ Xvfb not found"
                                    exit 1
                                fi

                                echo "Checking for Chrome..."
                                if command -v google-chrome &> /dev/null; then
                                    echo "✅ Google Chrome is available"
                                elif command -v chromium-browser &> /dev/null; then
                                    echo "✅ Chromium is available"
                                else
                                    echo "❌ No Chrome/Chromium browser found"
                                    exit 1
                                fi

                                echo "✅ E2E environment ready!"
                            '''

                            echo "🚀 Starting Angular development server for E2E tests..."
                            sh '''
                                # Kill any existing Angular servers
                                echo "Cleaning up any existing servers..."
                                pkill -f "ng serve" || true
                                pkill -f "node.*ng serve" || true
                                sleep 3

                                # Check if port 4200 is free
                                if netstat -tuln | grep :4200 >/dev/null 2>&1; then
                                    echo "⚠️  Port 4200 is still in use, waiting..."
                                    sleep 5
                                fi

                                # Show current directory and contents
                                echo "Current working directory: $(pwd)"
                                echo "Directory contents:"
                                ls -la

                                # Verify we have package.json
                                if [ ! -f package.json ]; then
                                    echo "❌ package.json not found in current directory"
                                    exit 1
                                fi

                                # Start Angular dev server in background
                                echo "Starting Angular development server..."
                                nohup npm start > angular-server.log 2>&1 &
                                SERVER_PID=$!
                                echo "Started Angular server with PID: $SERVER_PID"
                                echo $SERVER_PID > angular-server.pid

                                # Give it a moment to start
                                sleep 5
                                echo "Initial server log:"
                                head -n 5 angular-server.log || echo "No log yet"
                            '''

                            echo "⏳ Waiting for server to be ready..."
                            sh '''
                                echo "Checking server availability..."
                                ATTEMPTS=0
                                MAX_ATTEMPTS=60

                                while [ $ATTEMPTS -lt $MAX_ATTEMPTS ]; do
                                    ATTEMPTS=$((ATTEMPTS + 1))

                                    if curl -f http://localhost:4200 >/dev/null 2>&1; then
                                        echo "✅ Server is ready at http://localhost:4200 (attempt $ATTEMPTS)"
                                        echo "Server response preview:"
                                        curl -s http://localhost:4200 | head -n 3 || echo "Could not fetch preview"
                                        break
                                    fi

                                    echo "Attempt $ATTEMPTS/$MAX_ATTEMPTS: Waiting for server..."

                                    # Check if server process is still running
                                    if [ -f angular-server.pid ]; then
                                        SERVER_PID=$(cat angular-server.pid)
                                        if ! ps -p $SERVER_PID > /dev/null 2>&1; then
                                            echo "❌ Angular server process died. Check logs:"
                                            echo "=== Angular Server Log ==="
                                            cat angular-server.log || echo "No log file found"
                                            echo "=========================="
                                            exit 1
                                        else
                                            echo "Server process $SERVER_PID is still running..."
                                        fi
                                    fi

                                    # Show partial logs every 10 attempts
                                    if [ $((ATTEMPTS % 10)) -eq 0 ]; then
                                        echo "=== Current server log (last 10 lines) ==="
                                        tail -n 10 angular-server.log 2>/dev/null || echo "No log available yet"
                                        echo "=========================================="
                                    fi

                                    sleep 5
                                done

                                # Final check
                                if ! curl -f http://localhost:4200 >/dev/null 2>&1; then
                                    echo "❌ Server failed to start after 5 minutes"
                                    echo "=== Full Angular Server Log ==="
                                    cat angular-server.log || echo "No log file found"
                                    echo "==============================="
                                    exit 1
                                fi
                            '''

                            echo "🧪 Running E2E tests..."
                            sh 'xvfb-run -a npm run e2e:headless'

                        } catch (Exception e) {
                            echo "❌ E2E tests failed: ${e.getMessage()}"
                            throw e
                        } finally {
                            echo "🧹 Cleaning up Angular dev server..."
                            sh '''
                                # Kill server using PID if available
                                if [ -f angular-server.pid ]; then
                                    SERVER_PID=$(cat angular-server.pid)
                                    echo "Killing Angular server with PID: $SERVER_PID"
                                    kill $SERVER_PID || true
                                    rm -f angular-server.pid
                                fi

                                # Fallback: kill by process name
                                pkill -f "ng serve" || true
                                pkill -f "node.*ng serve" || true

                                # Clean up log file
                                rm -f angular-server.log || true

                                # Wait a moment for processes to terminate
                                sleep 2
                            '''
                        }
                    }
                }
            }
            post {
                always {
                    dir('FF-Angular') {
                        // Archive Cypress test artifacts
                        archiveArtifacts artifacts: 'cypress/screenshots/**/*', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'cypress/videos/**/*', allowEmptyArchive: true

                        // Clean up any remaining processes and files
                        sh '''
                            pkill -f "ng serve" || true
                            pkill -f "node.*ng serve" || true
                            rm -f angular-server.pid || true
                            rm -f angular-server.log || true
                        '''
                    }
                }
                success {
                    echo "✅ All E2E tests passed successfully!"
                }
                failure {
                    echo "❌ E2E tests failed - check archived screenshots and videos for details"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    updateGitHubStatus('pending', 'Building application', 'jenkins/build')
                }
            }
        }

        stage('Compile & Package') {
            parallel {
                stage('Build Backend') {
                    steps {
                        dir('FF-API') {
                            // Copy Firebase service account key from Jenkins secret to workspace
                            withCredentials([
                                file(credentialsId: 'firebase-service-account-key', variable: 'FIREBASE_KEY_FILE'),
                                string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                                string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                                string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                                string(credentialsId: 'DOLIBARR_DB_NAME', variable: 'DOLIBARR_DB_NAME'),
                                string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                                string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                                string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                                string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                                string(credentialsId: 'JWT_EXPIRATION', variable: 'JWT_EXPIRATION'),
                                string(credentialsId: 'GMAIL_ENABLED', variable: 'GMAIL_ENABLED'),
                                string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                                string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                                string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                                string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY'),
                                string(credentialsId: 'DOLIBARR_API_BASE_URL', variable: 'DOLIBARR_API_BASE_URL'),
                                string(credentialsId: 'DOLIBARR_API_KEY', variable: 'DOLIBARR_API_KEY'),
                                string(credentialsId: 'DOLIBARR_FF_FINANCIALS_GROUP_ID', variable: 'DOLIBARR_FF_FINANCIALS_GROUP_ID'),
                                string(credentialsId: 'DOLIBARR_FF_HR_GROUP_ID', variable: 'DOLIBARR_FF_HR_GROUP_ID'),
                                string(credentialsId: 'DOLIBARR_FF_LOGISTICS_GROUP_ID', variable: 'DOLIBARR_FF_LOGISTICS_GROUP_ID'),
                                string(credentialsId: 'DOLIBARR_FF_FMANAGER_GROUP_ID', variable: 'DOLIBARR_FF_FMANAGER_GROUP_ID')
                            ]) {
                                sh 'cp $FIREBASE_KEY_FILE src/main/resources/firebase-service-account.json'
                                sh 'mvn package -DskipTests=true'
                                // Clean up Firebase key for security
                                sh 'rm -f src/main/resources/firebase-service-account.json'
                            }
                        }
                    }
                }
                stage('Build Frontend') {
                    steps {
                        dir('FF-Angular') {
                            sh 'ng build'
                        }
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "📦 Archiving build artifacts..."

                // Archive the JAR file for deployment
                dir('FF-API') {
                    archiveArtifacts artifacts: 'target/firefighter-platform-0.0.1-SNAPSHOT.jar', fingerprint: true
                }

                // Archive the frontend build
                dir('FF-Angular') {
                    archiveArtifacts artifacts: 'www/**/*', fingerprint: true
                }

                echo "✅ Artifacts archived successfully!"
            }
        }
    }

    post {
        always {
            // Clean up workspace
            cleanWs()
        }
        success {
            echo "✅ Build completed successfully!"
            echo "📦 JAR file: target/firefighter-platform-0.0.1-SNAPSHOT.jar"
            echo "🌐 Frontend build: www/"
            echo "🧪 Unit tests: ✅ Passed"
            echo "🔗 Integration tests: ✅ Passed"
            echo "🎯 E2E tests: ✅ Passed"
            echo "🐳 Ready for Portainer deployment!"

            // Update GitHub status to success
            script {
                updateGitHubStatus('success', 'Fire-Fighter build completed successfully', 'jenkins/build')
                updateGitHubStatus('success', 'All tests passed (Unit + Integration + E2E)', 'jenkins/tests')
            }
        }
        failure {
            echo "❌ Build failed!"

            // Update GitHub status to failure
            script {
                updateGitHubStatus('failure', 'Fire-Fighter build failed', 'jenkins/build')
            }
        }
        unstable {
            echo "⚠️ Build unstable!"

            // Update GitHub status to failure for unstable builds
            script {
                updateGitHubStatus('failure', 'Fire-Fighter build unstable (tests failed)', 'jenkins/tests')
            }
        }
    }
}
