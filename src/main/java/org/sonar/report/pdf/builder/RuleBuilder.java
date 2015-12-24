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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.HttpDownloader.HttpException;
import org.sonar.report.pdf.entity.Rule;
import org.sonar.report.pdf.entity.Violation;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Credentials;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;
import org.sonar.wsclient.issue.Issues;

public class RuleBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(RuleBuilder.class);

  private static RuleBuilder builder;

  private Credentials credentials;

  public RuleBuilder(final Credentials credentials) {
    this.credentials = credentials;
  }

  public static RuleBuilder getInstance(final Credentials credentials, final Sonar sonar) {
    if (builder == null) {
      return new RuleBuilder(credentials);
    }

    return builder;
  }

  /**
   * Initialize a rule given an XML Node that contains one rule
   * 
   * @return
   */
  public Rule initFromNode(final org.sonar.wsclient.services.Measure ruleNode) {
    Rule rule = new Rule();
    rule.setKey(ruleNode.getRuleKey());
    rule.setName(ruleNode.getRuleName());
    rule.setViolationsNumber(ruleNode.getValue());
    rule.setViolationsNumberFormatted(ruleNode.getFormattedValue());
    return rule;
  }

  /**
   * This method provide the possibility of init a Rule without init all
   * violated resources.
   * 
   * @return
   * @throws UnsupportedEncodingException
   * @throws DocumentException
   * @throws IOException
   * @throws HttpException
   */
  public void loadViolatedResources(final Rule rule, final String ruleKey, final String projectKey)
      throws ReportException, UnsupportedEncodingException {

    if (ruleKey == null) {
      throw new ReportException("Rule not initialized. Forget call to initFromNode() previously?");
    } else {
      // ruleKey = URLEncoder.encode(ruleKey, "UTF8");
      LOG.debug("Accessing Sonar: getting violated resurces by one given rule (" + ruleKey + ")");

      SonarClient client = SonarClient.builder().url(credentials.getUrl())
                                                .login(credentials.getUsername())
                                                .password(credentials.getPassword())
                                                .build();
      IssueClient issueClient = client.issueClient();

      IssueQuery issueQuery = IssueQuery.create();
      issueQuery.componentRoots(projectKey);
      issueQuery.pageSize(20);
      issueQuery.rules(ruleKey);
      // "&scopes=FIL&depth=-1&limit=20

      Issues issues = issueClient.find(issueQuery);

      List<Issue> violatedResources = issues.list();
      List<Violation> topViolatedResources = new LinkedList<Violation>();
      rule.setTopViolatedResources(topViolatedResources);
      Iterator<Issue> it = violatedResources.iterator();

      while (it.hasNext()) {
        Issue resource = it.next();
        if (rule.getMessage() == null) {
          rule.setMessage(resource.message());
        }
        // resource key as: net.java.openjdk:jdk7:src/com/sun/rowset/internal/CachedRowSetReader.java
        String resourceKey = resource.componentKey();
        String line = "N/A";
        Integer resourceLine = resource.line();
        if (resourceLine != null) {
          line = String.valueOf(resourceLine);
        }
        topViolatedResources.add(new Violation(line, resourceKey, ""));
      }
    }
  }

}
