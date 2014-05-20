package org.sonar.report.pdf.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SonarPDFTaskIntegrationSpec extends Specification{
    static final TASK_NAME = 'generateSonarPDFReport'
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    /*
     * This is a larger and longer running task s best suited in the integration test package.
     */
    def "Ensure no exception is thrown when running task"() {
        expect:
            project.tasks.findByName(TASK_NAME) == null

        when:
            project.task(TASK_NAME, type: SonarPDFTask) {
                sonarHostUrl = 'http://nemo.sonarqube.org/'
                sonarProjectId = 'net.sourceforge.pmd:pmd'
                reportType = 'executive'
            }

        then:
            Task task = project.tasks.findByName(TASK_NAME)
            task.run()
    }
}
