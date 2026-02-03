#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    environment {
        //DOCKER_REPO_SERVER = '330673547330.dkr.ecr.eu-central-1.amazonaws.com'
        //DOCKER_REPO = "${DOCKER_REPO_SERVER}/java-maven-app"
        DOCKER_REPO = 'tonyjacob79/java-maven-app'
    }
    stages {
        stage('increment version') {
            steps {
                script {
                    echo 'incrementing app version...'
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }
        stage('build app') {
            steps {
                script {
                    echo 'building the application...'
                    sh 'mvn clean package'
                }
            }
        }

        stage('commit version update'){
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'GitHub-PAT', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        //sh "git remote set-url origin https://${USER}:${PASS}@gitlab.com/twn-devops-bootcamp/latest/11-eks/java-maven-app.git"
                         // Set Git identity so commit works
                        sh 'git config user.email "jenkins@ci.local"'
                        sh 'git config user.name "Jenkins CI"'                       
                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/bigtony79/java-maven-app-eks-deployment-ci-cd-multibranch-jenkins.git"
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:jenkins-jobs-dockerhub'
                    }
                }
            }
        }


        stage('build image') {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-pat', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        sh "docker build -t ${DOCKER_REPO}:${IMAGE_NAME} ."
                        //sh 'echo $PASS | docker login -u $USER --password-stdin ${DOCKER_REPO_SERVER}'
                        sh 'echo $PASS | docker login -u $USER --password-stdin'
                        sh "docker push ${DOCKER_REPO}:${IMAGE_NAME}"
                    }
                }
            }
        }
        stage('deploy') {
           
            steps {
                script {
                   echo 'deploying docker image...'
                   withCredentials([usernamePassword(credentialsId: 'aws-creds', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')])
 {
                       sh 'aws eks update-kubeconfig --region us-east-1 --name eks-cluster-demo'
                       sh 'kubectl get nodes'
                       
                   }
                   //sh 'envsubst < kubernetes/deployment.yaml | kubectl apply -f -'
                   //sh 'envsubst < kubernetes/service.yaml | kubectl apply -f -'
                }
            }
        }
        
    }
}

