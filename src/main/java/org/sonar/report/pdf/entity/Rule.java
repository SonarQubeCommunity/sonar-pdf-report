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

import java.util.List;

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
  private Double violationsNumber;

  // Total vilations of this rule
  private String violationsNumberFormatted;

  private String message;

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<Violation> getTopViolations() {
    return topViolatedResources;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setTopViolations(final List<Violation> violations) {
    this.topViolatedResources = violations;
  }

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public Double getViolationsNumber() {
    return violationsNumber;
  }

  public void setViolationsNumber(final Double violationsNumber) {
    this.violationsNumber = violationsNumber;
  }

  public String getViolationsNumberFormatted() {
    return violationsNumberFormatted;
  }

  public void setViolationsNumberFormatted(
      final String violationsNumberFormatted) {
    this.violationsNumberFormatted = violationsNumberFormatted;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public List<Violation> getTopViolatedResources() {
    return topViolatedResources;
  }

  public void setTopViolatedResources(final List<Violation> topViolatedResources) {
    this.topViolatedResources = topViolatedResources;
  }

}
