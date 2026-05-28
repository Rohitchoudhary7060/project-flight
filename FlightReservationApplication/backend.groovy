pipeline {
    agent any
    
    stages {
        stage('PULL') {
            steps {
                git branch: 'main', url: 'https://github.com/Rohitchoudhary7060/project-flight.git'
            }
        }
        
        stage('BUILD') {
            steps {
                sh '''
                    cd FlightReservationApplication
                    mvn clean package
                '''
            }
        }
        
        
        stage('QA') {
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar') {
                    sh '''
                        cd FlightReservationApplication
                        mvn sonar:sonar
                        -Dsonar.projectKey=flight-reservation 
                        -Dsonar.projectName='flight-reservation'"
                    '''
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('DOCKER BUILD') {
            steps {
                    sh '''
                        cd FlightReservationApplication
                        docker build -t rohit7060/flight-reservation:latest
                        docker push rohit7060/flight-reservation:latest
                        docker rmi rohit7060/flight-reservation:latest
                        '''
                

                }
            }
        }
        }
