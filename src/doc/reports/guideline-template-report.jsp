<?xml version="1.0" encoding="UTF-8"?>

<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.model.*,
                 com.mindbox.pe.model.rule.RuleDefinition,
                 com.mindbox.pe.common.UtilBase,
                 com.mindbox.pe.server.cache.EntityManager,
                 com.mindbox.pe.server.cache.GuidelineTemplateManager,
                 com.mindbox.pe.server.report.RuleDefinitionReportWriter" %>                 
<%@ include file="report-util.jsp" %>
<templates>
<%
	String templateNameStr = request.getParameter("template");
	String templateIDStr = request.getParameter("templateid");
	String usageStr = request.getParameter("usage");
	
	StringBuffer errorMessage = new StringBuffer();
	java.util.List templateList = new java.util.ArrayList();
	if (UtilBase.isEmpty(templateIDStr) && UtilBase.isEmpty(usageStr) && UtilBase.isEmpty(templateNameStr)) { 
		templateList.addAll((GuidelineTemplateManager.getInstance().getAllTemplates()));
	}
	else if (!UtilBase.isEmpty(usageStr)) {
		String[] usageNames = usageStr.split(",");
		for (int i = 0; i < usageNames.length; i++) {
			try {
				templateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(TemplateUsageType.valueOf(usageNames[i].trim())));
			}
			catch (IllegalArgumentException ex) {
				addErrorMessage(errorMessage, "Invalid usage: checking spelling and case: " + usageNames[i]);
			}
		} // for usage names
	}
	else if (!UtilBase.isEmpty(templateNameStr)) {
		String[] templateNames = templateNameStr.split(",");
		for (int i = 0; i < templateNames.length; i++) {
			List tmpList = GuidelineTemplateManager.getInstance().getTemplatesByName(templateNames[i].trim());
			if (tmpList == null || tmpList.isEmpty()) {
				addErrorMessage(errorMessage, "Invalid template name: no templates found with name " + templateNames[i]);
			}
			templateList.addAll(tmpList);
		}
	}
	else {
		try {
			templateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(UtilBase.toIntArray(templateIDStr.trim())));
		}
		catch (NumberFormatException ex) { 
			addErrorMessage(errorMessage, "Invalid template ids: must be integer delimited by comma.");
		}
	}
	
	if (templateList == null || templateList.isEmpty()) { 
		addErrorMessage(errorMessage,"No template report generated: no templates found.");
	}
	else {
		for (java.util.Iterator iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate template = (GridTemplate) iter.next();
			int numberOfColumns = template.getNumColumns();
			boolean templateHasRule = !(template.getRuleDefinition() == null || template.getRuleDefinition().isEmpty());
%>
  <guideline-template template-id="<%=template.getID()%>">
    <name><%=ReportGenerator.htmlify(template.getName())%></name>
    <version><%=template.getVersion()%></version>
    <usage><%=template.getUsageType()%></usage>
    <description><%=ReportGenerator.formatAsTextElements(template.getDescription())%></description>
    <fit-to-screen><%=(template.fitToScreen()?"yes":"no")%></fit-to-screen>
    <max-rows><%=template.getMaxNumOfRows()%></max-rows>
    <status><%=template.getStatus()%></status>
    <complete-cols><%=UtilBase.toString(template.getCompletenessColumns())%></complete-cols>
    <consistent-cols><%=UtilBase.toString(template.getConsistencyColumns())%></consistent-cols>
    <comments><%=ReportGenerator.formatAsTextElements(template.getComment())%></comments>
	<template-rule template-id="<%=template.getID()%>">
	  <usage><%=template.getUsageType()%></usage>
	  <rule>
<%
			if (templateHasRule) {
				try {
					out.print(ReportGenerator.generateRuleStringForReport(template.getRuleDefinition(), template));
				}
				catch (Exception ex) {
					out.print("<!-- Error: " + ex.toString() + " -->");
				}
			}
%>
	  </rule>
	</template-rule>
    <template-message template-id="<%=template.getID()%>">
      <message>
<%
			if (templateHasRule) {
				try {
					out.print(ReportGenerator.generateMessageStringForReport(-1, template));
				}
				catch (Exception ex) {
					out.print("<!-- Error: " + ex.toString() + " -->");
				}
			}
%>
	  </message>
    </template-message>
	<column-count><%=numberOfColumns%></column-count>
<%
			for (int col = 1; col <= numberOfColumns; ++col) { 
				GridTemplateColumn templateColumn = (GridTemplateColumn)template.getColumn(col); 
%>
	<template-column template-id="<%=template.getID()%>" column-no="<%=templateColumn.getID()%>">
	  <name><%=ReportGenerator.htmlify(templateColumn.getName())%></name>
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
	    <multi-select><%=(templateColumn.getColumnDataSpecDigest().isMultiSelectAllowed()?"Yes":"No")%></multi-select>
	  </data-spec>
<%
				java.util.List messageFragmentList = templateColumn.getAllMessageFragmentDigests();
				for (java.util.Iterator mfi = messageFragmentList.iterator(); mfi.hasNext();) {
					ColumnMessageFragmentDigest mfDigest = (ColumnMessageFragmentDigest) mfi.next();
%>
      <message-fragment template-id="<%=template.getID()%>" column-no="<%=templateColumn.getID()%>">
        <type><%=mfDigest.getType()%></type>
        <cell-selection><%=mfDigest.getCellSelection()%></cell-selection>
        <range-style><%=mfDigest.getRangeStyle()%></range-style>
        <enum-delimiter><%=ReportGenerator.htmlify(mfDigest.getEnumDelimiter())%></enum-delimiter>
        <enum-final-delimiter><%=ReportGenerator.htmlify(mfDigest.getEnumFinalDelimiter())%></enum-final-delimiter>
        <enum-prefix><%=ReportGenerator.htmlify(mfDigest.getEnumPrefix())%></enum-prefix>
        <text><%=ReportGenerator.formatAsTextElements(mfDigest.getText())%></text>
      </message-fragment>
<%
				}
				RuleDefinition ruleDefinition = templateColumn.getRuleDefinition();
				if (ruleDefinition != null && !ruleDefinition.isEmpty()) {
%>
      <column-rule template-id="<%=template.getID()%>" column-no="<%=templateColumn.getID()%>">
        <usage><%=ruleDefinition.getUsageType()%></usage>
        <rule>
<%
					try {
						out.print(ReportGenerator.generateRuleStringForReport(ruleDefinition, template));
					}
					catch (Exception ex) {
						out.print("<!-- Error: " + ex.toString() + " -->");
					}
%>
		 </rule>
      </column-rule>
      <column-message template-id="<%=template.getID()%>">
        <message>
<%
					try {
						out.print(ReportGenerator.generateMessageStringForReport(templateColumn.getID(), template));
					}
					catch (Exception ex) {
						out.print("<!-- Error: " + ex.toString() + " -->");
					}
%>
	    </message>
      </column-message>
<%
				}	// if ruleDef isn't empty
%>
	</template-column>
<%
			}	// for columns in template
%>
  </guideline-template>
<%
		}	// for templates in templateList
	}	// if templates are found
	
	if (errorMessage.length() > 0) {
		out.print("<error-message>" + ReportGenerator.formatAsTextElements(errorMessage.toString()) + "</error-message>");
	}
%>
</templates>
