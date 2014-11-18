require 'base64'
require 'fileutils'

class PdfReportController < ApplicationController

  def get
    #project=Project.by_key(params[:resource])
    #measure=project.last_snapshot.measure('pdf-data')
    #send_data(Base64.decode64(measure.data), :filename => "sonar-report.pdf", :type => "application/pdf")
    project = Project.by_key(params[:resource])
    send_file Rails.root.join('pdf-files', project.key.gsub(':', '-') + '.pdf'), :type => 'application/pdf', :disposition => 'attachment'
  end

  def store
    uploaded = params[:upload]
    filename = params[:pdfname] || uploaded.original_filename
    #filename = 'antonio.txt'
    # Rails.root is WEB-INF dir
    FileUtils::mkdir_p Rails.root.join('pdf-files') unless File.exists?(Rails.root.join('pdf-files'))
    File.open(Rails.root.join('pdf-files', filename), 'wb') do |file|
      file.write(uploaded.read)
    end
    render :nothing => true, :status => 200
  end

  def railsroot
    render :text => Rails.root.to_s
  end
end