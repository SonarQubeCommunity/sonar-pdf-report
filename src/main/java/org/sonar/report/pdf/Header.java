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
