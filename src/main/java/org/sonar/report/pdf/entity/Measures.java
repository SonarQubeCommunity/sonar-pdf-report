/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.util.SonarAccess;
import org.sonar.report.pdf.util.UrlPath;

/**
 *This class encapsulates the measures info.
 */
public class Measures {

  private final static String MEASURES = "//resources/resource/msr";
  private final static String DATE = "//resources/resource/date";
  private final static String VERSION = "//resources/resource/version";
  private static String measuresKeys = null;
  

  private Hashtable<String, Measure> measuresTable = new Hashtable<String, Measure>();
  private Date date;
  private String version = "N/A";

  public void initMeasuresByProjectKey(SonarAccess sonarAccess, String projectKey) throws HttpException, IOException, DocumentException {
    if (measuresKeys == null) {
      measuresKeys = getAllMetricKeys(sonarAccess);
    }
    String urlAllMesaures = UrlPath.RESOURCES + projectKey + "&depth=0&format=xml&includetrends=true"
          + "&metrics=" + measuresKeys;
    this.addAllMeasuresFromDocument(sonarAccess.getUrlAsDocument(urlAllMesaures));
  }
  
  public String getAllMetricKeys(SonarAccess sonarAccess) throws HttpException, IOException, org.dom4j.DocumentException {
    String urlAllMetrics = "/api/metrics?format=xml";
    org.dom4j.Document allMetricsDocument = sonarAccess.getUrlAsDocument(urlAllMetrics);
    List<Node> allMetricKeysNodes = allMetricsDocument.selectNodes("//metrics/metric/key");
    String allMetricKeys= ""; 
    Iterator<Node> it = allMetricKeysNodes.iterator();
    allMetricKeys += it.next().getText();
    while(it.hasNext()) {
      allMetricKeys += "," + it.next().getText();
    }
    return allMetricKeys;
  }
  
  
  public Date getDate() {
    return date;
  }

  public void setDate(String date) throws ParseException {
    if (date.equals("now")) {
      this.date = new Date();
    } else {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      this.date = df.parse(date);
    }
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getMeasuresCount() {
    return measuresTable.size();
  }

  public Set<String> getMeasuresKeys() {
    return measuresTable.keySet();
  }

  public Measure getMeasure(String key) {
    return measuresTable.get(key);
  }

  public void addMeasure(String name, Measure value) {
    measuresTable.put(name, value);
  }

  public boolean containsMeasure(String measureKey) {
    return measuresTable.containsKey(measureKey);
  }

  public void addMesaureFromNode(Node measureNode) {
    Measure measure = new Measure();
    measure.initFromNode(measureNode);
    measuresTable.put(measure.getKey(), measure);
  }

  public void addAllMeasuresFromDocument(Document allMeasuresNode) {
    List<Node> allNodes = allMeasuresNode.selectNodes(MEASURES);
    Iterator<Node> it = allNodes.iterator();
    while (it.hasNext()) {
      addMesaureFromNode(it.next());
    }
    try {
      setDate(allMeasuresNode.selectSingleNode(DATE).getText());
      setVersion(allMeasuresNode.selectSingleNode(VERSION).getText());
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}
