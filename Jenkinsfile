pipeline {
    agent any
    stages {
        stage('CheckMavenVersion') {
            steps {
                sh 'JAVA_HOME=/usr/lib/jvm/jdk-20 mvn -version'
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
