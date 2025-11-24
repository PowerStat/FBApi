// Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0.
pipeline
 {
  agent any


  tools
   {
    maven 'Maven3'
    jdk 'JDK12'
    git 'GIT'
   }


  options
   {
    buildDiscarder(logRotator(numToKeepStr: '4'))
    skipStagesAfterUnstable()
    disableConcurrentBuilds()
   }


  triggers
   {
    // MINUTE HOUR DOM MONTH DOW
    pollSCM('H 6-18/4 * * 1-5')
   }


  environment
   {
    DISABLE_AUTH = 'true'
    expectedRemoteUrl = "https://github.com/PowerStat/FBApi.git"
   }


  stages
   {
    stage('Clean')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode clean'
           }
          else
           {
            bat 'mvn --batch-mode clean'
           }
         }
       }
     }

    stage('Build')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode compile'
           }
          else
           {
            bat 'mvn --batch-mode compile'
           }
         }
       }
     }

    stage('UnitTests')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode resources:testResources compiler:testCompile surefire:test'
           }
          else
           {
            bat 'mvn --batch-mode resources:testResources compiler:testCompile surefire:test'
            //  -Dmaven.test.failure.ignore=true
           }
         }
       }
      post
       {
        always
         {
          junit testResults: 'target/surefire-reports/*.xml'
         }
       }
     }

    stage('MutationTests')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sht 'mvn --batch-mode org.pitest:pitest-maven:mutationCoverage'
           }
          else
           {
            bat 'mvn --batch-mode org.pitest:pitest-maven:mutationCoverage'
           }
         }
       }
     }

    stage('Sanity check')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
           }
          else
           {
            bat 'mvn --batch-mode checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
           }
         }
       }
     }

    stage('Packaging')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode jar:jar'
           }
          else
           {
            bat 'mvn --batch-mode jar:jar'
           }
         }
       }
     }

    stage('Install local')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode jar:jar install:install'
           }
          else
           {
            bat 'mvn --batch-mode jar:jar install:install' // maven-jar-plugin falseCreation default is false, so no doubled jar construction here, but required for maven-install-plugin internal data
           }
         }
       }
     }

    stage('Integration tests')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode failsafe:integration-test failsafe:verify'
           }
          else
           {
            bat 'mvn --batch-mode failsafe:integration-test failsafe:verify'
           }
         }
       }
     }

    stage('Documentation')
     {
      steps
       {
        script
         {
          if (isUnix())
           {
            sh 'mvn --batch-mode -Dweb.server=www.powerstat.de site'
           }
          else
           {
            bat 'mvn --batch-mode -Dweb.server=www.powerstat.de site'
           }
         }
       }
      post
       {
        always
         {
          publishHTML(target: [reportName: 'Site', reportDir: 'target/site', reportFiles: 'index.html', keepAll: false])
         }
       }
     }


   }

 }
