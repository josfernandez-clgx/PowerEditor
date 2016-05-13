<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="com.mindbox.pe.server.report.ReportGeneratorHelper" %>
<%@ page import="com.mindbox.pe.model.template.GridTemplate" %>
<%@ page import="com.mindbox.pe.model.grid.ProductGrid" %>
  
<% ReportGeneratorHelper reportHelper = new ReportGeneratorHelper(request.getParameter("template"), 
        request.getParameter("templateid"), request.getParameter("usage"), null, request.getParameter("context-elements"),
        request.getParameter("include-children"), request.getParameter("include-parents"), 
        request.getParameter("filter-column-data"), request.getParameter("include-empty-contexts"),
        request.getParameter("status"), request.getParameter("date"));
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
            <<c:out value="${reportHelper.templateElementName}"/>
                activation-id="<c:out value="${activationID}"/>"
                template-id="<c:out value="${reportHelper.templateNameVersion}"/>"
                template-name="<c:out value="${reportHelper.templateNameForReport}"/>"
                template-version="<c:out value="${reportHelper.templateVersionForReport}"/>"
                usage="<c:out value="${reportHelper.usageForReport}"/>"
                template-internal-id="<c:out value="${reportHelper.currentTemplate.ID}"/>">
            <context activation-id="<c:out value="${activationID}"/>">
            <% for (Iterator k=reportHelper.getEntityContextValuesMap().entrySet().iterator();k.hasNext();) {
               Map.Entry entry = (Map.Entry)k.next(); %>
               <<%=entry.getKey()%>><%=entry.getValue()%></<%=entry.getKey()%>>
            <%}%>
            </context>
            <activation-date><c:out value="${reportHelper.gridEffectiveDate}"/></activation-date>
            <expiration-date><c:out value="${reportHelper.gridExpirationDate}"/></expiration-date>
            <created-date><c:out value="${reportHelper.gridCreationDate}"/></created-date>
            <status><c:out value="${reportHelper.currentGrid.status}"/></status>
            <status-change-date><c:out value="${reportHelper.gridStatusChangeDate}"/></status-change-date>
            <<c:out value="${reportHelper.templateElementName}"/>-grid-rows>
            <% for (int row = 1; row <= reportHelper.getCurrentGrid().getNumRows(); ++row) { 
                reportHelper.setCurrentRowNumber(row); %>
                <grid-row activation-id="<c:out value="${activationID}"/>" row-number="<%=row%>">
                    <% for (int col = 1; col <= reportHelper.getCurrentTemplate().getNumColumns(); ++col) {
                    reportHelper.setCurrentColNumber(col); %>
                    <<c:out value="${reportHelper.columnElementNameForReport}"/>>
                        <c:out value="${reportHelper.cellValueForReport}"/>
                    </<c:out value="${reportHelper.columnElementNameForReport}"/>> <%}%>
                    <per-row-template-rule>
                        <c:out escapeXml="false" value="${reportHelper.rowRuleStringForReport}"/>
                    </per-row-template-rule>
                    <per-row-template-message>
                        <c:out escapeXml="false" value="${reportHelper.rowMessageStringForReport}"/>
                    </per-row-template-message>
                    <% for (Iterator k=reportHelper.getColumnRuleAndMessageMap().entrySet().iterator();k.hasNext();) {
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
                </grid-row>
            <%}%>
            </<c:out value="${reportHelper.templateElementName}"/>-grid-rows>
            <comments><c:out value="${reportHelper.gridComments}"/></comments>
            <template-rule>
                <c:out escapeXml="false" value="${reportHelper.templateRuleStringForReport}"/>
            </template-rule>
            <template-message>
                <c:out escapeXml="false" value="${reportHelper.templateMessageForReport}"/>
            </template-message>
        </<c:out value="${reportHelper.templateElementName}"/>>
        <%}%>
<%}%>
<% if (reportHelper.getErrorMessages().size() > 0) {%>
<error-messages>
    <% for (Iterator i=reportHelper.getErrorMessages().iterator();i.hasNext();) {%>
        <error-message><text><%=i.next()%></text></error-message><%}%>
</error-messages><%}%>
<%}%>
</guideline-activations>

<%-- this doesnt work for some reason
    <c:forEach var="template" items="${reportHelper.templateList}" varStatus="vstat">
        ...
    </c:forEach> 
--%>