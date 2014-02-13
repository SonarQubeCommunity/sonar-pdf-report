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
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import org.sonar.report.pdf.ExecutivePDFReporter
import org.sonar.report.pdf.PDFReporter
import org.sonar.report.pdf.TeamWorkbookPDFReporter
import org.sonar.report.pdf.entity.exception.ReportException
import org.sonar.report.pdf.util.Credentials
import org.sonar.report.pdf.util.Logger

import com.lowagie.text.DocumentException

class SonarPDFPlugin extends DefaultTask {

    File outputDirectory

    String sonarHostUrl

    String branch

    String sonarBranch

    String reportType

    String username

    String password


    SonarPDFPlugin() {
      description = "Generate a project quality report in PDF format"
      group = "SonarPDF"
    }

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
          Logger.warn("Use of branch parameter is deprecated, use sonar.branch instead");
          Logger.info("Branch " + branch + " selected");
      } else if (sonarBranch != null) {
        sonarProjectId += ":" + sonarBranch;
          Logger.info("Branch " + sonarBranch + " selected");
      }

      PDFReporter reporter = null;
      if (reportType != null) {
        if (reportType.equals("executive")) {
            Logger.info("Executive report type selected");
          reporter = new ExecutivePDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang);
        } else if (reportType.equals("workbook")) {
            Logger.info("Team workbook report type selected");
          reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"), sonarProjectId, config.getProperty("sonar.base.url"), config, configLang);
        }
      } else {
          Logger.info("No report type provided. Default report selected (Team workbook)");
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
      Logger.info("PDF report generated (see " + project.name + ".pdf on build output directory)");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (DocumentException e) {
        Logger.error("Problem generating PDF file.");
      e.printStackTrace();
    } catch (org.dom4j.DocumentException e) {
        Logger.error("Problem parsing response data.");
      e.printStackTrace();
    } catch (ReportException e) {
        Logger.error("Internal error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
