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
/**
 * Fetch secrets from HashiCorp Vault and inject them as environment variables.
 *
 * @param vaultPath      Path in Vault to fetch secrets from (e.g., 'docker')
 * @param secretMapping  List of [envVar, vaultKey] pairs
 * @param config         Map of Vault plugin config options (optional, uses sensible defaults)
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
        secretValues: secretMapping.collect { pair ->
            [envVar: pair.envVar, vaultKey: pair.vaultKey]
        }
    ]]

    withVault(
        configuration: vaultConfig,
        vaultSecrets: vaultSecrets
    ) {
        body()
    }
}
