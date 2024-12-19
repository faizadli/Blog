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
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", "--cache-from ${DOCKER_IMAGE}:${DOCKER_TAG} .")
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").inside {
                        bat 'gradlew test'
                    }
                }
            }
        }
        
        stage('Build APK') {
            steps {
                script {
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").inside {
                        bat 'gradlew assembleDebug'
                    }
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
            echo "Pipeline failed. Error log:"
            bat 'docker logs %DOCKER_IMAGE%'
        }
        always {
            cleanWs()
        }
    }
}