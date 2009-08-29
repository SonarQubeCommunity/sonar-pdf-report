package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.entity.exception.ReportException;
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
    if (getKey() == null) {
      throw new ReportException("Rule not initialized. Forget call to initFromNode() previously?");
    } else {
      Document violatedResourcesDocument = sonarAccess.getUrlAsDocument(UrlPath.VIOLATIONS + projectKey
          + UrlPath.VIOLATED_RESOURCES_BY_RULE + getKey() + UrlPath.XML_SOURCE);
      List<Node> violatedResources = violatedResourcesDocument.selectNodes(RULE_VIOLATIONS);
      topViolatedResources = new LinkedList<Violation>();
      Iterator<Node> it = violatedResources.iterator();
      while (it.hasNext()) {
        Node resource = it.next();
        String resourceKey = resource.selectSingleNode(RESOURCE_KEY).getText();
        String line = "N/A";
        if(resource.selectSingleNode(RESOURCE_LINE) != null) {
          line = resource.selectSingleNode(RESOURCE_LINE).getText();
        }
        topViolatedResources.add(new Violation(line , resourceKey, ""));
        
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
