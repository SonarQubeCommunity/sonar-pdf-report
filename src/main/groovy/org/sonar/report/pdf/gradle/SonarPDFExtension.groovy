package org.sonar.report.pdf.gradle

import org.gradle.api.Project

/**
 *
 * @author Sion Williams
 */
class SonarPDFExtension {
    final Project project

    String sonarHostUrl = 'http://localhost:9000/'
    String branch = null
    String sonarBranch = null
    String reportType = 'executive'
    String username = null
    String password = null
    String sonarProjectId = "${project.group}:${project.name}"

    SonarPDFExtension(final Project project){
        this.project = project
    }
}
