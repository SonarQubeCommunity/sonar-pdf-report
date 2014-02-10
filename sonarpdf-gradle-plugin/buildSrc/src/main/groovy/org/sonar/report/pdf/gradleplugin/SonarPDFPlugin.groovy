/*
 * Sonar PDF Report (Gradle plugin)
 * Copyright (C) 2010 klicap - ingenieria del puzle
 * dev@sonar.codehaus.org
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


import org.gradle.api.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging


import org.sonar.report.pdf.ExecutivePDFReporter
import org.sonar.report.pdf.PDFReporter
import org.sonar.report.pdf.TeamWorkbookPDFReporter
import org.sonar.report.pdf.entity.exception.ReportException
import org.sonar.report.pdf.util.Credentials

import com.lowagie.text.DocumentException
//import org.sonar.report.pdf.util.Logger

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties

/**
 * Generate a PDF report. WARNING, Sonar server must be started.
 * 
 * @goal generate
 * @aggregator
 */
class SonarPDFPlugin extends DefaultTask {
  /**
   * Project build directory
   * 
   * @parameter expression="${project.build.directory}"
   * @required
   */
    File outputDirectory


  /**
   * Sonar Base URL.
   * 
   * @parameter expression="${sonar.host.url}"
   * @optional
   */
    String sonarHostUrl

  /**
   * Branch to be used.
   * 
   * @parameter expression="${branch}"
   * @optional
   */
    String branch

  /**
   * Branch to be used.
   * 
   * @parameter expression="${sonar.branch}"
   * @optional
   */
    String sonarBranch

  /**
   * Type of report.
   * 
   * @parameter expression="${report.type}"
   * @optional
   */
    String reportType

  /**
   * Username to access WS API.
   * 
   * @parameter expression="${sonar.pdf.username}"
   * @optional
   */
    String username

  /**
   * Password to access WS API.
   * 
   * @parameter expression="${sonar.pdf.password}"
   * @optional
   */
    String password

    Logger sl4jLogger = Logging.getLogger('logger')

  @TaskAction
  def run() {

    

    Properties config = new Properties()
    Properties configLang = new Properties()

    try {
      if (sonarHostUrl != null) {
        if (sonarHostUrl.endsWith("/")) {
          sonarHostUrl = sonarHostUrl.substring(0, sonarHostUrl.length() - 1)
        }
        config.put("sonar.base.url", sonarHostUrl);
        config.put("front.page.logo", "sonar.png");
      } else {
        config.load(this.getClass().getResourceAsStream("/report.properties"))
      }
      configLang.load(this.getClass().getResourceAsStream("/report-texts-en.properties"))

      String sonarProjectId = project.group + ":" + project.name

      if (branch != null) {
        sonarProjectId += ":" + branch;
          sl4jLogger.warn("Use of branch parameter is deprecated, use sonar.branch instead");
          sl4jLogger.info("Branch " + branch + " selected");
      } else if (sonarBranch != null) {
        sonarProjectId += ":" + sonarBranch;
          sl4jLogger.info("Branch " + sonarBranch + " selected");
      }

      PDFReporter reporter = null;
      if (reportType != null) {
        if (reportType.equals("executive")) {
            sl4jLogger.info("Executive report type selected");
          reporter = new ExecutivePDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang);
        } else if (reportType.equals("workbook")) {
            sl4jLogger.info("Team workbook report type selected");
          reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang);
        }
      } else {
          sl4jLogger.info("No report type provided. Default report selected (Team workbook)");
        reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang);
      }

      Credentials.setUsername(username);
      Credentials.setPassword(password);

      ByteArrayOutputStream baos = reporter.getReport();
      FileOutputStream fos = null;
      if (!outputDirectory.exists()) {
        outputDirectory.mkdirs();
      }
      File reportFile = new File(outputDirectory, project.name + ".pdf");
      fos = new FileOutputStream(reportFile);
      baos.writeTo(fos);
      fos.flush();
      fos.close();
      sl4jLogger.info("PDF report generated (see " + project.name + ".pdf on build output directory)");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (DocumentException e) {
        sl4jLogger.error("Problem generating PDF file.");
      e.printStackTrace();
    } catch (org.dom4j.DocumentException e) {
        sl4jLogger.error("Problem parsing response data.");
      e.printStackTrace();
    } catch (ReportException e) {
        sl4jLogger.error("Internal error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
