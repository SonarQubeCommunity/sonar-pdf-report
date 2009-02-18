package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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

  private static final String PROJECTS = "//projects/project";

  @SuppressWarnings("unchecked")
  public static Project parse(String url) throws HttpException, IOException, DocumentException {
    Project project = null;
    HttpClient client = new HttpClient();
    HttpMethod method = new GetMethod(url);
    int status = 0;

    status = client.executeMethod(method);
    if (!(status == HttpStatus.SC_OK)) {
      return null;
    }
    SAXReader reader = new SAXReader();
    Document document = reader.read(method.getResponseBodyAsStream());
    List<Node> allProjectsNode = document.selectNodes(PROJECTS);
    Iterator<Node> it = allProjectsNode.iterator();
    Node projectNode = it.next();
    project = createProjectFromNode(projectNode);
    while (it.hasNext()) {
      projectNode = it.next();
      project.getSubprojects().add(createProjectFromNode(projectNode));
    }

    return project;
  }

  public String getMeasureValue(String measureKey) {
    if (measures.containsMeasure(measureKey)) {
      return measures.getMeasure(measureKey);
    } else {
      return "N/A";
    }

  }

  @SuppressWarnings("unchecked")
  private static Project createProjectFromNode(Node projectNode) {
    Project project = new Project();
    Node name = projectNode.selectSingleNode("name");
    if (name != null) {
      project.setName(name.getText());
    }
    Node description = projectNode.selectSingleNode("description");
    if (description != null) {
      project.setDescription(description.getText());
    }
    project.setKey(projectNode.selectSingleNode("key").getText());
    project.setLinks(new LinkedList<String>());
    List<Node> linksNode = projectNode.selectNodes("links");
    Iterator<Node> it = linksNode.iterator();
    while (it.hasNext()) {
      project.getLinks().add(it.next().selectSingleNode("url").getText());
    }
    project.setSubprojects(new LinkedList<Project>());
    return project;

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
