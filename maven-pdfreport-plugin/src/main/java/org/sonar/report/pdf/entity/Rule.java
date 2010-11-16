/*
 * Sonar PDF Plugin, open source plugin for Sonar
 *
 * Copyright (C) 2009 GMV-SGI
 * Copyright (C) 2010 klicap - ingenieria del puzle
 *
 * Sonar PDF Plugin is free software; you can redistribute it and/or
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Logger;
import org.sonar.report.pdf.util.SonarAccess;
import org.sonar.report.pdf.util.UrlPath;

public class Rule {

  // Rule key
  private String key;

  // Rule name
  private String name;

  // Rule description
  private String description;

  // Violations of this rule: <resource key, violation line> (with limit 100)
  private List<Violation> topViolatedResources;

  // Total vilations of this rule
  private float violationsNumber;

  // Total vilations of this rule
  private String violationsNumberFormatted;

  private static final String RULE_NAME = "rule_name";
  private static final String RULE_KEY = "rule_key";
  private static final String RULE_VIOLATIONS_NUMBER = "val";
  private static final String RULE_VIOLATIONS_NUMBER_FORMATTED = "frmt_val";

  private static final String RULE_VIOLATIONS = "/violations/violation";
  private static final String RESOURCE_LINE = "line";
  private static final String RESOURCE_KEY = "resource/key";
  private static final String SOURCE = "/source/line/val";

  /**
   * Initialize a rule given an XML Node that contains one rule
   * 
   * @return
   */
  public static Rule initFromNode(Node ruleNode) {
    Rule rule = new Rule();
    rule.setKey(ruleNode.selectSingleNode(RULE_KEY).getText());
    rule.setName(ruleNode.selectSingleNode(RULE_NAME).getText());
    rule.setViolationsNumber(Float.valueOf(ruleNode.selectSingleNode(RULE_VIOLATIONS_NUMBER).getText()));
    rule.setViolationsNumberFormatted(ruleNode.selectSingleNode(RULE_VIOLATIONS_NUMBER_FORMATTED).getText());
    return rule;
  }

  /**
   * This method provide the possibility of init a Rule without init all violated resources.
   * 
   * @return
   * @throws DocumentException
   * @throws IOException
   * @throws HttpException
   */
  public void loadViolatedResources(SonarAccess sonarAccess, String projectKey) throws ReportException, HttpException,
    IOException, DocumentException {
    String ruleKey = getKey();

    if (ruleKey == null) {
      throw new ReportException("Rule not initialized. Forget call to initFromNode() previously?");
    } else {
      ruleKey = URLEncoder.encode(ruleKey, "UTF8");
      Logger.debug("Accessing Sonar: getting violated resurces by one given rule (" + getKey() + ")");
      Document violatedResourcesDocument = sonarAccess.getUrlAsDocument(UrlPath.VIOLATIONS + projectKey
          + UrlPath.VIOLATED_RESOURCES_BY_RULE + ruleKey + UrlPath.XML_SOURCE);
      List<Node> violatedResources = violatedResourcesDocument.selectNodes(RULE_VIOLATIONS);
      topViolatedResources = new LinkedList<Violation>();
      Iterator<Node> it = violatedResources.iterator();

      while (it.hasNext()) {
        Node resource = it.next();
        String resourceKey = resource.selectSingleNode(RESOURCE_KEY).getText();
        String line = "N/A";
        if (resource.selectSingleNode(RESOURCE_LINE) != null) {
          line = resource.selectSingleNode(RESOURCE_LINE).getText();
        }
        topViolatedResources.add(new Violation(line, resourceKey, ""));
      }
    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<Violation> getTopViolations() {
    return topViolatedResources;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTopViolations(List<Violation> violations) {
    this.topViolatedResources = violations;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public float getViolationsNumber() {
    return violationsNumber;
  }

  public void setViolationsNumber(float violationsNumber) {
    this.violationsNumber = violationsNumber;
  }

  public String getViolationsNumberFormatted() {
    return violationsNumberFormatted;
  }

  public void setViolationsNumberFormatted(String violationsNumberFormatted) {
    this.violationsNumberFormatted = violationsNumberFormatted;
  }

}
