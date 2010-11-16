/*
 * Sonar PDF Report (Maven plugin)
 * Copyright (C) 2010 klicap - ingeniería del puzle
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
