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
package org.sonar.report.pdf.util;

import org.sonar.report.pdf.entity.Priority;

public class UrlPath {

  public static String getViolationsLevelPath(final String priority) {
    if (priority.equals(Priority.INFO)) {
      return MetricKeys.INFO_VIOLATIONS;
    } else if (priority.equals(Priority.MINOR)) {
      return MetricKeys.MINOR_VIOLATIONS;
    } else if (priority.equals(Priority.MAJOR)) {
      return MetricKeys.MAJOR_VIOLATIONS;
    } else if (priority.equals(Priority.CRITICAL)) {
      return MetricKeys.CRITICAL_VIOLATIONS;
    } else if (priority.equals(Priority.BLOCKER)) {
      return MetricKeys.BLOCKER_VIOLATIONS;
    } else {
      return null;
    }
  }

}