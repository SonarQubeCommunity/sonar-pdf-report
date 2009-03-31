package org.sonar.report.pdf.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Node;

/**
 *This class encapsulates the measures info.
 */
public class Measures {

  private final static String MEASURES = "//resources/resource/msr";
  private final static String DATE = "//resources/resource/date";
  private final static String VERSION = "//resources/resource/version";
  

  private Hashtable<String, Measure> measuresTable = new Hashtable<String, Measure>();
  private Date date;
  private String version = "N/A";

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
