/*
 * SonarQube PDF Report
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

import java.util.Iterator;
import java.util.List;

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

  public Project(final String key) {
    this.key = key;
  }

  public Measure getMeasure(final String measureKey) {
    if (measures.containsMeasure(measureKey)) {
      return measures.getMeasure(measureKey);
    } else {
      return new Measure(null, "N/A");
    }
  }

  public Project getChildByKey(final String key) {
    Iterator<Project> it = this.subprojects.iterator();
    while (it.hasNext()) {
      Project child = it.next();
      if (child.getKey().equals(key)) {
        return child;
      }
    }
    return null;
  }

  public void setId(final short id) {
    this.id = id;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setLinks(final List<String> links) {
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

  public void setSubprojects(final List<Project> subprojects) {
    this.subprojects = subprojects;
  }

  public Measures getMeasures() {
    return measures;
  }

  public void setMeasures(final Measures measures) {
    this.measures = measures;
  }

  public List<Rule> getMostViolatedRules() {
    return mostViolatedRules;
  }

  public List<FileInfo> getMostViolatedFiles() {
    return mostViolatedFiles;
  }

  public void setMostViolatedRules(final List<Rule> mostViolatedRules) {
    this.mostViolatedRules = mostViolatedRules;
  }

  public void setMostViolatedFiles(final List<FileInfo> mostViolatedFiles) {
    this.mostViolatedFiles = mostViolatedFiles;
  }

  public void setMostComplexFiles(final List<FileInfo> mostComplexFiles) {
    this.mostComplexFiles = mostComplexFiles;
  }

  public List<FileInfo> getMostComplexFiles() {
    return mostComplexFiles;
  }

  public List<FileInfo> getMostDuplicatedFiles() {
    return mostDuplicatedFiles;
  }

  public void setMostDuplicatedFiles(final List<FileInfo> mostDuplicatedFiles) {
    this.mostDuplicatedFiles = mostDuplicatedFiles;
  }
}
