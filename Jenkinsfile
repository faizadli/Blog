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
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    bat """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    """
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    bat """
                        docker run --rm -v "${WORKSPACE_PATH}:/app" -w /app ${DOCKER_IMAGE}:${DOCKER_TAG} ./gradlew test
                    """
                }
            }
        }
        
        stage('Build APK') {
            steps {
                script {
                    bat """
                        docker run --rm -v "${WORKSPACE_PATH}:/app" -w /app ${DOCKER_IMAGE}:${DOCKER_TAG} ./gradlew assembleDebug
                    """
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
                bat """
                    docker ps -a
                    docker logs ${DOCKER_IMAGE} || echo "No container logs available"
                """
            }
        }
        always {
            cleanWs()
        }
    }
}