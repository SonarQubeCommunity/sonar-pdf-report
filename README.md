# Overview
![SonarQube Logo](http://upload.wikimedia.org/wikipedia/commons/e/e6/Sonarqube-48x200.png)

This plugin allows you to generate a PDF report directly from your project. It will connect to the popular SonarQube tool and collate all
stats in to a well presented readable format.

# Build Status

[![Build Status](https://travis-ci.org/willis7/sonarpdf-gradle-plugin.svg?branch=master)](https://travis-ci.org/willis7/sonarpdf-gradle-plugin)

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

NOTE: As the sonarpdf plugin uses the group property its important to ensure it is set before the plugin is applied.
Failure to do so will result in an error such as: 

Execution failed for task ':generateSonarPDFReport'. 
Can┬┤t access to Sonar or project doesn't exist on Sonar instance.   

Alternatively, you can set the `sonarProjectId` property in the `sonarPDF` extension.
 

# Tasks
The plugin adds the `generateSonarPDFReport` task to your project which allows you to generate a PDF quality report.

## Configuration

### build.gradle
```groovy
    sonarPDF {
            sonarHostUrl = 'http://nemo.sonarqube.org/'
            reportType = 'executive'
            username = 'admin'
            password = 'secret'
    }
```

## Task properties
### generateSonarPDFReport properties

To configure the `generateSonarPDFReport` task you can choose to set the following properties within the 
`sonarPDF` extension:

* `sonarProjectId` : Specify the name of the Sonar project. Default is *"${project.group}:${project.name}"*.
* `sonarHostUrl` : Sonar Base URL.
* `username` : Username to access WS API. *Optional*.
* `password` : Password to access WS API. *Optional*.
* `sonarBranch` : Branch to be used. *Optional*.
* `branch` : Use of branch parameter is deprecated, use sonarBranch instead.
* `reportType` : Type of report. Options are `executive` or `workbook`.