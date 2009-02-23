package org.sonar.report.pdf;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.sonar.report.pdf.entity.Project;

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

  private URL logo;
  private String projectKey;
  private String sonarUrl;

  public TeamWorkbookPDFReporter(URL logo, String projectKey, String sonarUrl) {
    this.logo = logo;
    this.projectKey = projectKey;
    this.sonarUrl = sonarUrl;
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

      String projectRow = super.getTextProperty("general.project") + ": " + super.getProject().getName();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String dateRow = df.format(super.getProject().getMeasures().getDate());
      String descriptionRow = super.getProject().getDescription();

      title.addCell(new Phrase(projectRow, FontStyle.frontPageFont1));
      title.addCell(new Phrase(descriptionRow, FontStyle.frontPageFont2));
      title.addCell(new Phrase(dateRow, FontStyle.frontPageFont3));
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
    // Chapter 1: Report Overview
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(getTextProperty("general.report_overview"),
        FontStyle.chapterFont));
    Project project = super.getProject();
    chapter1.add(new Paragraph(getTextProperty("main.text.misc.overview"), FontStyle.normalFont));
    printDashboard(project, chapter1);
    document.add(chapter1);
  }

  // TODO: tendency info
  private void printDashboard(Project project, Section section) throws DocumentException {

    // Dashboard special fonts
    Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK);
    Font dataFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
    Font dataFont2 = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, new Color(100, 150, 190));

    // Static Analysis
    Paragraph staticAnalysis = new Paragraph(getTextProperty("general.static_analysis"), FontStyle.underlinedFont);
    PdfPTable staticAnalysisTable = new PdfPTable(3);
    staticAnalysisTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable linesOfCode = new PdfPTable(1);
    linesOfCode.getDefaultCell().setBorderColor(Color.WHITE);
    linesOfCode.addCell(new Phrase(getTextProperty("general.lines_of_code"), titleFont));
    linesOfCode.addCell(new Phrase(project.getMeasureValue("ncss"), dataFont));
    linesOfCode.addCell(new Phrase(project.getMeasureValue("packages_count") + " packages", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasureValue("classes_count") + " classes", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasureValue("functions_count") + " methods", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasureValue("duplicated_lines_ratio") + " duplicated lines", dataFont2));

    PdfPTable comments = new PdfPTable(1);
    comments.getDefaultCell().setBorderColor(Color.WHITE);
    comments.addCell(new Phrase(getTextProperty("general.comments"), titleFont));
    comments.addCell(new Phrase(project.getMeasureValue("comment_ratio"), dataFont));
    comments.addCell(new Phrase(project.getMeasureValue("comment_lines") + " comment lines", dataFont2));

    PdfPTable complexity = new PdfPTable(1);
    complexity.getDefaultCell().setBorderColor(Color.WHITE);
    complexity.addCell(new Phrase(getTextProperty("general.complexity"), titleFont));
    complexity.addCell(new Phrase(project.getMeasureValue("ccn_function"), dataFont));
    complexity.addCell(new Phrase(project.getMeasureValue("ccn_class") + " /class", dataFont2));
    complexity.addCell(new Phrase(project.getMeasureValue("ccn") + " decision points", dataFont2));

    staticAnalysisTable.setSpacingBefore(10);
    staticAnalysisTable.addCell(linesOfCode);
    staticAnalysisTable.addCell(comments);
    staticAnalysisTable.addCell(complexity);
    staticAnalysisTable.setSpacingAfter(20);

    // Dynamic Analysis
    Paragraph dynamicAnalysis = new Paragraph(getTextProperty("general.dynamic_analysis"), FontStyle.underlinedFont);
    PdfPTable dynamicAnalysisTable = new PdfPTable(3);
    dynamicAnalysisTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable codeCoverage = new PdfPTable(1);
    codeCoverage.getDefaultCell().setBorderColor(Color.WHITE);
    codeCoverage.addCell(new Phrase(getTextProperty("general.code_coverage"), titleFont));
    codeCoverage.addCell(new Phrase(project.getMeasureValue("code_coverage") + " coverage", dataFont));
    codeCoverage.addCell(new Phrase(project.getMeasureValue("test_count") + " tests", dataFont2));

    PdfPTable testSuccess = new PdfPTable(1);
    testSuccess.getDefaultCell().setBorderColor(Color.WHITE);
    testSuccess.addCell(new Phrase(getTextProperty("general.test_success"), titleFont));
    testSuccess.addCell(new Phrase(project.getMeasureValue("test_success_percentage"), dataFont));
    testSuccess.addCell(new Phrase(project.getMeasureValue("test_failures_count") + " failures", dataFont2));
    testSuccess.addCell(new Phrase(project.getMeasureValue("test_errors_count") + " errors", dataFont2));

    dynamicAnalysisTable.setSpacingBefore(10);
    dynamicAnalysisTable.addCell(codeCoverage);
    dynamicAnalysisTable.addCell(testSuccess);
    dynamicAnalysisTable.addCell("");
    dynamicAnalysisTable.setSpacingAfter(20);

    Paragraph codingRulesViolations = new Paragraph(getTextProperty("general.coding_rules_violations"),
        FontStyle.underlinedFont);
    PdfPTable codingRulesViolationsTable = new PdfPTable(3);
    codingRulesViolationsTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable rulesCompliance = new PdfPTable(1);
    rulesCompliance.getDefaultCell().setBorderColor(Color.WHITE);
    rulesCompliance.addCell(new Phrase(getTextProperty("general.rules_compliance"), titleFont));
    rulesCompliance.addCell(new Phrase(project.getMeasureValue("rules_compliance"), dataFont));

    PdfPTable violations = new PdfPTable(1);
    violations.getDefaultCell().setBorderColor(Color.WHITE);
    violations.addCell(new Phrase(getTextProperty("general.violations"), titleFont));
    violations.addCell(new Phrase(project.getMeasureValue("rules_violations"), dataFont));

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

  @Override
  protected void printTocTitle(Toc tocDocument) throws com.lowagie.text.DocumentException {
    Paragraph tocTitle = new Paragraph(super.getTextProperty("main.table.of.contents"), FontStyle.tocTitleFont);
    tocTitle.setAlignment(Element.ALIGN_CENTER);
    tocDocument.getTocDocument().add(tocTitle);
    tocDocument.getTocDocument().add(Chunk.NEWLINE);
  }

}
