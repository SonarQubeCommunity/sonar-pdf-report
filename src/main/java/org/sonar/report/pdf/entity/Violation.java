package org.sonar.report.pdf.entity;

public class Violation {
  
  private String resource;
  private String line;
  
  public Violation(String line, String resource) {
    this.line = line;
    this.resource = resource;
  }
  
  public String getResource() {
    return resource;
  }
  public String getLine() {
    return line;
  }
  public void setResource(String resource) {
    this.resource = resource;
  }
  public void setLine(String line) {
    this.line = line;
  }

}
