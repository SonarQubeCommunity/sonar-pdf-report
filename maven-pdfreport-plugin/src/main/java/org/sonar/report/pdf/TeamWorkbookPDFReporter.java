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

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.entity.Rule;
import org.sonar.report.pdf.entity.Violation;
import org.sonar.report.pdf.entity.exception.ReportException;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPTable;

public class TeamWorkbookPDFReporter extends ExecutivePDFReporter {

  public TeamWorkbookPDFReporter(URL logo, String projectKey, String sonarUrl, Properties configProperties,
      Properties langProperties) {
    super(logo, projectKey, sonarUrl, configProperties, langProperties);
    reportType = "workbook";
  }

  public void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException,
    ReportException {
    Project project = super.getProject();
    // Chapter 1: Report Overview (Parent project)
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(project.getName(), Style.CHAPTER_FONT));
    chapter1.add(new Paragraph(getTextProperty("main.text.misc.overview"), Style.NORMAL_FONT));
    Section section11 = chapter1
        .addSection(new Paragraph(getTextProperty("general.report_overview"), Style.TITLE_FONT));
    printDashboard(project, section11);
    Section section12 = chapter1.addSection(new Paragraph(getTextProperty("general.violations_analysis"),
        Style.TITLE_FONT));
    printMostViolatedRules(project, section12);
    printMostViolatedFiles(project, section12);
    printMostComplexFiles(project, section12);
    printMostDuplicatedFiles(project, section12);

    Section section13 = chapter1.addSection(new Paragraph(getTextProperty("general.violations_details"),
        Style.TITLE_FONT));
    printMostViolatedRulesDetails(project, section13);

    document.add(chapter1);

    Iterator<Project> it = project.getSubprojects().iterator();
    while (it.hasNext()) {
      Project subproject = it.next();
      ChapterAutoNumber chapterN = new ChapterAutoNumber(new Paragraph(subproject.getName(), Style.CHAPTER_FONT));

      Section sectionN1 = chapterN.addSection(new Paragraph(getTextProperty("general.report_overview"),
          Style.TITLE_FONT));
      printDashboard(subproject, sectionN1);

      Section sectionN2 = chapterN.addSection(new Paragraph(getTextProperty("general.violations_analysis"),
          Style.TITLE_FONT));
      printMostViolatedRules(subproject, sectionN2);
      printMostViolatedFiles(subproject, sectionN2);
      printMostComplexFiles(subproject, sectionN2);
      printMostDuplicatedFiles(subproject, sectionN2);
      Section sectionN3 = chapterN.addSection(new Paragraph(getTextProperty("general.violations_details"),
          Style.TITLE_FONT));
      printMostViolatedRulesDetails(subproject, sectionN3);
      document.add(chapterN);
    }
  }

  private void printMostViolatedRulesDetails(Project project, Section section13) {
    Iterator<Rule> it = project.getMostViolatedRules().iterator();

    while (it.hasNext()) {
      Rule rule = it.next();
      List<String> files = new LinkedList<String>();
      List<String> lines = new LinkedList<String>();
      Iterator<Violation> itViolations = rule.getTopViolations().iterator();
      while (itViolations.hasNext()) {
        Violation violation = itViolations.next();
        String[] components = violation.getResource().split("\\.");
        files.add(components[components.length - 1]);
        lines.add(violation.getLine());
      }
      section13.add(createViolationsDetailedTable(rule.getName(), files, lines));
    }
  }

  private PdfPTable createViolationsDetailedTable(String ruleName, List<String> files, List<String> lines) {

    // TODO: internationalize this

    PdfPTable table = new PdfPTable(10);
    Iterator<String> itLeft = files.iterator();
    Iterator<String> itRight = lines.iterator();
    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setBackgroundColor(new Color(255, 228, 181));
    table.addCell(new Phrase("Rule", Style.NORMAL_FONT));
    table.getDefaultCell().setColspan(9);
    table.getDefaultCell().setBackgroundColor(Color.WHITE);
    table.addCell(new Phrase(ruleName, Style.NORMAL_FONT));
    table.getDefaultCell().setColspan(10);
    table.getDefaultCell().setBackgroundColor(Color.GRAY);
    table.addCell("");
    table.getDefaultCell().setColspan(7);
    table.getDefaultCell().setBackgroundColor(new Color(255, 228, 181));
    table.addCell(new Phrase("File", Style.NORMAL_FONT));
    table.getDefaultCell().setColspan(3);
    table.addCell(new Phrase("Line", Style.NORMAL_FONT));
    table.getDefaultCell().setBackgroundColor(Color.WHITE);

    int i = 0;
    String lineNumbers = "";
    while (i < files.size() - 1) {
      if (lineNumbers.equals("")) {
        lineNumbers += lines.get(i);
      } else {
        lineNumbers += ", " + lines.get(i);
      }

      if (!files.get(i).equals(files.get(i + 1))) {
        table.getDefaultCell().setColspan(7);
        table.addCell(files.get(i));
        table.getDefaultCell().setColspan(3);
        table.addCell(lineNumbers);
        lineNumbers = "";
      }
      i++;
    }

    table.getDefaultCell().setColspan(7);
    table.addCell(files.get(files.size() - 1));
    table.getDefaultCell().setColspan(3);
    if (lineNumbers.equals("")) {
      lineNumbers += lines.get(i);
    } else {
      lineNumbers += ", " + lines.get(lines.size() - 1);
    }
    table.addCell(lineNumbers);

    table.setSpacingBefore(20);
    table.setSpacingAfter(20);
    table.setLockedWidth(false);
    table.setWidthPercentage(90);
    return table;
  }
}