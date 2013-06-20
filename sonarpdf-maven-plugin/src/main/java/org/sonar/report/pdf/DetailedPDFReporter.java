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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.sonar.report.pdf.Style.Backgrounds;
import org.sonar.report.pdf.entity.Priority;
import org.sonar.report.pdf.entity.Project;
import org.sonar.report.pdf.entity.Rule;
import org.sonar.report.pdf.entity.Violation;
import org.sonar.report.pdf.entity.exception.ReportException;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;

// TODO: Generate another class... "DetailedTeamWorkbookPDFReporter", or "BugfixPDFReporter".
public class DetailedPDFReporter extends ExecutivePDFReporter {

  public DetailedPDFReporter(final URL logo, final String projectKey, final String sonarUrl, final Properties configProperties,
      final Properties langProperties) {
    super(logo, projectKey, sonarUrl, configProperties, langProperties);
    reportType = "detailed";
  }

  @Override
public void printPdfBody(final Document document) throws DocumentException, IOException, org.dom4j.DocumentException,
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
    //section12.addMarkedSection();
    printAllViolatedRules(project, section12);
    printMostViolatedFiles(project, section12);
    printMostComplexFiles(project, section12);
    printMostDuplicatedFiles(project, section12);

    Section section13 = chapter1.addSection(new Paragraph(getTextProperty("general.violations_details"),
        Style.TITLE_FONT));
    printViolatedRulesDetails(project, section13);

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
      printAllViolatedRules(subproject, sectionN2);
      printMostViolatedFiles(subproject, sectionN2);
      printMostComplexFiles(subproject, sectionN2);
      printMostDuplicatedFiles(subproject, sectionN2);
      Section sectionN3 = chapterN.addSection(new Paragraph(getTextProperty("general.violations_details"),
          Style.TITLE_FONT));
      printViolatedRulesDetails(subproject, sectionN3);
      document.add(chapterN);
    }
  }
  
  protected void printAllViolatedRules(Project project, Section section){
	  List<Rule> mostViolatedRules = project.getMostViolatedRules();
	    Iterator<Rule> it = mostViolatedRules.iterator();

	    List<String> left = new LinkedList<String>();
	    List<String> right = new LinkedList<String>();
	    List<String> center = new LinkedList<String>();
	    while (it.hasNext()) {
	      Rule rule = it.next();
	      left.add(rule.getName());
	      center.add(rule.getPriority());
	      right.add(String.valueOf(rule.getViolationsNumberFormatted()));
	    }
	    List<String> columnTitles = new ArrayList<String>();
	    columnTitles.add(getTextProperty("general.rule"));
	    columnTitles.add(getTextProperty("general.priority"));
	    columnTitles.add(getTextProperty("general.violations"));
	    
	    PdfPTable mostViolatedRulesTable = Style.createViolatedRulesTable(left,center, right,
	        getTextProperty("general.violated_rules"), getTextProperty("general.no_violated_rules"),
	        columnTitles);
	    section.add(mostViolatedRulesTable);
  }

  private void printViolatedRulesDetails(final Project project, final Section section13) {
    Iterator<Rule> it = project.getMostViolatedRules().iterator();

    while (it.hasNext()) {
      Rule rule = it.next();
      List<String> files = new LinkedList<String>();
      List<String> lines = new LinkedList<String>();
      List<String> priority = new LinkedList<String>();
      Iterator<Violation> itViolations = rule.getTopViolations().iterator();
      while (itViolations.hasNext()) {
        Violation violation = itViolations.next();
        String[] components = violation.getResource().split("\\.");
        files.add(components[components.length - 1]);
        lines.add(violation.getLine());
        priority.add(violation.getPriority());
      }
      section13.add(createViolationsDetailedTable(rule.getName(), files, lines,priority));
    }
  }

  private PdfPTable createViolationsDetailedTable(final String ruleName, final List<String> files, final List<String> lines, final List<String> priority) {
    PdfPTable table = new PdfPTable(14);
    table.setWidthPercentage(100f);
    table.setHeaderRows(3);
    PdfPCell ruleTitleCell = new PdfPCell(new Phrase(getTextProperty("general.rule"), Style.NORMAL_FONT));
    ruleTitleCell.setBorderWidth(0f);
    ruleTitleCell.setUseBorderPadding(true);
    ruleTitleCell.setPaddingBottom(4.0f);
    ruleTitleCell.setColspan(1);
    ruleTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    ruleTitleCell.setCellEvent(new PdfPCellEvent() {
		
    	public void cellLayout(PdfPCell cell, Rectangle rect,
    			PdfContentByte[] canvas) {
    			PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
    			cb.setColorStroke(Color.blue);
    			cb.setColorFill(Style.Backgrounds.TABLE_TAB_BACKGROUND.getColor());
    			cb.roundRectangle(rect.getLeft(), rect.getBottom(),
    			rect.getWidth(), rect.getHeight(), 7);
    			cb.fill();
    			cb.moveTo(rect.getLeft(), rect.getTop());
    			cb.roundRectangle(rect.getLeft(),rect.getBottom(),
    					rect.getWidth(),rect.getHeight()-7	,0);
    			cb.fill();
    			cb.resetRGBColorFill();
    	}
	});
    
    table.addCell(ruleTitleCell);
    
    table.getDefaultCell().setBorderWidth(0f);
    table.getDefaultCell().setUseBorderPadding(true);
    table.getDefaultCell().setPaddingBottom(4.0f);
    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setColspan(14);
    table.getDefaultCell().setBackgroundColor(Color.WHITE);
    table.addCell(new Phrase(ruleName, Style.NORMAL_FONT));
    table.getDefaultCell().setColspan(14);
    table.getDefaultCell().setBackgroundColor(Color.GRAY);
    table.addCell("");
    table.getDefaultCell().setBackgroundColor(Style.Backgrounds.TABLE_TH_BACKGROUND.color);
    table.getDefaultCell().setColspan(7);
    table.addCell(new Phrase(getTextProperty("general.file"), Style.NORMAL_FONT));
    table.getDefaultCell().setColspan(3);
    table.addCell(new Phrase(getTextProperty("general.line"), Style.NORMAL_FONT));
    table.getDefaultCell().setColspan(4);
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(new Phrase(getTextProperty("general.priority"), Style.NORMAL_FONT));
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    table.getDefaultCell().setBackgroundColor(Color.WHITE);
    
    int i = 0;
    String lineNumbers = "";
    boolean even = true;
    while (i < files.size() - 1) {
      if (lineNumbers.equals("")) {
        lineNumbers += lines.get(i);
      } else {
        lineNumbers += ", " + lines.get(i);
      }

      if (!files.get(i).equals(files.get(i + 1))) {
        //table.getDefaultCell().setColspan(7);
    	even = !even;
  		Color backgroundColor = Backgrounds.TABLE_ROW_ODD.getColor();
  		if (even)
  			backgroundColor = Backgrounds.TABLE_ROW_EVEN.getColor();
  		
        PdfPCell fileCell = new PdfPCell(new Phrase(files.get(i),Style.smallFont));
        fileCell.setColspan(7);
        fileCell.setBackgroundColor(backgroundColor);
        fileCell.setBorder(0);
        table.addCell(fileCell);

        PdfPCell linenumbersCell = new PdfPCell(new Phrase(lineNumbers,Style.smallFont));
        linenumbersCell.setColspan(3);
        linenumbersCell.setBackgroundColor(backgroundColor);
        linenumbersCell.setBorder(0);
        table.addCell(linenumbersCell);

        String priorityText = priority.get(i);
        PdfPCell priorityCell = new PdfPCell(new Phrase(priorityText,Priority.getPriorityFont(Style.smallFont,priorityText)));
        priorityCell.setColspan(4);
        priorityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        priorityCell.setBackgroundColor(backgroundColor);
        priorityCell.setBorder(0);
        table.addCell(priorityCell);
        lineNumbers = "";
      }
      i++;
    }

    if(files.size() != 0) {
    	
      even = !even;
	  Color backgroundColor = Backgrounds.TABLE_ROW_ODD.getColor();
	  if (even)
	    backgroundColor = Backgrounds.TABLE_ROW_EVEN.getColor();
		
      if (lineNumbers.equals("")) {
        lineNumbers += lines.get(i);
      } else {
        lineNumbers += ", " + lines.get(lines.size() - 1);
      }
      
      PdfPCell fileCell = new PdfPCell(new Phrase(files.get(files.size() - 1),Style.smallFont));
      fileCell.setColspan(7);
      fileCell.setBackgroundColor(backgroundColor);
      fileCell.setBorder(0);
      table.addCell(fileCell);
      
      PdfPCell linenumbersCell = new PdfPCell(new Phrase(lineNumbers,Style.smallFont));
      linenumbersCell.setColspan(3);
      linenumbersCell.setBackgroundColor(backgroundColor);
      linenumbersCell.setBorder(0);
      table.addCell(linenumbersCell);
      
      String priorityText = priority.get(i);
      PdfPCell priorityCell = new PdfPCell(new Phrase(priority.get(priority.size() -1),Priority.getPriorityFont(Style.smallFont,priorityText)));
      priorityCell.setColspan(4);
      priorityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      priorityCell.setBackgroundColor(backgroundColor);
      priorityCell.setBorder(0);
      table.addCell(priorityCell);
      
    }

    table.setSpacingBefore(20);
    table.setSpacingAfter(20);
    table.setLockedWidth(false);
    return table;
  }
}