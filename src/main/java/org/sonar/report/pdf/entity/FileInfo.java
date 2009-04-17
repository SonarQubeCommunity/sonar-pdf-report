/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
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
