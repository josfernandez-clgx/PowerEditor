<%@ page import="com.crystaldecisions.report.web.viewer.ReportExportControl" %>
<%@ page import="com.crystaldecisions.sdk.occa.report.exportoptions.ExportOptions" %>
<%@ page import="com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat" %>
<%@ page import="com.crystaldecisions.sdk.occa.report.exportoptions.PDFExportFormatOptions" %>

<%
    Object reportSource = session.getAttribute("reportSource");
    if (reportSource == null) {
%>
		<jsp:forward page="/invalid_request.jsp">
			<jsp:param name="reason" value="report source not available."/>
		</jsp:forward>
<%	
    }
	else {
	    ReportExportControl exportControl = new ReportExportControl();
	    ExportOptions exportOptions = new ExportOptions();
	    exportOptions.setExportFormatType(ReportExportFormat.PDF);
	    PDFExportFormatOptions pdfOptions = new PDFExportFormatOptions();
	    pdfOptions.setStartPageNumber(1);
	    pdfOptions.setEndPageNumber(999);
	    exportOptions.setFormatOptions(pdfOptions);    
	    exportControl.setReportSource(reportSource);
	    exportControl.setExportOptions(exportOptions);
	    exportControl.setExportAsAttachment(true);
	    exportControl.processHttpRequest(request, response, getServletConfig().getServletContext(), null);
	}
%>