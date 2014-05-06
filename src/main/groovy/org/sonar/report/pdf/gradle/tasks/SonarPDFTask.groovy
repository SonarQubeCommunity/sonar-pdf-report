/*
 * Sonar PDF Report (Gradle plugin)
 * Copyright (C) 2014 willis7
 * sion5@hotmail.co.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.report.pdf.gradle.tasks

import com.lowagie.text.DocumentException
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.sonar.report.pdf.ExecutivePDFReporter
import org.sonar.report.pdf.PDFReporter
import org.sonar.report.pdf.TeamWorkbookPDFReporter
import org.sonar.report.pdf.entity.exception.ReportException
import org.sonar.report.pdf.util.Credentials

/*
 * The SonarPDF report generator task
 *
 * @author Sion Williams
 */

class SonarPDFTask extends DefaultTask {

    /**
     * Sonar Base URL.
     */
    @Input
    @Optional
    String sonarHostUrl

    /**
     * Branch to be used.
     */
    @Input
    @Optional
    String branch

    /**
     * Branch to be used.
     */
    @Input
    @Optional
    String sonarBranch

    /**
     * Type of report.
     */
    @Input
    @Optional
    String reportType

    /**
     * Username to access WS API.
     */
    @Input
    @Optional
    String username

    /**
     * Password to access WS API.
     */
    @Input
    @Optional
    String password


    @OutputFile
    File reportFile

    SonarPDFTask() {
        description = "Generate a project quality report in PDF format"
        group = "SonarPDF"
    }

    File getOutputDirectory() {
        project.file(outputDirectory)
    }

    @TaskAction
    def run() {

        String sonarProjectId = "${project.group}:${project.name}"
        Properties config = new Properties()
        Properties configLang = new Properties()

        try {
            if (getSonarHostUrl()) {
                if (getSonarHostUrl().endsWith("/")) {
                    sonarHostUrl = getSonarHostUrl().substring(0, getSonarHostUrl().length() - 1)
                }
                config.put("sonar.base.url", sonarHostUrl);
                config.put("front.page.logo", "sonar.png");
            } else {
                config.load(this.getClass().getResourceAsStream("/report.properties"))
            }
            configLang.load(this.getClass().getResourceAsStream("/report-texts-en.properties"))

            if (getBranch()) {
                sonarProjectId += ":" + getBranch()
                logger.warn("Use of branch parameter is deprecated, use sonar.branch instead")
                logger.info("Branch " + getBranch() + " selected")
            } else if (sonarBranch != null) {
                sonarProjectId += ":" + getBranch()
                logger.info("Branch " + getBranch() + " selected")
            }

            PDFReporter reporter = null
            if (getReportType()) {
                if (getReportType() == "executive") {
                    logger.info("Executive report type selected")
                    reporter = new ExecutivePDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang)
                } else if (reportType == "workbook") {
                    logger.info("Team workbook report type selected")
                    reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang)
                }
            } else {
                logger.info("No report type provided. Default report selected (Team workbook)");
                reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang)
            }

            Credentials.setUsername(username)
            Credentials.setPassword(password)

            ByteArrayOutputStream baos = reporter.getReport()
            FileOutputStream fos = null

            def outputDir = project.file("$project.buildDir/sonar")
            outputDir.parentFile.mkdirs()

            reportFile = new File(outputDir, "${project.name}.pdf")
            fos = new FileOutputStream(reportFile)
            baos.writeTo(fos)
            fos.flush()
            fos.close()
            logger.info("PDF report generated (see ${project.name}.pdf in build output directory)")

        } catch (IOException e) {
            throw new GradleException(e.message)
        } catch (DocumentException e) {
            throw new GradleException("Problem generating PDF file.")
        } catch (org.dom4j.DocumentException e) {
            throw new GradleException("Problem parsing response data.")
        } catch (ReportException e) {
            throw new GradleException(e.message)
        }
    }
}
