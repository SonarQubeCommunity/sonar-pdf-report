package org.sonar.report.pdf.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

import org.dom4j.DocumentException;
import org.sonar.report.pdf.entity.Measures;
import org.sonar.report.pdf.util.MetricKeys;
import org.sonar.report.pdf.util.SonarAccess;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MetricsTest {
  
  @Test(alwaysRun = true, enabled = false, groups = { "metrics" })
  public void metricsShouldBeConsistent() throws IOException, DocumentException, IllegalArgumentException, IllegalAccessException {
    URL resource = this.getClass().getClassLoader().getResource("report.properties");
    Properties config = new Properties();
    config.load(resource.openStream());
    
    URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
    Properties configText = new Properties();
    configText.load(resourceText.openStream());

    SonarAccess sonarAccess = new SonarAccess("http://localhost:9000");
    Measures measures = new Measures();
    String allMetricsKeys = measures.getAllMetricKeys(sonarAccess);
    
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
