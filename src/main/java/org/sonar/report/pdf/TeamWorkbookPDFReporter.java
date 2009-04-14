package org.sonar.report.pdf;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

public class TeamWorkbookPDFReporter extends PDFReporter {

  @Override
  protected URL getLogo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getProjectKey() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getSonarUrl() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter)
      throws org.dom4j.DocumentException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void printTocTitle(Toc tocDocument) throws DocumentException, IOException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected Properties getLangProperties() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Properties getReportProperties() {
    // TODO Auto-generated method stub
    return null;
  }

}