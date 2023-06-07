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
                sh 'JAVA_HOME=/usr/lib/jvm/jdk-20 mvn clean validate compile'
            }
        }
        stage('Test') {
            steps {
                sh 'JAVA_HOME=/usr/lib/jvm/jdk-20 mvn test'
            }
        }
        stage('Package') {
            steps {
                sh 'JAVA_HOME=/usr/lib/jvm/jdk-20 mvn package'
            }
        }
        stage('who'){
            steps {
                sh 'whoami'
            }
        }
        stage('Run'){
            steps {
                sh 'systemctl restart wg_controller_dev'
            }
        }

    }
}
