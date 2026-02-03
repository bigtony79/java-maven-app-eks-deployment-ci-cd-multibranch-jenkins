def buildJar() {
    echo 'building the application...'
    sh 'mvn package'
}

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'dockerhub-pat', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t tonyjacob79/java-app:java-mvn-1.3 .'
        sh 'echo $PASS | docker login -u $USER --password-stdin'
        sh 'docker push tonyjacob79/java-app:java-mvn-1.3'
    }
}

def deployApp() {
    echo 'deploying the docker image in Kubernetes....'
    //sh 'kubectl set image deployment/java-deploy java-container=tonyjacob79/java-app:java-mvn-1.2'
    sh 'kubectl create deployment nginx-deployment --image=nginx:latest'
}

return this
