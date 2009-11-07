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

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class SonarAccess {
  
  private String sonarUrl;
  
  
  // TODO: provide POST method
  public SonarAccess(String sonarUrl) {
    this.sonarUrl = sonarUrl;
  }
  
  public Document getUrlAsDocument(String urlPath) throws HttpException, IOException, DocumentException {
    HttpClient client = new HttpClient();
    HttpMethod method = new GetMethod(this.sonarUrl + urlPath);
    int status = 0;

    Logger.debug("HTTP Request: " + this.sonarUrl + urlPath);
    status = client.executeMethod(method);
    if (!(status == HttpStatus.SC_OK)) {
      Logger.error("Can´t access to Sonar or project doesn't exist on Sonar instance. HTTP KO to " + this.sonarUrl + urlPath);
      throw new IOException("Can´t access to Sonar or project doesn't exist on Sonar instance.");
    }
    Logger.debug("Received response.");
    SAXReader reader = new SAXReader();
    return reader.read(method.getResponseBodyAsStream());
  }
}
