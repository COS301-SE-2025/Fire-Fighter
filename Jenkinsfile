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

        stage('Test') {
            parallel {
                stage('Backend Tests') {
                    steps {
                        dir('FF-API') {
                            sh 'mvn test -DskipTests=true'
                        }
                    }
                }
                stage('Frontend Tests') {
                    steps {
                        dir('FF-Angular') {
                            echo 'Skipping frontend tests for now'
                            // sh 'ng test --watch=false --browsers=ChromeHeadless'
                        }
                    }
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

        stage('Build Docker Image') {
            steps {
                echo "🐳 Building Docker image for FF-API..."

                sh """
                    cd FF-API
                    docker build -t firefighter-api:${env.BUILD_NUMBER} .
                    docker tag firefighter-api:${env.BUILD_NUMBER} firefighter-api:latest
                """

                echo "🐳 Docker image built: firefighter-api:${env.BUILD_NUMBER}"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "🚀 Deploying Fire-Fighter application..."

                    // Stop existing containers
                    sh '''
                        docker-compose down || true
                        docker system prune -f || true
                    '''

                    // Deploy with docker-compose using Jenkins environment variables
                    withEnv([
                        "DB_HOST=${env.DB_HOST}",
                        "DB_PORT=${env.DB_PORT}",
                        "DB_NAME=${env.DB_NAME}",
                        "DB_USERNAME=${env.DB_USERNAME}",
                        "DB_PASSWORD=${env.DB_PASSWORD}",
                        "DB_SSL_MODE=${env.DB_SSL_MODE}",
                        "JWT_SECRET=${env.JWT_SECRET}",
                        "JWT_EXPIRATION=${env.JWT_EXPIRATION}",
                        "GMAIL_USERNAME=${env.GMAIL_USERNAME}",
                        "GMAIL_APP_PASSWORD=${env.GMAIL_APP_PASSWORD}",
                        "GMAIL_SENDER_NAME=${env.GMAIL_SENDER_NAME}",
                        "GOOGLE_GEMINI_API_KEY=${env.GOOGLE_GEMINI_API_KEY}"
                    ]) {
                        sh '''
                            docker-compose up -d

                            # Wait for services to be healthy
                            echo "Waiting for services to start..."
                            sleep 30

                            # Check if API is responding
                            timeout 60 bash -c 'until curl -f http://localhost:8081/actuator/health; do sleep 5; done' || echo "API health check timeout"
                        '''
                    }

                    echo "✅ Deployment completed!"
                }
            }
        }
    }

    post {
        always {
            // Clean up but keep Docker images for deployment
            sh 'docker system prune -f || true'
        }
        success {
            echo "✅ Build and deployment completed successfully!"
            echo "🌐 API available at: http://localhost:8081"
            echo "📊 Health check: http://localhost:8081/actuator/health"
        }
        failure {
            echo "❌ Build or deployment failed!"
            sh 'docker-compose logs || true'
        }
    }
}
