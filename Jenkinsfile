pipeline {
    agent any
    stages {
        stage('CheckMavenVersion') {
            steps {
                sh 'mvn -version'
            }
        }
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
