package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *This class encapsulates the measures info.
 */
public class Measures {

  private Hashtable<String, Measure> measuresTable = new Hashtable<String, Measure>();
  private Date date;
  private String version;

  private static final String MEASURES = "//projects/project/measures/msr";
  private static final String DATE = "//projects/project/measures/date";
  private static final String VERSION = "//projects/project/measures/version";

  public Date getDate() {
    return date;
  }

  public void setDate(String date) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    this.date = df.parse(date);
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

  public static Measures parse(String url) throws HttpException, IOException, DocumentException {
    HttpClient client = new HttpClient();
    HttpMethod method = new GetMethod(url);
    int status = 0;
    status = client.executeMethod(method);
    if (!(status == HttpStatus.SC_OK)) {
      return null;
    }

    SAXReader reader = new SAXReader();
    Document document = reader.read(method.getResponseBodyAsStream());
    List<Node> measuresNodes = document.selectNodes(MEASURES);
    Measures measures = new Measures();
    try {
      measures.setDate(document.selectSingleNode(DATE).getText());
    } catch (ParseException e) {
      System.out.println("La fecha no tiene un formato correcto");
      e.printStackTrace();
    }
    measures.setVersion(document.selectSingleNode(VERSION).getText());
    Iterator<Node> it = measuresNodes.iterator();
    while (it.hasNext()) {
      Node n = it.next();
      measures.addMeasure(n.selectSingleNode("metric").getText(), Measure.createMeasureFromNode(n));
    }
    return measures;
  }
}
