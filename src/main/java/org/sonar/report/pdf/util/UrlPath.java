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
