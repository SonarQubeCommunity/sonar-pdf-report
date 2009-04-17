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
package org.sonar.report.pdf.util;

public class UrlPath {
  
  // First level
  public static final String RESOURCES = "/api/resources?resource=";
  
  // Second level
  public static final String PARENT_PROJECT = "&depth=0&format=xml";
  public static final String CHILD_PROJECTS = "&depth=1&format=xml";
  public static final String CATEGORIES_VIOLATIONS = "&metrics=rules_violations&filter_rules_cats=false";
  public static final String MOST_VIOLATED_RULES = "&metrics=rules_violations&limit=5&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_FILES = "&metrics=rules_violations&scopes=FIL&depth=-1&limit=5&format=xml";

}
