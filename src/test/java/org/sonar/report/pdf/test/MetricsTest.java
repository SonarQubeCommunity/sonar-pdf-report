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

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.sonar.report.pdf.builder.MeasuresBuilder;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.MetricKeys;
import org.sonar.wsclient.Sonar;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MetricsTest {

  @Test(alwaysRun = true, enabled = true, groups = { "metrics" })
  public void metricsShouldBeConsistent() throws IOException, IllegalArgumentException, IllegalAccessException,
      ReportException {
    Properties config = new Properties();
    config.setProperty("front.page.logo", "sonar.png");
    String sonarUrl = "http://nemo.sonarsource.org";
    config.setProperty("sonar.base.url", sonarUrl);

    URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
    Properties configText = new Properties();
    configText.load(resourceText.openStream());

    Sonar sonar = Sonar.create(sonarUrl, null, null);
    MeasuresBuilder measuresBuilder = MeasuresBuilder.getInstance(sonar);
    List<String> allMetricsKeys = measuresBuilder.getAllMetricKeys();

    System.out.println("Checking metrics consistency...");
    Field[] fields = MetricKeys.class.getFields();
    for (int i = 0; i < fields.length; i++) {
      String metricKey = (String) fields[i].get(MetricKeys.class);
      Assert.assertTrue(allMetricsKeys.contains(fields[i].get(MetricKeys.class)), "Metric " + metricKey + " is not provided");
      System.out.println(metricKey + "... OK");
    }
    System.out.println("\nAll metrics are consistent.");
  }

}
