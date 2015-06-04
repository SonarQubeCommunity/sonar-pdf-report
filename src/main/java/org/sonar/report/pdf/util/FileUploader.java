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

import java.io.File;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.report.pdf.batch.PDFPostJob;

public class FileUploader {

  private static final Logger LOG = LoggerFactory.getLogger(PDFPostJob.class);

  public static void upload(final File file, final String url, String username, String password) {
    PostMethod filePost = new PostMethod(url);

    try {
      LOG.info("Uploading PDF to server...");

      Part[] parts = { new FilePart("upload", file) };

      filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

      HttpClient client = new HttpClient();
      if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
        client.getParams().setAuthenticationPreemptive(true);
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        client.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), credentials);
      }
      client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
      int status = client.executeMethod(filePost);
      if (status == HttpStatus.SC_OK) {
        LOG.info("PDF uploaded.");
      } else {
        LOG.error("Something went wrong storing the PDF at server side. Status: " + status);
      }
    } catch (Exception ex) {
      LOG.error("Something went wrong storing the PDF at server side", ex);
      ex.printStackTrace();
    } finally {
      filePost.releaseConnection();
    }
  }

}
