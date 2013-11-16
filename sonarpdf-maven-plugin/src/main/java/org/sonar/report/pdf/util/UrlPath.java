/*
 * Sonar PDF Report (Maven plugin)
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

package org.sonar.report.pdf.util;

import org.sonar.report.pdf.entity.Priority;

public class UrlPath {

  // First level
  public static final String RESOURCES = "/api/resources?resource=";
  // public static final String VIOLATIONS = "/api/violations?resource=";
  public static final String COMPONENTS = "/issues/components/component";
  public static final String ISSUES = "/api/issues/search?resource=";
  public static final String SOURCES = "/api/sources?resource=";

  // Second level
  public static final String PARENT_PROJECT = "&depth=0";
  public static final String CHILD_PROJECTS = "&depth=1";
  public static final String CATEGORIES_VIOLATIONS = "&metrics=" + MetricKeys.VIOLATIONS + "&filter_rules_cats=false";
  public static final String CATEGORIES_VIOLATIONS_DENSITY = "&metrics=" + MetricKeys.VIOLATIONS_DENSITY
      + "&filter_rules_cats=false";
  public static final String MOST_VIOLATED_INFO_RULES = "&metrics=" + MetricKeys.INFO_VIOLATIONS
      + "&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_MINOR_RULES = "&metrics=" + MetricKeys.MINOR_VIOLATIONS
      + "&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_MAJOR_RULES = "&metrics=" + MetricKeys.MAJOR_VIOLATIONS
      + "&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_CRITICAL_RULES = "&metrics=" + MetricKeys.CRITICAL_VIOLATIONS
      + "&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_BLOCKER_RULES = "&metrics=" + MetricKeys.BLOCKER_VIOLATIONS
      + "&filter_rules=false&filter_rules_cats=true";
  public static final String MOST_VIOLATED_FILES = "&metrics=" + MetricKeys.VIOLATIONS + "&scopes=FIL&depth=-1&limit=5";
  public static final String MOST_COMPLEX_FILES = "&metrics=" + MetricKeys.COMPLEXITY + "&scopes=FIL&depth=-1&limit=5";
  public static final String MOST_DUPLICATED_FILES = "&metrics=" + MetricKeys.DUPLICATED_LINES
      + "&scopes=FIL&depth=-1&limit=5";
  public static final String VIOLATED_RESOURCES_BY_RULE = "&scopes=FIL&depth=-1&limit=20&rules=";
  public static final String XML_SOURCE = "&format=xml";
  public static final String LIMIT10 = "&limit=5";

  public static String getViolationsLevelPath(final String priority) {
    if (priority.equals(Priority.INFO)) {
      return MOST_VIOLATED_INFO_RULES;
    } else if (priority.equals(Priority.MINOR)) {
      return MOST_VIOLATED_MINOR_RULES;
    } else if (priority.equals(Priority.MAJOR)) {
      return MOST_VIOLATED_MAJOR_RULES;
    } else if (priority.equals(Priority.CRITICAL)) {
      return MOST_VIOLATED_CRITICAL_RULES;
    } else if (priority.equals(Priority.BLOCKER)) {
      return MOST_VIOLATED_BLOCKER_RULES;
    } else {
      return null;
    }
  }

  public static String getLimit(final Integer limit) {
    return "&limit=" + limit;
  }
}