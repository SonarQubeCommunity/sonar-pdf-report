/*
 * Sonar PDF Report (Maven plugin)
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

import org.sonar.report.pdf.DetailedPDFReporter;
import org.sonar.report.pdf.ExecutivePDFReporter;
import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.TeamWorkbookPDFReporter;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.SonarAccess;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lowagie.text.DocumentException;

public class ReporterTest {

    /**
     * Build a PDF report for the Sonar project on "sonar.base.url" instance of Sonar. The property "sonar.base.url" is
     * set in report.properties, this file will be provided by the artifact consumer.
     * 
     * The key of the project is not place in properties, this is provided in execution time.
     * 
     * @throws ReportException
     */
    @Test(enabled = true, groups = { "report" }, dependsOnGroups = { "metrics" })
    public void getReportExecutiveTest() throws DocumentException, IOException, org.dom4j.DocumentException, ReportException {
        URL resource = this.getClass().getClassLoader().getResource("report.properties");
        Properties config = new Properties();
        config.load(resource.openStream());
        config.setProperty("sonar.base.url", "http://localhost:9000");

        URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
        Properties configText = new Properties();
        configText.load(resourceText.openStream());

        PDFReporter reporter = new ExecutivePDFReporter(this.getClass().getResource("/sonar.png"),
                "ar.com.osde:workflowContacto", "http://localhost:9000", config, configText);
        
        ByteArrayOutputStream baos = reporter.getReport();
        FileOutputStream fos = null;

        fos = new FileOutputStream("target/testExecutiveReport.pdf");

        baos.writeTo(fos);
        fos.flush();
        fos.close();

    }
    
    @Test(enabled = true, groups = { "report" }, dependsOnGroups = { "metrics" })
    public void getReportTeamWorkbookPDFReporterTest() throws DocumentException, IOException, org.dom4j.DocumentException, ReportException {
        URL resource = this.getClass().getClassLoader().getResource("report.properties");
        Properties config = new Properties();
        config.load(resource.openStream());
        config.setProperty("sonar.base.url", "http://localhost:9000");

        URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
        Properties configText = new Properties();
        configText.load(resourceText.openStream());

        PDFReporter reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"),
                "ar.com.osde:workflowContacto", "http://localhost:9000", config, configText);
        
        ByteArrayOutputStream baos = reporter.getReport();
        FileOutputStream fos = null;

        fos = new FileOutputStream("target/testTeamWorkbookReport.pdf");

        baos.writeTo(fos);
        fos.flush();
        fos.close();

    }
    
    @Test(enabled = true, groups = { "report" }, dependsOnGroups = { "metrics" })
    public void getReportDetailedPDFReporterTest() throws DocumentException, IOException, org.dom4j.DocumentException, ReportException {
        URL resource = this.getClass().getClassLoader().getResource("report.properties");
        Properties config = new Properties();
        config.load(resource.openStream());
        config.setProperty("sonar.base.url", "http://localhost:9000");

        URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
        Properties configText = new Properties();
        configText.load(resourceText.openStream());

        PDFReporter reporter = new DetailedPDFReporter(this.getClass().getResource("/sonar.png"),
                "ar.com.osde:workflowContacto", "http://localhost:9000", config, configText);
        
        ByteArrayOutputStream baos = reporter.getReport();
        FileOutputStream fos = null;

        fos = new FileOutputStream("target/testDetailedReport.pdf");

        baos.writeTo(fos);
        fos.flush();
        fos.close();

    }

    @Test(enabled = true)
    public void hostAndPortShouldBeParsedCorrectly() throws ReportException {
      SonarAccess sonar = new SonarAccess("http://localhost:80/sonar", null, null);
      Assert.assertTrue(sonar.getHost().equals("localhost") && sonar.getPort() == 80);
      sonar = new SonarAccess("https://localhost:443/sonar", null, null);
      Assert.assertTrue(sonar.getHost().equals("localhost") && sonar.getPort() == 443);
      sonar = new SonarAccess("http://host:9000", null, null);
      Assert.assertTrue(sonar.getHost().equals("host") && sonar.getPort() == 9000);
    }
}
