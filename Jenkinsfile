pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'blog-android'
        DOCKER_TAG = 'latest'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                // Convert line endings and fix permissions
                bat '''
                    git config --global core.autocrlf input
                    git checkout .
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    bat 'docker build -t blog-android:latest . --no-cache'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    bat '''
                        docker run --rm ^
                        -v "%CD%:/app" ^
                        -w /app ^
                        --user root ^
                        blog-android:latest ^
                        sh -c "./gradlew --no-daemon test"
                    '''
                }
            }
        }
        
        stage('Build APK') {
            steps {
                script {
                    bat '''
                        docker run --rm ^
                        -v "%CD%:/app" ^
                        -w /app ^
                        --user root ^
                        blog-android:latest ^
                        sh -c "./gradlew --no-daemon assembleDebug"
                    '''
                }
            }
        }
        
        stage('Archive APK') {
            steps {
                archiveArtifacts artifacts: '**/build/outputs/apk/debug/*.apk', fingerprint: true
            }
        }
    }
    
    post {
        failure {
            echo 'Pipeline failed'
        }
        always {
            cleanWs()
        }
    }
}