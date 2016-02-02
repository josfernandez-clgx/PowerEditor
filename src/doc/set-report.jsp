<%@ page import="com.mindbox.pe.wrapper.crystalreports.ReportUtil" %>
<%
	String reportName = request.getParameter("reportname");
	if (reportName == null || reportName.trim().length() == 0) { %>
		<jsp:forward page="/invalid_request.jsp">
			<jsp:param name="reason" value="report name not provided."/>
		</jsp:forward>
<%	
	}
	// prepare Crystal Reports report document
	String reportID = request.getParameter("reportid");
	
	Object reportDocument = (reportID != null && reportID.length() > 0 ? 
							ReportUtil.createPolicySummaryReportSource(reportName, reportID) : 
							ReportUtil.createCustomReportSource(reportName));

	session.setAttribute("reportDocument", reportDocument);
	
	// store reports parameters from input parameters
	session.setAttribute("reportParameterFields", ReportUtil.createParameterFields(reportDocument, request));

	// launch viewer
	response.sendRedirect("view-report.jsp");
%>
