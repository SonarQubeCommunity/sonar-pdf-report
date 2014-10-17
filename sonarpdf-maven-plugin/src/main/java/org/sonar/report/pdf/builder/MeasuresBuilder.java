package org.sonar.report.pdf.builder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.sonar.report.pdf.entity.Measure;
import org.sonar.report.pdf.entity.Measures;
import org.sonar.report.pdf.util.Logger;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Metric;
import org.sonar.wsclient.services.MetricQuery;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

public class MeasuresBuilder {

  private static MeasuresBuilder builder;

  private Sonar sonar;

  private static List<String> measuresKeys = null;

  private static Integer DEFAULT_SPLIT_LIMIT = 20;

  public MeasuresBuilder(Sonar sonar) {
    this.sonar = sonar;
  }

  public static MeasuresBuilder getInstance(Sonar sonar) {
    if (builder == null) {
      return new MeasuresBuilder(sonar);
    }

    return builder;
  }

  public List<String> getAllMetricKeys() throws HttpException, IOException {

    MetricQuery query = MetricQuery.all();
    List<Metric> allMetricKeysNodes = sonar.findAll(query);
    List<String> allMetricKeys = new ArrayList<String>();
    Iterator<Metric> it = allMetricKeysNodes.iterator();
    while (it.hasNext()) {
      allMetricKeys.add(it.next().getKey());
    }
    return allMetricKeys;
  }

  public Measures initMeasuresByProjectKey(String projectKey)
      throws HttpException, IOException {

    Measures measures = new Measures();

    if (measuresKeys == null) {
      measuresKeys = getAllMetricKeys();
    }

    // Avoid "Post too large"
    if (measuresKeys.size() > DEFAULT_SPLIT_LIMIT) {
      initMeasuresSplittingRequests(measures, projectKey);
    } else {
      this.addMeasures(measures, measuresKeys, projectKey);
    }

    return measures;

  }

  /**
   * This method does the required requests to get all measures from Sonar, but
   * taking care to avoid too large requests (measures are taken by 20).
   */
  private void initMeasuresSplittingRequests(Measures measures,
      final String projectKey) throws HttpException, IOException {
    Iterator<String> it = measuresKeys.iterator();
    Logger.debug("Getting " + measuresKeys.size()
        + " metric measures from Sonar by splitting requests");
    List<String> twentyMeasures = new ArrayList<String>(20);
    int i = 0;
    while (it.hasNext()) {
      twentyMeasures.add(it.next());
      i++;
      if (i % DEFAULT_SPLIT_LIMIT == 0) {
        Logger.debug("Split request for: " + twentyMeasures);
        addMeasures(measures, twentyMeasures, projectKey);
        i = 0;
        twentyMeasures.clear();
      }
    }
    if (i != 0) {
      Logger.debug("Split request for remain metric measures: "
          + twentyMeasures);
      addMeasures(measures, twentyMeasures, projectKey);
    }
  }

  /**
   * Add measures to this.
   */
  private void addMeasures(Measures measures,
      final List<String> measuresAsString, final String projectKey)
      throws HttpException, IOException {

    String[] measuresAsArray = measuresAsString
        .toArray(new String[measuresAsString.size()]);
    ResourceQuery query = ResourceQuery.createForMetrics(projectKey,
        measuresAsArray);
    query.setDepth(0);
    query.setIncludeTrends(true);
    Resource resource = sonar.find(query);
    this.addAllMeasuresFromDocument(measures, resource);
  }

  private void addAllMeasuresFromDocument(Measures measures, Resource resource) {

    List<org.sonar.wsclient.services.Measure> allNodes = resource.getMeasures();
    Iterator<org.sonar.wsclient.services.Measure> it = allNodes.iterator();
    while (it.hasNext()) {
      addMeasureFromNode(measures, it.next());
    }
    try {

      Date dateNode = resource.getDate();
      if (dateNode != null) {
        measures.setDate(dateNode);
      }

      String versionNode = resource.getVersion();
      if (versionNode != null) {
        measures.setVersion(versionNode);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void addMeasureFromNode(Measures measures,
      org.sonar.wsclient.services.Measure measureNode) {
    Measure measure = MeasureBuilder.initFromNode(measureNode);
    measures.addMeasure(measure.getKey(), measure);
  }
}
