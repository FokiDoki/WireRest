    pipeline {
        agent any

        environment {
            SPRING_PROFILE="prod"
        }
        stages {
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
                    sh 'echo ${SPRING_PROFILE}'
                    sh 'sudo systemctl restart wg_controller_dev'
                }
            }

        }
    }
