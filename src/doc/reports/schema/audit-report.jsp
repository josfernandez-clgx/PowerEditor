<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=utf-8" errorPage="/error.jsp" %>
<%@ include file="../../includes/global.jsp" %>

<!-- =================================================================
XML Schema Definition of PowerEditor Audit Report.

(c) Copyright 2007 MDA MindBox, Inc. All rights reserved.
================================================================= -->

<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
	<xsd:element name="audit-report">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element name="audit-event" minOccurs="0" maxOccurs="unbounded">
					<xsd:complexType>
						<xsd:sequence>
							<xsd:element name="type" type="xsd:string" />
							<xsd:element name="audit-date" type="xsd:string" />
							<xsd:element name="username" type="xsd:string" />
							<xsd:element ref="change-details" minOccurs="0" maxOccurs="unbounded"/>
						</xsd:sequence>
						<xsd:attribute name="auditID" type="xsd:positiveInteger" use="required" />
					</xsd:complexType>
				</xsd:element>
				<xsd:element name="error-message" type="xsd:string" minOccurs="0" maxOccurs="unbounded" />
				<xsd:element name="summary">
					<xsd:complexType>
						<xsd:sequence>
							<xsd:element name="report-date" type="xsd:string" />
							<xsd:element name="audit-event-count" type="xsd:integer" />
						</xsd:sequence>
					</xsd:complexType>
				</xsd:element>
			</xsd:sequence>
		</xsd:complexType>
	</xsd:element>

	<xsd:element name="change-details">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element name="changed-element">
					<xsd:complexType>
						<xsd:sequence>
							<xsd:element name="usage-type" type="xsd:string" minOccurs="0" />
							<xsd:element name="template-name" type="xsd:string" minOccurs="0" />
							<xsd:element name="activation-date-name" type="xsd:string" minOccurs="0" />
							<xsd:element name="activation-date" type="xsd:string" minOccurs="0" />
							<xsd:element name="expiration-date-name" type="xsd:string" minOccurs="0" />
							<xsd:element name="expiration-date" type="xsd:string" minOccurs="0" />
							<xsd:element name="context" type="xsd:string" minOccurs="0" />
						</xsd:sequence>
						<xsd:attribute name="type" type="xsd:string" use="required" />
						<xsd:attribute name="name" type="xsd:string" use="required" />
					</xsd:complexType>
				</xsd:element>
				<xsd:element name="change-detail" minOccurs="0" maxOccurs="unbounded">
					<xsd:complexType>
						<xsd:sequence>
							<xsd:element name="change-type" type="xsd:string" />
							<xsd:element name="change-description" type="xsd:string" />
							<xsd:element name="context-element" type="xsd:string" />
							<xsd:element name="previous-value" type="xsd:string" minOccurs="0" />
							<xsd:element name="new-value" type="xsd:string" minOccurs="0" />
							<xsd:element name="row-number" type="xsd:string" minOccurs="0" />
							<xsd:element name="column-name" type="xsd:string" minOccurs="0" />
							<xsd:element name="effective-date" type="xsd:string" minOccurs="0" />
							<xsd:element name="expiration-date" type="xsd:string" minOccurs="0" />
						</xsd:sequence>
					</xsd:complexType>
				</xsd:element>
			</xsd:sequence>
		</xsd:complexType>
	</xsd:element>

</xsd:schema>
