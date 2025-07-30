package org.example

class DockerOperations implements Serializable {
    def script
    
    DockerOperations(script) {
        this.script = script
    }
    
    /**
     * Login to Docker Hub using credentials from Vault
     * @param credentials Map containing username and password
     */
    def dockerLogin(Map credentials) {
        // Store password in a temporary variable that Jenkins can mask
        script.withEnv(["DOCKER_PASS=${credentials.password}"]) {
            // The password will be automatically masked in logs
            script.sh """
                docker login -u ${credentials.username} -p ${script.env.DOCKER_PASS}
            """
        }
    }
    
    /**
     * Run a Docker container
     * @param config Configuration map with:
     *   - imageName: Docker image name (required)
     *   - containerName: Name for the container (optional)
     *   - hostPort: Host port to map (optional)
     *   - containerPort: Container port to map (optional)
     *   - envVars: Environment variables to pass (optional)
     *   - detach: Run in detached mode (default: true)
     */
    def runContainer(Map config) {
        if (!config.imageName) {
            script.error("imageName is required to run a container")
        }
        
        def cmd = ["docker run"]
        
        if (config.detach != false) {
            cmd << "-d"
        } else {
            cmd << "-it"
        }
        
        if (config.containerName) {
            cmd << "--name ${config.containerName}"
        }
        
        if (config.hostPort && config.containerPort) {
            cmd << "-p ${config.hostPort}:${config.containerPort}"
        }
        
        if (config.envVars) {
            config.envVars.each { k, v ->
                cmd << "-e ${k}=${v}"
            }
        }
        
        cmd << config.imageName
        
        script.sh cmd.join(' ')
    }
    
    /**
     * Convenience method for the specific use case
     */
    def runBloodBankContainer() {
        runContainer(
            imageName: 'anildoc143/blood_bank:app_image',
            containerName: 'bb',
            hostPort: 9000,
            containerPort: 80,
            detach: true
        )
    }
}
