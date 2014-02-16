package org.sonar.report.pdf.gradle

import org.sonar.report.pdf.util.SonarAccess
import spock.lang.Specification
import org.gradle.api.*
import org.gradle.testfixtures.ProjectBuilder

class SonarPDFSpec extends Specification{
    static final TASK_NAME = 'sonarPDF'
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Adds Sonar PDF task"() {

        expect:
        project.tasks.findByName(TASK_NAME) == null

        when:
        project.task(TASK_NAME, type: SonarPDF) {
            sonarHostUrl = "http://localhost:9000/"
            username = 'admin'
            password = 'admin'
            reportType = 'executive'
        }

        then:
        Task task = project.tasks.findByName(TASK_NAME)
        task != null
        task.description == 'Generate a project quality report in PDF format'
        task.group == 'SonarPDF'
        task.sonarHostUrl == 'http://localhost:9000/'
        task.username == 'admin'
        task.password == 'admin'
        task.reportType == 'executive'
    }

}
