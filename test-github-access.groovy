// Test GitHub Access Pipeline
// Use this to verify your GitHub token and repository access

pipeline {
    agent any
    
    stages {
        stage('Test GitHub Access') {
            steps {
                script {
                    echo "=== Testing GitHub Repository Access ==="
                    
                    // Test basic git operations
                    sh '''
                        echo "Testing git clone access..."
                        git ls-remote --heads https://github.com/COS301-SE-2025/Fire-Fighter.git
                        echo "✅ Repository is accessible"
                    '''
                    
                    // Test with credentials
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', 
                                                   usernameVariable: 'GIT_USERNAME', 
                                                   passwordVariable: 'GIT_TOKEN')]) {
                        sh '''
                            echo "Testing authenticated access..."
                            echo "Username: $GIT_USERNAME"
                            echo "Token length: ${#GIT_TOKEN}"
                            
                            # Test API access
                            curl -H "Authorization: token $GIT_TOKEN" \
                                 -H "Accept: application/vnd.github.v3+json" \
                                 https://api.github.com/repos/COS301-SE-2025/Fire-Fighter
                        '''
                    }
                }
            }
        }
        
        stage('Test Repository Operations') {
            steps {
                script {
                    echo "=== Testing Repository Operations ==="
                    
                    // Test checkout
                    checkout scm
                    
                    sh '''
                        echo "Repository checked out successfully"
                        echo "Current directory: $(pwd)"
                        echo "Repository contents:"
                        ls -la
                        
                        echo ""
                        echo "Git status:"
                        git status
                        
                        echo ""
                        echo "Latest commit:"
                        git log --oneline -1
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo "🎉 GitHub access test successful!"
            echo "Your token and repository configuration are working correctly."
        }
        failure {
            echo "❌ GitHub access test failed!"
            echo "Please check:"
            echo "1. GitHub token permissions"
            echo "2. Repository URL"
            echo "3. Credentials configuration in Jenkins"
        }
    }
}
