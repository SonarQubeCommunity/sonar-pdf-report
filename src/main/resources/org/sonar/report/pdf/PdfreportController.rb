require "base64"

class Api::PdfreportController < ApplicationController
  def getReport
    project=Project.by_key(params[:resource])
    measure=project.last_snapshot.measure('pdf-data')
    send_data(Base64.decode64(measure.data), :filename => "sonar-report.pdf", :type => "application/pdf")
  end
end