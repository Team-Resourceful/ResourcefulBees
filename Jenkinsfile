pipeline {
  agent any
  stages {
    stage('Running tests') {
      steps {
        sh 'chmod +x gradlew'
        sh './gradlew'
        sh './gradlew test'
      }
    }

    stage('Building') {
      steps {
        sh './gradlew publish'
      }
    }

    stage('Fetching artifacts') {
      steps {
        archiveArtifacts(fingerprint: true, artifacts: 'build/reobfJar/*.jar')
      }
    }

  }
}