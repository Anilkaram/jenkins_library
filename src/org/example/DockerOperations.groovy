/*package org.example

class DockerOperations implements Serializable {
    def script
    
    DockerOperations(script) {
        this.script = script
    }
    
    def dockerLogin(Map credentials) {
        script.withEnv(["DOCKER_PASS=${credentials.password}"]) {
            script.sh """
                docker login -u ${credentials.username} -p ${script.env.DOCKER_PASS}
            """
        }
    }
    
    def runBloodBankContainer() {
        script.sh """
            docker run -itd --name bb -p 9000:80 anildoc143/blood_bank:app_image
        """
    }
}
*?
