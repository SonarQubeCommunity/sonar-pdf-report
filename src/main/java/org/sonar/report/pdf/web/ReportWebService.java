package org.sonar.report.pdf.web;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWebservice;

/**
 * Web Service accesible at sonar.host.url/api/plugins/Pdfreport/hola?resource=kee
 */
public class ReportWebService extends AbstractRubyTemplate implements RubyRailsWebservice {
  
  @Override
  public String getTemplatePath() {
    return "/org/sonar/report/pdf/PdfreportController.rb";
  }
  
  public String getId() {
    return "Pdfreport";
  }

}
