<%@ page import="com.crystaldecisions.report.web.viewer.CrystalReportViewer,
                 com.crystaldecisions.sdk.occa.report.data.*,
                 com.crystaldecisions.reports.sdk.ReportClientDocument" %>
<%@ page import="com.crystaldecisions.report.web.viewer.CrystalImageCleaner" %>
<%
	ReportClientDocument reportDocument = (ReportClientDocument) session.getAttribute("reportDocument");
	String reportName = request.getParameter("reportname");
	if (reportDocument == null) {
%>
		<jsp:forward page="/invalid_request.jsp">
			<jsp:param name="reason" value="report source not available."/>
		</jsp:forward>
<%	
	}
	else {
		// set reports parameters from input parameters
		CrystalReportViewer viewer = new CrystalReportViewer();
		viewer.setReportSource(reportDocument.getReportSource());
		viewer.setHasLogo(false);
		viewer.setSeparatePages(true);
		viewer.setOwnPage(true);
		viewer.setOwnForm(true);
		
		Fields fields = (Fields) session.getAttribute("reportParameterFields");
		if (fields != null && !fields.isEmpty()) {
			viewer.setParameterFields(fields);
		    viewer.setEnableParameterPrompt(false);
		    viewer.setReuseParameterValuesOnRefresh(true);
			//viewer.refresh();
		}
		else {
			viewer.setEnableParameterPrompt(true);
		}
		
		//refresh the CrystalReportViewer if necessary (only required once)
		if (session.getAttribute("refreshed") == null) {
			viewer.refresh();
			session.setAttribute("refreshed", "true");
		}
		viewer.processHttpRequest(request, response, getServletConfig().getServletContext(), null);
	}
%>
<%-- Required to remove unused Crystal Reports resources  --%>
<%!
public void jspInit(){
    CrystalImageCleaner.start(getServletContext(), 60000, 12000);
}    
%>

<%!
public void jspDestroy(){
    CrystalImageCleaner.stop(getServletContext());
}
%>