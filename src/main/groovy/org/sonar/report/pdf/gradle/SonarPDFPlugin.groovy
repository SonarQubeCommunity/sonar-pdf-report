package org.sonar.report.pdf.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.sonar.report.pdf.gradle.tasks.SonarPDFTask

/**
 * SonarPDF plugin for generating a projects Sonar report. The plugin hides the
 * complexity of having to deal with the communication between client and backend through Sonar API.
 *
 * @author Sion Williams
 */
class SonarPDFPlugin implements Plugin <Project>{
    static final String EXTENSION_NAME = 'sonarPDF'
    static final String PDF_TASK_NAME = 'generateSonarPDFReport'

    @Override
    void apply(Project project){
        project.extensions.create(EXTENSION_NAME, SonarPDFExtension)

        configSonarPDFTask(project)
    }

    /**
     * Configures and adds main task.
     *
     * @param project Project
     */
    void configSonarPDFTask(Project project){
        project.tasks.withType(SonarPDFTask){
            def sonarPDFExtension = project.extensions.findByName(EXTENSION_NAME)
            conventionMapping.sonarHostUrl = { sonarPDFExtension.sonarHostUrl }
            conventionMapping.branch = { sonarPDFExtension.branch }
            conventionMapping.sonarBranch = { sonarPDFExtension.sonarBranch }
            conventionMapping.reportType = { sonarPDFExtension.reportType }
            conventionMapping.username = { sonarPDFExtension.username }
            conventionMapping.password = { sonarPDFExtension.password }
            conventionMapping.sonarProjectId = { sonarPDFExtension.sonarProjectId ?: "${project.group}:${project.name}" }
        }

        project.task(PDF_TASK_NAME, type: SonarPDFTask)
    }
}
