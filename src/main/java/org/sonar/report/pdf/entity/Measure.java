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
package org.sonar.report.pdf.entity;

public class Measure {

  private String key;
  private String value;
  private String formatValue;
  private String textValue;
  private String dataValue;
  private Integer qualitativeTendency;
  private Integer quantitativeTendency;
  private String alert;

  public Measure(final String measureKey, final String measureFValue) {
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

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public String getFormatValue() {
    return formatValue;
  }

  public void setFormatValue(final String formatValue) {
    this.formatValue = formatValue;
  }

  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(final String textValue) {
    this.textValue = textValue;
  }

  public String getDataValue() {
    return dataValue;
  }

  public void setDataValue(final String dataValue) {
    this.dataValue = dataValue;
  }

  public Integer getQualitativeTendency() {
    return qualitativeTendency;
  }

  public void setQualitativeTendency(final Integer qualitativeTendency) {
    this.qualitativeTendency = qualitativeTendency;
  }

  public Integer getQuantitativeTendency() {
    return quantitativeTendency;
  }

  public void setQuantitativeTendency(final Integer quantitativeTendency) {
    this.quantitativeTendency = quantitativeTendency;
  }

  public String getAlert() {
    return alert;
  }

  public void setAlert(final String alert) {
    this.alert = alert;
  }

}