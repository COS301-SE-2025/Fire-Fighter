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
                            echo "🔧 Setting up E2E testing environment..."
                            sh './setup-e2e-ci.sh'

                            echo "🚀 Starting Angular development server for E2E tests..."
                            sh 'npm start &'

                            echo "⏳ Waiting for server to be ready..."
                            sh '''
                                for i in {1..30}; do
                                    if curl -f http://localhost:4200 >/dev/null 2>&1; then
                                        echo "✅ Server is ready!"
                                        break
                                    fi
                                    echo "Waiting for server... ($i/30)"
                                    sleep 2
                                done
                            '''

                            echo "🧪 Running E2E tests..."
                            sh '''
                                # Try to run with xvfb-run if available, otherwise run directly
                                if command -v xvfb-run &> /dev/null; then
                                    echo "Running E2E tests with Xvfb..."
                                    xvfb-run -a npm run e2e:headless
                                else
                                    echo "Running E2E tests without Xvfb (using Electron headless)..."
                                    npm run e2e:headless
                                fi
                            '''

                        } catch (Exception e) {
                            echo "❌ E2E tests failed: ${e.getMessage()}"
                            throw e
                        } finally {
                            echo "🧹 Cleaning up Angular dev server..."
                            sh 'pkill -f "ng serve" || true'
                            sh 'pkill -f "node.*ng serve" || true'
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

                        // Clean up any remaining processes
                        sh 'pkill -f "ng serve" || true'
                        sh 'pkill -f "node.*ng serve" || true'
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
