    pipeline {
        agent any

        environment {
            RUN_ARGS="--spring.profiles.active=prod"
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
                    sh 'echo ARGS=${RUN_ARGS} > env'
                    sh 'sudo cp env /etc/default/wg_controller_dev'
                    sh 'sudo systemctl restart wg_controller_dev'
                }
            }

        }
    }
