package org.sonar.report.pdf.entity;

import org.dom4j.Node;

public class Measure {

  private String key;
  private String value;
  private String formatValue;
  private String textValue;
  private Integer qualitativeTendency;
  private Integer quantitativeTendency;
  private String alert;

  public Measure(String measureKey, String measureFValue) {
    this.key = measureKey;
    this.formatValue = measureFValue;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getFormatValue() {
    return formatValue;
  }

  public void setFormatValue(String formatValue) {
    this.formatValue = formatValue;
  }

  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }

  public Integer getQualitativeTendency() {
    return qualitativeTendency;
  }

  public void setQualitativeTendency(Integer qualitativeTendency) {
    this.qualitativeTendency = qualitativeTendency;
  }

  public Integer getQuantitativeTendency() {
    return quantitativeTendency;
  }

  public void setQuantitativeTendency(Integer quantitativeTendency) {
    this.quantitativeTendency = quantitativeTendency;
  }

  public String getAlert() {
    return alert;
  }

  public void setAlert(String alert) {
    this.alert = alert;
  }

  public static Measure createMeasureFromNode(Node measureNode) {
    Measure measure = new Measure(measureNode.selectSingleNode("metric").getText(), measureNode.selectSingleNode(
        "f_value").getText());
    measure.setValue(measureNode.selectSingleNode("value").getText());
    if (measureNode.selectSingleNode("alert") != null) {
      measure.setAlert(measureNode.selectSingleNode("alert").getText());
    } else {
      measure.setAlert(null);
    }

    if (measureNode.selectSingleNode("t_qual") != null) {
      measure.setQualitativeTendency(Integer.parseInt(measureNode.selectSingleNode("t_qual").getText()));
    } else {
      measure.setQualitativeTendency(null);
    }

    if (measureNode.selectSingleNode("t_quant") != null) {
      measure.setQuantitativeTendency(Integer.parseInt(measureNode.selectSingleNode("t_quant").getText()));
    } else {
      measure.setQuantitativeTendency(null);
    }
    
    if(measureNode.selectSingleNode("text_value") != null) {
      measure.setTextValue(measureNode.selectSingleNode("text_value").getText());
    } else {
      measure.setTextValue(null);
    }
    return measure;
  }
}
