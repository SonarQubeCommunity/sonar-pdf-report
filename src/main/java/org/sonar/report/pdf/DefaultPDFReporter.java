/*
 * SonarQube PDF Report
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

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.report.pdf.entity.Measures;
import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Credentials;

import com.lowagie.text.BadElementException;
import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Concrete PDFReporter. Implements printPdfBody method. This will be the way to
 * extend PDFReport.
 */
public class DefaultPDFReporter extends PDFReporter {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultPDFReporter.class);

  private static final String REPORT_TYPE_WORKBOOK = "workbook";

  private URL logo;
  private String projectKey;
  private Properties configProperties;
  private Properties langProperties;

  private final static int indentation = 18;
  private final static int tablePaddingBottom = 5;

  private Document document;

  public DefaultPDFReporter(final Credentials credentials, final URL logo,
      final String projectKey, final Properties configProperties, final Properties langProperties) {
    super(credentials);
    this.logo = logo;
    this.projectKey = projectKey;
    this.configProperties = configProperties;
    this.langProperties = langProperties;

  }

  @Override
  protected void printPdfBody(final Document document) throws DocumentException,
      IOException, ReportException {
    this.document = document;
    // Chapter 1
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(
        getTextProperty("main.chapter1.title"), Style.CHAPTER_FONT));
    chapter1.add(new Paragraph(getTextProperty("main.chapter1.intro"),
        Style.NORMAL_FONT));
    // Section 1.1
    Section section11 = chapter1.addSection(new Paragraph(
        getTextProperty("main.chapter1.subtitle1"), Style.TITLE_FONT));
    Project project = super.getProject();
    printDashboard(project, section11);
    // Section 1.2
    Section section12 = chapter1.addSection(new Paragraph(
        getTextProperty("main.chapter1.subtitle2"), Style.TITLE_FONT));
    printProjectInfo(project, section12);

    document.add(chapter1);

    // Subprojects Chapters (2, 3, 4, ...)
    Iterator<Project> it = project.getSubprojects().iterator();
    while (it.hasNext()) {
      Project subproject = it.next();
      ChapterAutoNumber subprojectChapter = new ChapterAutoNumber(
          new Paragraph(getTextProperty("general.module") + ": "
              + subproject.getName(), Style.TITLE_FONT));
      Section sectionX1 = subprojectChapter.addSection(new Paragraph(
          getTextProperty("main.chapter2.subtitle2X1"), Style.TITLE_FONT));
      printDashboard(subproject, sectionX1);
      Section sectionX2 = subprojectChapter.addSection(new Paragraph(
          getTextProperty("main.chapter2.subtitle2X2"), Style.TITLE_FONT));
      printProjectInfo(subproject, sectionX2);
      document.add(subprojectChapter);
    }
  }

  private void printDashboard(final Project project, final Section section)
      throws DocumentException {
    PdfPTable dashboard = new PdfPTable(3);
    dashboard.getDefaultCell().setBorderColor(Color.WHITE);
    Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK);
    Font dataFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
    Font dataFont2 = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, new Color(100,
        150, 190));

    PdfPTable linesOfCode = new PdfPTable(1);
    linesOfCode.getDefaultCell().setBorderColor(Color.WHITE);
    linesOfCode.addCell(new Phrase(getTextProperty("general.lines_of_code"),
        titleFont));
    linesOfCode.addCell(new Phrase(project.getMeasure("ncss").getFormatValue(),
        dataFont));
    linesOfCode.addCell(new Phrase(project.getMeasure("packages_count")
        .getFormatValue() + " packages", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("classes_count")
        .getFormatValue() + " classes", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("functions_count")
        .getFormatValue() + " methods", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("duplicated_lines_ratio")
        .getFormatValue() + " duplicated lines", dataFont2));

    PdfPTable comments = new PdfPTable(1);
    comments.getDefaultCell().setBorderColor(Color.WHITE);
    comments
        .addCell(new Phrase(getTextProperty("general.comments"), titleFont));
    comments.addCell(new Phrase(project.getMeasure("comment_ratio")
        .getFormatValue(), dataFont));
    comments.addCell(new Phrase(project.getMeasure("comment_lines")
        .getFormatValue() + " comment lines", dataFont2));

    PdfPTable codeCoverage = new PdfPTable(1);
    codeCoverage.getDefaultCell().setBorderColor(Color.WHITE);
    codeCoverage.addCell(new Phrase(getTextProperty("general.test_count"),
        titleFont));
    codeCoverage.addCell(new Phrase(project.getMeasure("test_count")
        .getFormatValue(), dataFont));
    codeCoverage.addCell(new Phrase(project.getMeasure(
        "test_success_percentage").getFormatValue()
        + " success", dataFont2));
    codeCoverage.addCell(new Phrase(project.getMeasure("code_coverage")
        .getFormatValue() + " coverage", dataFont2));

    PdfPTable complexity = new PdfPTable(1);
    complexity.getDefaultCell().setBorderColor(Color.WHITE);
    complexity.addCell(new Phrase(getTextProperty("general.complexity"),
        titleFont));
    complexity.addCell(new Phrase(project.getMeasure("ccn_function")
        .getFormatValue(), dataFont));
    complexity.addCell(new Phrase(project.getMeasure("ccn_class")
        .getFormatValue() + " /class", dataFont2));
    complexity.addCell(new Phrase(project.getMeasure("ccn").getFormatValue()
        + " decision points", dataFont2));

    PdfPTable rulesCompliance = new PdfPTable(1);
    rulesCompliance.getDefaultCell().setBorderColor(Color.WHITE);
    rulesCompliance.addCell(new Phrase(
        getTextProperty("general.rules_compliance"), titleFont));
    rulesCompliance.addCell(new Phrase(project.getMeasure("rules_compliance")
        .getFormatValue(), dataFont));

    PdfPTable violations = new PdfPTable(1);
    violations.getDefaultCell().setBorderColor(Color.WHITE);
    violations.addCell(new Phrase(getTextProperty("general.violations"),
        titleFont));
    violations.addCell(new Phrase(project.getMeasure("rules_violations")
        .getFormatValue(), dataFont));

    dashboard.addCell(linesOfCode);
    dashboard.addCell(comments);
    dashboard.addCell(codeCoverage);
    dashboard.addCell(complexity);
    dashboard.addCell(rulesCompliance);
    dashboard.addCell(violations);
    dashboard.setSpacingBefore(8);
    section.add(dashboard);
    Image ccnDistGraph = getCCNDistribution(project);
    if (ccnDistGraph != null) {
      section.add(ccnDistGraph);
      Paragraph imageFoot = new Paragraph(
          getTextProperty("metrics.ccn_classes_count_distribution"),
          Style.FOOT_FONT);
      imageFoot.setAlignment(Paragraph.ALIGN_CENTER);
      section.add(imageFoot);
    }
  }

  private void printMeasures(final Measures measures, final Section section) {

    PdfPTable versioningTable = new PdfPTable(2);
    formatTable(versioningTable);
    versioningTable.getDefaultCell().setColspan(2);
    versioningTable.addCell(new Phrase(super
        .getTextProperty("general.versioning_information"), Style.TITLE_FONT));
    versioningTable.addCell(measures.getVersion());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss");
    versioningTable.addCell(df.format(measures.getDate()));

    PdfPTable measuresTable = new PdfPTable(2);
    formatTable(measuresTable);

    Iterator<String> it = measures.getMeasuresKeys().iterator();
    measuresTable.addCell(new Phrase(super.getTextProperty("general.metric"),
        Style.TITLE_FONT));
    measuresTable.addCell(new Phrase(super.getTextProperty("general.value"),
        Style.TITLE_FONT));
    boolean colorEnabled = true;
    while (it.hasNext()) {
      String measureKey = it.next();
      if (colorEnabled) {
        measuresTable.getDefaultCell().setGrayFill(0.9f);
        colorEnabled = false;
      } else {
        measuresTable.getDefaultCell().setGrayFill(1);
        colorEnabled = true;
      }
      if (!measureKey.equals("ccn_classes_count_distribution")
          && !measureKey.equals("ccn_classes_percent_distribution")) {
        measuresTable.getDefaultCell().setHorizontalAlignment(
            PdfCell.ALIGN_LEFT);
        measuresTable.addCell(super.getTextProperty("metrics." + measureKey));
        measuresTable.getDefaultCell().setHorizontalAlignment(
            PdfCell.ALIGN_CENTER);
        measuresTable.addCell(measures.getMeasure(measureKey).getFormatValue());
      }
    }
    measuresTable.setHeaderRows(1);
    section.add(versioningTable);
    section.add(measuresTable);

  }

  private void formatTable(final PdfPTable table) {
    Rectangle page = document.getPageSize();
    table.getDefaultCell().setVerticalAlignment(PdfCell.ALIGN_MIDDLE);
    table.getDefaultCell().setPaddingBottom(tablePaddingBottom);
    table.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_CENTER);
    table.setTotalWidth(page.getWidth() - document.leftMargin()
        - document.rightMargin());
    table.setSpacingBefore(20);
  }

  private void printProjectInfo(final Project project, final Section section)
      throws DocumentException {
    List data = new List();
    data.add(new ListItem(super.getTextProperty("general.name") + ": "
        + project.getName()));
    data.add(new ListItem(super.getTextProperty("general.description") + ": "
        + project.getDescription()));
    data.add(new ListItem(super.getTextProperty("general.modules") + ": "));

    List sublist = new List();
    if (project.getSubprojects().size() != 0) {
      Iterator<Project> it = project.getSubprojects().iterator();
      while (it.hasNext()) {
        sublist.add(new ListItem(it.next().getName()));
      }
    } else {
      sublist.add(new ListItem(super.getTextProperty("general.no_modules")));
    }

    sublist.setIndentationLeft(indentation);
    data.add(sublist);
    section.add(data);
    printMeasures(project.getMeasures(), section);
  }

  @Override
  protected URL getLogo() {
    return this.logo;
  }

  @Override
  protected String getProjectKey() {
    return this.projectKey;
  }

  @Override
  protected void printTocTitle(final Toc tocDocument) throws DocumentException {
    Paragraph tocTitle = new Paragraph(
        super.getTextProperty("main.table.of.contents"), Style.TOC_TITLE_FONT);
    tocTitle.setAlignment(Element.ALIGN_CENTER);
    tocDocument.getTocDocument().add(tocTitle);
    tocDocument.getTocDocument().add(Chunk.NEWLINE);
  }

  @Override
  protected void printFrontPage(final Document frontPageDocument,
      final PdfWriter frontPageWriter) throws ReportException {
    try {
      URL largeLogo;
      if (super.getConfigProperty("front.page.logo").startsWith("http://")) {
        largeLogo = new URL(super.getConfigProperty("front.page.logo"));
      } else {
        largeLogo = this.getClass().getClassLoader()
            .getResource(super.getConfigProperty("front.page.logo"));
      }
      Image logoImage = Image.getInstance(largeLogo);
      Rectangle pageSize = frontPageDocument.getPageSize();
      float positionX = pageSize.getWidth() / 2f - logoImage.getWidth() / 2f;
      logoImage.setAbsolutePosition(positionX,
          pageSize.getHeight() - logoImage.getHeight() - 100);
      frontPageDocument.add(logoImage);

      PdfPTable title = new PdfPTable(1);
      title.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      title.getDefaultCell().setBorder(Rectangle.NO_BORDER);

      String projectRow = super.getTextProperty("general.project") + ": "
          + super.getProject().getName();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String dateRow = df.format(super.getProject().getMeasures().getDate());
      String descriptionRow = super.getProject().getDescription();

      title.addCell(new Phrase(projectRow, Style.FRONTPAGE_FONT_1));
      title.addCell(new Phrase(descriptionRow, Style.FRONTPAGE_FONT_2));
      title.addCell(new Phrase(dateRow, Style.FRONTPAGE_FONT_3));
      title.setTotalWidth(pageSize.getWidth() - frontPageDocument.leftMargin()
          - frontPageDocument.rightMargin());
      title.writeSelectedRows(0, -1, frontPageDocument.leftMargin(),
          pageSize.getHeight() - logoImage.getHeight() - 150,
          frontPageWriter.getDirectContent());

    } catch (IOException e) {
      LOG.error("Can not generate front page", e);
    } catch (BadElementException e) {
      LOG.error("Can not generate front page", e);
    } catch (DocumentException e) {
      LOG.error("Can not generate front page", e);
    }
  }

  @Override
  protected Properties getLangProperties() {
    return langProperties;
  }

  @Override
  protected Properties getReportProperties() {
    return configProperties;
  }

  @Override
  public String getReportType() {
    return REPORT_TYPE_WORKBOOK;
  }
}
