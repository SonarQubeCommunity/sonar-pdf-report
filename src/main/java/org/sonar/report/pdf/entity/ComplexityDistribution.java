/*
 * Sonar PDF Plugin, open source plugin for Sonar
 *
 * Copyright (C) 2009 GMV-SGI
 * Copyright (C) 2010 klicap - ingenieria del puzle
 *
 * Sonar PDF Plugin is free software; you can redistribute it and/or
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

/**
 * This class provides the complexity distribution graphic.
 */
public class ComplexityDistribution {

    String[] xValues;
    String[] yValues;
    String sonarBaseUrl;

    public ComplexityDistribution(String data, String sonarBaseUrl) {
        this.sonarBaseUrl = sonarBaseUrl;
        String[] unitData = data.split(";");
        xValues = new String[unitData.length];
        yValues = new String[unitData.length];
        if (!data.equals("N/A")) {
            for (int i = 0; i < unitData.length; i++) {
                String[] values = unitData[i].split("=");
                xValues[i] = values[0];
                yValues[i] = values[1];
            }
        }
    }

    public Image getGraphic() {
        Image image = null;
        try {
            if (yValues.length != 0) {
                image = Image
                        .getInstance(sonarBaseUrl
                                + "/chart?cht=cvb&chdi=300x200&chca="
                                + formatXValues()
                                + "&chov=y&chrav=y&chv="
                                + formatYValues()
                                + "&chorgv=y&chcaaml=0.05&chseamu=0.2&chins=5&chcaamu=0.05&chcav=y&chc=777777,777777,777777,777777,777777,777777,777777");
                image.setAlignment(Image.ALIGN_MIDDLE);
            }
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private String formatYValues() {
        String formatValues = "";
        for (int i = 0; i < yValues.length; i++) {
            if (i != yValues.length - 1) {
                formatValues += yValues[i] + ",";
            } else {
                formatValues += yValues[i];
            }
        }
        return formatValues;
    }

    private String formatXValues() {
        String formatValues = "";
        for (int i = 0; i < xValues.length; i++) {
            if (i != xValues.length - 1) {
                formatValues += xValues[i] + "%2b,";
            } else {
                formatValues += xValues[i] + "%2b";
            }
        }
        return formatValues;
    }
}
