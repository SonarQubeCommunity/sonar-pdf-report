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

import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

/**
 * This class encapsulates the measures info.
 */
public class Measures {

  private Hashtable<String, Measure> measuresTable = new Hashtable<String, Measure>();
  private Date date;
  private String version = "N/A";

  public Measures() {
  }

  public Date getDate() {
    return date;
  }

  public void setDate(final Date date) throws ParseException {
    this.date = date;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public int getMeasuresCount() {
    return measuresTable.size();
  }

  public Set<String> getMeasuresKeys() {
    return measuresTable.keySet();
  }

  public Measure getMeasure(final String key) {
    return measuresTable.get(key);
  }

  public void addMeasure(final String name, final Measure value) {
    measuresTable.put(name, value);
  }

  public boolean containsMeasure(final String measureKey) {
    return measuresTable.containsKey(measureKey);
  }

}
