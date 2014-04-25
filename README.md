# Overview
![SonarQube Logo](http://upload.wikimedia.org/wikipedia/commons/e/e6/Sonarqube-48x200.png)

This plugin allows you to generate a PDF report directly from your project. It will connect to the popular SonarQube tool and collate all
stats in to a well presented readable format. 

# Usage
To use the plugin, configure your `build.gradle` script and add the plugin:
```groovy
    buildscript {
        repositories {
            mavenCentral()
            maven { url 'http://dl.bintray.com/sion5/gradle-plugins/' }
        }
        dependencies {
            classpath 'org.sonar.report.pdf.gradle:sonarpdf-gradle-plugin:VERSION'
        }
    }
    apply plugin: 'sonarpdf'
```

# Tasks
The plugin adds the `generateSonarPDFReport` task to your projects, which allows you to generate a PDF quality report for your project.

## Configuration

### build.gradle
```groovy
    sonarPDF {
	        sonarHostUrl = 'http://localhost:9001/'
            reportType = 'executive'
            username = 'admin'
            password = 'secret'
            branch = 'someBranch' 			// can be left empty
            sonarBranch = 'someSonarBranch' // can be left empty
    }
```