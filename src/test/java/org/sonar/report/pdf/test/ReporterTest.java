package org.sonar.report.pdf.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.TeamWorkbookPDFReporter;
import org.testng.annotations.Test;

import com.lowagie.text.DocumentException;
import java.net.URL;

public class ReporterTest {

  /**
   * Build a PDF report for the Sonar project on "sonar.base.url" instance of Sonar. The property "sonar.base.url" is
   * set in report.properties, this file will be provided by the artifact consumer.
   * 
   * The key of the project is not place in properties, this is provided in execution time.
   */
  @Test(enabled = true, groups = { "report" }, dependsOnGroups = { "metrics" })
  public void getReportTest() throws DocumentException, IOException, org.dom4j.DocumentException {
    URL resource = this.getClass().getClassLoader().getResource("report.properties");
    Properties config = new Properties();
    config.load(resource.openStream());

    PDFReporter reporter = new TeamWorkbookPDFReporter(new URL(config.getProperty("sonar.base.url") + "/images/sonar.png"),
        "org.codehaus.sonar:sonar", config.getProperty("sonar.base.url"));

    ByteArrayOutputStream baos = reporter.getReport();
    FileOutputStream fos = null;

    fos = new FileOutputStream("target/testReport.pdf");

    baos.writeTo(fos);
    fos.flush();
    fos.close();

  }
}
