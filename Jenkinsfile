// Helper function to update GitHub status with error handling
def updateGitHubStatus(status, description, context) {
    try {
        githubNotify(
            status: status,
            description: description,
            context: context,
            targetUrl: env.BUILD_URL
        )
        echo "✅ GitHub status updated: ${context} - ${status}"
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
                    updateGitHubStatus('PENDING', 'Fire-Fighter build started', 'jenkins/build')
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
                    updateGitHubStatus('PENDING', 'Running unit tests', 'jenkins/tests')
                }

                dir('FF-API') {
                    echo "🧪 Running backend unit tests (unit folder)..."
                    // Copy Firebase service account key from Jenkins secret to workspace
                    withCredentials([
                        file(credentialsId: 'firebase-service-account-key', variable: 'FIREBASE_KEY_FILE'),
                        string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                        string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                        string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                        string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                        string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                        string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                        string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY')
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
                        string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                        string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                        string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                        string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY')
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
                    sh 'ng test --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    updateGitHubStatus('PENDING', 'Building application', 'jenkins/build')
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
                                string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                                string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                                string(credentialsId: 'DB_SSL_MODE', variable: 'DB_SSL_MODE'),
                                string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                                string(credentialsId: 'GMAIL_USERNAME', variable: 'GMAIL_USERNAME'),
                                string(credentialsId: 'GMAIL_APP_PASSWORD', variable: 'GMAIL_APP_PASSWORD'),
                                string(credentialsId: 'GMAIL_SENDER_NAME', variable: 'GMAIL_SENDER_NAME'),
                                string(credentialsId: 'GOOGLE_GEMINI_API_KEY', variable: 'GOOGLE_GEMINI_API_KEY')
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
            echo "🐳 Ready for Portainer deployment!"

            // Update GitHub status to success
            script {
                updateGitHubStatus('SUCCESS', 'Fire-Fighter build completed successfully', 'jenkins/build')
                updateGitHubStatus('SUCCESS', 'All tests passed', 'jenkins/tests')
            }
        }
        failure {
            echo "❌ Build failed!"

            // Update GitHub status to failure
            script {
                updateGitHubStatus('FAILURE', 'Fire-Fighter build failed', 'jenkins/build')
            }
        }
        unstable {
            echo "⚠️ Build unstable!"

            // Update GitHub status to failure for unstable builds
            script {
                updateGitHubStatus('FAILURE', 'Fire-Fighter build unstable (tests failed)', 'jenkins/tests')
            }
        }
    }
}
