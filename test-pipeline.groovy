// Test Pipeline for Fire-Fighter Jenkins Setup
// Use this to verify your Jenkins configuration before running the main pipeline

pipeline {
    agent any
    
    environment {
        // Test all your credentials
        DB_HOST = credentials('DB_HOST')
        DB_PORT = credentials('DB_PORT')
        DB_NAME = credentials('DB_NAME')
        DB_USERNAME = credentials('DB_USERNAME')
        DB_PASSWORD = credentials('DB_PASSWORD')
        DB_SSL_MODE = credentials('DB_SSL_MODE')
        
        JWT_SECRET = credentials('JWT_SECRET')
        JWT_EXPIRATION = credentials('JWT_EXPIRATION')
        
        GMAIL_USERNAME = credentials('GMAIL_USERNAME')
        GMAIL_SENDER_NAME = credentials('GMAIL_SENDER_NAME')
        
        GOOGLE_GEMINI_API_KEY = credentials('GOOGLE_GEMINI_API_KEY')
    }
    
    tools {
        jdk "jdk-17"
        nodejs "nodejs-22"
        maven 'maven-3.8'
    }
    
    stages {
        stage('Environment Check') {
            steps {
                echo "=== Environment Verification ==="
                sh '''
                    echo "Java Version:"
                    java -version
                    echo ""
                    
                    echo "Maven Version:"
                    mvn -version
                    echo ""
                    
                    echo "Node.js Version:"
                    node --version
                    echo ""
                    
                    echo "NPM Version:"
                    npm --version
                    echo ""
                    
                    echo "Git Version:"
                    git --version
                    echo ""
                '''
            }
        }
        
        stage('Credentials Test') {
            steps {
                echo "=== Credentials Verification ==="
                script {
                    echo "✅ Database Host: ${env.DB_HOST}"
                    echo "✅ Database Port: ${env.DB_PORT}"
                    echo "✅ Database Name: ${env.DB_NAME}"
                    echo "✅ Database Username: ${env.DB_USERNAME}"
                    echo "✅ Database Password: [HIDDEN - Length: ${env.DB_PASSWORD.length()}]"
                    echo "✅ Database SSL Mode: ${env.DB_SSL_MODE}"
                    echo ""
                    echo "✅ JWT Secret: [HIDDEN - Length: ${env.JWT_SECRET.length()}]"
                    echo "✅ JWT Expiration: ${env.JWT_EXPIRATION}"
                    echo ""
                    echo "✅ Gmail Username: ${env.GMAIL_USERNAME}"
                    echo "✅ Gmail Sender Name: ${env.GMAIL_SENDER_NAME}"
                    echo ""
                    echo "✅ Google Gemini API Key: [HIDDEN - Length: ${env.GOOGLE_GEMINI_API_KEY.length()}]"
                }
            }
        }
        
        stage('Network Connectivity') {
            steps {
                echo "=== Network Connectivity Test ==="
                script {
                    try {
                        sh "timeout 10 nc -z ${env.DB_HOST} ${env.DB_PORT}"
                        echo "✅ Database server is reachable"
                    } catch (Exception e) {
                        echo "⚠️ Database connectivity test failed: ${e.getMessage()}"
                        echo "This might be due to firewall rules or network configuration"
                    }
                }
            }
        }
        
        stage('Project Structure Check') {
            when {
                expression { fileExists('FF-API/pom.xml') && fileExists('FF-Angular/package.json') }
            }
            steps {
                echo "=== Project Structure Verification ==="
                sh '''
                    echo "✅ Fire-Fighter project structure detected"
                    echo ""
                    
                    echo "Backend (FF-API):"
                    if [ -f "FF-API/pom.xml" ]; then
                        echo "  ✅ pom.xml found"
                        echo "  📦 Maven project detected"
                    else
                        echo "  ❌ pom.xml not found"
                    fi
                    echo ""
                    
                    echo "Frontend (FF-Angular):"
                    if [ -f "FF-Angular/package.json" ]; then
                        echo "  ✅ package.json found"
                        echo "  📦 Node.js project detected"
                    else
                        echo "  ❌ package.json not found"
                    fi
                    echo ""
                    
                    echo "Configuration files:"
                    if [ -f "Jenkinsfile" ]; then
                        echo "  ✅ Jenkinsfile found"
                    else
                        echo "  ❌ Jenkinsfile not found"
                    fi
                '''
            }
        }
        
        stage('Quick Build Test') {
            when {
                expression { fileExists('FF-API/pom.xml') }
            }
            steps {
                echo "=== Quick Build Test ==="
                dir('FF-API') {
                    sh '''
                        echo "Testing Maven compilation..."
                        mvn clean compile -DskipTests=true -q
                        echo "✅ Maven compilation successful"
                    '''
                }
            }
        }
        
        stage('NPM Dependencies Test') {
            when {
                expression { fileExists('FF-Angular/package.json') }
            }
            steps {
                echo "=== NPM Dependencies Test ==="
                dir('FF-Angular') {
                    sh '''
                        echo "Testing NPM installation..."
                        npm install --silent
                        echo "✅ NPM installation successful"
                        
                        echo "Checking Angular CLI..."
                        npx ng version --skip-git || echo "Angular CLI check completed"
                    '''
                }
            }
        }
    }
    
    post {
        always {
            echo "=== Test Pipeline Summary ==="
            echo "This test pipeline verifies your Jenkins setup for Fire-Fighter project"
            echo ""
        }
        success {
            echo "🎉 SUCCESS: Your Jenkins setup is ready for Fire-Fighter!"
            echo ""
            echo "Next steps:"
            echo "1. Create your main pipeline job using the Jenkinsfile"
            echo "2. Configure webhooks for automatic builds"
            echo "3. Set up branch-specific build triggers"
            echo ""
        }
        failure {
            echo "❌ FAILURE: Some issues were detected in your setup"
            echo ""
            echo "Please review the console output and:"
            echo "1. Check Global Tool Configuration"
            echo "2. Verify all credentials are properly set"
            echo "3. Ensure all required tools are installed"
            echo "4. Run the verify-jenkins-setup.sh script on your server"
            echo ""
        }
    }
}
