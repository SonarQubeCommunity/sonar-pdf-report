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

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.sonar.report.pdf.entity.exception.ReportException;

public class SonarAccess {

  /**
   * Sonar URL (i.e. http://localhost:9000/sonar).
   */
  private String sonarUrl;

  /**
   * Username for access Sonar WS API. Null for no authentication.
   */
  private String username;

  /**
   * Password for access Sonar WS API. Only used if username != null.
   */
  private String password;

  private String host;

  private int port;

  // TODO: provide POST method
  public SonarAccess(String sonarUrl, String username, String password) throws ReportException {
    if (!sonarUrl.endsWith("/")) {
      this.sonarUrl = sonarUrl;
    } else {
      this.sonarUrl = sonarUrl.substring(0, sonarUrl.length() - 1);
    }
    this.username = username;
    this.password = password;
    if (sonarUrl.startsWith("http://")) {
      String withoutProtocol = sonarUrl.substring(7);
      if (withoutProtocol.contains(":")) {
        this.host = withoutProtocol.substring(0, withoutProtocol.indexOf(':'));
      } else if (withoutProtocol.contains("/")) {
        this.host = withoutProtocol.substring(0, withoutProtocol.indexOf('/'));
      } else {
        this.host = withoutProtocol;
      }
      if (withoutProtocol.contains(":")) {
        if (withoutProtocol.contains("/")) {
          this.port = Integer.valueOf(withoutProtocol.substring(withoutProtocol.indexOf(':') + 1, withoutProtocol
              .indexOf('/')));
        } else {
          this.port = Integer.valueOf(withoutProtocol.substring(withoutProtocol.indexOf(':') + 1));
        }
      } else {
        this.port = 80;
      }
    } else if (sonarUrl.startsWith("https://")) {
      throw new ReportException("SSL not supported yet: " + sonarUrl);
    } else {
      throw new ReportException("Unknown URL format: " + sonarUrl + " (forgot http:// before host?)");
    }
  }

  public Document getUrlAsDocument(String urlPath) throws HttpException, IOException, DocumentException {
    HttpClient client = new HttpClient();
    HttpMethod method = new GetMethod(this.sonarUrl + urlPath);
    int status = 0;

    Logger.debug("HTTP Request: " + this.sonarUrl + urlPath);
    if (this.username != null) {
      Logger.debug("Setting authentication with username: " + this.username);
      client.getParams().setAuthenticationPreemptive(true);
      client.getState().setCredentials(new AuthScope(this.host, this.port),
          new UsernamePasswordCredentials(this.username, this.password));
      method.setDoAuthentication(true);
    }
    status = client.executeMethod(method);
    if (!(status == HttpStatus.SC_OK)) {
      Logger.error("Can´t access to Sonar or project doesn't exist on Sonar instance. HTTP KO to " + this.sonarUrl
          + urlPath);
      throw new IOException("Can´t access to Sonar or project doesn't exist on Sonar instance.");
    }
    Logger.debug("Received response.");
    SAXReader reader = new SAXReader();
    return reader.read(method.getResponseBodyAsStream());
  }
}
