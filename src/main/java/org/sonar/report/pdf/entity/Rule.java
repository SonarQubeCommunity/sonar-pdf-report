package org.sonar.report.pdf.entity;

import java.util.HashMap;
import org.dom4j.Node;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.SonarAccess;

public class Rule {

  // Rule key
  private String key;
  
  // Rule name
  private String name;

  // Rule description
  private String description;

  // Violations of this rule: <resource key, violation line> (with limit 100)
  private HashMap<String, String> topViolatedResources;
  
  // Total vilations of this rule
  private float violationsNumber;
  
  //Total vilations of this rule
  private String violationsNumberFormatted;
  
  
  private static final String RULE_NAME = "rule/name";
  private static final String RULE_KEY = "rule/key";
  private static final String RULE_VIOLATIONS_NUMBER = "val";
  private static final String RULE_VIOLATIONS_NUMBER_FORMATTED = "frmt_val";

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
   * This method provide the possibility of init a Rule without init all
   * violated resources.
   * @return
   */
  public void loadViolatedResources(SonarAccess sonarAccess) throws ReportException{
    //TODO: Do request to retrieve violations on resources
    //http://nemo.sonar.codehaus.org/api/violations?rules=AvoidThrowingRawExceptionTypes&resource=org.codehaus.sonar:sonar&scopes=FIL&depth=-1&limit=100&format=xml
    
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public HashMap<String, String> getTopViolations() {
    return topViolatedResources;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTopViolations(HashMap<String, String> violations) {
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
