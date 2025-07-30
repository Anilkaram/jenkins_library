package org.example

class VaultOperations implements Serializable {
    def script
    
    VaultOperations(script) {
        this.script = script
    }
    
    /**
     * Retrieve secrets from Vault and set as environment variables
     * @param config Configuration map with:
     *   - vaultUrl: Vault server URL (default: 'http://127.0.0.1:8200')
     *   - credentialId: Jenkins credential ID for Vault (default: 'vault_token')
     *   - secretPath: Path to secret in Vault (default: 'docker')
     *   - secretMappings: List of [envVar: '', vaultKey: ''] mappings
     * @return Map of retrieved secrets
     */
    def getSecrets(Map config = [:]) {
        def defaultConfig = [
            vaultUrl: 'http://127.0.0.1:8200',
            credentialId: 'vault_token',
            secretPath: 'docker',
            secretMappings: [
                [envVar: 'DOCKER_USER', vaultKey: 'username'],
                [envVar: 'DOCKER_PASS', vaultKey: 'password']
            ]
        ]
        
        def finalConfig = defaultConfig << config
        
        script.withVault(
            configuration: [
                disableChildPoliciesOverride: false,
                engineVersion: 2,
                timeout: 60,
                vaultCredentialId: finalConfig.credentialId,
                vaultUrl: finalConfig.vaultUrl,
                prefixPath: 'secret'
            ],
            vaultSecrets: [[
                path: finalConfig.secretPath,
                secretValues: finalConfig.secretMappings
            ]]
        ) {
            // Return the secrets as a map
            def secrets = [:]
            finalConfig.secretMappings.each { mapping ->
                secrets[mapping.vaultKey] = script.env[mapping.envVar]
            }
            return secrets
        }
    }
    
    /**
     * Specifically get Docker credentials from Vault
     * @return Map with username and password
     */
    def getDockerCredentials() {
        return getSecrets(secretPath: 'docker', secretMappings: [
            [envVar: 'DOCKER_USER', vaultKey: 'username'],
            [envVar: 'DOCKER_PASS', vaultKey: 'password']
        ])
    }
}
