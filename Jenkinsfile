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
                        -v "%CD%/.gradle:/root/.gradle" ^
                        -w /app ^
                        blog-android:latest ^
                        ./gradlew assembleDebug --info --stacktrace
                    '''
                    
                    // Debug: List direktori setelah build
                    bat '''
                        echo "Listing directory structure:"
                        dir /s
                    '''
                }
            }
        }
        
        stage('Archive APK') {
            steps {
                script {
                    bat '''
                        echo "Checking for APK files..."
                        dir /s *.apk
                    '''
                    archiveArtifacts(
                        artifacts: '**/*.apk',
                        fingerprint: true,
                        allowEmptyArchive: false
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
                bat '''
                    echo "Listing running containers:"
                    docker ps
                    echo "Listing all containers:"
                    docker ps -a
                '''
            }
        }
    }
}