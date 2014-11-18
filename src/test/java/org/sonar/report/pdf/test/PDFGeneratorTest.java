/*
 * SonarQube PDF Report
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

package org.sonar.report.pdf.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.TeamWorkbookPDFReporter;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Credentials;
import org.testng.annotations.Test;

import com.lowagie.text.DocumentException;

public class PDFGeneratorTest {

  /**
   * Build a PDF report for the Sonar project on "sonar.base.url" instance of
   * Sonar. The property "sonar.base.url" is set in report.properties, this file
   * will be provided by the artifact consumer.
   * 
   * The key of the project is not place in properties, this is provided in
   * execution time.
   * 
   * @throws ReportException
   */
  @Test(enabled = true, groups = { "report" }, dependsOnGroups = { "metrics" })
  public void getReportTest() throws DocumentException, IOException, ReportException {
    Properties config = new Properties();
    config.setProperty("front.page.logo", "sonar.png");
    String sonarUrl = "http://nemo.sonarsource.org";
    config.setProperty("sonar.base.url", sonarUrl);

    URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
    Properties configText = new Properties();
    configText.load(resourceText.openStream());

    Credentials credentials = new Credentials(sonarUrl, null, null);

    PDFReporter reporter = new TeamWorkbookPDFReporter(credentials, this.getClass().getResource("/sonar.png"),
        "net.java.openjdk:jdk7", config, configText);

    ByteArrayOutputStream baos = reporter.getReport();
    FileOutputStream fos = null;

    fos = new FileOutputStream("target/testReport.pdf");

    baos.writeTo(fos);
    fos.flush();
    fos.close();

  }
}