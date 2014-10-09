package org.sonar.report.pdf.builder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.sonar.report.pdf.entity.Rule;
import org.sonar.report.pdf.entity.Violation;
import org.sonar.report.pdf.entity.exception.ReportException;
import org.sonar.report.pdf.util.Credentials;
import org.sonar.report.pdf.util.Logger;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;
import org.sonar.wsclient.issue.Issues;

import com.lowagie.text.DocumentException;

public class RuleBuilder {
	
	private static RuleBuilder builder;

	private Credentials credentials;

	private Sonar sonar;
	
	
	public RuleBuilder(Credentials credentials, Sonar sonar) {
		this.credentials = credentials;
		this.sonar = sonar;
	}

	public static RuleBuilder getInstance(Credentials credentials,
			Sonar sonar) {
		if (builder == null) {
			return new RuleBuilder(credentials, sonar);
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
	public void loadViolatedResources(Rule rule, String ruleKey, final String projectKey)
			throws ReportException, UnsupportedEncodingException {
		
		
		if (ruleKey == null) {
			throw new ReportException(
					"Rule not initialized. Forget call to initFromNode() previously?");
		} else {
			// ruleKey = URLEncoder.encode(ruleKey, "UTF8");
			Logger.debug("Accessing Sonar: getting violated resurces by one given rule ("
					+ ruleKey + ")");

			SonarClient client = SonarClient.create(credentials.getUrl());
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
