<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ include file="../../includes/global.jsp" %>

<!-- =================================================================
XML Schema Definition of PowerEditor Entity Report.
	
(c) Copyright 2007 MDA MindBox, Inc. All rights reserved.
================================================================= -->
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
	<xsd:element name="entity-report">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element ref="category" minOccurs="0" maxOccurs="unbounded"></xsd:element>
				<xsd:element ref="entity" minOccurs="0" maxOccurs="unbounded"></xsd:element>
				<xsd:element name="error-message" minOccurs="0" maxOccurs="unbounded" />
			</xsd:sequence>
		</xsd:complexType>
	</xsd:element>

	<xsd:element name="category">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element name="entity-link">
					<xsd:complexType>
						<xsd:attribute name="id" type="xsd:positiveInteger" use="required" />
						<xsd:attribute name="name" type="xsd:string" use="required" />
					</xsd:complexType>
				</xsd:element>
			</xsd:sequence>
			<xsd:attribute name="id" type="xsd:positiveInteger" use="required" />
			<xsd:attribute name="type" type="xsd:string" use="required" />
			<xsd:attribute name="name" type="xsd:string" use="required" />
			<xsd:attribute name="fullyQualifiedName" type="xsd:string" use="required" />
			<xsd:attribute name="parentID" type="xsd:integer" default="-1"/>
			<xsd:attribute name="parentName" type="xsd:string" />
		</xsd:complexType>
	</xsd:element>

	<xsd:element name="entity">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element name="property" minOccurs="0" maxOccurs="unbounded">
					<xsd:complexType>
						<xsd:attribute name="name" type="xsd:string" use="required" />
						<xsd:attribute name="displayName" type="xsd:string" use="required" />
						<xsd:attribute name="value" type="xsd:string" use="required" />
					</xsd:complexType>
				</xsd:element>
				<xsd:element name="category-link">
					<xsd:complexType>
						<xsd:attribute name="id" type="xsd:positiveInteger" use="required" />
						<xsd:attribute name="name" type="xsd:string" use="required" />
						<xsd:attribute name="fullyQualifiedName" type="xsd:string" use="required" />
						<xsd:attribute name="tier1Category" type="xsd:string" use="required" />
					</xsd:complexType>
				</xsd:element>
				<xsd:element name="entity-link">
					<xsd:complexType>
						<xsd:attribute name="id" type="xsd:positiveInteger" use="required" />
						<xsd:attribute name="name" type="xsd:string" use="required" />
					</xsd:complexType>
				</xsd:element>
			</xsd:sequence>
			<xsd:attribute name="id" type="xsd:positiveInteger" use="required" />
			<xsd:attribute name="type" type="xsd:string" use="required" />
			<xsd:attribute name="name" type="xsd:string" use="required" />
			<xsd:attribute name="parentID" type="xsd:integer" default="-1"/>
			<xsd:attribute name="parentName" type="xsd:string" />
		</xsd:complexType>
	</xsd:element>
</xsd:schema>
