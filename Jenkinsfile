pipeline {
    agent any
    
    tools {
        jdk 'jdk-11'
        gradle 'gradle'
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Checkout dari GitHub
                git branch: 'master',
                    url: 'https://github.com/faizadli/Blog.git'
            }
        }
        
        stage('Build') {
            steps {
                // Build Android project
                sh './gradlew clean assembleDebug'
            }
        }
        
        stage('Test') {
            steps {
                // Run unit tests
                sh './gradlew test'
            }
        }
    }
    
    post {
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}