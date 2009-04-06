package org.sonar.report.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Node;
import org.sonar.report.pdf.entity.ComplexityDistribution;
import org.sonar.report.pdf.entity.Measures;
import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.util.SonarAccess;

import com.lowagie.text.BadElementException;
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
  private String measuresKeys;

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
    for (int i = 1; i <= tocDocumentReader.getNumberOfPages(); i++) {
      copy.addPage(copy.getImportedPage(tocDocumentReader, i));
    }
    for (int i = 1; i <= mainDocumentReader.getNumberOfPages(); i++) {
      copy.addPage(copy.getImportedPage(mainDocumentReader, i));
    }
    documentWithToc.close();

    // Return the final document (with TOC)
    return finalBaos;
  }

  public Project getProject() throws HttpException, IOException, org.dom4j.DocumentException {
    if (project == null) {
      SonarAccess sonarAccess = new SonarAccess();
      project = new Project();
      project.initFromDocuments(sonarAccess.getUrlAsDocument(getSonarUrl() + "/api/resources?resource=" + getProjectKey() + "&depth=0&format=xml"), 
          sonarAccess.getUrlAsDocument(getSonarUrl() + "/api/resources?resource=" + getProjectKey() + "&depth=1&format=xml"));

      project.setMeasures(getMeasures(project.getKey()));
      project.setCategoriesViolationsFromDocuments(sonarAccess.getUrlAsDocument(getSonarUrl() + "/api/resources?resource=" + getProjectKey()
          + "&metrics=rules_violations&depth=0&filter_rules_cats=false&format=xml"), 
          sonarAccess.getUrlAsDocument(getSonarUrl() + "/api/resources?resource=" + getProjectKey()
              + "&metrics=rules_violations&depth=1&filter_rules_cats=false&format=xml"));
      project.setMostViolatedRulesFromDocuments(sonarAccess.getUrlAsDocument(getSonarUrl() + "/api/resources?resource=" + getProjectKey()
          + "&metrics=rules_violations&limit=5&filter_rules=false&filter_rules_cats=true&depth=0"),
          sonarAccess.getUrlAsDocument(getSonarUrl() + "/api/resources?resource=" + getProjectKey()
              + "&metrics=rules_violations&limit=5&filter_rules=false&filter_rules_cats=true&depth=1"));
      Iterator<Project> it = project.getSubprojects().iterator();
      while (it.hasNext()) {
        Project subproject = it.next();
        subproject.setMeasures(getMeasures(subproject.getKey()));
      }
    }
    return project;
  }

  private Measures getMeasures(String projectKey) throws HttpException, IOException, org.dom4j.DocumentException {
    if (measuresKeys == null) {
      measuresKeys = getAllMetricKeys();
    }
    Measures measures = new Measures();
    SonarAccess sonarAccess = new SonarAccess();
    String urlAllMesaures = getSonarUrl() + "/api/resources?resource=" + projectKey + "&depth=0&format=xml&includetrends=true"
          + "&metrics=" + measuresKeys;
    measures.addAllMeasuresFromDocument(sonarAccess.getUrlAsDocument(urlAllMesaures));
    return measures;
  }

  public String getAllMetricKeys() throws HttpException, IOException, org.dom4j.DocumentException {
    String urlAllMetrics = getSonarUrl() + "/api/metrics?format=xml";
    SonarAccess sonarAccess = new SonarAccess();
    org.dom4j.Document allMetricsDocument = sonarAccess.getUrlAsDocument(urlAllMetrics);
    List<Node> allMetricKeysNodes = allMetricsDocument.selectNodes("//metrics/metric/key");
    String allMetricKeys= ""; 
    Iterator<Node> it = allMetricKeysNodes.iterator();
    allMetricKeys += it.next().getText();
    while(it.hasNext()) {
      allMetricKeys += "," + it.next().getText();
    }
    return allMetricKeys;
  }

  // TODO: add xradar graphic (need ISO9126 measures for this, SONAR-563).
  public void getRadarGraphic() {

    // RadarGraphic graphics = new RadarGraphic();
    // Image imageRadar = graphics.getGraphic(null, null);

  }

  public Image getCCNDistribution(Project project) {
    String data;
    if (project.getMeasure("ccn_classes_count_distribution").getTextValue() != null) {
      data = project.getMeasure("ccn_classes_count_distribution").getTextValue();
    } else {
      data = "N/A";
    }
    ComplexityDistribution ccnDist = new ComplexityDistribution(data, getSonarUrl());
    return ccnDist.getGraphic();
  }

  public String getTextProperty(String key) {
    if (textProperties == null) {
      textProperties = new Properties();
      URL resource = this.getClass().getClassLoader().getResource("report-texts-en.properties");
      try {
        textProperties.load(new FileInputStream(resource.getFile()));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return textProperties.getProperty(key);
  }

  public String getConfigProperty(String key) {
    if (configProperties == null) {
      configProperties = new Properties();
      URL resource = this.getClass().getClassLoader().getResource("report.properties");
      try {
        configProperties.load(new FileInputStream(resource.getFile()));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return configProperties.getProperty(key);
  }

  public Image getTendencyImage(int tendencyQualitative, int tendencyCuantitative) {
    // tendency parameters are t_qual and t_quant tags returned by webservices api
    String baseUrl = getConfigProperty("sonar.base.url") + "/images/tendency/";
    String iconName;
    if (tendencyQualitative == 0) {
      switch (tendencyCuantitative) {
      case -2:
        iconName = "-2-black.png";
        break;
      case -1:
        iconName = "-1-black.png";
        break;
      case 1:
        iconName = "1-black.png";
        break;
      case 2:
        iconName = "2-black.png";
        break;
      default:
        iconName = "none.png";
      }
    } else {
      switch (tendencyQualitative) {
      case -2:
        iconName = "-2-red.png";
        break;
      case -1:
        iconName = "-1-red.png";
        break;
      case 1:
        iconName = "1-green.png";
        break;
      case 2:
        iconName = "2-green.png";
        break;
      default:
        iconName = "none.png";
      }
    }
    Image tendencyImage = null;
    try {
      tendencyImage = Image.getInstance(baseUrl + iconName);
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tendencyImage;
  }

  protected abstract String getSonarUrl();

  protected abstract void printPdfBody(Document document) throws DocumentException, IOException,
      org.dom4j.DocumentException;

  protected abstract void printTocTitle(Toc tocDocument) throws DocumentException, IOException;

  protected abstract URL getLogo();

  protected abstract String getProjectKey();

  protected abstract void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter)
      throws org.dom4j.DocumentException;

}
