pipeline {
  agent any
  //     docker {
  //       image 'abhishekf5/maven-abhishek-docker-agent:v1'
  //       args '--user root -v /var/run/docker.sock:/var/run/docker.sock' // mount Docker socket to access the host's Docker daemon
  //     }

  stages {
    stage('Checkout source repo') {
      steps {
        sh 'echo Checkout VCS'
        //git branch: 'main', url: 'https://github.com/iam-veeramalla/Jenkins-Zero-To-Hero.git'
      }
    }
    stage('Build and Test') {
      steps {
        sh 'ls -ltr'
        // build the project and create a JAR file
        sh 'mvn clean package -DskipTests'
      }
    }
    stage('Static Code Analysis') {
      environment {
        SONAR_URL = "http://13.250.33.202:9000"
      }
      steps {
        withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_AUTH_TOKEN')]) {
          sh 'mvn sonar:sonar -Dsonar.login=$SONAR_AUTH_TOKEN -Dsonar.host.url=${SONAR_URL}'
        }
      }
    }
    stage('Build and Push Docker Image') {
      environment {
        DOCKER_IMAGE = "hoangit3/springboot-cicd:${BUILD_NUMBER}"
        // DOCKERFILE_LOCATION = "java-maven-sonar-argocd-helm-k8s/spring-boot-app/Dockerfile"
        REGISTRY_CREDENTIALS = credentials('docker-cred')
      }
      steps {
        script {
          sh 'docker build -t ${DOCKER_IMAGE} .'
          def dockerImage = docker.image("${DOCKER_IMAGE}")
          docker.withRegistry('', "docker-cred") {
            dockerImage.push()
          }
        }
      }
    }
    stage('Checkout manifest repo') {
      steps {
        sh 'echo Checkout VCS manifest'
        git branch: 'master', url: 'https://github.com/huyhoangit3/springboot-cicd-manifests.git'
      }
    }
    stage('Update Deployment File') {
      environment {
          GIT_REPO_NAME = "springboot-cicd-manifests"
          GIT_USER_NAME = "huyhoangit3"
      }
      steps {
          withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
              sh '''
                  git config user.email "luongbahoang@devops.vn"
                  git config user.name "hoangdevops"
                  BUILD_NUMBER=${BUILD_NUMBER}
                  sed -i -E "s/hoangit3\\/springboot-cicd:[0-9]+/hoangit3\\/springboot-cicd:${BUILD_NUMBER}/g" springboot-cicd-deploy.yaml
                  git add .
                  git commit -m "Update deployment image to version ${BUILD_NUMBER}"
                  git push https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} master
              '''
          }
      }
    }
  }
}