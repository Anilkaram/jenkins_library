# What is a Jenkins Shared Library?
A Jenkins Shared Library is a collection of reusable Groovy code that can be shared across multiple Jenkins pipelines. It helps to:
- Reduce code duplication
- Standardize pipeline implementations
- Centralize common functions
- Make pipelines more maintainable

Common Use Cases
- Secret management (like retrieving credentials from Vault)
- Common pipeline steps (build, test, deploy) 
- Notifications (Slack, email, Teams)
- Environment setup
- Custom reporting
- Integration with external tools

# Typical Folder Structure

jenkins-shared-library/
├── src/                  # Groovy source files
│   └── org/example/      # Package structure
│       ├── VaultHelper.groovy
│       └── DockerHelper.groovy
├── vars/                 # Global variables/scripts
│   ├── buildApp.groovy
│   └── deployApp.groovy
├── resources/            # Non-Groovy files
│   └── scripts/          # Shell scripts, etc.
└── README.md
