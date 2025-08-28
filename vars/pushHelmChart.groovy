#!/usr/bin/env groovy

def call(Map params = [:]) {
    def defaults = [
        helmRepoUrl: '',
        helmRepoBranch: 'main',
        credentialsId: 'github'
    ]
    
    params = defaults + params
    
    // Validate required parameters
    if (!params.helmRepoUrl) {
        error "helmRepoUrl parameter is required"
    }
    
    script {
        def repoName = "helm_repo_${UUID.randomUUID().toString().substring(0, 8)}"
        
        // Clean and clone the helm repository
        sh "rm -rf ${repoName}"
        sh "git clone -b ${params.helmRepoBranch} ${params.helmRepoUrl} ${repoName}"
        
        // Copy packaged chart and index
        sh "cp *.tgz ${repoName}/"
        sh "cp index.yaml ${repoName}/"
        
        dir(repoName) {
            // Verify changes
            sh "git status"
            sh "ls -la *.tgz"
            
            // Configure git and commit changes
            sh "git config user.name 'jenkins'"
            sh "git config user.email 'jenkins@example.com'"
            sh "git add ."
            sh "git commit -m 'Update chart to new version'"
            
            withCredentials([gitUsernamePassword(
                credentialsId: params.credentialsId, 
                usernameVariable: 'GIT_USERNAME', 
                passwordVariable: 'GIT_PASSWORD'
            )]) {
                // Extract clean Git URL for pushing
                def cleanUrl = params.helmRepoUrl.replaceFirst(/^(https?|git):\/\//, '').replaceFirst(/\.git$/, '')
                sh "git push https://\${GIT_USERNAME}:\${GIT_PASSWORD}@${cleanUrl} ${params.helmRepoBranch}"
            }
        }
    }
}
