Sonar PDF Report Plugin
=========================

[Compatibility and download information](http://update.sonarsource.org/plugins/pdfreport-confluence.html).

## Description / Features

Generate a project quality report in PDF format with the most relevant information from SonarQube web interface. The report aims to be a deliverable as part of project documentation.

The report contains:

* Dashboard
* Violations by categories
* Hotspots:
  * Most violated rules
  * Most violated files
  * Most complex classes
  * Most duplicated files
* Dashboard, violations and hotspots for all child modules (if they exists)

## Installation

1. Install the plugin through the [Update Center](http://docs.sonarqube.org/display/SONAR/Update+Center) or download it into the SONARQUBE_HOME/extensions/plugins directory
1. Restart SonarQube

## Usage

SonarQube PDF works as a post-job task. In this way, a PDF report is generated after each analysis in SonarQube.

### Configuration

You can skip report generation or select report type (executive or workbook) globally or at the project level. You can also provide an username/password if your project is secured by SonarQube user management:

TODO: [image]

### Download the report

PDF report can be downloaded from the SonarQube GUI:

TODO: [image]

Issue tracking:
https://jira.codehaus.org/browse/SONARPLUGINS/component/14372

CI builds:
https://sonarplugins.ci.cloudbees.com/job/report-pdf
