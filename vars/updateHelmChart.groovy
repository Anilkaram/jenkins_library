#!/usr/bin/env groovy

def call(Map params = [:]) {
    def defaults = [
        appRepoUrl: 'https://github.com/Anilkaram/travel_ease.git',
        appRepoBranch: 'main',
        valuesFilePath: 'values.yaml',
        chartPath: 'app/',
        frontendImage: env.FRONTEND_IMAGE,
        frontendTag: env.FRONTEND_TAG,
        backendImage: env.BACKEND_IMAGE,
        backendTag: env.BACKEND_TAG,
        helmRepoUrl: env.HELM_REPO_URL
    ]
    
    params = defaults + params
    
    // Validate required parameters
    if (!params.appRepoUrl) {
        error "appRepoUrl parameter is required"
    }
    if (!params.helmRepoUrl) {
        error "helmRepoUrl parameter is required"
    }
    
    script {
        // Clone the application repository
        git branch: params.appRepoBranch, url: params.appRepoUrl
        
        dir(params.chartPath) {
            // Update client values
            sh """
                sed -i -e 's|cimage: .*|cimage: ${params.frontendImage}|' \
                       -e 's|ctag: .*|ctag: ${params.frontendTag}|' \
                       ${params.valuesFilePath}
            """
            
            // Update backend values
            sh """
                sed -i -e 's|simage: .*|simage: ${params.backendImage}|' \
                       -e 's|stag: .*|stag: ${params.backendTag}|' \
                       ${params.valuesFilePath}
            """
            
            // Verify changes
            sh "cat ${params.valuesFilePath}"
            sh "cat Chart.yaml"
        }
        
        // Package the chart
        sh "helm package ${params.chartPath}"
        
        // Update index.yaml
        sh "helm repo index . --url ${params.helmRepoUrl}"
    }
}
