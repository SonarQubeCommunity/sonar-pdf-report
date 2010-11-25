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
import java.net.MalformedURLException;
import java.net.URL;

import org.sonar.report.pdf.entity.Project;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class Header extends PdfPageEventHelper {

  private URL logo;
  private Project project;

  public Header(URL logo, Project project) {
    this.logo = logo;
    this.project = project;
  }

  public void onEndPage(PdfWriter writer, Document document) {
    try {
      Image logoImage = Image.getInstance(logo);
      Rectangle page = document.getPageSize();
      PdfPTable head = new PdfPTable(4);
      head.getDefaultCell().setVerticalAlignment(PdfCell.ALIGN_MIDDLE);
      head.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_CENTER);
      head.addCell(logoImage);
      Phrase projectName = new Phrase(project.getName(), FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL,
          Color.GRAY));
      Phrase phrase = new Phrase("Sonar PDF Report", FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL,
          Color.GRAY));
      head.getDefaultCell().setColspan(2);
      head.addCell(phrase);
      head.getDefaultCell().setColspan(1);
      head.addCell(projectName);
      head.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
      head.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - 20, writer.getDirectContent());
      head.setSpacingAfter(10);
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
