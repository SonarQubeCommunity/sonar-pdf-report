package org.sonar.report.pdf.builder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sonar.report.pdf.entity.FileInfo;
import org.sonar.report.pdf.util.MetricKeys;
import org.sonar.wsclient.services.Resource;

public class FileInfoBuilder {

	public static List<FileInfo> initFromDocument(List<Resource> resources,
			int content) {
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
	 *            DOM Node that contains file info
	 * @param content
	 *            Type of content
	 */
	public static void initFromNode(FileInfo fileInfo, Resource fileNode,
			int content) {
		fileInfo.setKey(fileNode.getKey());
		fileInfo.setName(fileNode.getName());

		if (content == FileInfo.VIOLATIONS_CONTENT) {
			fileInfo.setViolations(fileNode.getMeasure(MetricKeys.VIOLATIONS)
					.getFormattedValue());
		} else if (content == FileInfo.CCN_CONTENT) {
			fileInfo.setComplexity(fileNode.getMeasure(MetricKeys.COMPLEXITY)
					.getFormattedValue());
		} else if (content == FileInfo.DUPLICATIONS_CONTENT) {
			fileInfo.setDuplicatedLines(fileNode.getMeasure(MetricKeys.DUPLICATED_LINES)
					.getFormattedValue());
		}
	}

}
