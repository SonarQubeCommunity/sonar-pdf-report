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
import org.sonar.report.pdf.entity.ComplexityDistribution;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class ComplexityDistributionBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(ComplexityDistributionBuilder.class);

  private static ComplexityDistributionBuilder builder;

  private String sonarBaseUrl;

  private ComplexityDistributionBuilder(final String sonarBaseUrl) {
    this.sonarBaseUrl = sonarBaseUrl;
  }

  public static ComplexityDistributionBuilder getInstance(final String sonarBaseUrl) {
    if (builder == null) {
      return new ComplexityDistributionBuilder(sonarBaseUrl);
    }

    return builder;
  }

  public Image getGraphic(final ComplexityDistribution complexityDistribution) {
    Image image = null;
    try {
      if (complexityDistribution.getyValues().length != 0) {
        image = Image
            .getInstance(sonarBaseUrl
                + "/chart?cht=cvb&chdi=300x200&chca="
                + complexityDistribution.formatXValues()
                + "&chov=y&chrav=y&chv="
                + complexityDistribution.formatYValues()
                + "&chorgv=y&chcaaml=0.05&chseamu=0.2&chins=5&chcaamu=0.05&chcav=y&chc=777777,777777,777777,777777,777777,777777,777777");
        image.setAlignment(Image.ALIGN_MIDDLE);
      }
    } catch (BadElementException e) {
      LOG.error("Can not generate complexity distribution image", e);
    } catch (MalformedURLException e) {
      LOG.error("Can not generate complexity distribution image", e);
    } catch (IOException e) {
      LOG.error("Can not generate complexity distribution image", e);
    }
    return image;
  }

}
