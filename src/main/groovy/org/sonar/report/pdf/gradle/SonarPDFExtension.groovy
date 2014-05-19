package org.sonar.report.pdf.gradle

/**
 *
 * @author Sion Williams
 */
class SonarPDFExtension {
    String sonarHostUrl = 'http://localhost:9000/'
    String branch = null
    String sonarBranch = null
    String reportType = 'executive'
    String username = 'admin'
    String password = 'admin'
    String sonarProjectId
}
