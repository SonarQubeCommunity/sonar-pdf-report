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
import org.sonar.report.pdf.mojo.SonarPDFMojo;

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

    Logger.debug("Accessing Sonar: " + this.sonarUrl + urlPath);
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
