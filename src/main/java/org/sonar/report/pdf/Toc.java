package org.sonar.report.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class Toc extends PdfPageEventHelper {

  private Document toc;
  private ByteArrayOutputStream tocOutputStream;
  private PdfPTable content;
  private PdfWriter writer;

  public Toc() {
    toc = new Document(PageSize.A4, 50, 50, 110, 50);
    content = new PdfPTable(2);
    Rectangle page = toc.getPageSize();
    content.setTotalWidth(page.getWidth() - toc.leftMargin() - toc.rightMargin());
    content.getDefaultCell().setUseVariableBorders(true);
    content.getDefaultCell().setBorderColorBottom(Color.WHITE);
    content.getDefaultCell().setBorderColorRight(Color.WHITE);
    content.getDefaultCell().setBorderColorLeft(Color.WHITE);
    content.getDefaultCell().setBorderColorTop(Color.WHITE);
    content.getDefaultCell().setBorderWidthBottom(2f);
  }

  @Override
  public void onChapter(PdfWriter writer, Document document, float position, Paragraph title) {
    //toc.add(new Paragraph(title.getContent() + " page " + document.getPageNumber()));
    content.getDefaultCell().setBorderColorBottom(Color.LIGHT_GRAY);
    content.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_LEFT);
    content.getDefaultCell().setUseBorderPadding(true);
    content.addCell(new Phrase(title.getContent(), new Font(Font.HELVETICA, 14)));
    content.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
    content.addCell(new Phrase("Page " + document.getPageNumber(), new Font(Font.HELVETICA, 14)));
    content.getDefaultCell().setBorderColorBottom(Color.WHITE);
    content.getDefaultCell().setUseBorderPadding(false);
  }

  @Override
  public void onChapterEnd(PdfWriter writer, Document document, float position) {
    content.addCell("");
    content.addCell("");
  }

  @Override
  public void onSection(PdfWriter writer, Document document, float position, int depth, Paragraph title) {
    content.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_LEFT);
    switch (depth) {
    case 2:
      content.getDefaultCell().setIndent(10);
      content.addCell(new Phrase(title.getContent(), new Font(Font.HELVETICA, 12)));
      content.getDefaultCell().setIndent(0);
      content.addCell("");
      break;
    default:
      content.getDefaultCell().setIndent(20);
      content.addCell(new Phrase(title.getContent(), new Font(Font.HELVETICA, 10)));
      content.getDefaultCell().setIndent(0);
      content.addCell("");
    }
  }

  @Override
  public void onCloseDocument(PdfWriter writer, Document document) {
    //Rectangle page = toc.getPageSize();
    //content.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - 200, this.writer.getDirectContent());
    try {
      toc.add(content);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  public Document getTocDocument() {
    return toc;
  }

  public ByteArrayOutputStream getTocOutputStream() {
    return tocOutputStream;
  }

  public void setHeader(Header header) {
    tocOutputStream = new ByteArrayOutputStream();
    writer = null;
    try {
      writer = PdfWriter.getInstance(toc, tocOutputStream);
      writer.setPageEvent(header);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }
}
