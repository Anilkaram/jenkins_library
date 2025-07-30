def call(Map config = [:]) {
    def helper = new org.example.VaultDockerHelper(this)
    
    pipeline {
        agent any
        
        stages {
            stage('Fetch values from Vault and Login to Docker') {
                steps {
                    script {
                        helper.loginToDockerHub()
                    }
                }
            }
            
            stage('Run Application') {
                steps {
                    script {
                        helper.runDockerContainer(
                            imageName: config.imageName ?: 'anildoc143/blood_bank:app_image',
                            containerName: config.containerName ?: 'bb',
                            hostPort: config.hostPort ?: 9000,
                            containerPort: config.containerPort ?: 80
                        )
                    }
                }
            }
        }
    }
}
