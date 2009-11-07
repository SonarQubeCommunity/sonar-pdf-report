package org.sonar.report.pdf.plugin;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class ReportDataMetric implements Metrics{
  public final static Metric PDF_DATA = new Metric("pdf-data", "PDF binary data", "PDF binary data", Metric.ValueType.DATA, Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_GENERAL);

  public List<Metric> getMetrics() {
    return Arrays.asList(PDF_DATA);
  }
}
