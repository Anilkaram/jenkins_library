/*package org.example

class VaultDockerHelper implements Serializable {
    def script
    
    VaultDockerHelper(script) {
        this.script = script
    }
    
    def loginToDockerHub() {
        script.withVault(
            configuration: [
                disableChildPoliciesOverride: false,
                engineVersion: 2,
                timeout: 60,
                vaultCredentialId: 'vault_token',
                vaultUrl: 'http://127.0.0.1:8200',
                prefixPath: 'secret'
            ],
            vaultSecrets: [[
                path: 'docker',
                secretValues: [
                    [envVar: 'DOCKER_PASS', vaultKey: 'password'],
                    [envVar: 'DOCKER_USER', vaultKey: 'username']
                ]
            ]]) {
                script.sh "docker login -u $script.env.DOCKER_USER -p $script.env.DOCKER_PASS"
        }
    }
    
    def runDockerContainer(String imageName, String containerName, int hostPort, int containerPort) {
        script.sh "docker run -itd --name $containerName -p $hostPort:$containerPort $imageName"
    }
}
