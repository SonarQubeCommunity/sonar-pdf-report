/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 GMV-SGI
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

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.sonar.report.pdf.entity.Project;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;

public class TeamWorkbookPDFReporter extends ExecutivePDFReporter {

  public TeamWorkbookPDFReporter(URL logo, String projectKey, String sonarUrl, Properties configProperties,
      Properties langProperties) {
    super(logo, projectKey, sonarUrl, configProperties, langProperties);
  }
  
  public void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException {
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
    
    Section section13 = chapter1.addSection(new Paragraph(getTextProperty("general.violations_detailed"),
        Style.TITLE_FONT));
    printMostViolatedRulesDetails(project, section13);
    
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
      Section sectionN3 = chapterN.addSection(new Paragraph(getTextProperty("general.violations_detailed"),
          Style.TITLE_FONT));
      printMostViolatedRulesDetails(project, sectionN3);
      document.add(chapterN);
    }
  }

  private void printMostViolatedRulesDetails(Project project, Section section13) {
    
  }
}