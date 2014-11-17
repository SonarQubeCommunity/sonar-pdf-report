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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.report.pdf.plugin.ReportDataMetric;

public class PDFStoreDecorator implements Decorator {

  private static final Logger LOG = LoggerFactory.getLogger(PDFStoreDecorator.class);

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
  
  public static final String SONAR_BRANCH = "sonar.branch";
  public static final String SONAR_BRANCH_DEFAULT_VALUE = null;

  public boolean shouldExecuteOnProject(final Project project) {
    return !project.getConfiguration().getBoolean(SKIP_PDF_KEY, SKIP_PDF_DEFAULT_VALUE);
  }

  public void decorate(final Resource resource, final DecoratorContext context) {
    if (Qualifiers.PROJECT.equals(resource.getQualifier())) {
      LOG.info("Executing decorator: PDF Report");
      Project project = context.getProject();
      String sonarHostUrl = project.getConfiguration().getString(SONAR_HOST_URL, SONAR_HOST_URL_DEFAULT_VALUE);
      String username = project.getConfiguration().getString(USERNAME, USERNAME_DEFAULT_VALUE);
      String password = project.getConfiguration().getString(PASSWORD, PASSWORD_DEFAULT_VALUE);
      String branch = project.getConfiguration().getString(SONAR_BRANCH, SONAR_BRANCH_DEFAULT_VALUE);
      String reportType = project.getConfiguration().getString(REPORT_TYPE, REPORT_TYPE_DEFAULT_VALUE);
      PDFGenerator generator = new PDFGenerator(project, sonarHostUrl, username, password, branch, reportType);

      generator.execute();
      String path = project.getFileSystem().getSonarWorkingDirectory().getAbsolutePath() + "/"
          + project.getEffectiveKey().replace(':', '-') + ".pdf";

      Measure measure = new Measure(ReportDataMetric.PDF_DATA);
      File[] targetFiles = project.getFileSystem().getBuildDir().listFiles();
      int i = 0;
      File pdf = null;
      while (i < targetFiles.length) {
        if (targetFiles[i].getName().equals(project.getArtifactId() + ".pdf")) {
          pdf = targetFiles[i];
          break;
        }
        i++;
      }
      try {
        LOG.debug("Storing PDF data in DB");
        byte[] encoded = Base64.encodeBase64(loadFile(pdf));
        String data = new String(encoded);
        measure.setData(data);
        measure.setPersistenceMode(PersistenceMode.DATABASE);
        context.saveMeasure(measure);
        LOG.debug("PDF data stored in DB as measure");
      } catch (FileNotFoundException e) {
        LOG.error("Can not read PDF file", e);
      } catch (IOException e) {
        LOG.error("Can not read/write PDF file", e);
      }
    }
  }

  private void copy(final InputStream in, final OutputStream out) throws IOException {
    byte[] barr = new byte[1024];
    while (true) {
      int r = in.read(barr);
      if (r <= 0) {
        break;
      }
      out.write(barr, 0, r);
    }
  }

  private byte[] loadFile(final File file) throws IOException {
    InputStream in = new FileInputStream(file);
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      copy(in, buffer);
      return buffer.toByteArray();
    } finally {
      in.close();
    }
  }

}
