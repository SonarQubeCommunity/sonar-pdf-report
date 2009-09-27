/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonar.report.pdf.batch;

import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.resources.Project;
import org.slf4j.LoggerFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

public class PDFMavenPluginHandler implements MavenPluginHandler {

  public String getGroupId() {
    return "org.codehaus.sonar-plugins";
  }

  public String getArtifactId() {
    return "pdf-report";
  }

  public String getVersion() {
    InputStream input = null;
    try {
      Properties props = new Properties();
      input = this.getClass().getResourceAsStream(
          "/META-INF/maven/org.codehaus.sonar-plugins/pdf-report/pom.properties");
      props.load(input);
      return props.getProperty("version");

    } catch (IOException e) {
      LoggerFactory.getLogger(getClass()).error("can not load the plugin version from report.properties", e);
      return null;

    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[] { "generate" };
  }

  public void configure(Project project, MavenPlugin plugin) {
    plugin.setParameter("reportType", project.getConfiguration().getString(PDFPostJob.REPORT_TYPE,
        PDFPostJob.REPORT_TYPE_DEFAULT_VALUE));
  }
}
