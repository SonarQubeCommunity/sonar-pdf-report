/*
 * Sonar PDF Report (Maven plugin)
 * Copyright (C) 2010 klicap - ingeniería del puzle
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
import java.util.Properties;

import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.TeamWorkbookPDFReporter;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.testng.annotations.Test;

import com.lowagie.text.DocumentException;
import java.net.URL;

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
    public void getReportTest() throws DocumentException, IOException, org.dom4j.DocumentException, ReportException {
        URL resource = this.getClass().getClassLoader().getResource("report.properties");
        Properties config = new Properties();
        config.load(resource.openStream());
        config.setProperty("sonar.base.url", "http://nemo.sonarsource.org");

        URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
        Properties configText = new Properties();
        configText.load(resourceText.openStream());

        PDFReporter reporter = new TeamWorkbookPDFReporter(this.getClass().getResource("/sonar.png"),
                "net.objectlab.kit:kit-parent", "http://nemo.sonarsource.org", config, configText);

        ByteArrayOutputStream baos = reporter.getReport();
        FileOutputStream fos = null;

        fos = new FileOutputStream("target/testReport.pdf");

        baos.writeTo(fos);
        fos.flush();
        fos.close();

    }
}
