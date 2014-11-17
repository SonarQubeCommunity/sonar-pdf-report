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

public class RadarGraphic {

  private String efficiency;
  private String maintainability;
  private String portability;
  private String reliavility;
  private String usability;

  public RadarGraphic(final String efficiency, final String maintainability,
      final String portability, final String reliavility, final String usability) {
    this.efficiency = efficiency;
    this.maintainability = maintainability;
    this.portability = portability;
    this.reliavility = reliavility;
    this.usability = usability;
  }

  public String getEfficiency() {
    return efficiency;
  }

  public void setEfficiency(final String efficiency) {
    this.efficiency = efficiency;
  }

  public String getMaintainability() {
    return maintainability;
  }

  public void setMaintainability(final String maintainability) {
    this.maintainability = maintainability;
  }

  public String getPortability() {
    return portability;
  }

  public void setPortability(final String portability) {
    this.portability = portability;
  }

  public String getReliavility() {
    return reliavility;
  }

  public void setReliavility(final String reliavility) {
    this.reliavility = reliavility;
  }

  public String getUsability() {
    return usability;
  }

  public void setUsability(final String usability) {
    this.usability = usability;
  }

}
