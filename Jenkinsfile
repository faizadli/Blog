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
                // Fix permissions
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
        
        stage('Run Tests') {
            steps {
                script {
                    bat '''
                        docker run --rm ^
                        -v "%CD%":/app ^
                        -w /app ^
                        -e GRADLE_USER_HOME=/app/.gradle ^
                        blog-android:latest ^
                        ./gradlew test --info
                    '''
                }
            }
        }
        
        stage('Build APK') {
            steps {
                script {
                    bat '''
                        docker run --rm ^
                        -v "%CD%":/app ^
                        -w /app ^
                        -e GRADLE_USER_HOME=/app/.gradle ^
                        blog-android:latest ^
                        ./gradlew assembleDebug --info
                    '''
                }
            }
        }
        
        stage('Archive APK') {
            steps {
                script {
                    bat 'dir /s /b "**/build/outputs/apk/debug/*.apk"'
                    archiveArtifacts(
                        artifacts: '**/build/outputs/apk/debug/*.apk',
                        fingerprint: true,
                        allowEmptyArchive: true
                    )
                }
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
        failure {
            echo 'Pipeline failed'
        }
        success {
            echo 'Pipeline succeeded'
        }
    }
}