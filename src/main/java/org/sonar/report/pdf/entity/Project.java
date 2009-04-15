package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.util.SonarAccess;
import org.sonar.report.pdf.util.UrlPath;

/**
 * This class encapsulates the Project info.
 */
public class Project {

  // Project info
  private short id;
  private String key;
  private String name;
  private String description;
  private List<String> links;

  // Measures
  private Measures measures;

  // Child projects
  private List<Project> subprojects;

  // Most violated rules
  private HashMap<String, String> mostViolatedRules;

  // Most violated files
  private List<FileInfo> mostViolatedFiles;

  // Rules categories violations
  private Integer maintainabilityViolations;
  private Integer reliabilityViolations;
  private Integer efficiencyViolations;
  private Integer portabilityViolations;
  private Integer usabilityViolations;

  // PROJECT INFO XPATH
  private static final String PROJECT = "//resources/resource";
  private static final String KEY = "key";
  private static final String NAME = "name";

  // RULES INFO XPATH
  private static final String ALL_MEASURES = "msr";
  private static final String MEASURE_FRMT_VAL = "frmt_val";
  private static final String RULE_NAME = "rule/name";

  private static final String DESCRIPTION = "description";
  private static final String MAINTAINABILITY = "msr[rules_categ/name='Maintainability']/frmt_val";
  private static final String RELIABILITY = "msr[rules_categ/name='Reliability']/frmt_val";
  private static final String EFFICIENCY = "msr[rules_categ/name='Efficiency']/frmt_val";
  private static final String PORTABILITY = "msr[rules_categ/name='Portability']/frmt_val";
  private static final String USABILITY = "msr[rules_categ/name='Usability']/frmt_val";

  public Project(String key) {
    this.key = key;
  }

  /**
   * Initialize: - Project basic data - Project measures - Project categories violations - Project most violated rules -
   * Project most violated files
   * 
   * @param sonarAccess
   * @throws HttpException
   * @throws IOException
   * @throws DocumentException
   */
  public void initializeProject(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Document parent = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT);
    initFromNode(parent.selectSingleNode(PROJECT));
    initMeasures(sonarAccess);
    initCategories(sonarAccess);
    initMostViolatedRules(sonarAccess);
    initMostViolatedFiles(sonarAccess);
    Document childs = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.CHILD_PROJECTS);
    List<Node> childNodes = childs.selectNodes(PROJECT);
    Iterator<Node> it = childNodes.iterator();
    setSubprojects(new ArrayList<Project>());
    while (it.hasNext()) {
      Node childNode = it.next();
      if (childNode.selectSingleNode("scope").getText().equals("PRJ")) {
        Project childProject = new Project(childNode.selectSingleNode(KEY).getText());
        childProject.initializeProject(sonarAccess);
        getSubprojects().add(childProject);
      }
    }
  }

  /**
   * Initialize project object and his childs (except categories violations).
   */
  private void initFromNode(Node projectNode) {
    Node name = projectNode.selectSingleNode(NAME);
    if (name != null) {
      this.setName(name.getText());
    }
    Node description = projectNode.selectSingleNode(DESCRIPTION);
    if (description != null) {
      this.setDescription(description.getText());
    }
    this.setKey(projectNode.selectSingleNode(KEY).getText());
    this.setLinks(new LinkedList<String>());
    this.setSubprojects(new LinkedList<Project>());
    this.setMostViolatedRules(new HashMap<String, String>());
  }

  public void initMeasures(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Measures measures = new Measures();
    measures.initMeasuresByProjectKey(sonarAccess, this.key);
    this.setMeasures(measures);
  }

  public void initMostViolatedRules(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Document mostViolatedRules = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT
        + UrlPath.MOST_VIOLATED_RULES);
    initMostViolatedRulesFromNode(mostViolatedRules.selectSingleNode(PROJECT));
  }

  public void initMostViolatedFiles(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Document mostVilatedFiles = sonarAccess
        .getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.MOST_VIOLATED_FILES);
    this.setMostViolatedFiles(FileInfo.initFromDocument(mostVilatedFiles));
  }

  public Measure getMeasure(String measureKey) {
    if (measures.containsMeasure(measureKey)) {
      return measures.getMeasure(measureKey);
    } else {
      return new Measure(null, "N/A");
    }
  }

  private void initCategories(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Document categories = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT
        + UrlPath.CATEGORIES_VIOLATIONS);
    this.initCategoriesViolationsFromNode(categories.selectSingleNode(PROJECT));
  }

  private void initCategoriesViolationsFromNode(Node categoriesNode) {
    this.setMaintainabilityViolations(Integer.valueOf(categoriesNode.selectSingleNode(MAINTAINABILITY).getText()));
    this.setReliabilityViolations(Integer.valueOf(categoriesNode.selectSingleNode(RELIABILITY).getText()));
    this.setEfficiencyViolations(Integer.valueOf(categoriesNode.selectSingleNode(EFFICIENCY).getText()));
    this.setPortabilityViolations(Integer.valueOf(categoriesNode.selectSingleNode(PORTABILITY).getText()));
    this.setUsabilityViolations(Integer.valueOf(categoriesNode.selectSingleNode(USABILITY).getText()));
  }

  private void initMostViolatedRulesFromNode(Node mostViolatedNode) {
    List<Node> measures = mostViolatedNode.selectNodes(ALL_MEASURES);
    Iterator<Node> it = measures.iterator();
    while (it.hasNext()) {
      Node measure = it.next();
      this.mostViolatedRules.put(measure.selectSingleNode(RULE_NAME).getText(), measure.selectSingleNode(
          MEASURE_FRMT_VAL).getText());
    }
  }

  public Project getChildByKey(String key) {
    Iterator<Project> it = this.subprojects.iterator();
    while (it.hasNext()) {
      Project child = it.next();
      if (child.getKey().equals(key)) {
        return child;
      }
    }
    return null;
  }

  public void setId(short id) {
    this.id = id;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLinks(List<String> links) {
    this.links = links;
  }

  public short getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getLinks() {
    return links;
  }

  public List<Project> getSubprojects() {
    return subprojects;
  }

  public void setSubprojects(List<Project> subprojects) {
    this.subprojects = subprojects;
  }

  public Measures getMeasures() {
    return measures;
  }

  public void setMeasures(Measures measures) {
    this.measures = measures;
  }

  public Integer getMaintainabilityViolations() {
    return maintainabilityViolations;
  }

  public Integer getReliabilityViolations() {
    return reliabilityViolations;
  }

  public Integer getEfficiencyViolations() {
    return efficiencyViolations;
  }

  public Integer getPortabilityViolations() {
    return portabilityViolations;
  }

  public Integer getUsabilityViolations() {
    return usabilityViolations;
  }

  public HashMap<String, String> getMostViolatedRules() {
    return mostViolatedRules;
  }

  public List<FileInfo> getMostViolatedFiles() {
    return mostViolatedFiles;
  }

  public void setMostViolatedRules(HashMap<String, String> mostViolatedRules) {
    this.mostViolatedRules = mostViolatedRules;
  }

  public void setMaintainabilityViolations(Integer maintainabilityViolations) {
    this.maintainabilityViolations = maintainabilityViolations;
  }

  public void setReliabilityViolations(Integer reliabilityViolations) {
    this.reliabilityViolations = reliabilityViolations;
  }

  public void setEfficiencyViolations(Integer efficiencyViolations) {
    this.efficiencyViolations = efficiencyViolations;
  }

  public void setPortabilityViolations(Integer portabilityValue) {
    this.portabilityViolations = portabilityValue;
  }

  public void setUsabilityViolations(Integer usabilityValue) {
    this.usabilityViolations = usabilityValue;
  }

  public void setMostViolatedFiles(List<FileInfo> mostViolatedFiles) {
    this.mostViolatedFiles = mostViolatedFiles;
  }
}
