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
                bat 'attrib -R gradlew'
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
                        docker run --rm ^
                        -v "%CD%":/app ^
                        -v "%CD%/.gradle:/root/.gradle" ^
                        -w /app ^
                        blog-android:latest ^
                        ./gradlew assembleDebug --info
                    '''
                }
            }
        }
        
        stage('Archive APK') {
            steps {
                archiveArtifacts(
                    artifacts: '**/*.apk',
                    fingerprint: true,
                    allowEmptyArchive: false
                )
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        failure {
            script {
                bat 'docker ps && docker ps -a'
            }
        }
    }
}