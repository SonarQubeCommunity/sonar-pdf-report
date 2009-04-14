package org.sonar.report.pdf;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;

import org.sonar.report.pdf.entity.Measures;
import org.sonar.report.pdf.entity.Project;

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

import java.net.URL;

/**
 * Concrete PDFReporter. Implements printPdfBody method. This will be the way to extend PDFReport.
 */
public class DefaultPDFReporter extends PDFReporter {

  private URL logo;
  private String projectKey;
  private String sonarUrl;
  private Properties configProperties;
  private Properties langProperties;

  private final static int indentation = 18;
  private final static int tablePaddingBottom = 5;

  private Document document;

  public DefaultPDFReporter(URL logo, String projectKey, String sonarUrl, Properties configProperties, Properties langProperties) {
    this.logo = logo;
    this.projectKey = projectKey;
    this.sonarUrl = sonarUrl;
    this.configProperties = configProperties;
    this.langProperties = langProperties;
  }

  @Override
  protected void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException {
    this.document = document;
    // Chapter 1
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(getTextProperty("main.chapter1.title"),
        Style.chapterFont));
    chapter1.add(new Paragraph(getTextProperty("main.chapter1.intro"), Style.normalFont));
    // Section 1.1
    Section section11 = chapter1.addSection(new Paragraph(getTextProperty("main.chapter1.subtitle1"),
        Style.titleFont));
    Project project = super.getProject();
    printDashboard(project, section11);
    // Section 1.2
    Section section12 = chapter1.addSection(new Paragraph(getTextProperty("main.chapter1.subtitle2"),
        Style.titleFont));
    printProjectInfo(project, section12);

    document.add(chapter1);

    // Subprojects Chapters (2, 3, 4, ...)
    Iterator<Project> it = project.getSubprojects().iterator();
    while (it.hasNext()) {
      Project subproject = it.next();
      ChapterAutoNumber subprojectChapter = new ChapterAutoNumber(new Paragraph(getTextProperty("general.module")
          + ": " + subproject.getName(), Style.titleFont));
      Section sectionX1 = subprojectChapter.addSection(new Paragraph(getTextProperty("main.chapter2.subtitle2X1"),
          Style.titleFont));
      printDashboard(subproject, sectionX1);
      Section sectionX2 = subprojectChapter.addSection(new Paragraph(getTextProperty("main.chapter2.subtitle2X2"),
          Style.titleFont));
      printProjectInfo(subproject, sectionX2);
      document.add(subprojectChapter);
    }
  }

  private void printDashboard(Project project, Section section) throws DocumentException{
    PdfPTable dashboard = new PdfPTable(3);
    dashboard.getDefaultCell().setBorderColor(Color.WHITE);
    Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK);
    Font dataFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
    Font dataFont2 = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, new Color(100, 150, 190));

    PdfPTable linesOfCode = new PdfPTable(1);
    linesOfCode.getDefaultCell().setBorderColor(Color.WHITE);
    linesOfCode.addCell(new Phrase(getTextProperty("general.lines_of_code"), titleFont));
    linesOfCode.addCell(new Phrase(project.getMeasure("ncss").getFormatValue(), dataFont));
    linesOfCode.addCell(new Phrase(project.getMeasure("packages_count").getFormatValue() + " packages", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("classes_count").getFormatValue() + " classes", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("functions_count").getFormatValue() + " methods", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("duplicated_lines_ratio").getFormatValue() + " duplicated lines", dataFont2));

    PdfPTable comments = new PdfPTable(1);
    comments.getDefaultCell().setBorderColor(Color.WHITE);
    comments.addCell(new Phrase(getTextProperty("general.comments"), titleFont));
    comments.addCell(new Phrase(project.getMeasure("comment_ratio").getFormatValue(), dataFont));
    comments.addCell(new Phrase(project.getMeasure("comment_lines").getFormatValue() + " comment lines", dataFont2));

    PdfPTable codeCoverage = new PdfPTable(1);
    codeCoverage.getDefaultCell().setBorderColor(Color.WHITE);
    codeCoverage.addCell(new Phrase(getTextProperty("general.test_count"), titleFont));
    codeCoverage.addCell(new Phrase(project.getMeasure("test_count").getFormatValue(), dataFont));
    codeCoverage.addCell(new Phrase(project.getMeasure("test_success_percentage").getFormatValue() + " success", dataFont2));
    codeCoverage.addCell(new Phrase(project.getMeasure("code_coverage").getFormatValue() + " coverage", dataFont2));

    PdfPTable complexity = new PdfPTable(1);
    complexity.getDefaultCell().setBorderColor(Color.WHITE);
    complexity.addCell(new Phrase(getTextProperty("general.complexity"), titleFont));
    complexity.addCell(new Phrase(project.getMeasure("ccn_function").getFormatValue(), dataFont));
    complexity.addCell(new Phrase(project.getMeasure("ccn_class").getFormatValue() + " /class", dataFont2));
    complexity.addCell(new Phrase(project.getMeasure("ccn").getFormatValue() + " decision points", dataFont2));

    PdfPTable rulesCompliance = new PdfPTable(1);
    rulesCompliance.getDefaultCell().setBorderColor(Color.WHITE);
    rulesCompliance.addCell(new Phrase(getTextProperty("general.rules_compliance"), titleFont));
    rulesCompliance.addCell(new Phrase(project.getMeasure("rules_compliance").getFormatValue(), dataFont));

    PdfPTable violations = new PdfPTable(1);
    violations.getDefaultCell().setBorderColor(Color.WHITE);
    violations.addCell(new Phrase(getTextProperty("general.violations"), titleFont));
    violations.addCell(new Phrase(project.getMeasure("rules_violations").getFormatValue(), dataFont));

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
      Paragraph imageFoot = new Paragraph(getTextProperty("metrics.ccn_classes_count_distribution"), Style.footFont);
      imageFoot.setAlignment(Paragraph.ALIGN_CENTER);
      section.add(imageFoot);
    }
  }

  private void printMeasures(Measures measures, Section section) throws org.dom4j.DocumentException,
      DocumentException {

    PdfPTable versioningTable = new PdfPTable(2);
    formatTable(versioningTable);
    versioningTable.getDefaultCell().setColspan(2);
    versioningTable.addCell(new Phrase(super.getTextProperty("general.versioning_information"), Style.titleFont));
    versioningTable.addCell(measures.getVersion());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss");
    versioningTable.addCell(df.format(measures.getDate()));

    PdfPTable measuresTable = new PdfPTable(2);
    formatTable(measuresTable);

    Iterator<String> it = measures.getMeasuresKeys().iterator();
    measuresTable.addCell(new Phrase(super.getTextProperty("general.metric"), Style.titleFont));
    measuresTable.addCell(new Phrase(super.getTextProperty("general.value"), Style.titleFont));
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
        measuresTable.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_LEFT);
        measuresTable.addCell(super.getTextProperty("metrics." + measureKey));
        measuresTable.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_CENTER);
        measuresTable.addCell(measures.getMeasure(measureKey).getFormatValue());
      }
    }
    measuresTable.setHeaderRows(1);
    section.add(versioningTable);
    section.add(measuresTable);

  }

  private void formatTable(PdfPTable table) {
    Rectangle page = document.getPageSize();
    table.getDefaultCell().setVerticalAlignment(PdfCell.ALIGN_MIDDLE);
    table.getDefaultCell().setPaddingBottom(tablePaddingBottom);
    table.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_CENTER);
    table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
    table.setSpacingBefore(20);
  }

  private void printProjectInfo(Project project, Section section) throws DocumentException,
      org.dom4j.DocumentException {
    List data = new List();
    data.add(new ListItem(super.getTextProperty("general.name") + ": " + project.getName()));
    data.add(new ListItem(super.getTextProperty("general.description") + ": " + project.getDescription()));
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
  protected String getSonarUrl() {
    return this.sonarUrl;
  }

  @Override
  protected void printTocTitle(Toc tocDocument) throws DocumentException {
    Paragraph tocTitle = new Paragraph(super.getTextProperty("main.table.of.contents"), Style.tocTitleFont);
    tocTitle.setAlignment(Element.ALIGN_CENTER);
    tocDocument.getTocDocument().add(tocTitle);
    tocDocument.getTocDocument().add(Chunk.NEWLINE);
  }

  @Override
  protected void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter)
      throws org.dom4j.DocumentException {
    try {
      URL largeLogo;
      if(super.getConfigProperty("front.page.logo").startsWith("http://")) {
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
      
      title.addCell(new Phrase(projectRow, Style.frontPageFont1));
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
  protected Properties getLangProperties() {
    return langProperties;
  }

  @Override
  protected Properties getReportProperties() {
    return configProperties;
  } 
}
