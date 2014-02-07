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


public class Logger {

  private static Logger log;

  public static void info(CharSequence message) {
    if (log != null) {
      log.info(message);
    }
  }

  public static void error(CharSequence message) {
    if (log != null) {
      log.error(message);
    }
  }

  public static void debug(CharSequence message) {
    if (log != null) {
      log.debug(message);
    }
  }

  public static void warn(CharSequence message) {
    if (log != null) {
      log.warn(message);
    }
  }

  public static void setLog(Logger log) {
    Logger.log = log;
  }
}
