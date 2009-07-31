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

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.CheckProject;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

public class PDFPostJob implements PostJob, DependsUponMavenPlugin, CheckProject {

  public static final String SKIP_PDF_KEY = "sonar.pdf.skip";
  public static final boolean SKIP_PDF_DEFAULT_VALUE = false;

  private PDFMavenPluginHandler handler;

  public PDFPostJob(PDFMavenPluginHandler handler) {
    this.handler = handler;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return !project.getConfiguration().getBoolean(SKIP_PDF_KEY, SKIP_PDF_DEFAULT_VALUE);
  }

  public void executeOn(Project project, SensorContext context) {
     // nothing to do, the maven plugin has been executed 
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    return handler;
  }
}
