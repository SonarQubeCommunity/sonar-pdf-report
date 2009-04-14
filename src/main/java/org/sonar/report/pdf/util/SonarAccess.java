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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SonarAccess {
  
  final Logger logger = LoggerFactory.getLogger(SonarAccess.class);
  
  // TODO: provide POST method
  public SonarAccess() {
    
  }
  
  public Document getUrlAsDocument(String url) throws HttpException, IOException, DocumentException {
    HttpClient client = new HttpClient();
    HttpMethod method = new GetMethod(url);
    int status = 0;

    logger.debug("Accessing Sonar: {}", url);
    status = client.executeMethod(method);
    if (!(status == HttpStatus.SC_OK)) {
      logger.error("Can´t access to Sonar or project doesn't exist on Sonar instance. HTTP KO to {}", url);
      throw new IOException("Can´t access to Sonar or project doesn't exist on Sonar instance.");
    }
    logger.debug("Received response.", url);
    SAXReader reader = new SAXReader();
    return reader.read(method.getResponseBodyAsStream());
  }
}
