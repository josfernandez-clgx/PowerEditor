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
        request.getParameter("templateid"), request.getParameter("usage"), request.getParameter("columns"),
        request.getParameter("context-elements"),
        request.getParameter("include-children"), request.getParameter("include-parents"), 
        request.getParameter("filter-column-data"), request.getParameter("include-empty-contexts"),
        request.getParameter("status"), request.getParameter("date"));
   pageContext.setAttribute("reportHelper", reportHelper);
   String[] gridColumnNames = reportHelper.getGridColumnNames();
%>

<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">	  

 <xsd:element name="guideline-activations">
  <xsd:complexType>
     <xsd:sequence>
      <xsd:element name="guideline-activation" maxOccurs="unbounded">
     	<xsd:complexType>
         <xsd:sequence>
	       <xsd:element name="usage"             type="xsd:string"          />
	       <xsd:element name="template-id"       type="xsd:string" />
           <xsd:element name="template-name"     type="xsd:string"          />
           <xsd:element name="template-version"  type="xsd:string"          />
           <xsd:element name="template-internal-id"  type="xsd:integer" />
	       <xsd:element name="activation-date"   type="xsd:dateTime"/>
	       <xsd:element name="expiration-date"   type="xsd:dateTime"/>
		   <xsd:element name="created-date"       type="xsd:dateTime"/>
		   <xsd:element name="status"             type="xsd:string"/>
		   <xsd:element name="status-change-date" type="xsd:dateTime"/>
		   <xsd:element name="comments"           type="largeText"/>
<%
	EntityConfiguration entityConfig = ConfigurationManager.getInstance().getEntityConfigHelper();
	EntityTypeDefinition[] typeDefs = entityConfig.getEntityTypeDefinitions();
    for (int i = 0; i < typeDefs.length; i++) {
     if (typeDefs[i].useInContext()) {
%>
		<xsd:element name="<%=ReportGenerator.toElementName(typeDefs[i].getName())%>" type="xsd:string" />
         <% if (typeDefs[i].hasCategory()) {
              CategoryTypeDefinition categoryDef = entityConfig.findCategoryTypeDefinition(typeDefs[i].getCategoryType());
				if (categoryDef != null) { %>
		            <xsd:element name="<%=ReportGenerator.toElementName(categoryDef.getName())%>" type="xsd:string" />
                <%}
         }
     } // if entity used in context
} // for type Defs %>
	       <xsd:element name="number-of-columns" type="xsd:integer"/>
	       <xsd:element ref="column-names"/>
	       <xsd:element ref="grid-rows"/>
	       <xsd:element ref="template-rules-messages"/>
	       <xsd:element ref="per-row-rules-messages"/> 
         </xsd:sequence>
         <xsd:attribute name="activation-id" type="xsd:string" use="required"/>
     	</xsd:complexType>
       </xsd:element>
       <xsd:element ref="error-messages" />
     </xsd:sequence>
   </xsd:complexType>
 </xsd:element>
 <xsd:element name="template-rules-messages">
   <xsd:complexType>
     <xsd:sequence>
       <xsd:element name="template-rule"       type="largeText"/>
	   <xsd:element name="template-message"    type="largeText"/>
<%
	for (int i = 0; i < gridColumnNames.length; i++) {	%>
	   <xsd:element name="<%=gridColumnNames[i]%>-rule" type="largeText"/>
	   <xsd:element name="<%=gridColumnNames[i]%>-message" type="largeText"/>
<%	} %>
     </xsd:sequence>
   </xsd:complexType>    
 </xsd:element>

 <xsd:element name="column-names">
  <xsd:complexType>
   <xsd:sequence>
<%
	for (int i = 0; i < gridColumnNames.length; i++) {	%>
	  <xsd:element name="<%=gridColumnNames[i]%>_Heading" type="xsd:string"/>
<%	} %>
   </xsd:sequence>
  </xsd:complexType>
 </xsd:element>
 
 <xsd:element name="grid-rows">
   <xsd:complexType>
     <xsd:sequence>
       <xsd:element name="grid-row" maxOccurs="unbounded">
         <xsd:complexType>
		   <xsd:sequence>
			 <xsd:element name="row-no"  type="xsd:integer"/>
<%
	for (int i = 0; i < gridColumnNames.length; i++) {	%>
	  <xsd:element name="<%=gridColumnNames[i]%>" type="xsd:string"/>
<%	} %>
		   </xsd:sequence>
         </xsd:complexType>
       </xsd:element>
     </xsd:sequence>
   </xsd:complexType>
 </xsd:element>
 
 <xsd:element name="per-row-rules-messages">
   <xsd:complexType>
     <xsd:sequence>
       <xsd:element name="per-row-rule-message" maxOccurs="unbounded">
         <xsd:complexType>
		   <xsd:sequence>
			 <xsd:element name="row-no"              type="xsd:positiveInteger"/>
			 <xsd:element name="per-row-template-rule"       type="largeText"/>
			 <xsd:element name="per-row-template-message"    type="largeText"/>
<%
	for (int i = 0; i < gridColumnNames.length; i++) {	%>
	         <xsd:element name="per-row-<%=gridColumnNames[i]%>-rule" type="largeText"/>
	         <xsd:element name="per-row-<%=gridColumnNames[i]%>-message" type="largeText"/>
<%	} %>
		   </xsd:sequence>
         </xsd:complexType>
       </xsd:element>
     </xsd:sequence>
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
     <xsd:element name="text" maxOccurs="unbounded"/>
   </xsd:sequence>
 </xsd:complexType>
</xsd:schema>
<% if (reportHelper.getErrorMessages().size() > 0) {%>
		<jsp:forward page="/invalid_request.jsp">
			<jsp:param name="reason" value="<%=reportHelper.getErrorMessages()%>"/>
		</jsp:forward>
<%}%>
