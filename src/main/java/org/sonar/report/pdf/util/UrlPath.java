/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 GMV-SGI
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
  public static final String VIOLATIONS = "/api/violations?resource=";
  public static final String SOURCES = "/api/sources?resource=";
  
  // Second level
  public static final String PARENT_PROJECT = "&depth=0";
  public static final String CHILD_PROJECTS = "&depth=1";
  public static final String CATEGORIES_VIOLATIONS = "&metrics="+ MetricKeys.VIOLATIONS +"&filter_rules_cats=false";
  public static final String MOST_VIOLATED_RULES = "&metrics="+ MetricKeys.VIOLATIONS +"&limit=10&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_FILES = "&metrics="+ MetricKeys.VIOLATIONS +"&scopes=FIL&depth=-1&limit=5";
  public static final String MOST_COMPLEX_FILES = "&metrics="+ MetricKeys.COMPLEXITY +"&scopes=FIL&depth=-1&limit=5";
  public static final String MOST_DUPLICATED_FILES = "&metrics="+ MetricKeys.DUPLICATED_LINES +"&scopes=FIL&depth=-1&limit=5";
  public static final String VIOLATED_RESOURCES_BY_RULE = "&scopes=FIL&depth=-1&limit=20&rules=";
  public static final String XML_SOURCE = "&format=xml";

}