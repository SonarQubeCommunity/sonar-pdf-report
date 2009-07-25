/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 GMV-SGI
 * mailto:contact AT sonarsource DOT com
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

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.sonar.report.pdf.entity.FileInfo;
import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.entity.Rule;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.MetricKeys;

import com.lowagie.text.BadElementException;
import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ExecutivePDFReporter extends PDFReporter {

  private URL logo;
  private String projectKey;
  private String sonarUrl;
  private Properties configProperties;
  private Properties langProperties;

  public ExecutivePDFReporter(URL logo, String projectKey, String sonarUrl, Properties configProperties,
      Properties langProperties) {
    this.logo = logo;
    this.projectKey = projectKey;
    this.sonarUrl = sonarUrl;
    this.configProperties = configProperties;
    this.langProperties = langProperties;
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
  protected String getSonarUrl() {
    return this.sonarUrl;
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
  protected void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter)
      throws org.dom4j.DocumentException, ReportException {
    try {
      URL largeLogo;
      if (super.getConfigProperty("front.page.logo").startsWith("http://")) {
        largeLogo = new URL(super.getConfigProperty("front.page.logo"));
      } else {
        largeLogo = this.getClass().getClassLoader().getResource(super.getConfigProperty("front.page.logo"));
      }
      Image logoImage = Image.getInstance(largeLogo);
      logoImage.scaleAbsolute(360, 200);
      Rectangle pageSize = frontPageDocument.getPageSize();
      logoImage.setAbsolutePosition(Style.FRONTPAGE_LOGO_POSITION_X, Style.FRONTPAGE_LOGO_POSITION_Y);
      frontPageDocument.add(logoImage);

      PdfPTable title = new PdfPTable(1);
      title.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      title.getDefaultCell().setBorder(Rectangle.NO_BORDER);

      String projectRow = super.getProject().getName();
      String versionRow = super.getProject().getMeasures().getVersion();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String dateRow = df.format(super.getProject().getMeasures().getDate());
      String descriptionRow = super.getProject().getDescription();

      title.addCell(new Phrase(projectRow, Style.FRONTPAGE_FONT_1));
      title.addCell(new Phrase(versionRow, Style.FRONTPAGE_FONT_1));
      title.addCell(new Phrase(descriptionRow, Style.FRONTPAGE_FONT_2));
      title.addCell(new Phrase(dateRow, Style.FRONTPAGE_FONT_3));
      title.setTotalWidth(pageSize.getWidth() - frontPageDocument.leftMargin() - frontPageDocument.rightMargin());
      title.writeSelectedRows(0, -1, frontPageDocument.leftMargin(),
          Style.FRONTPAGE_LOGO_POSITION_Y - 150, frontPageWriter.getDirectContent());

    } catch (IOException e) {
      e.printStackTrace();
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException, ReportException {
    Project project = super.getProject();
    // Chapter 1: Report Overview (Parent project)
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(project.getName(), Style.CHAPTER_FONT));
    chapter1.add(new Paragraph(getTextProperty("main.text.misc.overview"), Style.NORMAL_FONT));
    Section section11 = chapter1.addSection(new Paragraph(getTextProperty("general.report_overview"), Style.TITLE_FONT));
    printDashboard(project, section11);
    Section section12 = chapter1.addSection(new Paragraph(getTextProperty("general.violations_analysis"),
        Style.TITLE_FONT));
    printRulesCategories(project, section12);
    printMostViolatedRules(project, section12);
    printMostViolatedFiles(project, section12);
    printMostComplexFiles(project, section12);
    printMostDuplicatedFiles(project, section12);
    document.add(chapter1);

    Iterator<Project> it = project.getSubprojects().iterator();
    while (it.hasNext()) {
      Project subproject = it.next();
      ChapterAutoNumber chapterN = new ChapterAutoNumber(new Paragraph(subproject.getName(), Style.CHAPTER_FONT));

      Section sectionN1 = chapterN
          .addSection(new Paragraph(getTextProperty("general.report_overview"), Style.TITLE_FONT));
      printDashboard(subproject, sectionN1);

      Section sectionN2 = chapterN.addSection(new Paragraph(getTextProperty("general.violations_analysis"),
          Style.TITLE_FONT));
      printRulesCategories(subproject, sectionN2);
      printMostViolatedRules(subproject, sectionN2);
      printMostViolatedFiles(subproject, sectionN2);
      printMostComplexFiles(subproject, sectionN2);
      printMostDuplicatedFiles(subproject, sectionN2);
      document.add(chapterN);
    }
  }

  protected void printDashboard(Project project, Section section) throws DocumentException {

    // Static Analysis
    Paragraph staticAnalysis = new Paragraph(getTextProperty("general.static_analysis"), Style.UNDERLINED_FONT);
    PdfPTable staticAnalysisTable = new PdfPTable(3);
    staticAnalysisTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable linesOfCode = new PdfPTable(1);
    Style.noBorderTable(linesOfCode);
    linesOfCode.addCell(new Phrase(getTextProperty("general.lines_of_code"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable linesOfCodeTendency = new PdfPTable(2);
    Style.noBorderTable(linesOfCodeTendency);
    linesOfCodeTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    linesOfCodeTendency.addCell(new Phrase(project.getMeasure(MetricKeys.NCLOC).getFormatValue(),
        Style.DASHBOARD_DATA_FONT));
    linesOfCodeTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.NCLOC).getQualitativeTendency(), project
        .getMeasure(MetricKeys.NCLOC).getQuantitativeTendency()));

    linesOfCode.addCell(linesOfCodeTendency);
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.PACKAGES).getFormatValue() + " packages",
        Style.DASHBOARD_DATA_FONT_2));
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.CLASSES).getFormatValue() + " classes",
        Style.DASHBOARD_DATA_FONT_2));
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.FUNCTIONS).getFormatValue() + " methods",
        Style.DASHBOARD_DATA_FONT_2));
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.DUPLICATED_LINES_DENSITY).getFormatValue()
        + " duplicated lines", Style.DASHBOARD_DATA_FONT_2));

    PdfPTable comments = new PdfPTable(1);
    Style.noBorderTable(comments);
    comments.addCell(new Phrase(getTextProperty("general.comments"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable commentsTendency = new PdfPTable(2);
    commentsTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    Style.noBorderTable(commentsTendency);
    commentsTendency.addCell(new Phrase(project.getMeasure(MetricKeys.COMMENT_LINES_DENSITY).getFormatValue(),
        Style.DASHBOARD_DATA_FONT));
    commentsTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.COMMENT_LINES_DENSITY).getQualitativeTendency(),
        project.getMeasure(MetricKeys.COMMENT_LINES_DENSITY).getQuantitativeTendency()));
    comments.addCell(commentsTendency);
    comments.addCell(new Phrase(project.getMeasure(MetricKeys.COMMENT_LINES).getFormatValue() + " comment lines",
        Style.DASHBOARD_DATA_FONT_2));

    PdfPTable complexity = new PdfPTable(1);
    Style.noBorderTable(complexity);
    complexity.addCell(new Phrase(getTextProperty("general.complexity"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable complexityTendency = new PdfPTable(2);
    complexityTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    Style.noBorderTable(complexityTendency);
    complexityTendency.addCell(new Phrase(project.getMeasure(MetricKeys.FUNCTION_COMPLEXITY).getFormatValue(),
        Style.DASHBOARD_DATA_FONT));
    complexityTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.FUNCTION_COMPLEXITY).getQualitativeTendency(),
        project.getMeasure(MetricKeys.FUNCTION_COMPLEXITY).getQuantitativeTendency()));
    complexity.addCell(complexityTendency);
    complexity.addCell(new Phrase(project.getMeasure(MetricKeys.CLASS_COMPLEXITY).getFormatValue() + " /class",
        Style.DASHBOARD_DATA_FONT_2));
    complexity.addCell(new Phrase(project.getMeasure(MetricKeys.COMPLEXITY).getFormatValue() + " decision points",
        Style.DASHBOARD_DATA_FONT_2));

    staticAnalysisTable.setSpacingBefore(10);
    staticAnalysisTable.addCell(linesOfCode);
    staticAnalysisTable.addCell(comments);
    staticAnalysisTable.addCell(complexity);
    staticAnalysisTable.setSpacingAfter(20);

    // Dynamic Analysis
    Paragraph dynamicAnalysis = new Paragraph(getTextProperty("general.dynamic_analysis"), Style.UNDERLINED_FONT);
    PdfPTable dynamicAnalysisTable = new PdfPTable(3);
    Style.noBorderTable(dynamicAnalysisTable);

    PdfPTable codeCoverage = new PdfPTable(1);
    Style.noBorderTable(codeCoverage);
    codeCoverage.addCell(new Phrase(getTextProperty("general.code_coverage"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable codeCoverageTendency = new PdfPTable(2);
    Style.noBorderTable(codeCoverageTendency);
    codeCoverageTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    codeCoverageTendency.addCell(new Phrase(
        project.getMeasure(MetricKeys.COVERAGE).getFormatValue() + " coverage", Style.DASHBOARD_DATA_FONT));
    codeCoverageTendency.addCell(getTendencyImage(
        project.getMeasure(MetricKeys.COVERAGE).getQualitativeTendency(), project.getMeasure(
            MetricKeys.COVERAGE).getQuantitativeTendency()));
    codeCoverage.addCell(codeCoverageTendency);
    codeCoverage.addCell(new Phrase(project.getMeasure(MetricKeys.TESTS).getFormatValue() + " tests",
        Style.DASHBOARD_DATA_FONT_2));

    PdfPTable testSuccess = new PdfPTable(1);
    Style.noBorderTable(testSuccess);
    testSuccess.addCell(new Phrase(getTextProperty("general.test_success"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable testSuccessTendency = new PdfPTable(2);
    Style.noBorderTable(testSuccessTendency);
    testSuccessTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    testSuccessTendency.addCell(new Phrase(project.getMeasure(MetricKeys.TEST_SUCCESS_DENSITY).getFormatValue(),
        Style.DASHBOARD_DATA_FONT));
    testSuccessTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.TEST_SUCCESS_DENSITY)
        .getQualitativeTendency(), project.getMeasure(MetricKeys.TEST_SUCCESS_DENSITY).getQuantitativeTendency()));
    testSuccess.addCell(testSuccessTendency);
    testSuccess.addCell(new Phrase(project.getMeasure(MetricKeys.TEST_FAILURES).getFormatValue() + " failures",
        Style.DASHBOARD_DATA_FONT_2));
    testSuccess.addCell(new Phrase(project.getMeasure(MetricKeys.TEST_ERRORS).getFormatValue() + " errors",
        Style.DASHBOARD_DATA_FONT_2));

    dynamicAnalysisTable.setSpacingBefore(10);
    dynamicAnalysisTable.addCell(codeCoverage);
    dynamicAnalysisTable.addCell(testSuccess);
    dynamicAnalysisTable.addCell("");
    dynamicAnalysisTable.setSpacingAfter(20);

    Paragraph codingRulesViolations = new Paragraph(getTextProperty("general.coding_rules_violations"),
        Style.UNDERLINED_FONT);
    PdfPTable codingRulesViolationsTable = new PdfPTable(3);
    Style.noBorderTable(codingRulesViolationsTable);

    PdfPTable rulesCompliance = new PdfPTable(1);
    Style.noBorderTable(rulesCompliance);
    rulesCompliance.addCell(new Phrase(getTextProperty("general.rules_compliance"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable rulesComplianceTendency = new PdfPTable(2);
    Style.noBorderTable(rulesComplianceTendency);
    rulesComplianceTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    rulesComplianceTendency.addCell(new Phrase(project.getMeasure(MetricKeys.VIOLATIONS_DENSITY).getFormatValue(),
        Style.DASHBOARD_DATA_FONT));
    rulesComplianceTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.VIOLATIONS_DENSITY)
        .getQualitativeTendency(), project.getMeasure(MetricKeys.VIOLATIONS_DENSITY).getQuantitativeTendency()));
    rulesCompliance.addCell(rulesComplianceTendency);

    PdfPTable violations = new PdfPTable(1);
    Style.noBorderTable(violations);
    violations.addCell(new Phrase(getTextProperty("general.violations"), Style.DASHBOARD_TITLE_FONT));
    PdfPTable violationsTendency = new PdfPTable(2);
    Style.noBorderTable(violationsTendency);
    violationsTendency.getDefaultCell().setFixedHeight(Style.TENDENCY_ICONS_HEIGHT);
    violationsTendency.addCell(new Phrase(project.getMeasure(MetricKeys.VIOLATIONS).getFormatValue(),
        Style.DASHBOARD_DATA_FONT));
    violationsTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.VIOLATIONS)
        .getQualitativeTendency(), project.getMeasure(MetricKeys.VIOLATIONS).getQuantitativeTendency()));
    violations.addCell(violationsTendency);

    codingRulesViolationsTable.setSpacingBefore(10);
    codingRulesViolationsTable.addCell(rulesCompliance);
    codingRulesViolationsTable.addCell(violations);
    codingRulesViolationsTable.addCell("");
    codingRulesViolationsTable.setSpacingAfter(20);

    section.add(Chunk.NEWLINE);
    section.add(staticAnalysis);
    section.add(staticAnalysisTable);
    section.add(dynamicAnalysis);
    section.add(dynamicAnalysisTable);
    section.add(codingRulesViolations);
    section.add(codingRulesViolationsTable);
  }

  protected void printMostDuplicatedFiles(Project project, Section section) {
    List<FileInfo> files = project.getMostDuplicatedFiles();
    Iterator<FileInfo> it = files.iterator();
    List<String> left = new LinkedList<String>();
    List<String> right = new LinkedList<String>();

    while (it.hasNext()) {
      FileInfo file = it.next();
      left.add(file.getName());
      right.add(file.getDuplicatedLines());
    }

    PdfPTable mostDuplicatedFilesTable = Style.createSimpleTable(left, right,
        getTextProperty("general.most_duplicated_files"), getTextProperty("general.no_duplicated_files"));
    section.add(mostDuplicatedFilesTable);
  }
  
  
  protected void printMostComplexFiles(Project project, Section section) {
    List<FileInfo> files = project.getMostComplexFiles();
    Iterator<FileInfo> it = files.iterator();
    List<String> left = new LinkedList<String>();
    List<String> right = new LinkedList<String>();

    while (it.hasNext()) {
      FileInfo file = it.next();
      left.add(file.getName());
      right.add(file.getComplexity());
    }

    PdfPTable mostComplexFilesTable = Style.createSimpleTable(left, right,
        getTextProperty("general.most_complex_files"), getTextProperty("general.no_complex_files"));
    section.add(mostComplexFilesTable);
  }

  protected void printMostViolatedRules(Project project, Section section) {
    List<Rule> mostViolatedRules = project.getMostViolatedRules();
    Iterator<Rule> it = mostViolatedRules.iterator();
    
    List<String> left = new LinkedList<String>();
    List<String> right = new LinkedList<String>();
    int limit = 0;
    while (it.hasNext() && limit < 5) {
      Rule rule = it.next();
      left.add(rule.getName());
      right.add(String.valueOf(rule.getViolationsNumberFormatted()));
      limit++;
    }

    PdfPTable mostViolatedRulesTable = Style.createSimpleTable(left, right,
        getTextProperty("general.most_violated_rules"), getTextProperty("general.no_violated_rules"));
    section.add(mostViolatedRulesTable);
  }

  protected void printMostViolatedFiles(Project project, Section section) {
    List<FileInfo> files = project.getMostViolatedFiles();
    Iterator<FileInfo> it = files.iterator();
    List<String> left = new LinkedList<String>();
    List<String> right = new LinkedList<String>();

    while (it.hasNext()) {
      FileInfo file = it.next();
      left.add(file.getName());
      right.add(file.getViolations());
    }

    PdfPTable mostViolatedFilesTable = Style.createSimpleTable(left, right,
        getTextProperty("general.most_violated_files"), getTextProperty("general.no_violated_files"));
    section.add(mostViolatedFilesTable);
  }

  protected void printRulesCategories(Project project, Section section) {
    PdfPTable categoriesTable = new PdfPTable(2);
    categoriesTable.getDefaultCell().setColspan(2);
    categoriesTable.addCell(new Phrase(getTextProperty("general.violations_by_category"), Style.DASHBOARD_TITLE_FONT));
    categoriesTable.getDefaultCell().setBackgroundColor(Color.GRAY);
    categoriesTable.addCell("");
    categoriesTable.getDefaultCell().setColspan(1);
    categoriesTable.getDefaultCell().setBackgroundColor(Color.WHITE);
    categoriesTable.addCell("Maintainability");
    categoriesTable.addCell(project.getMaintainabilityViolations().toString());
    categoriesTable.addCell("Reliability");
    categoriesTable.addCell(project.getReliabilityViolations().toString());
    categoriesTable.addCell("Efficiency");
    categoriesTable.addCell(project.getEfficiencyViolations().toString());
    categoriesTable.addCell("Portability");
    categoriesTable.addCell(project.getPortabilityViolations().toString());
    categoriesTable.addCell("Usability");
    categoriesTable.addCell(project.getUsabilityViolations().toString());
    categoriesTable.setSpacingBefore(10);
    categoriesTable.setSpacingAfter(20);
    section.add(categoriesTable);
  }

  @Override
  protected void printTocTitle(Toc tocDocument) throws com.lowagie.text.DocumentException {
    Paragraph tocTitle = new Paragraph(super.getTextProperty("main.table.of.contents"), Style.TOC_TITLE_FONT);
    tocTitle.setAlignment(Element.ALIGN_CENTER);
    tocDocument.getTocDocument().add(tocTitle);
    tocDocument.getTocDocument().add(Chunk.NEWLINE);
  }
}
