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
			org.sonar.wsclient.services.Measure measureNode) {
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
