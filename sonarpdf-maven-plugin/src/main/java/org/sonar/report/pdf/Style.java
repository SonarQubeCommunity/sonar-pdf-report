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
import java.util.Iterator;
import java.util.List;

import org.sonar.report.pdf.entity.Priority;
import org.sonar.report.pdf.util.Logger;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class Style {

	/**
	 * Font used in main chapters title
	 */
	public static final Font CHAPTER_FONT = new Font(Font.TIMES_ROMAN, 18,
			Font.BOLD, Color.GRAY);

	/**
	 * Font used in sub-chapters title
	 */
	public static final Font TITLE_FONT = new Font(Font.TIMES_ROMAN, 14,
			Font.BOLD, Color.GRAY);

	/**
	 * Font used in graphics foots
	 */
	public static final Font FOOT_FONT = new Font(Font.TIMES_ROMAN, 10,
			Font.BOLD, Color.GRAY);

	/**
	 * Font used in general plain text
	 */
	public static final Font NORMAL_FONT = new Font(Font.TIMES_ROMAN, 11,
			Font.NORMAL, Color.BLACK);

	/**
	 * Font used in code text (bold)
	 */
	public static final Font MONOSPACED_BOLD_FONT = new Font(Font.COURIER, 11,
			Font.BOLD, Color.BLACK);

	/**
	 * Font used in code text
	 */
	public static final Font MONOSPACED_FONT = new Font(Font.COURIER, 10,
			Font.NORMAL, Color.BLACK);

	/**
	 * Font used in table of contents title
	 */
	public static final Font TOC_TITLE_FONT = new Font(Font.HELVETICA, 24,
			Font.BOLD, Color.GRAY);

	/**
	 * Font used in front page (Project name)
	 */
	public static final Font FRONTPAGE_FONT_1 = new Font(Font.HELVETICA, 22,
			Font.BOLD, Color.BLACK);

	/**
	 * Font used in front page (Project description)
	 */
	public static final Font FRONTPAGE_FONT_2 = new Font(Font.HELVETICA, 18,
			Font.ITALIC, Color.BLACK);

	/**
	 * Font used in front page (Project date)
	 */
	public static final Font FRONTPAGE_FONT_3 = new Font(Font.HELVETICA, 16,
			Font.BOLDITALIC, Color.GRAY);

	/**
	 * Underlined font
	 */
	public static final Font UNDERLINED_FONT = new Font(Font.HELVETICA, 14,
			Font.UNDERLINE, Color.BLACK);

	/**
	 * Dashboard metric title font
	 */
	public static final Font DASHBOARD_TITLE_FONT = new Font(Font.TIMES_ROMAN,
			14, Font.BOLD, Color.BLACK);

	/**
	 * Dashboard metric value font
	 */
	public static final Font DASHBOARD_DATA_FONT = new Font(Font.TIMES_ROMAN,
			14, Font.BOLD, Color.GRAY);

	/**
	 * Dashboard metric details font
	 */
	public static final Font DASHBOARD_DATA_FONT_2 = new Font(Font.TIMES_ROMAN,
			10, Font.BOLD, new Color(100, 150, 190));

	/**
	 * Tendency icons height + 2 (used in tables style)
	 */
	public static final int TENDENCY_ICONS_HEIGHT = 20;

	public static final float FRONTPAGE_LOGO_POSITION_X = 114;

	public static final float FRONTPAGE_LOGO_POSITION_Y = 542;

	public static final Font smallFont = FontFactory.getFont(FontFactory.TIMES,
			10);
	public static final Font bigFont = FontFactory.getFont(FontFactory.TIMES,
			12);

	public enum Backgrounds{
		TABLE_ROW_EVEN(240, 240, 255),
		TABLE_ROW_ODD(255,255,255),
		TABLE_TH_BACKGROUND(210, 210, 255),
		TABLE_TAB_BACKGROUND(180, 180, 255),
		NONE(255,255,255);
		Color color;
		Backgrounds(int r, int g, int b){
			color = new Color(r,g,b);
		}
		
		public Color getColor(){
			return this.color;
		}
	}
	
	public static void noBorderTable(final PdfPTable table) {
		table.getDefaultCell().setBorderColor(Color.WHITE);
	}

	/**
	 * This method makes a simple table with content.
	 * 
	 * @param left
	 *            Data for left column
	 * @param right
	 *            Data for right column
	 * @param title
	 *            The table title
	 * @param noData
	 *            Showed when left or right are empty
	 * @return The table (iText table) ready to add to the document
	 */
	public static PdfPTable createSimpleTable(final List<String> left,
			final List<String> right, final String title, final String noData) {
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100.0f);
		float[] widths = { 9f, 1f };
		try {
			table.setWidths(widths);
		} catch (DocumentException e) {
			Logger.error("Couldn't set widths to table.");
		}
		table.getDefaultCell().setBorder(0);

		table.getDefaultCell().setColspan(2);
		table.addCell(new Phrase(title, Style.DASHBOARD_TITLE_FONT));
		table.getDefaultCell().setBackgroundColor(Color.GRAY);
		table.addCell("");
		table.getDefaultCell().setColspan(1);
		table.getDefaultCell().setBackgroundColor(Color.WHITE);
		table.getDefaultCell().setNoWrap(false);
		Iterator<String> itLeft = left.iterator();
		Iterator<String> itRight = right.iterator();

		Font actualFont = Style.bigFont;
		Font actualBoldFont = new Font(actualFont);
		actualBoldFont.setStyle(Font.BOLD);

		if (left.size() > 25) {
			actualFont = Style.smallFont;
			actualBoldFont = new Font(actualFont);
			actualBoldFont.setStyle(Font.BOLD);
		}

		table.getDefaultCell().setBorderWidthTop(3);
		table.getDefaultCell().setBorderColorTop(Color.white);

		boolean even = false;

		while (itLeft.hasNext()) {
			even = !even;
			Color backgroundColor = Backgrounds.TABLE_ROW_ODD.getColor();
			if (even)
				backgroundColor = Backgrounds.TABLE_ROW_EVEN.getColor();
			
			String textLeft = itLeft.next();
			String textRight = itRight.next();
			
			PdfPCell leftCell = new PdfPCell(new Phrase(textLeft, actualFont));
			leftCell.setUseBorderPadding(true);
			leftCell.setBorderWidth(0.0f);
			leftCell.setBackgroundColor(backgroundColor);
			table.addCell(leftCell);
			
			PdfPCell rightCell = new PdfPCell(new Phrase(textRight, actualFont));
			rightCell.setUseBorderPadding(true);
			rightCell.setBorderWidth(0.0f);
			rightCell.setBackgroundColor(backgroundColor);
			rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(rightCell);
		}

		if (left.isEmpty()) {
			table.getDefaultCell().setColspan(2);
			table.addCell(noData);
		}

		table.setSpacingBefore(20);
		table.setSpacingAfter(20);

		return table;
	}

	/**
	 * TODO: Generalize this method to n columns.
	 * 
	 * @param left
	 * @param right
	 * @param title
	 * @param noData
	 * @return
	 */
	@Deprecated
	public static PdfPTable createViolatedRulesTable(final List<String> left,
			final List<String> center, final List<String> right,
			final String title, final String noData, List<String> columnTitles) {
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100.0f);
		float[] widths = { 10f, 2f, 2f };
		try {
			table.setWidths(widths);
		} catch (DocumentException e) {
			Logger.error("Couldn't set widths to table.");
		}
		table.setHeaderRows(3);
		table.getDefaultCell().setUseBorderPadding(true);
		table.getDefaultCell().setPaddingBottom(4.0f);
		table.getDefaultCell().setBorder(0);
		table.getDefaultCell().setColspan(3);
		table.addCell(new Phrase(title, Style.DASHBOARD_TITLE_FONT));
		table.getDefaultCell().setBackgroundColor(Color.GRAY);
		table.addCell("");
		table.getDefaultCell().setColspan(1);
		table.getDefaultCell().setBackgroundColor(Color.WHITE);
		table.getDefaultCell().setNoWrap(false);

		Font actualFont = Style.smallFont;

		Font actualBoldFont = new Font(actualFont);
		actualBoldFont.setStyle(Font.BOLD);

		// Setting the last header row

		table.getDefaultCell().setBackgroundColor(Style.Backgrounds.TABLE_TH_BACKGROUND.color);
		table.addCell(new Phrase(columnTitles.get(0), actualBoldFont));
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(new Phrase(columnTitles.get(1), actualBoldFont));
		table.addCell(new Phrase(columnTitles.get(2), actualBoldFont));
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		table.getDefaultCell().setBackgroundColor(Style.Backgrounds.NONE.color);
		Iterator<String> itLeft = left.iterator();
		Iterator<String> itRight = right.iterator();
		Iterator<String> itCenter = center.iterator();
		boolean even = false;
		while (itLeft.hasNext()) {
			even = !even;
			Color backgroundColor = Backgrounds.TABLE_ROW_ODD.getColor();
			if (even)
				backgroundColor = Backgrounds.TABLE_ROW_EVEN.getColor();

			String textLeft = itLeft.next();
			String textCenter = itCenter.next();
			String textRight = itRight.next();

			// Left
			PdfPCell leftCell = new PdfPCell(new Phrase(textLeft, actualFont));
			leftCell.setUseBorderPadding(true);
			leftCell.setBorderWidth(0.0f);
			leftCell.setBackgroundColor(backgroundColor);
			leftCell.setPaddingBottom(4.0f);
			table.addCell(leftCell);

			// Center
			PdfPCell centerCell;
			if (textCenter != null)
				centerCell = new PdfPCell(new Phrase(textCenter,
						Priority.getPriorityFont(actualBoldFont, textCenter)));
			else
				centerCell = new PdfPCell(new Phrase(textCenter, actualFont));
			centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			centerCell.setBorderWidth(0.0f);
			centerCell.setBackgroundColor(backgroundColor);
			centerCell.setPaddingBottom(4.0f);
			table.addCell(centerCell);

			// Right
			PdfPCell rightCell = new PdfPCell(new Phrase(textRight, actualFont));
			rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			rightCell.setBorderWidth(0.0f);
			rightCell.setBackgroundColor(backgroundColor);
			rightCell.setPaddingBottom(4.0f);
			table.addCell(rightCell);
		}

		if (left.isEmpty()) {
			table.getDefaultCell().setColspan(3);
			table.addCell(noData);
		}

		table.setSpacingBefore(20);
		table.setSpacingAfter(20);

		return table;
	}

	public static PdfPTable createTwoColumnsTitledTable(
			final List<String> titles, final List<String> content) {
		PdfPTable table = new PdfPTable(10);
		Iterator<String> itLeft = titles.iterator();
		Iterator<String> itRight = content.iterator();
		while (itLeft.hasNext()) {
			String textLeft = itLeft.next();
			String textRight = itRight.next();
			table.getDefaultCell().setColspan(1);
			table.addCell(textLeft);
			table.getDefaultCell().setColspan(9);
			table.addCell(textRight);
		}
		table.setSpacingBefore(20);
		table.setSpacingAfter(20);
		table.setLockedWidth(false);
		table.setWidthPercentage(90);
		return table;
	}
}
