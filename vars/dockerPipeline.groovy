/**def call(Map config = [:]) {
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
*/
def call(String vaultPath, List secretMapping, Map config = [:], Closure body) {
    def vaultConfig = [
        disableChildPoliciesOverride: false,
        engineVersion: 2,
        timeout: 60,
        vaultCredentialId: 'vault_token',
        vaultUrl: 'http://127.0.0.1:8200',
        prefixPath: 'secret'
    ] + config // allow overrides

    def vaultSecrets = [[
        path: vaultPath,
        secretValues: secretMapping
    ]]

    withVault(
        configuration: vaultConfig,
        vaultSecrets: vaultSecrets
    ) {
        body()
    }
}
