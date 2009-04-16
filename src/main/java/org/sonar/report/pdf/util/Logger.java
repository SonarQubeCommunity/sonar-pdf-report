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

  public static void setLog(Log log) {
    Logger.log = log;
  }
}
