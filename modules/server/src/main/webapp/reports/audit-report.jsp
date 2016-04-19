<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" %>
<%@ include file="../includes/global.jsp" %>
<pe:audit-report
	templateName='<%=request.getParameter("template")%>'
	templateID='<%=request.getParameter("templateid")%>'
	usageType='<%=request.getParameter("usage")%>'
	columns='<%=request.getParameter("columns")%>'
	contextElements='<%=request.getParameter("context-elements")%>'
	includeChildren='<%=request.getParameter("include-children")%>'
	includeParents='<%=request.getParameter("include-parents")%>'
	includeEmptyContexts='<%=request.getParameter("include-empty-contexts")%>'
	status='<%=request.getParameter("status")%>'
	beginDate='<%=request.getParameter("begin-date")%>'
	endDate='<%=request.getParameter("end-date")%>'
	auditTypes='<%=request.getParameter("type")%>' />
