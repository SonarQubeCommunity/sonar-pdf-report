package org.sonar.report.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.httpclient.HttpException;
import org.sonar.report.pdf.entity.ComplexityDistribution;
import org.sonar.report.pdf.entity.Measures;
import org.sonar.report.pdf.entity.Project;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This is the superclass of concrete reporters. It provides the access to Sonar data (project, measures, graphics) and
 * report config data.
 * 
 * The concrete reporter class will provide: sonar base URL, logo (it will be used in yhe PDF document), the project key
 * and the implementation of printPdfBody method.
 */
public abstract class PDFReporter {

  private Properties textProperties;
  private Properties configProperties;
  private Project project = null;

  public ByteArrayOutputStream getReport() throws DocumentException, IOException, org.dom4j.DocumentException {
    // Creation of documents
    Document mainDocument = new Document(PageSize.A4, 50, 50, 110, 50);
    Toc tocDocument = new Toc();
    Document frontPageDocument = new Document(PageSize.A4, 50, 50, 110, 50);
    ByteArrayOutputStream mainDocumentBaos = new ByteArrayOutputStream();
    ByteArrayOutputStream frontPageDocumentBaos = new ByteArrayOutputStream();
    PdfWriter mainDocumentWriter = PdfWriter.getInstance(mainDocument, mainDocumentBaos);
    PdfWriter frontPageDocumentWriter = PdfWriter.getInstance(frontPageDocument, frontPageDocumentBaos);
    
    // Events for TOC, header and pages numbers
    Events events = new Events(tocDocument, new Header(this.getLogo(), this.getProject()));
    mainDocumentWriter.setPageEvent(events);
    
    mainDocument.open();
    tocDocument.getTocDocument().open();
    frontPageDocument.open();
    
    printFrontPage(frontPageDocument, frontPageDocumentWriter);
    printTocTitle(tocDocument);
    printPdfBody(mainDocument);
    mainDocument.close();
    tocDocument.getTocDocument().close();
    frontPageDocument.close();

    // Get Readers
    PdfReader mainDocumentReader = new PdfReader(mainDocumentBaos.toByteArray());
    PdfReader tocDocumentReader = new PdfReader(tocDocument.getTocOutputStream().toByteArray());
    PdfReader frontPageDocumentReader = new PdfReader(frontPageDocumentBaos.toByteArray());
    
    // New document
    Document documentWithToc = new Document(tocDocumentReader.getPageSizeWithRotation(1));
    ByteArrayOutputStream finalBaos = new ByteArrayOutputStream();
    PdfCopy copy = new PdfCopy(documentWithToc, finalBaos);
    
    documentWithToc.open();
    copy.addPage(copy.getImportedPage(frontPageDocumentReader, 1));
    for(int i = 1; i <= tocDocumentReader.getNumberOfPages(); i++) {
      copy.addPage(copy.getImportedPage(tocDocumentReader, i));
    }
    for(int i = 1; i <= mainDocumentReader.getNumberOfPages(); i++) {
      copy.addPage(copy.getImportedPage(mainDocumentReader, i));
    }
    documentWithToc.close();
    
    // Return the final document (with TOC)
    return finalBaos;
  }

  public Project getProject() throws HttpException, IOException, org.dom4j.DocumentException {
    if (project == null) {
      project = Project.parse(getSonarUrl() + "/api/projects/" + getProjectKey()
          + "?includelinks=true&format=xml&includechildren=true");

      project.setMeasures(getMeasures(project.getKey()));
      Iterator<Project> it = project.getSubprojects().iterator();
      while (it.hasNext()) {
        Project subproject = it.next();
        subproject.setMeasures(getMeasures(subproject.getKey()));
      }
    }
    return project;
  }

  private Measures getMeasures(String projectKey) throws HttpException, IOException, org.dom4j.DocumentException {
    Measures measures = Measures.parse(getSonarUrl() + "/api/projects/" + projectKey
        + "/measures?includeparams=true&format=xml");
    return measures;
  }

  // TODO: add xradar graphic (need ISO9126 measures for this, SONAR-563).
  public void getRadarGraphic() {

    // RadarGraphic graphics = new RadarGraphic();
    // Image imageRadar = graphics.getGraphic(null, null);

  }

  public Image getCCNDistribution(Project project) {
    String data = project.getMeasureValue("ccn_classes_count_distribution");
    ComplexityDistribution ccnDist = new ComplexityDistribution(data, getSonarUrl());
    return ccnDist.getGraphic();
  }

  public String getTextProperty(String key) throws IOException {
    if (textProperties == null) {
      textProperties = new Properties();
      URL resource = this.getClass().getClassLoader().getResource("report-texts-en.properties");
      textProperties.load(new FileInputStream(resource.getFile()));
    }
    return textProperties.getProperty(key);
  }

  public String getConfigProperty(String key) throws IOException {
    if (configProperties == null) {
      configProperties = new Properties();
      URL resource = this.getClass().getClassLoader().getResource("report.properties");
      configProperties.load(new FileInputStream(resource.getFile()));
    }
    return configProperties.getProperty(key);
  }

  protected abstract String getSonarUrl();

  protected abstract void printPdfBody(Document document) throws DocumentException, IOException,
      org.dom4j.DocumentException;
  
  protected abstract void printTocTitle(Toc tocDocument) throws DocumentException, IOException;
  
  protected abstract URL getLogo();

  protected abstract String getProjectKey();

  protected abstract void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter) throws org.dom4j.DocumentException;

}
