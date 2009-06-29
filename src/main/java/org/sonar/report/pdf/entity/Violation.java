package org.sonar.report.pdf.entity;

public class Violation {
  
  private String resource;
  private String line;
  private String source;
  
  public Violation(String line, String resource, String source) {
    this.line = line;
    this.resource = resource;
    this.source = source;
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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

}
