package org.sonar.report.pdf.plugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;

/**
 * {@inheritDoc}
 */
public final class PdfReportWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  protected String getTemplatePath() {
    return "/org/sonar/report/pdf/dashboard_widget.erb";
  }

  public String getId() {
    return "pdf-report-widget";
  }

  public String getTitle() {
    return "PDF report widget";
  }
}