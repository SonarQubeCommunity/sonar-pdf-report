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
package org.sonar.report.pdf.builder;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.report.pdf.entity.RadarGraphic;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class RadarGraphicBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(RadarGraphicBuilder.class);

  private static RadarGraphicBuilder builder;

  private String sonarBaseUrl;

  private RadarGraphicBuilder(final String sonarBaseUrl) {
    this.sonarBaseUrl = sonarBaseUrl;
  }

  public static RadarGraphicBuilder getInstance(final String sonarBaseUrl) {
    if (builder == null) {
      return new RadarGraphicBuilder(sonarBaseUrl);
    }

    return builder;
  }

  public Image getGraphic(final RadarGraphic radarGraphic) {
    Image image = null;

    try {
      String requestUrl = sonarBaseUrl
          + "/chart?ck=xradar&w=210&h=110&c=777777|F8A036&m=100&g=0.25&"
          + "l=Eff.(" + radarGraphic.getEfficiency() + "%25),Mai.("
          + radarGraphic.getMaintainability() + "%25),Por.("
          + radarGraphic.getPortability() + "%25),Rel.("
          + radarGraphic.getReliavility() + "%25),Usa.("
          + radarGraphic.getUsability() + "%25)&" + "v="
          + radarGraphic.getEfficiency() + ","
          + radarGraphic.getMaintainability() + ","
          + radarGraphic.getPortability() + "," + radarGraphic.getReliavility()
          + "," + radarGraphic.getUsability();
      LOG.debug("Getting radar graphic: " + requestUrl);
      image = Image.getInstance(requestUrl);
      image.setAlignment(Image.ALIGN_MIDDLE);
    } catch (BadElementException e) {
      LOG.error("Can not generate radar graphic", e);
    } catch (MalformedURLException e) {
      LOG.error("Can not generate radar graphic", e);
    } catch (IOException e) {
      LOG.error("Can not generate radar graphic", e);
    }
    return image;
  }

}
