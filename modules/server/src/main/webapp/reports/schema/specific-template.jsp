<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="com.mindbox.pe.server.report.ReportGeneratorHelper" %>
<%@ page import="com.mindbox.pe.server.report.ReportGenerator" %>
<%@ page import="com.mindbox.pe.model.template.GridTemplate" %>
<%@ page import="com.mindbox.pe.model.grid.ProductGrid" %>
<%@ page import="com.mindbox.pe.common.config.EntityConfiguration" %>
<%@ page import="com.mindbox.pe.server.config.ConfigurationManager" %>
<%@ page import="com.mindbox.pe.common.config.EntityTypeDefinition" %>
<%@ page import="com.mindbox.pe.common.config.CategoryTypeDefinition" %>

<%-- Generates guideline grid report schema for the specified template --%>
<% ReportGeneratorHelper reportHelper = new ReportGeneratorHelper(request.getParameter("template"), 
        request.getParameter("templateid"), request.getParameter("usage"), null, request.getParameter("context-elements"),
        request.getParameter("include-children"), request.getParameter("include-parents"), 
        request.getParameter("filter-column-data"), request.getParameter("include-empty-contexts"),
        request.getParameter("status"), request.getParameter("date"));
   pageContext.setAttribute("reportHelper", reportHelper);
%>

<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">	  

<xsd:element name="guideline-activations">
  <xsd:complexType>
    <xsd:sequence>
<% for (Iterator i=reportHelper.getTemplateList().iterator();i.hasNext();) {
        reportHelper.setCurrentTemplate((GridTemplate)i.next()); %>
        <xsd:element name="<c:out value="${reportHelper.templateElementName}"/>" maxOccurs="unbounded">
	      <xsd:complexType>
	        <xsd:sequence>
			  <xsd:element ref="context" />
			  <xsd:element name="activation-date"    type="xsd:dateTime" />
			  <xsd:element name="expiration-date"    type="xsd:dateTime" />
			  <xsd:element name="created-date"       type="xsd:dateTime"/>
			  <xsd:element name="status"             type="xsd:string"/>
			  <xsd:element name="status-change-date" type="xsd:dateTime"/>
			  <xsd:element ref="<c:out value="${reportHelper.templateElementName}"/>-grid-rows"/>
			  <xsd:element name="comments"           type="largeText"/>
			  <xsd:element name="template-rule"      type="largeText"/>
			  <xsd:element name="template-message"   type="largeText"/>
            <% for (int col = 1; col <= reportHelper.getCurrentTemplate().getNumColumns(); ++col) {
                reportHelper.setCurrentColNumber(col); 
                if (reportHelper.isColumnHaveRuleDef()) {%>
                    <xsd:element name="<c:out value="${reportHelper.columnElementNameForReport}"/>-column-rule"
                                 type="largeText"/>
                    <xsd:element name="<c:out value="${reportHelper.columnElementNameForReport}"/>-column-message"
                                 type="largeText"/>
                <%}
            }%>
	        </xsd:sequence>
	        <xsd:attribute name="activation-id"    type="xsd:integer" use="required"/>
	        <xsd:attribute name="template-id"      type="xsd:string" use="required"/>
	        <xsd:attribute name="template-name"    type="xsd:string" use="required"/>
	        <xsd:attribute name="template-version" type="xsd:string" use="required"/>
	        <xsd:attribute name="usage"            type="xsd:string" use="required"/>
	        <xsd:attribute name="template-internal-id" type="xsd:integer" use="required"/>
	      </xsd:complexType>
	    </xsd:element>
<%}%>
		<xsd:element ref="error-messages"/>
      </xsd:sequence>
    </xsd:complexType>
  </xsd:element>

<% for (Iterator i=reportHelper.getTemplateList().iterator();i.hasNext();) {
        reportHelper.setCurrentTemplate((GridTemplate)i.next()); %>
 <xsd:element name="<c:out value="${reportHelper.templateElementName}"/>-grid-rows">
  <xsd:complexType>
   <xsd:sequence>
    <xsd:element name="grid-row" maxOccurs="unbounded">
      <xsd:complexType>
       <xsd:sequence>
       <% StringBuffer colRuleBuff = new StringBuffer();
		for (int col = 1; col <= reportHelper.getCurrentTemplate().getNumColumns(); ++col) {
            reportHelper.setCurrentColNumber(col); 
            String columnElementName = reportHelper.getColumnElementNameForReport();
            if (reportHelper.isColumnHaveRuleDef()) {
			 colRuleBuff.append("<xsd:element name=\"per-row-"+columnElementName+"-column-rule\" type=\"largeText\"/>");
			 colRuleBuff.append("<xsd:element name=\"per-row-"+columnElementName+"-column-message\" type=\"largeText\"/>");
			}%>
	    <xsd:element name="<%=columnElementName%>" type="xsd:string"/>
        <%}%>
	    <xsd:element name="per-row-template-rule" type="largeText"/>
	    <xsd:element name="per-row-template-message" type="largeText"/>
	    <%=colRuleBuff.toString()%>
	   </xsd:sequence>
   	   <xsd:attribute name="activation-id" type="xsd:positiveInteger" use="required"/>
	   <xsd:attribute name="row-number"    type="xsd:positiveInteger" use="required"/>
	  </xsd:complexType>
	 </xsd:element>
	</xsd:sequence>
  </xsd:complexType>
 </xsd:element>
<%}%>
<%
	EntityConfiguration entityConfig = ConfigurationManager.getInstance().getEntityConfigHelper();
    EntityTypeDefinition[] typeDefs = entityConfig.getEntityTypeDefinitions();
%>
 <xsd:element name="context">
  <xsd:complexType>
   <xsd:sequence>
      <% for (int i = 0; i < typeDefs.length; i++) {
      if (typeDefs[i].useInContext()) { %>
        <xsd:element name="<%=ReportGenerator.toElementName(typeDefs[i].getName())%>" type="xsd:string" />
        <% if (typeDefs[i].hasCategory()) { 
              CategoryTypeDefinition categoryDef = entityConfig.findCategoryTypeDefinition(typeDefs[i].getCategoryType());
	          if (categoryDef != null) {%>
                 <xsd:element name="<%=ReportGenerator.toElementName(categoryDef.getName())%>" type="xsd:string" />
              <%}
         }
       } // if entity used in context
      } // for type Defs %>
    </xsd:sequence>
    <xsd:attribute name="activation-id" type="xsd:positiveInteger" use="required"/>
  </xsd:complexType>
 </xsd:element>
   <xsd:element name="error-messages">
    <xsd:complexType>
     <xsd:sequence>
       <xsd:element name="error-message" minOccurs="0" maxOccurs="unbounded" type="largeText"/>
     </xsd:sequence>
    </xsd:complexType>
   </xsd:element>
  <xsd:complexType name="largeText">
    <xsd:sequence>
      <xsd:element name="text" type="xsd:string" maxOccurs="unbounded"/>
    </xsd:sequence>
  </xsd:complexType>
<% if (reportHelper.getErrorMessages().size() > 0) {%>
		<jsp:forward page="/invalid_request.jsp">
			<jsp:param name="reason" value="Invalid template ids: must be integer delimited by comma."/>
		</jsp:forward>
<%}%>
</xsd:schema>

