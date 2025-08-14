pipeline {
    agent any

    parameters {
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip unit tests for faster build'
        )
        string(
            name: 'BUILD_MESSAGE',
            defaultValue: 'Manual build',
            description: 'Custom build message'
        )
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        skipDefaultCheckout(false)
    }
    
    environment {
        // Java and Node versions
        JAVA_VERSION = '17'
        NODE_VERSION = '22'
        
        // Project paths
        API_PATH = 'FF-API'
        ANGULAR_PATH = 'FF-Angular'
        
        // Build artifacts
        API_JAR = 'firefighter-platform-0.0.1-SNAPSHOT.jar'
        ANGULAR_DIST = 'www'
        
        // Database configuration (using external PostgreSQL)
        DB_HOST = credentials('DB_HOST')
        DB_PORT = credentials('DB_PORT')
        DB_NAME = credentials('DB_NAME')
        DB_USERNAME = credentials('DB_USERNAME')
        DB_PASSWORD = credentials('DB_PASSWORD')
        DB_SSL_MODE = credentials('DB_SSL_MODE')

        // JWT Configuration
        JWT_SECRET = credentials('JWT_SECRET')
        JWT_EXPIRATION = credentials('JWT_EXPIRATION')

        // Email Configuration
        GMAIL_APP_PASSWORD = credentials('GMAIL_APP_PASSWORD')
        GMAIL_USERNAME = credentials('GMAIL_USERNAME')
        GMAIL_SENDER_NAME = credentials('GMAIL_SENDER_NAME')

        // AI API Configuration
        GOOGLE_GEMINI_API_KEY = credentials('GOOGLE_GEMINI_API_KEY')

        // Environment settings
        SPRING_PROFILES_ACTIVE = 'test'
        BUILD_ENV = 'development'
        DEPLOY_TARGET = 'staging'
    }
    
    tools {
        jdk "jdk-17"
        nodejs "nodejs-22"
        maven 'maven-3.8'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                    env.BUILD_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT_SHORT}"

                    // Set environment based on branch
                    def currentBranch = env.BRANCH_NAME ?: 'develop'
                    if (currentBranch == 'develop') {
                        env.SPRING_PROFILES_ACTIVE = 'dev'
                        env.BUILD_ENV = 'development'
                        env.DEPLOY_TARGET = 'staging'
                    } else if (currentBranch == 'main') {
                        env.SPRING_PROFILES_ACTIVE = 'prod'
                        env.BUILD_ENV = 'production'
                        env.DEPLOY_TARGET = 'production'
                    }

                    echo "🔥 Fire-Fighter Pipeline"
                    echo "📦 Build Version: ${env.BUILD_VERSION}"
                    echo "💬 Message: ${params.BUILD_MESSAGE}"
                    echo "⚡ Skip Tests: ${params.SKIP_TESTS}"
                    echo "🌿 Branch: ${currentBranch}"
                    echo "🎯 Environment: ${env.BUILD_ENV}"
                    echo "🚀 Deploy Target: ${env.DEPLOY_TARGET}"
                }
            }
        }
        
        stage('Environment Setup') {
            parallel {
                stage('Java Environment') {
                    steps {
                        sh '''
                            echo "Java Version:"
                            java -version
                            echo "Maven Version:"
                            mvn -version
                            echo "JAVA_HOME: $JAVA_HOME"
                            echo "Available memory:"
                            free -h
                        '''
                    }
                }
                stage('Node Environment') {
                    steps {
                        sh '''
                            echo "Node Version:"
                            node --version
                            echo "NPM Version:"
                            npm --version
                            echo "Available disk space:"
                            df -h
                        '''
                    }
                }
            }
        }
        
        stage('Install Dependencies') {
            parallel {
                stage('Backend Dependencies') {
                    steps {
                        dir(env.API_PATH) {
                            sh '''
                                echo "Installing Maven dependencies..."
                                mvn clean compile -DskipTests=true
                            '''
                        }
                    }
                }
                stage('Frontend Dependencies') {
                    steps {
                        dir(env.ANGULAR_PATH) {
                            sh '''
                                echo "Installing NPM dependencies..."
                                npm ci
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Code Quality & Linting') {
            parallel {
                stage('Backend Code Quality') {
                    steps {
                        dir(env.API_PATH) {
                            sh '''
                                echo "Running Maven validate and compile..."
                                mvn validate compile
                            '''
                        }
                    }
                }
                stage('Frontend Linting') {
                    steps {
                        dir(env.ANGULAR_PATH) {
                            sh '''
                                echo "Running Angular linting..."
                                npm run lint
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Unit Tests') {
            when {
                not {
                    params.SKIP_TESTS
                }
            }
            parallel {
                stage('Backend Tests') {
                    steps {
                        dir(env.API_PATH) {
                            sh '''
                                echo "Running Spring Boot unit tests..."
                                mvn test -Dspring.profiles.active=test
                            '''
                        }
                        publishTestResults testResultsPattern: "${env.API_PATH}/target/surefire-reports/*.xml"
                    }
                    post {
                        always {
                            dir(env.API_PATH) {
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: 'target/site/jacoco',
                                    reportFiles: 'index.html',
                                    reportName: 'Backend Code Coverage Report'
                                ])
                            }
                        }
                    }
                }
                stage('Frontend Tests') {
                    steps {
                        dir(env.ANGULAR_PATH) {
                            sh '''
                                echo "Running Angular unit tests..."
                                npm test -- --watch=false --browsers=ChromeHeadless --code-coverage
                            '''
                        }
                        publishTestResults testResultsPattern: "${env.ANGULAR_PATH}/coverage/lcov.info"
                    }
                    post {
                        always {
                            dir(env.ANGULAR_PATH) {
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: 'coverage',
                                    reportFiles: 'index.html',
                                    reportName: 'Frontend Code Coverage Report'
                                ])
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Applications') {
            parallel {
                stage('Build Backend') {
                    steps {
                        dir(env.API_PATH) {
                            sh '''
                                echo "Building Spring Boot application..."
                                mvn clean package -DskipTests=true
                            '''
                        }
                    }
                    post {
                        success {
                            dir(env.API_PATH) {
                                archiveArtifacts artifacts: "target/${env.API_JAR}", fingerprint: true
                            }
                        }
                    }
                }
                stage('Build Frontend') {
                    steps {
                        dir(env.ANGULAR_PATH) {
                            sh '''
                                echo "Building Angular application..."
                                npm run build --prod
                            '''
                        }
                    }
                    post {
                        success {
                            dir(env.ANGULAR_PATH) {
                                archiveArtifacts artifacts: "${env.ANGULAR_DIST}/**/*", fingerprint: true
                            }
                        }
                    }
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    changeRequest()
                }
            }
            steps {
                script {
                    // Start backend service for integration testing
                    dir(env.API_PATH) {
                        sh '''
                            echo "Starting backend service for integration tests..."
                            nohup java -jar target/${API_JAR} \
                                --spring.profiles.active=test \
                                --server.port=8081 > backend.log 2>&1 &
                            echo $! > backend.pid
                            
                            # Wait for backend to be ready
                            timeout 60 bash -c 'until curl -f http://localhost:8081/actuator/health; do sleep 2; done'
                        '''
                    }
                    
                    // Run integration tests
                    try {
                        sh '''
                            echo "Running integration tests..."
                            # Add your integration test commands here
                            # For example: newman run postman/collection.json
                        '''
                    } finally {
                        // Stop backend service
                        dir(env.API_PATH) {
                            sh '''
                                if [ -f backend.pid ]; then
                                    kill $(cat backend.pid) || true
                                    rm -f backend.pid
                                fi
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Security Scan') {
            parallel {
                stage('Backend Security') {
                    steps {
                        dir(env.API_PATH) {
                            sh '''
                                echo "Running Maven dependency security check..."
                                mvn org.owasp:dependency-check-maven:check || true
                            '''
                        }
                    }
                }
                stage('Frontend Security') {
                    steps {
                        dir(env.ANGULAR_PATH) {
                            sh '''
                                echo "Running NPM audit..."
                                npm audit --audit-level=high || true
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Build Mobile App') {
            when {
                anyOf {
                    branch 'main'
                    branch 'release/*'
                }
            }
            steps {
                dir(env.ANGULAR_PATH) {
                    sh '''
                        echo "Building Android APK..."
                        npx cap sync android
                        cd android
                        ./gradlew assembleDebug
                    '''
                }
            }
            post {
                success {
                    dir(env.ANGULAR_PATH) {
                        archiveArtifacts artifacts: 'android/app/build/outputs/apk/debug/*.apk', fingerprint: true
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                echo "Deploying to staging environment..."
                // Add your staging deployment commands here
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to production?', ok: 'Deploy'
                echo "Deploying to production environment..."
                // Add your production deployment commands here
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
            // Add success notifications here
        }
        failure {
            echo "Pipeline failed!"
            // Add failure notifications here
        }
        unstable {
            echo "Pipeline completed with warnings!"
            // Add unstable notifications here
        }
    }
}
