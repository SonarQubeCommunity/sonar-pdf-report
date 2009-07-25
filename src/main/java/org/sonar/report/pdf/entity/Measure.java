/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 GMV-SGI
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

import org.dom4j.Node;

public class Measure {

  private String key;
  private String value;
  private String formatValue;
  private String textValue;
  private Integer qualitativeTendency;
  private Integer quantitativeTendency;
  private String alert;

  private static final String KEY = "key";
  private static final String VALUE = "val";
  private static final String FORMAT_VALUE = "frmt_val";
  private static final String TREND = "trend";
  private static final String VAR = "var";

  public Measure(String measureKey, String measureFValue) {
    this.key = measureKey;
    this.formatValue = measureFValue;
    this.qualitativeTendency = 0;
    this.quantitativeTendency = 0;
  }

  public Measure() {

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

  /**
   * Init measurr from XML node. The root node must be "msr".
   * 
   * @param measureNode
   */
  public void initFromNode(Node measureNode) {
    this.setKey(measureNode.selectSingleNode(KEY).getText());
    
    if (measureNode.selectSingleNode(FORMAT_VALUE) != null) {
      this.setFormatValue(measureNode.selectSingleNode(FORMAT_VALUE).getText());
      this.setValue(measureNode.selectSingleNode(VALUE).getText());
    }
    if (measureNode.selectSingleNode(TREND) != null) {
      this.setQualitativeTendency(Integer.parseInt(measureNode.selectSingleNode(TREND).getText()));
    } else {
      this.setQualitativeTendency(0);
    }

    if (measureNode.selectSingleNode(VAR) != null) {
      this.setQuantitativeTendency(Integer.parseInt(measureNode.selectSingleNode(VAR).getText()));
    } else {
      this.setQuantitativeTendency(0);
    }

    if (measureNode.selectSingleNode(VALUE) != null) {
      this.setTextValue(measureNode.selectSingleNode(VALUE).getText());
    } else {
      this.setTextValue("");
    }
  }
}
