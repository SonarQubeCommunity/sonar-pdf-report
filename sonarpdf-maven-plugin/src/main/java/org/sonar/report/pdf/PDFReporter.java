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

package org.sonar.report.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.httpclient.HttpException;
import org.sonar.report.pdf.builder.ComplexityDistributionBuilder;
import org.sonar.report.pdf.builder.ProjectBuilder;
import org.sonar.report.pdf.entity.ComplexityDistribution;
import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Credentials;
import org.sonar.report.pdf.util.Logger;
import org.sonar.wsclient.Sonar;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This is the superclass of concrete reporters. It provides the access to Sonar
 * data (project, measures, graphics) and report config data.
 * 
 * The concrete reporter class will provide: sonar base URL, logo (it will be
 * used in yhe PDF document), the project key and the implementation of
 * printPdfBody method.
 */
public abstract class PDFReporter {

  private Credentials credentials;

  private Project project = null;

  public PDFReporter(Credentials credentials) {
    this.credentials = credentials;
  }

  public ByteArrayOutputStream getReport() throws DocumentException,
      IOException, ReportException {
    // Creation of documents
    Document mainDocument = new Document(PageSize.A4, 50, 50, 110, 50);
    Toc tocDocument = new Toc();
    Document frontPageDocument = new Document(PageSize.A4, 50, 50, 110, 50);
    ByteArrayOutputStream mainDocumentBaos = new ByteArrayOutputStream();
    ByteArrayOutputStream frontPageDocumentBaos = new ByteArrayOutputStream();
    PdfWriter mainDocumentWriter = PdfWriter.getInstance(mainDocument,
        mainDocumentBaos);
    PdfWriter frontPageDocumentWriter = PdfWriter.getInstance(
        frontPageDocument, frontPageDocumentBaos);

    // Events for TOC, header and pages numbers
    Events events = new Events(tocDocument, new Header(this.getLogo(),
        this.getProject()));
    mainDocumentWriter.setPageEvent(events);

    mainDocument.open();
    tocDocument.getTocDocument().open();
    frontPageDocument.open();

    Logger.info("Generating PDF report...");
    printFrontPage(frontPageDocument, frontPageDocumentWriter);
    printTocTitle(tocDocument);
    printPdfBody(mainDocument);
    mainDocument.close();
    tocDocument.getTocDocument().close();
    frontPageDocument.close();

    // Get Readers
    PdfReader mainDocumentReader = new PdfReader(mainDocumentBaos.toByteArray());
    PdfReader tocDocumentReader = new PdfReader(tocDocument
        .getTocOutputStream().toByteArray());
    PdfReader frontPageDocumentReader = new PdfReader(
        frontPageDocumentBaos.toByteArray());

    // New document
    Document documentWithToc = new Document(
        tocDocumentReader.getPageSizeWithRotation(1));
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

  public Project getProject() throws HttpException, IOException,
      ReportException {
    if (project == null) {
      Sonar sonar = Sonar.create(credentials.getUrl(),
          credentials.getUsername(), credentials.getPassword());
      ProjectBuilder projectBuilder = ProjectBuilder.getInstance(credentials,
          sonar, this);
      project = projectBuilder.initializeProject(getProjectKey());
    }
    return project;
  }

  public Image getCCNDistribution(Project project) {
    String data;
    if (project.getMeasure("class_complexity_distribution").getTextValue() != null) {
      data = project.getMeasure("class_complexity_distribution").getTextValue();
    } else {
      data = "N/A";
    }
    ComplexityDistributionBuilder complexityDistributionBuilder = ComplexityDistributionBuilder
        .getInstance(credentials.getUrl());
    ComplexityDistribution ccnDist = new ComplexityDistribution(data);
    return complexityDistributionBuilder.getGraphic(ccnDist);
  }

  public String getTextProperty(String key) {
    return getLangProperties().getProperty(key);
  }

  public String getConfigProperty(String key) {
    return getReportProperties().getProperty(key);
  }

  public Image getTendencyImage(int tendencyQualitative,
      int tendencyCuantitative) {
    // tendency parameters are t_qual and t_quant tags returned by
    // webservices api
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
      tendencyImage = Image.getInstance(this.getClass().getResource(
          "/tendency/" + iconName));
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tendencyImage;
  }

  protected abstract void printPdfBody(Document document)
      throws DocumentException, IOException, ReportException;

  protected abstract void printTocTitle(Toc tocDocument)
      throws DocumentException, IOException;

  protected abstract URL getLogo();

  protected abstract String getProjectKey();

  protected abstract void printFrontPage(Document frontPageDocument,
      PdfWriter frontPageWriter) throws ReportException;

  protected abstract Properties getReportProperties();

  protected abstract Properties getLangProperties();

  public abstract String getReportType();

}
