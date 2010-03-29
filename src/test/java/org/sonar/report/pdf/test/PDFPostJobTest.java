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
package org.sonar.report.pdf.test;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.sonar.api.resources.Project;
import org.sonar.report.pdf.batch.PDFPostJob;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PDFPostJobTest {

    @Test(groups = { "post-job" })
    public void doNotExecuteIfSkipParameter() {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        conf.setProperty(PDFPostJob.SKIP_PDF_KEY, Boolean.TRUE);

        Project project = mock(Project.class);
        when(project.getConfiguration()).thenReturn(conf);

        assertFalse(new PDFPostJob(null).shouldExecuteOnProject(project));
    }

    @Test(groups = { "post-job" })
    public void shouldExecuteIfNoSkipParameter() {
        Project project = mock(Project.class);
        when(project.getConfiguration()).thenReturn(new PropertiesConfiguration());

        assertTrue(new PDFPostJob(null).shouldExecuteOnProject(project));
    }
}
