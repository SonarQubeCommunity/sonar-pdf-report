package org.sonar.report.pdf.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SonarPDFTaskSpec extends Specification{
    static final TASK_NAME = 'generateSonarPDFReport'
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Adds Sonar PDF task"() {
        expect:
            project.tasks.findByName(TASK_NAME) == null

        when:
            project.task(TASK_NAME, type: SonarPDFTask) {
                sonarHostUrl = 'http://localhost:9000/'
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
