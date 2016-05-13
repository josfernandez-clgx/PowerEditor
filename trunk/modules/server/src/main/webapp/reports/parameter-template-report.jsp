<?xml version="1.0" encoding="UTF-8"?>

<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.model.*,
                 com.mindbox.pe.common.UtilBase,
                 com.mindbox.pe.server.cache.EntityManager,
                 com.mindbox.pe.server.cache.ParameterTemplateManager" %>                 
<%@ include file="report-util.jsp" %>
<templates>
<%
	String templateNameStr = request.getParameter("template");
	String templateIDStr = request.getParameter("templateid");	
	
	StringBuffer errorMessage = new StringBuffer();
	java.util.List templateList = new java.util.ArrayList();
	if (UtilBase.isEmpty(templateIDStr) && UtilBase.isEmpty(templateNameStr)) {
		templateList.addAll(ParameterTemplateManager.getInstance().getTemplates());
	}
	else if (!UtilBase.isEmpty(templateNameStr)) {
		String[] templateNames = templateNameStr.split(",");
		for (int i = 0; i < templateNames.length; i++) {
			ParameterTemplate template = ParameterTemplateManager.getInstance().getTemplate(templateNames[i].trim());
			if (template != null) {
				templateList.add(template);
			}
			else {
				addErrorMessage(errorMessage, "No parameter template with name " + templateNames[i]+ " found");
			}
		}
	}
	else {
		try {
			templateList.addAll(ParameterTemplateManager.getInstance().getTemplates(UtilBase.toIntArray(templateIDStr.trim())));
		}
		catch (NumberFormatException ex) { 
			addErrorMessage(errorMessage, "Invalid paremter template ids: must be integer delimited by comma.");
		}
	}
	
	if (templateList == null || templateList.isEmpty()) { 
			addErrorMessage(errorMessage, "No paremter template report generated: no parameter templates found.");
	}
	else {
		for (java.util.Iterator iter = templateList.iterator(); iter.hasNext();) {
			ParameterTemplate template = (ParameterTemplate) iter.next();
			int numberOfColumns = template.getColumnCount();
%>
  <parameter-template template-id="<%=template.getID()%>">
    <name><%=ReportGenerator.htmlify(template.getName())%></name>
    <description><%=ReportGenerator.formatAsTextElements(template.getDescription())%></description>
    <max-rows><%=template.getMaxNumOfRows()%></max-rows>
    <column-count><%=numberOfColumns%></column-count>
<%	
			for (int col = 1; col <= numberOfColumns; ++col) { 
				ParameterTemplateColumn templateColumn = (ParameterTemplateColumn)template.getColumn(col); 
%>
	<template-column template-id="<%=template.getID()%>" column-no="<%=templateColumn.getID()%>">
	  <name><%=templateColumn.getName()%></name>
	  <description><%=ReportGenerator.formatAsTextElements(templateColumn.getDescription())%></description>
	  <title><%=ReportGenerator.htmlify(templateColumn.getTitle())%></title>
	  <font><%=templateColumn.getFont()%></font>
	  <color><%=templateColumn.getColor()%></color>
	  <width><%=templateColumn.getColumnWidth()%></width>
	  <attribute-map><%=(templateColumn.getMappedAttribute()==null?"":templateColumn.getMappedAttribute())%></attribute-map>
	  <data-spec>
	    <type><%=templateColumn.getColumnDataSpecDigest().getType()%></type>
	    <min><%=templateColumn.getColumnDataSpecDigest().getMinValue()%></min>
	    <max><%=templateColumn.getColumnDataSpecDigest().getMaxValue()%></max>
	    <allow-blank><%=(templateColumn.getColumnDataSpecDigest().isBlankAllowed()?"Yes":"No")%></allow-blank>
	  </data-spec>
	</template-column>
<%
			}	// for columns in template
%>
  </parameter-template>
<%
		}	// for templates in templateList
	}	// if templates are found
	
	if (errorMessage.length() > 0) {
		out.print("<error-message>" + ReportGenerator.formatAsTextElements(errorMessage.toString()) + "</error-message>");
	}
%>
</templates>
