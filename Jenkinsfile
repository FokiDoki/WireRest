    pipeline {
        agent any

        environment {
            RUN_PROFILES="prod"
            RUN_PORT=8081
            POM_VERSION="0.2"
            SERVICE_NAME="wirerest"
            RUN_ARGS="--spring.profiles.active=${RUN_PROFILES} --server.port=${RUN_PORT}"
        }
        stages {
            stage('Build') {
                steps {
                    sh 'JAVA_HOME=/usr/lib/jvm/jdk-20 mvn clean validate compile'
                }
            }
            stage('Test/Package') {
                steps {
                    sh 'JAVA_HOME=/usr/lib/jvm/jdk-20 mvn package'
                    jacoco(execPattern: '**/target/*.exec')
                    recordCoverage(tools: [[parser: 'JACOCO']])
                }
            }
            stage('Run'){
                steps {
                    sh 'echo ARGS=${RUN_ARGS} > env'
                    sh 'echo JAR_PATH=`pwd`/target/${SERVICE_NAME}-${POM_VERSION}.jar >> env'
                    sh 'sudo cp env /etc/default/${SERVICE_NAME}'
                    sh 'sudo systemctl restart ${SERVICE_NAME}'
                }
            }

            stage('Check'){
                steps {
                    sleep 10
                    sh 'curl -s http://127.0.0.1:${RUN_PORT}/interface  > /dev/null'
                }
            }


        }
    }
