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
                bat 'dir'  // Debug: list files
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
                        echo "Current directory:"
                        dir
                        echo "Running tests..."
                        docker run --rm ^
                        -v "%CD%":/app ^
                        -w /app ^
                        blog-android:latest ^
                        ./gradlew test --stacktrace
                    '''
                }
            }
        }
        
        stage('Build APK') {
            steps {
                script {
                    bat '''
                        echo "Building APK..."
                        docker run --rm ^
                        -v "%CD%":/app ^
                        -w /app ^
                        blog-android:latest ^
                        ./gradlew assembleDebug --stacktrace
                    '''
                }
            }
        }
        
        stage('Archive APK') {
            steps {
                script {
                    bat '''
                        echo "Checking for APK files..."
                        dir /s build\\outputs\\apk\\debug\\*.apk
                    '''
                    archiveArtifacts(
                        artifacts: '**/build/outputs/apk/debug/*.apk',
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
            script {
                echo 'Pipeline failed'
                bat 'docker logs ${DOCKER_IMAGE} || echo "No logs available"'
            }
        }
    }
}