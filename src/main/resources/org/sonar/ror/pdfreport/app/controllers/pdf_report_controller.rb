require 'base64'
require 'fileutils'

class PdfReportController < ApplicationController

  def get
    project = Project.by_key(params[:resource])
    send_file Rails.root.join('pdf-files', project.key.gsub(':', '-') + '.pdf'), :type => 'application/pdf', :disposition => 'attachment'
  end

  def store
    uploaded = params[:upload]
    filename = params[:pdfname] || uploaded.original_filename
    # Rails.root is WEB-INF dir
    FileUtils::mkdir_p Rails.root.join('pdf-files') unless File.exists?(Rails.root.join('pdf-files'))
    File.open(Rails.root.join('pdf-files', filename), 'wb') do |file|
      file.write(uploaded.read)
    end
    render :nothing => true, :status => 200
  end
end