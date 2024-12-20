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
                // Fix permissions for Windows
                bat 'git update-index --chmod=+x gradlew'
                bat 'icacls gradlew /grant Everyone:F'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    bat 'docker build -t blog-android:latest . --no-cache'
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
                        bash -c "./gradlew assembleDebug"
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