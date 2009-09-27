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

import org.apache.maven.plugin.logging.Log;

public class Logger {

  private static Log log;

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

  public static void setLog(Log log) {
    Logger.log = log;
  }
}
