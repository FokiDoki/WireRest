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
        stage('Run'){
            steps {
                sh 'sudo /usr/lib/jvm/jdk-20/bin/java -jar target/WireguardController-0.2-SNAPSHOT.jar'
            }
        }

    }
}
