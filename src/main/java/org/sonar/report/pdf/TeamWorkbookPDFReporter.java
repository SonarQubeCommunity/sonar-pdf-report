package org.sonar.report.pdf;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.util.MetricKeys;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
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

}