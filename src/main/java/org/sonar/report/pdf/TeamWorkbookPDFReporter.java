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