/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.sonar.report.pdf.entity.FileInfo;
import org.sonar.report.pdf.entity.Project;
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

  public ExecutivePDFReporter(URL logo, String projectKey, String sonarUrl, Properties configProperties, Properties langProperties) {
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
      throws org.dom4j.DocumentException {
    try {
      URL largeLogo;
      if (super.getConfigProperty("front.page.logo").startsWith("http://")) {
        largeLogo = new URL(super.getConfigProperty("front.page.logo"));
      } else {
        largeLogo = this.getClass().getClassLoader().getResource(super.getConfigProperty("front.page.logo"));
      }
      Image logoImage = Image.getInstance(largeLogo);
      Rectangle pageSize = frontPageDocument.getPageSize();
      float positionX = pageSize.getWidth() / 2f - logoImage.getWidth() / 2f;
      logoImage.setAbsolutePosition(positionX, pageSize.getHeight() - logoImage.getHeight() - 100);
      frontPageDocument.add(logoImage);

      PdfPTable title = new PdfPTable(1);
      title.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      title.getDefaultCell().setBorder(Rectangle.NO_BORDER);

      String projectRow = super.getProject().getName();
      String versionRow = super.getProject().getMeasures().getVersion();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String dateRow = df.format(super.getProject().getMeasures().getDate());
      String descriptionRow = super.getProject().getDescription();

      title.addCell(new Phrase(projectRow, Style.frontPageFont1));
      title.addCell(new Phrase(versionRow, Style.frontPageFont1));
      title.addCell(new Phrase(descriptionRow, Style.frontPageFont2));
      title.addCell(new Phrase(dateRow, Style.frontPageFont3));
      title.setTotalWidth(pageSize.getWidth() - frontPageDocument.leftMargin() - frontPageDocument.rightMargin());
      title.writeSelectedRows(0, -1, frontPageDocument.leftMargin(),
          pageSize.getHeight() - logoImage.getHeight() - 150, frontPageWriter.getDirectContent());

    } catch (IOException e) {
      e.printStackTrace();
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException {
    Project project = super.getProject();
    // Chapter 1: Report Overview (Parent project)
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(project.getName(),
        Style.chapterFont));
    chapter1.add(new Paragraph(getTextProperty("main.text.misc.overview"), Style.normalFont));
    Section section11 = chapter1.addSection(new Paragraph(getTextProperty("general.report_overview"),
        Style.titleFont));
    printDashboard(project, section11);
    Section section12 = chapter1.addSection(new Paragraph(getTextProperty("general.violations_analysis"),
        Style.titleFont));
    printRulesCategories(project, section12);
    printMostViolatedRules(project, section12);
    printMostViolatedFiles(project, section12);
    document.add(chapter1);
    
    Iterator<Project> it = project.getSubprojects().iterator();
    while(it.hasNext()) {
      Project subproject = it.next();
      ChapterAutoNumber chapterN = new ChapterAutoNumber(new Paragraph(subproject.getName(),
          Style.chapterFont));
      
      Section sectionN1 = chapterN.addSection(new Paragraph(getTextProperty("general.report_overview"),
          Style.titleFont));
      printDashboard(subproject, sectionN1);
      
      Section sectionN2 = chapterN.addSection(new Paragraph(getTextProperty("general.violations_analysis"),
          Style.titleFont));
      printRulesCategories(subproject, sectionN2);
      printMostViolatedRules(subproject, sectionN2);
      printMostViolatedFiles(subproject, sectionN2);
      document.add(chapterN);
    }
  }

  private void printDashboard(Project project, Section section) throws DocumentException {

    // Static Analysis
    Paragraph staticAnalysis = new Paragraph(getTextProperty("general.static_analysis"), Style.underlinedFont);
    PdfPTable staticAnalysisTable = new PdfPTable(3);
    staticAnalysisTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable linesOfCode = new PdfPTable(1);
    Style.noBorderTable(linesOfCode);
    linesOfCode.addCell(new Phrase(getTextProperty("general.lines_of_code"), Style.dashboardTitleFont));
    PdfPTable linesOfCodeTendency = new PdfPTable(2);
    Style.noBorderTable(linesOfCodeTendency);
    linesOfCodeTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    linesOfCodeTendency.addCell(new Phrase(project.getMeasure(MetricKeys.NCLOC).getFormatValue(), Style.dashboardDataFont));
    linesOfCodeTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.NCLOC).getQualitativeTendency(), project
        .getMeasure(MetricKeys.NCLOC).getQuantitativeTendency()));

    linesOfCode.addCell(linesOfCodeTendency);
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.PACKAGES_COUNT).getFormatValue() + " packages", Style.dashboardDataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.CLASSES_COUNT).getFormatValue() + " classes", Style.dashboardDataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.FUNCTIONS_COUNT).getFormatValue() + " methods", Style.dashboardDataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure(MetricKeys.DUPLICATED_LINES_RATIO).getFormatValue() + " duplicated lines",
        Style.dashboardDataFont2));

    PdfPTable comments = new PdfPTable(1);
    Style.noBorderTable(comments);
    comments.addCell(new Phrase(getTextProperty("general.comments"), Style.dashboardTitleFont));
    PdfPTable commentsTendency = new PdfPTable(2);
    commentsTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    Style.noBorderTable(commentsTendency);
    commentsTendency.addCell(new Phrase(project.getMeasure(MetricKeys.COMMENT_RATIO).getFormatValue(), Style.dashboardDataFont));
    commentsTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.COMMENT_RATIO).getQualitativeTendency(), project
        .getMeasure(MetricKeys.COMMENT_RATIO).getQuantitativeTendency()));
    comments.addCell(commentsTendency);
    comments.addCell(new Phrase(project.getMeasure(MetricKeys.COMMENT_LINES).getFormatValue() + " comment lines", Style.dashboardDataFont2));

    PdfPTable complexity = new PdfPTable(1);
    Style.noBorderTable(complexity);
    complexity.addCell(new Phrase(getTextProperty("general.complexity"), Style.dashboardTitleFont));
    PdfPTable complexityTendency = new PdfPTable(2);
    complexityTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    Style.noBorderTable(complexityTendency);
    complexityTendency.addCell(new Phrase(project.getMeasure(MetricKeys.CCN_FUNCTION).getFormatValue(), Style.dashboardDataFont));
    complexityTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.CCN_FUNCTION).getQualitativeTendency(), project
        .getMeasure(MetricKeys.CCN_FUNCTION).getQuantitativeTendency()));
    complexity.addCell(complexityTendency);
    complexity.addCell(new Phrase(project.getMeasure(MetricKeys.CCN_CLASS).getFormatValue() + " /class", Style.dashboardDataFont2));
    complexity.addCell(new Phrase(project.getMeasure(MetricKeys.CCN).getFormatValue() + " decision points", Style.dashboardDataFont2));

    staticAnalysisTable.setSpacingBefore(10);
    staticAnalysisTable.addCell(linesOfCode);
    staticAnalysisTable.addCell(comments);
    staticAnalysisTable.addCell(complexity);
    staticAnalysisTable.setSpacingAfter(20);

    // Dynamic Analysis
    Paragraph dynamicAnalysis = new Paragraph(getTextProperty("general.dynamic_analysis"), Style.underlinedFont);
    PdfPTable dynamicAnalysisTable = new PdfPTable(3);
    Style.noBorderTable(dynamicAnalysisTable);

    PdfPTable codeCoverage = new PdfPTable(1);
    Style.noBorderTable(codeCoverage);
    codeCoverage.addCell(new Phrase(getTextProperty("general.code_coverage"), Style.dashboardTitleFont));
    PdfPTable codeCoverageTendency = new PdfPTable(2);
    Style.noBorderTable(codeCoverageTendency);
    codeCoverageTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    codeCoverageTendency.addCell(new Phrase(project.getMeasure(MetricKeys.CODE_COVERAGE).getFormatValue() + " coverage",
        Style.dashboardDataFont));
    codeCoverageTendency.addCell(getTendencyImage(project.getMeasure(MetricKeys.CODE_COVERAGE).getQualitativeTendency(), project
        .getMeasure(MetricKeys.CODE_COVERAGE).getQuantitativeTendency()));
    codeCoverage.addCell(codeCoverageTendency);
    codeCoverage.addCell(new Phrase(project.getMeasure(MetricKeys.TEST_COUNT).getFormatValue() + " tests", Style.dashboardDataFont2));

    PdfPTable testSuccess = new PdfPTable(1);
    Style.noBorderTable(testSuccess);
    testSuccess.addCell(new Phrase(getTextProperty("general.test_success"), Style.dashboardTitleFont));
    PdfPTable testSuccessTendency = new PdfPTable(2);
    Style.noBorderTable(testSuccessTendency);
    testSuccessTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    testSuccessTendency.addCell(new Phrase(project.getMeasure(MetricKeys.TEST_SUCCESS_PERCENTAGE).getFormatValue(), Style.dashboardDataFont));
    testSuccessTendency.addCell(getTendencyImage(
        project.getMeasure(MetricKeys.TEST_SUCCESS_PERCENTAGE).getQualitativeTendency(), project.getMeasure(
            MetricKeys.TEST_SUCCESS_PERCENTAGE).getQuantitativeTendency()));
    testSuccess.addCell(testSuccessTendency);
    testSuccess
        .addCell(new Phrase(project.getMeasure(MetricKeys.TEST_FAILURES_COUNT).getFormatValue() + " failures", Style.dashboardDataFont2));
    testSuccess.addCell(new Phrase(project.getMeasure(MetricKeys.TEST_ERRORS_COUNT).getFormatValue() + " errors", Style.dashboardDataFont2));

    dynamicAnalysisTable.setSpacingBefore(10);
    dynamicAnalysisTable.addCell(codeCoverage);
    dynamicAnalysisTable.addCell(testSuccess);
    dynamicAnalysisTable.addCell("");
    dynamicAnalysisTable.setSpacingAfter(20);

    Paragraph codingRulesViolations = new Paragraph(getTextProperty("general.coding_rules_violations"),
        Style.underlinedFont);
    PdfPTable codingRulesViolationsTable = new PdfPTable(3);
    Style.noBorderTable(codingRulesViolationsTable);

    PdfPTable rulesCompliance = new PdfPTable(1);
    Style.noBorderTable(rulesCompliance);
    rulesCompliance.addCell(new Phrase(getTextProperty("general.rules_compliance"), Style.dashboardTitleFont));
    PdfPTable rulesComplianceTendency = new PdfPTable(2);
    Style.noBorderTable(rulesComplianceTendency);
    rulesComplianceTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    rulesComplianceTendency.addCell(new Phrase(project.getMeasure(MetricKeys.RULES_COMPLIANCE).getFormatValue(), Style.dashboardDataFont));
    rulesComplianceTendency.addCell(getTendencyImage(
        project.getMeasure(MetricKeys.RULES_COMPLIANCE).getQualitativeTendency(), project.getMeasure(
            MetricKeys.RULES_COMPLIANCE).getQuantitativeTendency()));
    rulesCompliance.addCell(rulesComplianceTendency);

    PdfPTable violations = new PdfPTable(1);
    Style.noBorderTable(violations);
    violations.addCell(new Phrase(getTextProperty("general.violations"), Style.dashboardTitleFont));
    PdfPTable violationsTendency = new PdfPTable(2);
    Style.noBorderTable(violationsTendency);
    violationsTendency.getDefaultCell().setFixedHeight(Style.tendencyIconsHeight);
    violationsTendency.addCell(new Phrase(project.getMeasure(MetricKeys.RULES_VIOLATIONS).getFormatValue(), Style.dashboardDataFont));
    violationsTendency.addCell(getTendencyImage(
        project.getMeasure(MetricKeys.RULES_VIOLATIONS).getQualitativeTendency(), project.getMeasure(
            MetricKeys.RULES_VIOLATIONS).getQuantitativeTendency()));
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
  
  private void printMostViolatedRules(Project project, Section section) {
    // PDFing the info allocated in Project.mostViolatedRules
    Set<Entry <String, String>> mostViolatedRules = project.getMostViolatedRules().entrySet();
    Iterator<Entry<String, String>> it = mostViolatedRules.iterator();
    PdfPTable mostViolatedRulesTable = new PdfPTable(2);
    mostViolatedRulesTable.getDefaultCell().setColspan(2);
    mostViolatedRulesTable.addCell(new Phrase(getTextProperty("general.most_violated_rules"), Style.dashboardTitleFont));
    mostViolatedRulesTable.getDefaultCell().setBackgroundColor(Color.GRAY);
    mostViolatedRulesTable.addCell("");
    mostViolatedRulesTable.getDefaultCell().setColspan(1);
    mostViolatedRulesTable.getDefaultCell().setBackgroundColor(Color.WHITE);
    
    while(it.hasNext()) {
      Entry<String, String> entry = it.next();
      mostViolatedRulesTable.addCell(entry.getKey());
      mostViolatedRulesTable.addCell(entry.getValue());
    }
    if(project.getMostViolatedRules().isEmpty()) {
      mostViolatedRulesTable.getDefaultCell().setColspan(2);
      mostViolatedRulesTable.addCell(getTextProperty("general.no_violated_rules"));
    }
    
    mostViolatedRulesTable.setSpacingBefore(20);
    mostViolatedRulesTable.setSpacingAfter(20);
    section.add(mostViolatedRulesTable);
  }
  

  public void printMostViolatedFiles(Project project, Section section) {
    List<FileInfo> mostViolatedFiles = project.getMostViolatedFiles();
    Iterator<FileInfo> it = mostViolatedFiles.iterator();
    PdfPTable mostViolatedFilesTable = new PdfPTable(2);
    mostViolatedFilesTable.getDefaultCell().setColspan(2);
    mostViolatedFilesTable.addCell(new Phrase(getTextProperty("general.most_violated_files"), Style.dashboardTitleFont));
    mostViolatedFilesTable.getDefaultCell().setBackgroundColor(Color.GRAY);
    mostViolatedFilesTable.addCell("");
    mostViolatedFilesTable.getDefaultCell().setColspan(1);
    mostViolatedFilesTable.getDefaultCell().setBackgroundColor(Color.WHITE);
    
    while(it.hasNext()) {
      FileInfo fileInfo = it.next();
      mostViolatedFilesTable.addCell(fileInfo.getName());
      mostViolatedFilesTable.addCell(fileInfo.getViolations().toString());
    }
    if(project.getMostViolatedFiles().isEmpty()) {
      mostViolatedFilesTable.getDefaultCell().setColspan(2);
      mostViolatedFilesTable.addCell(getTextProperty("general.no_violated_files"));
    }
    
    mostViolatedFilesTable.setSpacingBefore(20);
    mostViolatedFilesTable.setSpacingAfter(20);
    section.add(mostViolatedFilesTable);
  }
  
  private void printRulesCategories(Project project, Section section) {
    PdfPTable categoriesTable = new PdfPTable(2);
    categoriesTable.getDefaultCell().setColspan(2);
    categoriesTable.addCell(new Phrase(getTextProperty("general.violations_by_category"), Style.dashboardTitleFont));
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
    Paragraph tocTitle = new Paragraph(super.getTextProperty("main.table.of.contents"), Style.tocTitleFont);
    tocTitle.setAlignment(Element.ALIGN_CENTER);
    tocDocument.getTocDocument().add(tocTitle);
    tocDocument.getTocDocument().add(Chunk.NEWLINE);
  }
}
