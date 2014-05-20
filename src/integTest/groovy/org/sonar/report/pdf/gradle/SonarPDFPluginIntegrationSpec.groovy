package org.sonar.report.pdf.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Sion on 21/04/2014.
 */
class SonarPDFPluginIntegrationSpec extends Specification {
    Project project
    static final String EXTENSION_NAME = 'sonarPDF'
    static final String PDF_TASK_NAME = 'generateSonarPDFReport'

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Ensure no exception is thrown when running task"() {
        expect:
            project.tasks.findByName(PDF_TASK_NAME) == null

        when:
        project.metaClass.getName {"pmd"}
        project.group = 'net.sourceforge.pmd'
            project.apply plugin: 'sonarpdf'
            project.sonarPDF {
                sonarHostUrl = 'http://nemo.sonarqube.org/'
                //sonarProjectId = 'net.sourceforge.pmd:pmd'
                reportType = 'executive'
            }

        then:
            Task task = project.tasks.findByName(PDF_TASK_NAME)
            task.run()
    }
}
