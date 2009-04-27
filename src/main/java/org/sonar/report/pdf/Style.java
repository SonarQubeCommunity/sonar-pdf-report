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

import java.awt.Color;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

public class Style {
  
  /**
   * Font used in main chapters title
   */
  public final static Font chapterFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD, Color.GRAY);
  
  /**
   * Font used in sub-chapters title
   */
  public final static Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
  
  /**
   * Font used in graphics foots
   */
  public final static Font footFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.GRAY);
  
  /**
   * Font used in general plain text
   */
  public final static Font normalFont = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL, Color.BLACK);
  
  /**
   * Font used in code text
   */
  public final static Font monospaceFont = new Font(Font.COURIER, 12, Font.BOLD, Color.BLACK);
  
  /**
   * Font used in table of contents title
   */
  public final static Font tocTitleFont = new Font(Font.HELVETICA, 24, Font.BOLD, Color.GRAY);
  
  /**
   * Font used in front page (Project name)
   */
  public final static Font frontPageFont1 = new Font(Font.HELVETICA, 22, Font.BOLD, Color.BLACK);
  
  /**
   * Font used in front page (Project description)
   */
  public final static Font frontPageFont2 = new Font(Font.HELVETICA, 18, Font.ITALIC, Color.BLACK);
  
  /**
   * Font used in front page (Project date)
   */
  public final static Font frontPageFont3 = new Font(Font.HELVETICA, 16, Font.BOLDITALIC, Color.GRAY);
  
  /**
   * Underlined font
   */
  public final static Font underlinedFont = new Font(Font.HELVETICA, 14, Font.UNDERLINE, Color.BLACK);
  
  /**
   * Dashboard metric title font
   */
  public final static Font dashboardTitleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK);
  
  /**
   * Dashboard metric value font
   */
  public final static Font dashboardDataFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
  
  /**
   * Dashboard metric details font
   */
  public final static Font dashboardDataFont2 = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, new Color(100, 150, 190));
  
  /**
   * Tendency icons height + 2 (used in tables style)
   */
  public final static int tendencyIconsHeight = 20;
  
  public static void noBorderTable(PdfPTable table) {
    table.getDefaultCell().setBorderColor(Color.WHITE);
  }
}
