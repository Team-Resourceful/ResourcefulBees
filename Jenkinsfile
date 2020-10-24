pipeline {
  agent any
  stages {
    stage('Running tests') {
      steps {
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