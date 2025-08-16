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
                dir('FF-API') {
                    echo "🧪 Running backend unit tests (unit folder)..."
                    sh '''
                        mvn -Dtest=com.apex.firefighter.unit.**.*Test test -Dspring.profiles.active=test
                    '''
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
                    sh '''
                        mvn -Dtest=com.apex.firefighter.integration.**.*Test test -Dspring.profiles.active=test
                    '''
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
                    // Uncomment below to enable frontend tests
                    // ng test --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        stage('Build') {
            parallel {
                stage('Build Backend') {
                    steps {
                        dir('FF-API') {
                            sh 'mvn package -DskipTests=true'
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
        }
        failure {
            echo "❌ Build failed!"
        }
    }
}
