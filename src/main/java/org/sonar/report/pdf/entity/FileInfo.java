package org.sonar.report.pdf.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;

public class FileInfo {
  
  private String key;
  private Integer violations;
  private String name;
  
  private static final String ALL_FILES = "/resources/resource";
  private static final String KEY = "key";
  private static final String NAME = "name";
  private static final String VIOLATIONS_NUMBER = "msr/frmt_val";
  
  
  public void initFromNode(Node fileNode) {
    this.setKey(fileNode.selectSingleNode(KEY).getText());
    this.setName(fileNode.selectSingleNode(NAME).getText());
    this.setViolations(Integer.valueOf(fileNode.selectSingleNode(VIOLATIONS_NUMBER).getText()));
  }
  
  public static List<FileInfo> initFromDocument(Document filesDocument) {
    List<Node> fileNodes = filesDocument.selectNodes(ALL_FILES);
    Iterator<Node> it = fileNodes.iterator();
    List<FileInfo> fileInfoList = new LinkedList<FileInfo>();
    while(it.hasNext()) {
      FileInfo file = new FileInfo();
      Node fileNode = it.next();
      file.initFromNode(fileNode);
      if(file.getViolations() != 0) {
        fileInfoList.add(file);
      }
    }
    return fileInfoList;
  }
  
  public String getKey() {
    return key;
  }
  public Integer getViolations() {
    return violations;
  }
  public String getName() {
    return name;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public void setViolations(Integer violations) {
    this.violations = violations;
  }
  public void setName(String name) {
    this.name = name;
  }
  

}
