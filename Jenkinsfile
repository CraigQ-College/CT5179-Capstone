pipeline {
    agent any

    tools {
        git 'Default'
    }

    stages {
        stage('GetProject') {
            steps {
                git branch: 'CD-UnitTesting', url: 'https://github.com/CraigQ-College/CT5179-Capstone.git'
            }
        }
        stage('Build') {
            steps {
                withMaven(maven: 'maven') {
                    sh "mvn clean:clean"

                    sh "mvn dependency:copy-dependencies"

                    sh "mvn compiler:compile"
                }
            }
        }

        stage('Test and Package'){
            steps {
                withMaven(maven: 'maven') {
                    sh 'mvn package'
                }
            }
        }

        stage('Archive'){
            steps {
                archiveArtifacts allowEmptyArchive: true,
                    artifacts:'**/vfc*.war'
            }
        }

        stage('Deploy'){
            steps {
               sh 'docker build -f Dockerfile -t vfc . '

               sh 'docker rm -f "vfcwebcontainer" || true'

               sh 'docker run --name "vfcwebcontainer" -p 9090:8080 --detach vfc:latest'
            }
        }
    }
}
