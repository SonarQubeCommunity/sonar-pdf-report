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
package org.sonar.report.pdf.builder;

import org.sonar.report.pdf.entity.Measure;

public class MeasureBuilder {

  /**
   * Init measure from XML node. The root node must be "msr".
   * 
   * @param measureNode
   * @return
   */
  public static Measure initFromNode(
      final org.sonar.wsclient.services.Measure measureNode) {
    Measure measure = new Measure();
    measure.setKey(measureNode.getMetricKey());

    String formatValueNode = measureNode.getFormattedValue();
    if (formatValueNode != null) {
      measure.setFormatValue(formatValueNode);
      measure.setValue(String.valueOf(measureNode.getValue()));
    }

    Integer trendNode = measureNode.getTrend();
    if (trendNode != null) {
      measure.setQualitativeTendency(trendNode);
    } else {
      measure.setQualitativeTendency(0);
    }

    Integer varNode = measureNode.getVar();
    if (varNode != null) {
      measure.setQuantitativeTendency(varNode);
    } else {
      measure.setQuantitativeTendency(0);
    }

    Double valueNode = measureNode.getValue();
    String dataNode = measureNode.getData();

    if (valueNode != null) {
      measure.setTextValue(String.valueOf(valueNode));
    } else if (dataNode != null) {
      measure.setTextValue(dataNode);
    } else {
      measure.setTextValue("");
    }

    if (dataNode != null) {
      measure.setDataValue(dataNode);
    } else {
      measure.setDataValue("");
    }

    return measure;
  }
}
