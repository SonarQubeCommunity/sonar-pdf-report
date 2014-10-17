package org.sonar.report.pdf.builder;

import java.io.IOException;
import java.net.MalformedURLException;

import org.sonar.report.pdf.entity.ComplexityDistribution;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class ComplexityDistributionBuilder {

  private static ComplexityDistributionBuilder builder;

  private String sonarBaseUrl;

  private ComplexityDistributionBuilder(String sonarBaseUrl) {
    this.sonarBaseUrl = sonarBaseUrl;
  }

  public static ComplexityDistributionBuilder getInstance(String sonarBaseUrl) {
    if (builder == null) {
      return new ComplexityDistributionBuilder(sonarBaseUrl);
    }

    return builder;
  }

  public Image getGraphic(ComplexityDistribution complexityDistribution) {
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
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return image;
  }

}
