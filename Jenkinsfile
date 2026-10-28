pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        DOCKERHUB_USER       = 'rickro77'
        IMAGE_TAG            = "${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_NAME = 'calculator'
        NOTIFY_EMAIL         = 'rrickyroger07@gmail.com'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                // builds both images, named and tagged via DOCKERHUB_USER / IMAGE_TAG
                sh 'docker compose build'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin

                        docker push ${DOCKERHUB_USER}/calculator:${IMAGE_TAG}
                        docker push ${DOCKERHUB_USER}/calculator-nginx:${IMAGE_TAG}

                        docker tag ${DOCKERHUB_USER}/calculator:${IMAGE_TAG} ${DOCKERHUB_USER}/calculator:latest
                        docker tag ${DOCKERHUB_USER}/calculator-nginx:${IMAGE_TAG} ${DOCKERHUB_USER}/calculator-nginx:latest
                        docker push ${DOCKERHUB_USER}/calculator:latest
                        docker push ${DOCKERHUB_USER}/calculator-nginx:latest

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker compose up -d --remove-orphans --wait --wait-timeout 120
                    docker compose ps
                    docker compose exec -T nginx wget -qO- http://127.0.0.1/actuator/health | grep UP
                '''
            }
        }
    }

    post {
        success {
            mail to: env.NOTIFY_EMAIL,
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build #${env.BUILD_NUMBER} passed and was deployed.\nImage tag: ${env.DOCKERHUB_USER}/calculator:${env.BUILD_NUMBER}\nDetails: ${env.BUILD_URL}"
        }
        failure {
            mail to: env.NOTIFY_EMAIL,
                 subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build #${env.BUILD_NUMBER} failed.\nConsole output: ${env.BUILD_URL}console"
        }
    }
}
