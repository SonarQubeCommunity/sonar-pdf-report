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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sonar.report.pdf.entity.FileInfo;
import org.sonar.report.pdf.util.MetricKeys;
import org.sonar.wsclient.services.Resource;

public class FileInfoBuilder {

  public static List<FileInfo> initFromDocument(final List<Resource> resources,
      final int content) {
    List<FileInfo> fileInfoList = new LinkedList<FileInfo>();
    if (resources != null) {
      Iterator<Resource> it = resources.iterator();
      while (it.hasNext()) {
        Resource fileNode = it.next();
        FileInfo fileInfo = new FileInfo();
        initFromNode(fileInfo, fileNode, content);
        if (fileInfo.isContentSet(content)) {
          fileInfoList.add(fileInfo);
        }

      }
    }
    return fileInfoList;
  }

  /**
   * A FileInfo object could contain information about violations, ccn or
   * duplications, this cases are distinguished in function of content param,
   * and defined by project context.
   * 
   * @param fileNode
   *          DOM Node that contains file info
   * @param content
   *          Type of content
   */
  public static void initFromNode(final FileInfo fileInfo, final Resource fileNode,
      final int content) {
    fileInfo.setKey(fileNode.getKey());
    fileInfo.setName(fileNode.getName());

    if (content == FileInfo.VIOLATIONS_CONTENT) {
      fileInfo.setViolations(fileNode.getMeasure(MetricKeys.VIOLATIONS)
          .getFormattedValue());
    } else if (content == FileInfo.CCN_CONTENT) {
      fileInfo.setComplexity(fileNode.getMeasure(MetricKeys.COMPLEXITY)
          .getFormattedValue());
    } else if (content == FileInfo.DUPLICATIONS_CONTENT) {
      fileInfo.setDuplicatedLines(fileNode.getMeasure(
          MetricKeys.DUPLICATED_LINES).getFormattedValue());
    }
  }

}
