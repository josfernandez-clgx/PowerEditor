<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="com.mindbox.pe.server.report.ReportGenerator" %>
<%@ page import="com.mindbox.pe.server.report.ReportGeneratorHelper" %>
<%@ page import="com.mindbox.pe.model.GridTemplate" %>
<%@ page import="com.mindbox.pe.model.ProductGrid" %>
  
<% ReportGeneratorHelper reportHelper = new ReportGeneratorHelper(request.getParameter("template"), 
        request.getParameter("templateid"), request.getParameter("usage"), request.getParameter("columns"), 
        request.getParameter("context-elements"), request.getParameter("include-children"),
        request.getParameter("include-parents"), request.getParameter("filter-column-data"), 
        request.getParameter("include-empty-contexts"), request.getParameter("status"), request.getParameter("date"));
   pageContext.setAttribute("reportHelper", reportHelper);
   int activationID = 0;
%>

<guideline-activations>
<% if (reportHelper.getErrorMessages().size() > 0) {%>
<error-messages>
    <% for (Iterator i=reportHelper.getErrorMessages().iterator();i.hasNext();) {%>
        <error-message><text><%=i.next()%></text></error-message><%}%>
</error-messages>
<%} else {%>
<% for (Iterator i=reportHelper.getTemplateList().iterator();i.hasNext();) {
        reportHelper.setCurrentTemplate((GridTemplate)i.next());
        for (Iterator j = reportHelper.getActivations().iterator();j.hasNext();) { 
            pageContext.setAttribute("activationID", String.valueOf(++activationID));
            reportHelper.setCurrentGrid((ProductGrid)j.next()); %>
            <guideline-activation activation-id="<c:out value="${activationID}"/>">
                <usage><c:out value="${reportHelper.currentTemplate.usageType}"/></usage>
                <template-id><c:out value="${reportHelper.templateNameVersion}"/></template-id>
                <template-name><c:out value="${reportHelper.templateNameForReport}"/></template-name>
                <template-version><c:out value="${reportHelper.currentTemplate.version}"/></template-version>
                <template-internal-id><c:out value="${reportHelper.currentTemplate.ID}"/></template-internal-id>
                <activation-date><c:out value="${reportHelper.gridEffectiveDate}"/></activation-date>
                <expiration-date><c:out value="${reportHelper.gridExpirationDate}"/></expiration-date>
                <created-date><c:out value="${reportHelper.gridCreationDate}"/></created-date>
                <status><c:out value="${reportHelper.currentGrid.status}"/></status>
                <status-change-date><c:out value="${reportHelper.gridStatusChangeDate}"/></status-change-date>
                <comments><c:out value="${reportHelper.gridComments}"/></comments>
                <% for (Iterator k=reportHelper.getEntityContextValuesMap().entrySet().iterator();k.hasNext();) {
                        Map.Entry entry = (Map.Entry)k.next(); %>
                <<%=entry.getKey()%>><%=entry.getValue()%></<%=entry.getKey()%>><%}%>
                <number-of-columns><c:out value="${reportHelper.currentTemplate.numColumns}"/></number-of-columns>
                <column-names>
                <% for (Iterator k=reportHelper.getColumnNumberElementMap().entrySet().iterator();k.hasNext();) {
                        Map.Entry entry = (Map.Entry)k.next(); 
                        int colID = ((Integer)entry.getKey()).intValue(); %>
                        <<%=entry.getValue()%>_Heading><%=ReportGenerator.formatForReport(reportHelper.getCurrentTemplate().getColumn(colID).getTitle())%>
                        </<%=entry.getValue()%>_Heading> <%}%>
                </column-names>
                <grid-rows>
                <% Object[][] dataObject = reportHelper.getCurrentGrid().getDataObjects();
                for (int row = 0; row < dataObject.length; ++row) {%>
                  <grid-row>
                    <row-no><%=(row+1)%></row-no> <%
                    for (int col = 0; col < dataObject[row].length; col++) { 
                      String columnName = (String) reportHelper.getColumnNumberElementMap().get(new Integer(col+1));%>
                      <<%=columnName%>><%=ReportGenerator.formatForReport(dataObject[row][col])%></<%=columnName%>> <%}%>
                  </grid-row> <%}%>
                </grid-rows>
                <template-rules-messages>
	  	            <template-rule>
                        <c:out escapeXml="false" value="${reportHelper.templateRuleStringForReport}"/>
                    </template-rule>
	  	            <template-message>
                        <c:out escapeXml="false" value="${reportHelper.templateMessageForReport}"/>
                    </template-message>
                    <% for (Iterator k=reportHelper.getColumnRuleAndMessageMap()
                            .entrySet().iterator();k.hasNext();) {
                        Map.Entry entry = (Map.Entry)k.next();
                        String[] ruleColInfo = (String[])entry.getValue(); %>
                    <<%=entry.getKey()%>-rule><%=ruleColInfo[0]%>
                    </<%=entry.getKey()%>-rule>
                    <<%=entry.getKey()%>-message><%=ruleColInfo[1]%>
                    </<%=entry.getKey()%>-message><%}%>
                </template-rules-messages>
                <per-row-rules-messages>
                <% for (int row = 1; row <= reportHelper.getCurrentGrid().getNumRows(); ++row) { 
                    reportHelper.setCurrentRowNumber(row); %>
                    <per-row-rule-message>
                        <row-no><%=row%></row-no>
                        <per-row-template-rule>
                            <c:out escapeXml="false" value="${reportHelper.rowRuleStringForReport}"/>
                        </per-row-template-rule>
                        <per-row-template-message>
                            <c:out escapeXml="false" value="${reportHelper.rowMessageStringForReport}"/>
                        </per-row-template-message>
                        <% for (Iterator k=reportHelper.getRowColumnRuleAndMessageMap()
                            .entrySet().iterator();k.hasNext();) {
                            Map.Entry entry = (Map.Entry)k.next();
                            String[] ruleColInfo = (String[])entry.getValue(); %>
                            <<%=entry.getKey()%>><%=entry.getValue()%></<%=entry.getKey()%>>
                        <per-row-<%=entry.getKey()%>-column-rule>
                        <%=ruleColInfo[0]%>
                        </per-row-<%=entry.getKey()%>-column-rule>
                        <per-row-<%=entry.getKey()%>-column-message>
                        <%=ruleColInfo[1]%>
                        </per-row-<%=entry.getKey()%>-column-message>
                        <%}%>
                    </per-row-rule-message>
                <%}%>
        	    </per-row-rules-messages>
          </guideline-activation>
        <%}%>
<%}%>
<% if (reportHelper.getErrorMessages().size() > 0) {%>
<error-messages>
    <% for (Iterator i=reportHelper.getErrorMessages().iterator();i.hasNext();) {%>
        <error-message><text><%=i.next()%></text></error-message><%}%>
</error-messages><%}%>
<%}%>
</guideline-activations>
