package org.sonar.report.pdf.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

import org.dom4j.DocumentException;
import org.sonar.report.pdf.ExecutivePDFReporter;
import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.TeamWorkbookPDFReporter;
import org.sonar.report.pdf.util.MetricKeys;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MetricsTest {
  
  @Test(alwaysRun = true, enabled = true, groups = { "metrics" })
  public void metricsShouldBeConsistent() throws IOException, DocumentException, IllegalArgumentException, IllegalAccessException {
    URL resource = this.getClass().getClassLoader().getResource("report.properties");
    Properties config = new Properties();
    config.load(resource.openStream());
    
    PDFReporter reporter = new ExecutivePDFReporter(new URL(config.getProperty("sonar.base.url") + "/images/sonar.png"),
        "es.juntadeandalucia.copt.transportes:autoriza", config.getProperty("sonar.base.url"));
    String allMetricsKeys = reporter.getAllMetricKeys();
    
    System.out.println("Checking metrics consistency...");
    Field[] fields = MetricKeys.class.getFields();
    for(int i = 0; i < fields.length; i++) {
      String metricKey = (String)fields[i].get(MetricKeys.class);
      Assert.assertTrue(allMetricsKeys.contains((String)fields[i].get(MetricKeys.class)), 
          "Metric " + metricKey + " is not provided");
      System.out.println(metricKey + "... OK");
    }
    System.out.println("\nAll metrics are consistent.");
  }

}
