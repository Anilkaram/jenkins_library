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
        // Using direct credentials from Vault without Jenkins credential store
        script.sh """
            docker login -u ${credentials.username} -p ${credentials.password}
        """
        
        // Mask credentials in console output
        script.mask passwords: [credentials.password], var: 'DOCKER_PASS'
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
}
