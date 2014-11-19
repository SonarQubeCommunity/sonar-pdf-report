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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Add the logo header to the PDF document.
 */
public class Events extends PdfPageEventHelper {

  private static final Logger LOG = LoggerFactory.getLogger(Events.class);

  private Toc toc;
  private Header header;

  public Events(final Toc toc, final Header header) {
    this.toc = toc;
    this.header = header;
    toc.setHeader(header);
  }

  @Override
  public void onChapter(final PdfWriter writer, final Document document, final float position,
      final Paragraph paragraph) {
    toc.onChapter(writer, document, position, paragraph);
  }

  @Override
  public void onChapterEnd(final PdfWriter writer, final Document document, final float position) {
    toc.onChapterEnd(writer, document, position);
  }

  @Override
  public void onSection(final PdfWriter writer, final Document document, final float position,
      final int depth, final Paragraph paragraph) {
    toc.onSection(writer, document, position, depth, paragraph);
  }

  @Override
  public void onEndPage(final PdfWriter writer, final Document document) {
    header.onEndPage(writer, document);
    printPageNumber(writer, document);
  }

  @Override
  public void onCloseDocument(final PdfWriter writer, final Document document) {
    toc.onCloseDocument(writer, document);
  }

  private void printPageNumber(final PdfWriter writer, final Document document) {
    PdfContentByte cb = writer.getDirectContent();
    cb.saveState();
    float textBase = document.bottom() - 20;
    try {
      cb.setFontAndSize(
          BaseFont.createFont("Helvetica", BaseFont.WINANSI, false), 12);
    } catch (DocumentException e) {
      LOG.error("Can not print page number", e);
    } catch (IOException e) {
      LOG.error("Can not print page number", e);
    }
    cb.beginText();
    cb.setTextMatrix(document.right() - 10, textBase);
    cb.showText(String.valueOf(writer.getPageNumber()));
    cb.endText();
    cb.saveState();
  }
}
