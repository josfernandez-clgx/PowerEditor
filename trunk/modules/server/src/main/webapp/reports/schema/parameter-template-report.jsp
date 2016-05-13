<%@ page contentType="text/xml; charset=utf-8" %>

<%-- Generates parameter template summary report schema --%>
<xsd:schema 
		xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		elementFormDefault="qualified">	

 <xsd:element name="templates">
  <xsd:complexType>
   <xsd:sequence>
     <xsd:element name="parameter-template" minOccurs="0" maxOccurs="unbounded">
     	<xsd:complexType>
         <xsd:sequence>
           <xsd:element name="name"             type="xsd:string"  />
           <xsd:element name="description"      type="largeText"  />
           <xsd:element name="max-rows"         type="xsd:integer" />
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
         </xsd:sequence>
       </xsd:complexType>
     </xsd:element>
   </xsd:sequence>
   <xsd:attribute name="column-no"   type="xsd:positiveInteger" use="required"/>
   <xsd:attribute name="template-id" type="xsd:positiveInteger" use="required"/>
 </xsd:complexType> 
 
 <xsd:complexType name="largeText">
   <xsd:sequence>
     <xsd:element name="text" maxOccurs="unbounded"/>
   </xsd:sequence>
 </xsd:complexType>

</xsd:schema>