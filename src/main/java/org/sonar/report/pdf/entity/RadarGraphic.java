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
