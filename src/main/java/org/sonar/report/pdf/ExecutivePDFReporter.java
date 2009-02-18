package org.sonar.report.pdf;

import java.io.IOException;
import java.net.URL;

import org.dom4j.DocumentException;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

public class ExecutivePDFReporter extends PDFReporter {

    @Override
    protected URL getLogo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getProjectKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getSonarUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter) throws DocumentException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void printPdfBody(Document document) throws com.lowagie.text.DocumentException, IOException,
        DocumentException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void printTocTitle(Toc tocDocument) throws com.lowagie.text.DocumentException, IOException {
        // TODO Auto-generated method stub
        
    }

}
