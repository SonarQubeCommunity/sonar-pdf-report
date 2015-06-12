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

package org.sonar.report.pdf.batch;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.CheckProject;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.report.pdf.util.FileUploader;

public class PDFPostJob implements PostJob, CheckProject {

  private static final Logger LOG = LoggerFactory.getLogger(PDFPostJob.class);

  public static final String SKIP_PDF_KEY = "sonar.pdf.skip";
  public static final boolean SKIP_PDF_DEFAULT_VALUE = false;

  public static final String REPORT_TYPE = "report.type";
  public static final String REPORT_TYPE_DEFAULT_VALUE = "workbook";

  public static final String USERNAME = "sonar.pdf.username";
  public static final String USERNAME_DEFAULT_VALUE = "";

  public static final String PASSWORD = "sonar.pdf.password";
  public static final String PASSWORD_DEFAULT_VALUE = "";

  public static final String SONAR_HOST_URL = "sonar.host.url";
  public static final String SONAR_HOST_URL_DEFAULT_VALUE = "http://localhost:9000";

  private final Settings settings;
  private final FileSystem fs;
  
  public PDFPostJob(Settings settings, FileSystem fs) {
    this.settings = settings;
    this.fs = fs;
  }

  @Override
  public boolean shouldExecuteOnProject(final Project project) {
    return settings.hasKey(SKIP_PDF_KEY) ? !settings.getBoolean(SKIP_PDF_KEY) : !SKIP_PDF_DEFAULT_VALUE;
  }

  @Override
  public void executeOn(final Project project, final SensorContext context) {
    LOG.info("Executing decorator: PDF Report");
    String sonarHostUrl = settings.hasKey(SONAR_HOST_URL) ? settings.getString(SONAR_HOST_URL) : SONAR_HOST_URL_DEFAULT_VALUE;
    String username = settings.hasKey(USERNAME) ? settings.getString(USERNAME) : USERNAME_DEFAULT_VALUE;
    String password = settings.hasKey(PASSWORD) ? settings.getString(PASSWORD) : PASSWORD_DEFAULT_VALUE;
    String reportType = settings.hasKey(REPORT_TYPE) ? settings.getString(REPORT_TYPE) : REPORT_TYPE_DEFAULT_VALUE;
    PDFGenerator generator = new PDFGenerator(project, fs, sonarHostUrl, username, password, reportType);

    generator.execute();

    String path = fs.workDir().getAbsolutePath() + "/" + project.getEffectiveKey().replace(':', '-') + ".pdf";

    File pdf = new File(path);
    if (pdf.exists()) {
      FileUploader.upload(pdf, sonarHostUrl + "/pdf_report/store", username, password);
    } else {
      LOG.error("PDF file not found in local filesystem. Report could not be sent to server.");
    }
  }

}
