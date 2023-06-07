pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean validate compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
    }
}
