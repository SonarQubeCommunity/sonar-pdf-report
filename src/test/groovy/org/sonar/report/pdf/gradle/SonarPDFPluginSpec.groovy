package org.sonar.report.pdf.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Sion on 21/04/2014.
 */
class SonarPDFPluginSpec extends Specification {
    Project project
    static final String EXTENSION_NAME = 'sonarPDF'
    static final String PDF_TASK_NAME = 'generateSonarPDFReport'

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Applies plugin and checks created tasks"() {
        expect:
            project.tasks.findByName(PDF_TASK_NAME) == null

        when:
            project.apply plugin: 'sonarpdf'

        then:
            project.extensions.findByName(SonarPDFPlugin.EXTENSION_NAME) != null
            project.tasks.findByName(PDF_TASK_NAME) != null
    }

    def "Applies plugin for sample task without custom extension configuration"() {
        expect:
            project.tasks.findByName(PDF_TASK_NAME) == null

        when:
            project.apply plugin: 'sonarpdf'

        then:
            project.extensions.findByName(SonarPDFPlugin.EXTENSION_NAME) != null
            Task task = project.tasks.findByName(PDF_TASK_NAME)
            task != null
            task.sonarHostUrl == 'http://localhost:9000/'
            task.branch == null
            task.sonarBranch == null
            task.reportType == 'executive'
            task.username == 'admin'
            task.password == 'admin'
    }

    def "Applies plugin and configures sample task through extension"() {
        expect:
            project.tasks.findByName(PDF_TASK_NAME) == null

        when:
            project.apply plugin: 'sonarpdf'
            project.sonarPDF {
                sonarHostUrl = 'http://localhost:9001/'
                reportType = 'executive'
                username = 'admin'
                password = 'secret'
            }

        then:
            project.extensions.findByName(SonarPDFPlugin.EXTENSION_NAME) != null
            Task task = project.tasks.findByName(PDF_TASK_NAME)
            task != null
            task.sonarHostUrl == 'http://localhost:9001/'
            task.branch == null
            task.sonarBranch == null
            task.reportType == 'executive'
            task.username == 'admin'
            task.password == 'secret'
    }
}
