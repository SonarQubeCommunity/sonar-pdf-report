package org.sonar.report.pdf.builder;

import java.io.IOException;
import java.net.MalformedURLException;

import org.sonar.report.pdf.entity.RadarGraphic;
import org.sonar.report.pdf.util.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class RadarGraphicBuilder {

	private static RadarGraphicBuilder builder;

	private String sonarBaseUrl;

	private RadarGraphicBuilder(String sonarBaseUrl) {
		this.sonarBaseUrl = sonarBaseUrl;
	}

	public static RadarGraphicBuilder getInstance(String sonarBaseUrl) {
		if (builder == null) {
			return new RadarGraphicBuilder(sonarBaseUrl);
		}

		return builder;
	}

	public Image getGraphic(RadarGraphic radarGraphic) {
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
					+ radarGraphic.getPortability() + ","
					+ radarGraphic.getReliavility() + ","
					+ radarGraphic.getUsability();
			Logger.debug("Getting radar graphic: " + requestUrl);
			image = Image.getInstance(requestUrl);
			image.setAlignment(Image.ALIGN_MIDDLE);
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
