package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.sonar.report.pdf.util.SonarAccess;

/**
 * This class encapsulates the Project info.
 */
public class Project {
  private short id;
  private String key;
  private String name;
  private String description;
  private List<String> links;
  private Measures measures;
  private List<Project> subprojects;

  private static final String PROJECT = "//resources/resource";
  private static final String KEY = "key";
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";

  public Measure getMeasure(String measureKey) {
    if (measures.containsMeasure(measureKey)) {
      return measures.getMeasure(measureKey);
    } else {
      return new Measure(null, "N/A");
    }
  }

  public void initFromDocuments(Document projectDoc, Document childsDoc) {
    initFromNode(projectDoc.selectSingleNode(PROJECT), childsDoc.selectNodes(PROJECT));
  }
  
  public void initFromNode(Node projectNode, List<Node> childsNodes) {
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
    if(childsNodes != null) {
      Iterator<Node> it = childsNodes.iterator();
      Node subprojectNode;
      while (it.hasNext()) {
        subprojectNode = it.next();
        Project childProject = new Project();
        childProject.initFromNode(subprojectNode, null);
        this.getSubprojects().add(childProject);
      }
    }
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

}
