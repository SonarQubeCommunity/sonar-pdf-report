/*
 * Sonar PDF Report (Maven plugin)
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

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.util.Logger;
import org.sonar.report.pdf.util.SonarAccess;
import org.sonar.report.pdf.util.UrlPath;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class encapsulates the measures info.
 */
public class Measures {

    private final static String MEASURES = "//resources/resource/msr";
    private final static String DATE = "//resources/resource/date";
    private final static String VERSION = "//resources/resource/version";
    private static List<String> measuresKeys = null;

    private Hashtable<String, Measure> measuresTable = new Hashtable<String, Measure>();
    private Date date;
    private String version = "N/A";

    private static Integer DEFAULT_SPLIT_LIMIT = 20;

    public void initMeasuresByProjectKey(SonarAccess sonarAccess, String projectKey) throws HttpException, IOException,
            DocumentException {
        if (measuresKeys == null) {
            measuresKeys = getAllMetricKeys(sonarAccess);
        }

        // Avoid "Post too large"
        if (measuresKeys.size() > DEFAULT_SPLIT_LIMIT) {
            initMeasuresSplittingRequests(sonarAccess, projectKey);
        } else {
            String keys = measuresKeys.toString();
            keys = keys.substring(1, keys.length() - 1);
            this.addMeasures(keys, sonarAccess, projectKey);
        }

    }

    public List<String> getAllMetricKeys(SonarAccess sonarAccess) throws HttpException, IOException,
            org.dom4j.DocumentException {

        String urlAllMetrics = "/api/metrics?format=xml";
        Logger.debug("Accessing Sonar: getting all metric keys");
        org.dom4j.Document allMetricsDocument = sonarAccess.getUrlAsDocument(urlAllMetrics);
        List<Node> allMetricKeysNodes = allMetricsDocument.selectNodes("//metrics/metric/key");
        List<String> allMetricKeys = new ArrayList<String>();
        Iterator<Node> it = allMetricKeysNodes.iterator();
        while (it.hasNext()) {
            allMetricKeys.add(it.next().getText());
        }
        return allMetricKeys;
    }

    /**
     * This method does the required requests to get all measures from Sonar, but
     * taking care to avoid too large requests (measures are taken by 20).
     */
    private void initMeasuresSplittingRequests(final SonarAccess sonarAccess, final String projectKey)
            throws HttpException, IOException, DocumentException {
        Iterator<String> it = measuresKeys.iterator();
        Logger.debug("Getting " + measuresKeys.size() + " metric measures from Sonar by splitting requests");
        String twentyMeasures = "";
        int i = 0;
        boolean isTheFirst = true;
        while (it.hasNext()) {
            if (isTheFirst) {
                twentyMeasures += it.next();
                isTheFirst = false;
            } else {
                twentyMeasures += "," + it.next();
            }
            i++;
            if (i % DEFAULT_SPLIT_LIMIT == 0) {
                Logger.debug("Split request for: " + twentyMeasures);
                this.addMeasures(twentyMeasures, sonarAccess, projectKey);
                i = 0;
                isTheFirst = true;
                twentyMeasures = "";
            }
        }
        if (i != 0) {
            Logger.debug("Split request for remain metric measures: " + twentyMeasures);
            this.addMeasures(twentyMeasures, sonarAccess, projectKey);
        }
    }

    /**
     * Add measures to this.
     */
    private void addMeasures(final String measuresAsString, final SonarAccess sonarAccess, final String projectKey)
            throws HttpException, IOException, DocumentException {
        String urlAllMesaures = UrlPath.RESOURCES + projectKey + "&depth=0&format=xml&includetrends=true" + "&metrics="
                + measuresAsString;
        this.addAllMeasuresFromDocument(sonarAccess.getUrlAsDocument(urlAllMesaures));
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
