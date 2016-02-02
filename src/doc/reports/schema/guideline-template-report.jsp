<%@ page contentType="text/xml; charset=utf-8" %>

<%-- Generates template summary report schema --%>
<xsd:schema 
		xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		elementFormDefault="qualified">	

 <xsd:element name="templates">
  <xsd:complexType>
   <xsd:sequence>
     <xsd:element name="guideline-template" minOccurs="0" maxOccurs="unbounded">
     	<xsd:complexType>
         <xsd:sequence>
           <xsd:element name="name"             type="xsd:string"  />
           <xsd:element name="version"          type="xsd:string"  />
           <xsd:element name="usage"            type="xsd:string"  />
           <xsd:element name="description"      type="largeText"  />
           <xsd:element name="fit-to-screen"    type="xsd:boolean" />
           <xsd:element name="max-rows"         type="xsd:integer" />
           <xsd:element name="status"           type="xsd:string" />
           <xsd:element name="complete-cols"    type="xsd:string" />
           <xsd:element name="consistent-cols"  type="xsd:string" />
           <xsd:element name="comments"          type="largeText" />
           <xsd:element name="template-rule"    type="template-rule-type"    maxOccurs="1"/>
           <xsd:element name="template-message" type="template-message-type" maxOccurs="unbounded"/>
           <xsd:element name="column-count"     type="xsd:positiveInteger" />
           <xsd:element name="template-column"  type="template-column-type" maxOccurs="unbounded" />
         </xsd:sequence>
         <xsd:attribute name="template-id"     type="xsd:positiveInteger" use="required"/>
     	</xsd:complexType>
     </xsd:element>
     <xsd:element name="error-message" type="largeText"/>
   </xsd:sequence>
  </xsd:complexType>
 </xsd:element>

 <xsd:complexType name="template-rule-type">
   <xsd:sequence>
     <xsd:element name="usage" type="xsd:string"  />
     <xsd:element name="rule"  type="largeText" />
   </xsd:sequence>
   <!--<xsd:attribute name="rule-id"     type="xsd:positiveInteger" use="required"/>-->
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType>

 <xsd:complexType name="template-message-type">
   <xsd:sequence>
     <xsd:element name="message" type="largeText" />
   </xsd:sequence>
   <!--<xsd:attribute name="message-id"  type="xsd:positiveInteger" use="required"/>-->
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType>

 <xsd:complexType name="template-column-type">
   <xsd:sequence>
     <xsd:element name="name"             type="xsd:string"  />
     <xsd:element name="description"      type="largeText"  />
     <xsd:element name="title"            type="xsd:string"  />
     <xsd:element name="font"             type="xsd:string"  />
     <xsd:element name="color"            type="xsd:string"  />
     <xsd:element name="width"            type="xsd:integer"  />
     <xsd:element name="attribute-map"    type="xsd:string"  />
     <xsd:element name="data-spec">
       <xsd:complexType>
         <xsd:sequence>
           <xsd:element name="type" type="xsd:string" />
           <xsd:element name="min"  type="xsd:string" />
           <xsd:element name="max"  type="xsd:string" />
           <xsd:element name="allow-blank"  type="xsd:boolean" />
           <xsd:element name="multi-select"  type="xsd:boolean" />
         </xsd:sequence>
       </xsd:complexType>
     </xsd:element>
     <xsd:element name="message-fragment" type="message-fragment-type" maxOccurs="unbounded" />
     <xsd:element name="column-rule"      type="column-rule-type"      maxOccurs="1" />
     <xsd:element name="column-message"   type="column-message-type"   maxOccurs="unbounded" />
   </xsd:sequence>
   <xsd:attribute name="column-no"   type="xsd:positiveInteger" use="required"/>
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType>

 <xsd:complexType name="column-rule-type">
   <xsd:sequence>
     <xsd:element name="usage" type="xsd:string" />
     <xsd:element name="rule"  type="largeText" />
   </xsd:sequence>
   <!--<xsd:attribute name="rule-id"     type="xsd:positiveInteger" use="required"/>-->
   <xsd:attribute name="column-no"   type="xsd:positiveInteger" use="required"/>
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType>
 
 <xsd:complexType name="column-message-type">
   <xsd:sequence>
     <xsd:element name="message" type="largeText" />
   </xsd:sequence>
   <!--<xsd:attribute name="message-id"  type="xsd:positiveInteger" use="required"/>-->
   <xsd:attribute name="column-no"   type="xsd:positiveInteger" use="required"/>
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType>
 
 <xsd:complexType name="message-fragment-type">
   <xsd:sequence>
     <xsd:element name="type"                 type="xsd:string" />
     <xsd:element name="cell-selection"       type="xsd:string" />
     <xsd:element name="range-style"          type="xsd:string" />
     <xsd:element name="enum-delimiter"       type="xsd:string" />
     <xsd:element name="enum-final-delimiter" type="xsd:string" />
     <xsd:element name="enum-prefix"          type="xsd:string" />
     <xsd:element name="text"                 type="largeText" />
   </xsd:sequence>
   <!--<xsd:attribute name="message-fragment-id"     type="xsd:positiveInteger" use="required"/>-->
   <xsd:attribute name="column-no"   type="xsd:positiveInteger" use="required"/>
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType>
 
 <xsd:complexType name="largeText">
   <xsd:sequence>
     <xsd:element name="text" maxOccurs="unbounded"/>
   </xsd:sequence>
 </xsd:complexType>

</xsd:schema>