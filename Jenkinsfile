def performHttpRequest() {
    echo "${HEALTH_CHECK_SUCCESS_CODE}"
    echo "${HEALTH_CHECK_URL}"
    try {


        echo "curl -s -o /dev/null -w ${HEALTH_CHECK_SUCCESS_CODE} ${HEALTH_CHECK_URL}"
        def response = sh(script: 'curl -s -o /dev/null -w 200 http://127.0.0.1:8081/v1/interface?token=admin').trim()
        echo "Done"
        return response
    } catch (e){
        echo e
        throw e
    }


}

def healthCheck() {
    def startTime = new Date().toInstant().toEpochMilli()

    timeout(time: HEALTH_CHECK_TIMEOUT.toInteger(), unit: 'SECONDS') {
        while (true) {
            def currentTime = new Date().toInstant().toEpochMilli()
            def elapsedTime = (currentTime - startTime) / 1000

            // Проверка на истечение времени выполнения
            if (elapsedTime >= HEALTH_CHECK_TIMEOUT.toInteger()) {
                error "Timeout reached. Exiting..."
            }

            // Выполнение запроса
            def httpResponse = performHttpRequest()
            echo "HTTP RESPONCE"
            echo httpResponse

            // Проверка HTTP-кода
            if (httpResponse.toInteger() == HEALTH_CHECK_SUCCESS_CODE.toInteger()) {
                echo "Successful response received. Exiting..."
                return
            } else {
                echo "Received HTTP code: $httpResponse. Retrying in ${HEALTH_CHECK_INTERVAL} seconds..."
                sleep HEALTH_CHECK_INTERVAL
            }
        }
    }
}

pipeline {
    agent any

    environment {
        RUN_PROFILES="prod"
        RUN_PORT=8081
        POM_VERSION="0.7"
        SERVICE_NAME="wirerest"
        JAVA_HOME="${JAVA21_HOME}"
        HEALTH_CHECK_TIMEOUT=30
        HEALTH_CHECK_SUCCESS_CODE=200
        HEALTH_CHECK_URL="http://127.0.0.1:${RUN_PORT}/v1/interface?token=admin"
        HEALTH_CHECK_INTERVAL=1
        RUN_ARGS="--spring.profiles.active=${RUN_PROFILES} " +
        "--server.port=${RUN_PORT} " +
        "--wg.interface.name=server " +
        "--debug "
    }
    stages {
        stage('Build/Test/Compile') {
            steps {
               // sh '${MAVEN_BIN} clean validate compile package'
                jacoco(execPattern: '**/target/*.exec')
                recordCoverage(tools: [[parser: 'JACOCO']])
            }
        }
        stage('Run'){
            steps {
                script {
                    sh 'echo ARGS=${RUN_ARGS} > env'
                    sh 'echo JAR_PATH=`pwd`/target/${SERVICE_NAME}-${POM_VERSION}.jar >> env'
                    sh 'sudo cp env /etc/default/${SERVICE_NAME}'
                    sh 'sudo systemctl restart ${SERVICE_NAME}'
                }
            }
        }

        stage('Check'){
            steps {
                healthCheck()
            }
        }


    }
    post {
        failure {
            sh 'sudo journalctl -u wirerest.service --no-pager -n 250'
        }
    }
}

