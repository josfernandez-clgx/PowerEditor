<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" %>
<%@ include file="../includes/global.jsp" %>


<!-- 
PowerEditor Entity Report.
 -->
 
<pe:entity-report
	date="<%=request.getParameter("date")%>"
	entityType="<%=request.getParameter("entity-type")%>" />
