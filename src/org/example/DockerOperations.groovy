package org.example

class DockerOperations implements Serializable {
    def script
    
    DockerOperations(script) {
        this.script = script
    }
    
    /**
     * Login to Docker Hub using credentials
     * @param credentials Map containing username and password
     */
    def dockerLogin(Map credentials) {
        script.withCredentials([
            script.usernamePassword(
                credentialsId: 'temp-docker-creds',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS',
                username: credentials.username,
                password: credentials.password
            )
        ]) {
            script.sh "docker login -u $script.env.DOCKER_USER -p $script.env.DOCKER_PASS"
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
}
