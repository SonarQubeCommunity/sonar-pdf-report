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
package org.sonar.report.pdf.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.report.pdf.batch.PDFPostJob;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class PDFPostJobTest {

  private Project project;
  private Settings settings;
  private DefaultFileSystem fs;
  private PDFPostJob pdfPostJob;

  @BeforeGroups(groups = { "post-job" })
  public void before() {
    project = mock(Project.class);
    settings = mock(Settings.class);
    fs = new DefaultFileSystem();
    pdfPostJob = new PDFPostJob(settings, fs);
  }

  @Test(groups = { "post-job" })
  public void doNotExecuteIfSkipParameter() {
    when(settings.hasKey(PDFPostJob.SKIP_PDF_KEY)).thenReturn(Boolean.TRUE);
    when(settings.getBoolean(PDFPostJob.SKIP_PDF_KEY)).thenReturn(Boolean.TRUE);

    assertFalse(pdfPostJob.shouldExecuteOnProject(project));
  }

  @Test(groups = { "post-job" })
  public void shouldExecuteIfNoSkipParameter() {
    when(settings.hasKey(PDFPostJob.SKIP_PDF_KEY)).thenReturn(Boolean.FALSE);

    assertTrue(pdfPostJob.shouldExecuteOnProject(project));
  }
}
