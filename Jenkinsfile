pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'blog-android'
        DOCKER_TAG = 'latest'
        WORKSPACE_PATH = 'C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\Blog-Android'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                // Fix gradlew line endings
                bat '''
                    @echo off
                    git update-index --chmod=+x gradlew
                    dos2unix gradlew || echo "Continuing without dos2unix"
                    
                    REM If dos2unix is not available, use PowerShell to fix line endings
                    powershell -Command "(Get-Content gradlew) | ForEach-Object { $_ -replace \\"\\r\\",\\"\\" } | Set-Content gradlew -NoNewline"
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    bat '''
                        docker build -t blog-android:latest . --no-cache
                    '''
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
                        blog-android:latest ^
                        sh -c "dos2unix gradlew && chmod +x gradlew && ./gradlew test"
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
                        blog-android:latest ^
                        sh -c "dos2unix gradlew && chmod +x gradlew && ./gradlew assembleDebug"
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
            echo 'Pipeline failed. Error log:'
            script {
                bat '''
                    docker ps -a
                    docker logs %DOCKER_IMAGE% || echo "No container logs available"
                '''
            }
        }
        always {
            cleanWs()
        }
    }
}