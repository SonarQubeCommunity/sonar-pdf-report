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


public class FileInfo {

  /**
   * Sonar resource key.
   */
  private String key;

  /**
   * Resource name (filename).
   */
  private String name;

  /**
   * Number of violations ins this resource (file).
   */
  private String violations;

  /**
   * Class complexity.
   */
  private String complexity;

  /**
   * Duplicated lines in this resource (file)
   */
  private String duplicatedLines;

  /**
   * It defines the content of this object: used for violations info, complexity
   * info or duplications info.
   */
  public static final int VIOLATIONS_CONTENT = 1;
  public static final int CCN_CONTENT = 2;
  public static final int DUPLICATIONS_CONTENT = 3;

  public boolean isContentSet(final int content) {
    boolean result = false;
    if (content == VIOLATIONS_CONTENT) {
      result = !this.getViolations().equals("0");
    } else if (content == CCN_CONTENT) {
      result = !this.getComplexity().equals("0");
    } else if (content == DUPLICATIONS_CONTENT) {
      result = !this.getDuplicatedLines().equals("0");
    }
    return result;
  }

  public String getKey() {
    return key;
  }

  public String getViolations() {
    return violations;
  }

  public String getComplexity() {
    return complexity;
  }

  public String getName() {
    return name;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public void setViolations(final String violations) {
    this.violations = violations;
  }

  public void setComplexity(final String complexity) {
    this.complexity = complexity;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDuplicatedLines() {
    return duplicatedLines;
  }

  public void setDuplicatedLines(final String duplicatedLines) {
    this.duplicatedLines = duplicatedLines;
  }

}
