def call(String vaultPath, List secretMapping, Map config = [:], Closure body) {
    def vaultConfig = [
        disableChildPoliciesOverride: false,
        engineVersion: 2,
        timeout: 60,
        vaultCredentialId: 'vault_token',
        vaultUrl: 'http://127.0.0.1:8200',
        prefixPath: 'secret'
    ] + config

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
