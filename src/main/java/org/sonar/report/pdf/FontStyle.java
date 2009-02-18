package org.sonar.report.pdf;

import java.awt.Color;

import com.lowagie.text.Font;

public class FontStyle {
  
  public final static Font chapterFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD, Color.GRAY);
  public final static Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
  public final static Font footFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.GRAY);
  public final static Font normalFont = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL, Color.BLACK);
  public final static Font monospaceFont = new Font(Font.COURIER, 12, Font.BOLD, Color.BLACK);
  public final static Font tocTitleFont = new Font(Font.HELVETICA, 24, Font.BOLD, Color.GRAY);
  public final static Font frontPageFont1 = new Font(Font.HELVETICA, 22, Font.BOLD, Color.BLACK);
  public final static Font frontPageFont2 = new Font(Font.HELVETICA, 18, Font.ITALIC, Color.BLACK);
  public final static Font frontPageFont3 = new Font(Font.HELVETICA, 16, Font.BOLDITALIC, Color.GRAY);
  public final static Font underlinedFont = new Font(Font.HELVETICA, 14, Font.UNDERLINE, Color.BLACK);

}
