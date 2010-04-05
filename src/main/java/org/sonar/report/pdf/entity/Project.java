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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Logger;
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
  private List<Rule> mostViolatedRules;

  // Most complex elements
  private List<FileInfo> mostComplexFiles;

  // Most violated files
  private List<FileInfo> mostViolatedFiles;

  // Most duplicated files
  private List<FileInfo> mostDuplicatedFiles;

  // Rules categories violations
  private String maintainabilityViolations = "0";
  private String reliabilityViolations = "0";
  private String efficiencyViolations = "0";
  private String portabilityViolations = "0";
  private String usabilityViolations = "0";

  // Rules compliance index
  private String maintainabilityRci = "0";
  private String reliabilityRci = "0";
  private String efficiencyRci = "0";
  private String portabilityRci = "0";
  private String usabilityRci = "0";

  // PROJECT INFO XPATH
  private static final String PROJECT = "//resources/resource";
  private static final String KEY = "key";
  private static final String NAME = "name";

  // RULES INFO XPATH
  private static final String ALL_MEASURES = "msr";
  private static final String MEASURE_FRMT_VAL = "frmt_val";

  // VIOLATIONS (number)
  private static final String DESCRIPTION = "description";
  private static final String MAINTAINABILITY = "msr[rule_category='Maintainability']/frmt_val";
  private static final String RELIABILITY = "msr[rule_category='Reliability']/frmt_val";
  private static final String EFFICIENCY = "msr[rule_category='Efficiency']/frmt_val";
  private static final String PORTABILITY = "msr[rule_category='Portability']/frmt_val";
  private static final String USABILITY = "msr[rule_category='Usability']/frmt_val";

  // VIOLATIONS (RCI)
  private static final String MAINTAINABILITY_RCI = "msr[rule_category='Maintainability']/val";
  private static final String RELIABILITY_RCI = "msr[rule_category='Reliability']/val";
  private static final String EFFICIENCY_RCI = "msr[rule_category='Efficiency']/val";
  private static final String PORTABILITY_RCI = "msr[rule_category='Portability']/val";
  private static final String USABILITY_RCI = "msr[rule_category='Usability']/val";

  public Project(String key) {
    this.key = key;
  }

  /**
   * Initialize: - Project basic data - Project measures - Project categories violations - Project most violated rules -
   * Project most violated files - Project most duplicated files
   * 
   * @param sonarAccess
   * @throws HttpException
   * @throws IOException
   * @throws DocumentException
   * @throws ReportException
   */
  public void initializeProject(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException,
    ReportException {
    Logger.info("Retrieving project info for " + this.key);
    Document parent = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT
        + UrlPath.XML_SOURCE);
    if (parent.selectSingleNode(PROJECT) != null) {
      initFromNode(parent.selectSingleNode(PROJECT));
      initMeasures(sonarAccess);
      initCategories(sonarAccess);
      initMostViolatedRules(sonarAccess);
      initMostViolatedFiles(sonarAccess);
      initMostComplexElements(sonarAccess);
      initMostDuplicatedFiles(sonarAccess);
      Logger.debug("Accessing Sonar: getting child projects");
      Document childs = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.CHILD_PROJECTS
          + UrlPath.XML_SOURCE);
      List<Node> childNodes = childs.selectNodes(PROJECT);
      Iterator<Node> it = childNodes.iterator();
      setSubprojects(new ArrayList<Project>());
      if (!it.hasNext()) {
        Logger.debug(this.key + " project has no childs");
      }
      while (it.hasNext()) {
        Node childNode = it.next();
        if (childNode.selectSingleNode("scope").getText().equals("PRJ")) {
          Project childProject = new Project(childNode.selectSingleNode(KEY).getText());
          childProject.initializeProject(sonarAccess);
          getSubprojects().add(childProject);
        }
      }
    } else {
      Logger.info("Can't retrieve project info. Have you set username/password in Sonar settings?");
      throw new ReportException("Can't retrieve project info. Parent project node is empty. Authentication?");
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
    this.setMostViolatedRules(new LinkedList<Rule>());
    this.setMostComplexFiles(new LinkedList<FileInfo>());
    this.setMostDuplicatedFiles(new LinkedList<FileInfo>());
    this.setMostViolatedFiles(new LinkedList<FileInfo>());
  }

  private void initMeasures(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Measures measures = new Measures();
    Logger.info("    Retrieving measures");
    measures.initMeasuresByProjectKey(sonarAccess, this.key);
    this.setMeasures(measures);
  }

  private void initMostViolatedRules(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException,
    ReportException {
    Logger.info("    Retrieving most violated rules");
    Logger.debug("Accessing Sonar: getting most violated rules");
    Document mostViolatedRules = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT
        + UrlPath.MOST_VIOLATED_RULES + UrlPath.XML_SOURCE);
    if (mostViolatedRules.selectSingleNode(PROJECT) != null) {
      initMostViolatedRulesFromNode(mostViolatedRules.selectSingleNode(PROJECT), sonarAccess);
    } else {
      Logger.warn("There is not result on select //resources/resource");
    }
  }

  private void initMostViolatedFiles(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Logger.info("    Retrieving most violated files");
    Logger.debug("Accessing Sonar: getting most violated files");
    Document mostViolatedFilesDoc = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key
        + UrlPath.MOST_VIOLATED_FILES + UrlPath.XML_SOURCE);
    this.setMostViolatedFiles(FileInfo.initFromDocument(mostViolatedFilesDoc, FileInfo.VIOLATIONS_CONTENT));

  }

  private void initMostComplexElements(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Logger.info("    Retrieving most complex elements");
    Logger.debug("Accessing Sonar: getting most complex elements");
    Document mostComplexFilesDoc = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key
        + UrlPath.MOST_COMPLEX_FILES + UrlPath.XML_SOURCE);
    this.setMostComplexFiles(FileInfo.initFromDocument(mostComplexFilesDoc, FileInfo.CCN_CONTENT));
  }

  private void initMostDuplicatedFiles(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Logger.info("    Retrieving most duplicated files");
    Logger.debug("Accessing Sonar: getting most duplicated files");
    Document mostDuplicatedFilesDoc = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key
        + UrlPath.MOST_DUPLICATED_FILES + UrlPath.XML_SOURCE);
    this.setMostDuplicatedFiles(FileInfo.initFromDocument(mostDuplicatedFilesDoc, FileInfo.DUPLICATIONS_CONTENT));
  }

  public Measure getMeasure(String measureKey) {
    if (measures.containsMeasure(measureKey)) {
      return measures.getMeasure(measureKey);
    } else {
      return new Measure(null, "N/A");
    }
  }

  private void initCategories(SonarAccess sonarAccess) throws HttpException, IOException, DocumentException {
    Logger.info("    Retrieving categories RCI");
    Logger.debug("Accessing Sonar: getting categories");
    Document categories = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT
        + UrlPath.CATEGORIES_VIOLATIONS + UrlPath.XML_SOURCE);
    if (categories.selectSingleNode(PROJECT) != null) {
      this.initCategoriesViolationsFromNode(categories.selectSingleNode(PROJECT));
    } else {
      Logger.warn("Init Categories. There is not result on select //resources/resource");
    }
    Document categoriesRci = sonarAccess.getUrlAsDocument(UrlPath.RESOURCES + this.key + UrlPath.PARENT_PROJECT
        + UrlPath.CATEGORIES_VIOLATIONS_DENSITY + UrlPath.XML_SOURCE);
    if (categories.selectSingleNode(PROJECT) != null) {
      this.initCategoriesRciFromNode(categoriesRci.selectSingleNode(PROJECT));
    } else {
      Logger.warn("Init Categories RCI. There is not result on select //resources/resource");
    }
  }

  private void initCategoriesRciFromNode(Node categoriesRciNode) {
    Logger.debug("Getting category RCI (initCategoriesRciFromNode)");
    if (categoriesRciNode.selectSingleNode(MAINTAINABILITY_RCI) != null) {
      this.setMaintainabilityRci(categoriesRciNode.selectSingleNode(MAINTAINABILITY_RCI).getText());
    } else {
      Logger.debug("Init Categories RCI. There is not result on select msr[rule_category='Maintainability']/val");
    }
    if (categoriesRciNode.selectSingleNode(RELIABILITY_RCI) != null) {
      this.setReliabilityRci(categoriesRciNode.selectSingleNode(RELIABILITY_RCI).getText());
    } else {
      Logger.debug("Init Categories RCI. There is not result on select msr[rule_category='Reliability']/val");
    }
    if (categoriesRciNode.selectSingleNode(EFFICIENCY_RCI) != null) {
      this.setEfficiencyRci(categoriesRciNode.selectSingleNode(EFFICIENCY_RCI).getText());
    } else {
      Logger.debug("Init Categories RCI. There is not result on select msr[rule_category='Efficiency']/val");
    }
    if (categoriesRciNode.selectSingleNode(PORTABILITY_RCI) != null) {
      this.setPortabilityRci(categoriesRciNode.selectSingleNode(PORTABILITY_RCI).getText());
    } else {
      Logger.debug("Init Categories RCI. There is not result on select msr[rule_category='Portability']/val");
    }
    if (categoriesRciNode.selectSingleNode(USABILITY_RCI) != null) {
      this.setUsabilityRci(categoriesRciNode.selectSingleNode(USABILITY_RCI).getText());
    } else {
      Logger.debug("Init Categories RCI. There is not result on select msr[rule_category='Usability']/val");
    }
  }

  private void initCategoriesViolationsFromNode(Node categoriesNode) {
    Logger.debug("Geting category violations count (initCategoriesViolationsFromNode)");
    if (categoriesNode.selectSingleNode(MAINTAINABILITY) != null) {
      this.setMaintainabilityViolations(categoriesNode.selectSingleNode(MAINTAINABILITY).getText());
    } else {
      Logger
          .debug("Init Categories violations. There is not result on select msr[rule_category='Maintainability']/frmt_val");
    }
    if (categoriesNode.selectSingleNode(RELIABILITY) != null) {
      this.setReliabilityViolations(categoriesNode.selectSingleNode(RELIABILITY).getText());
    } else {
      Logger
          .debug("Init Categories violations. There is not result on select msr[rule_category='Reliability']/frmt_val");
    }
    if (categoriesNode.selectSingleNode(EFFICIENCY) != null) {
      this.setEfficiencyViolations(categoriesNode.selectSingleNode(EFFICIENCY).getText());
    } else {
      Logger
          .debug("Init Categories violations. There is not result on select msr[rule_category='Efficiency']/frmt_val");
    }
    if (categoriesNode.selectSingleNode(PORTABILITY) != null) {
      this.setPortabilityViolations(categoriesNode.selectSingleNode(PORTABILITY).getText());
    } else {
      Logger
          .debug("Init Categories violations. There is not result on select msr[rule_category='Portability']/frmt_val");
    }
    if (categoriesNode.selectSingleNode(USABILITY) != null) {
      this.setUsabilityViolations(categoriesNode.selectSingleNode(USABILITY).getText());
    } else {
      Logger.debug("Init Categories violations. There is not result on select msr[rule_category='Usability']/frmt_val");
    }
  }

  private void initMostViolatedRulesFromNode(Node mostViolatedNode, SonarAccess sonarAccess) throws HttpException,
    ReportException, IOException, DocumentException {
    List<Node> measures = mostViolatedNode.selectNodes(ALL_MEASURES);
    Iterator<Node> it = measures.iterator();
    if (!it.hasNext()) {
      Logger.warn("There is not result on select //resources/resource/msr");
    }
    while (it.hasNext()) {
      Node measure = it.next();
      if (!measure.selectSingleNode(MEASURE_FRMT_VAL).getText().equals("0")) {
        Rule rule = Rule.initFromNode(measure);
        if (PDFReporter.reportType.equals("workbook")) {
          rule.loadViolatedResources(sonarAccess, this.key);
        }
        this.mostViolatedRules.add(rule);
      }
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

  public String getMaintainabilityViolations() {
    return maintainabilityViolations;
  }

  public String getReliabilityViolations() {
    return reliabilityViolations;
  }

  public String getEfficiencyViolations() {
    return efficiencyViolations;
  }

  public String getPortabilityViolations() {
    return portabilityViolations;
  }

  public String getUsabilityViolations() {
    return usabilityViolations;
  }

  public List<Rule> getMostViolatedRules() {
    return mostViolatedRules;
  }

  public List<FileInfo> getMostViolatedFiles() {
    return mostViolatedFiles;
  }

  public void setMostViolatedRules(List<Rule> mostViolatedRules) {
    this.mostViolatedRules = mostViolatedRules;
  }

  public void setMaintainabilityViolations(String maintainabilityViolations) {
    this.maintainabilityViolations = maintainabilityViolations;
  }

  public void setReliabilityViolations(String reliabilityViolations) {
    this.reliabilityViolations = reliabilityViolations;
  }

  public void setEfficiencyViolations(String efficiencyViolations) {
    this.efficiencyViolations = efficiencyViolations;
  }

  public void setPortabilityViolations(String portabilityValue) {
    this.portabilityViolations = portabilityValue;
  }

  public void setUsabilityViolations(String usabilityValue) {
    this.usabilityViolations = usabilityValue;
  }

  public void setMostViolatedFiles(List<FileInfo> mostViolatedFiles) {
    this.mostViolatedFiles = mostViolatedFiles;
  }

  public void setMostComplexFiles(List<FileInfo> mostComplexFiles) {
    this.mostComplexFiles = mostComplexFiles;
  }

  public List<FileInfo> getMostComplexFiles() {
    return mostComplexFiles;
  }

  public List<FileInfo> getMostDuplicatedFiles() {
    return mostDuplicatedFiles;
  }

  public void setMostDuplicatedFiles(List<FileInfo> mostDuplicatedFiles) {
    this.mostDuplicatedFiles = mostDuplicatedFiles;
  }

  public String getMaintainabilityRci() {
    return maintainabilityRci;
  }

  public void setMaintainabilityRci(String maintainabilityRci) {
    this.maintainabilityRci = maintainabilityRci;
  }

  public String getReliabilityRci() {
    return reliabilityRci;
  }

  public void setReliabilityRci(String reliabilityRci) {
    this.reliabilityRci = reliabilityRci;
  }

  public String getEfficiencyRci() {
    return efficiencyRci;
  }

  public void setEfficiencyRci(String efficiencyRci) {
    this.efficiencyRci = efficiencyRci;
  }

  public String getPortabilityRci() {
    return portabilityRci;
  }

  public void setPortabilityRci(String portabilityRci) {
    this.portabilityRci = portabilityRci;
  }

  public String getUsabilityRci() {
    return usabilityRci;
  }

  public void setUsabilityRci(String usabilityRci) {
    this.usabilityRci = usabilityRci;
  }
}
