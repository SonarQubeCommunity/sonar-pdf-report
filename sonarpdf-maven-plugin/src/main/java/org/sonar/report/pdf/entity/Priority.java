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
package org.sonar.report.pdf.entity;

import java.awt.Color;
import java.util.HashMap;

import com.lowagie.text.Font;

/**
 * Priorities.
 */
public class Priority {

  public static final String INFO = "INFO";
  public static final String MINOR = "MINOR";
  public static final String MAJOR = "MAJOR";
  public static final String CRITICAL = "CRITICAL";
  public static final String BLOCKER = "BLOCKER";

  public static HashMap<String,Color> priorityColors;
  
  public static String[] getPrioritiesArray() {
    return new String[]{INFO, MINOR, MAJOR, CRITICAL, BLOCKER};
  }
  
  public static Font getPriorityFont(Font f,String priority){
	  if(priorityColors == null){
		  priorityColors = new HashMap<String,Color>();
		  priorityColors.put(INFO, Color.black);
		  priorityColors.put(MINOR, new Color(0,128,0));
		  priorityColors.put(MAJOR, new Color(205,145,0));
		  priorityColors.put(CRITICAL, new Color(255,36,0));
		  priorityColors.put(BLOCKER, new Color(255,0,0));
	  }
	  Font resp = new Font(f);
	  resp.setColor(priorityColors.get(priority));
	  //resp.setStyle(Font.)
	  return resp;
  }
}
