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
package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class RadarGraphic {

  private float efficiency;
  private float maintainability;
  private float portability;
  private float reliavility;
  private float usability;
  private String sonarUrl;

  public RadarGraphic(float efficiency, float maintainability, float portability, float reliavility, float usability,
      String sonarUrl) {
    this.efficiency = efficiency;
    this.maintainability = maintainability;
    this.portability = portability;
    this.reliavility = reliavility;
    this.usability = usability;
    this.sonarUrl = sonarUrl;
  }

  public Image getGraphic() {
    Image image = null;
    try {
      image = Image.getInstance(sonarUrl + "/chart?ck=xradar&w=200&h=160&" + "c=777777|F8A036&m=100&g=0.25&"
          + "l=Eff.,Mai.,Por.,Rel.,Usa.&" + "v=" + efficiency + "," + maintainability + "," + portability + ","
          + reliavility + "," + usability);
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return image;
  }

}
