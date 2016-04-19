package com.mindbox.pe.common.digest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.FloatDomainAttribute;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DomainXMLDigesterTest extends AbstractTestBase {

	@After
	public void tearDown() throws Exception {
		DomainXMLDigester.getInstance().reset();
	}

	@Test
	public void testDigestDomainXMLHappyCaseForActiveEnumValue() throws Exception {
		String xml = "<DomainModel><DomainClass>" + "<DomainAttribute Name=\"TestAttribute\" DeployType=\"String\" Precision=\"3\">"
				+ "<EnumValue DeployID=\"2\" DeployValue=\"EnumDeploy\" DisplayLabel=\"EnumDisplay\" Inactive=\"No\"></EnumValue>" + "</DomainAttribute></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		DomainAttribute domainAttr = ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertNotNull(domainAttr);
		EnumValue[] enumValues = domainAttr.getEnumValues();
		assertEquals(1, enumValues.length);
		assertEquals(new Integer(2), enumValues[0].getDeployID());
		assertEquals("EnumDeploy", enumValues[0].getDeployValue());
		assertEquals("EnumDisplay", enumValues[0].getDisplayLabel());
		assertTrue(enumValues[0].isActive());
	}

	@Test
	public void testDigestDomainXMLHappyCaseForInactiveEnumValue() throws Exception {
		String xml = "<DomainModel><DomainClass>" + "<DomainAttribute Name=\"TestAttribute\" DeployType=\"String\" Precision=\"3\">"
				+ "<EnumValue DeployID=\"3\" DeployValue=\"EnumDeploy\" DisplayLabel=\"EnumDisplay\" Inactive=\"Yes\"/>" + "</DomainAttribute></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		DomainAttribute domainAttr = ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertNotNull(domainAttr);
		EnumValue[] enumValues = domainAttr.getEnumValues();
		assertEquals(1, enumValues.length);
		assertEquals(new Integer(3), enumValues[0].getDeployID());
		assertEquals("EnumDeploy", enumValues[0].getDeployValue());
		assertEquals("EnumDisplay", enumValues[0].getDisplayLabel());
		assertFalse(enumValues[0].isActive());
	}

	@Test
	public void testNonFloatDomainAttributeSetPrecision() throws Exception {
		// Precision attribute silently ignored
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"String\" Precision=\"3\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		DomainAttribute domainAttr = ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertFalse(domainAttr instanceof FloatDomainAttribute);
	}

	@Test
	public void testFloatDomainAttributePrecisionHappyPath() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Float\" Precision=\"3\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(3, domainAttr.getPrecision());
	}

	@Test
	public void testFloatDomainAttributePrecisionNoValueDefault() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Float\" /></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, domainAttr.getPrecision());
	}

	@Test
	public void testFloatDomainAttributePrecisionIllegalValueDefault() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Float\" Precision=\"-1\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, domainAttr.getPrecision());
	}

	@Test
	public void testCurrencyDomainAttributePrecisionHappyPath() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Currency\" Precision=\"3\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(3, domainAttr.getPrecision());
	}

	@Test
	public void testCurrencyDomainAttributePrecisionNoValueDefault() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Currency\" /></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, domainAttr.getPrecision());
	}

	@Test
	public void testCurrencyDomainAttributePrecisionIllegalValueDefault() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Currency\" Precision=\"-1\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, domainAttr.getPrecision());
	}

	@Test
	public void testPercentDomainAttributePrecisionHappyPath() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Percent\" Precision=\"3\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(3, domainAttr.getPrecision());
	}

	@Test
	public void testPercentDomainAttributePrecisionNoValueDefault() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Percent\" /></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, domainAttr.getPrecision());
	}

	@Test
	public void testPercentDomainAttributePrecisionIllegalValueDefault() throws Exception {
		String xml = "<DomainModel><DomainClass><DomainAttribute Name=\"TestAttribute\" DeployType=\"Percent\" Precision=\"-1\"/></DomainClass></DomainModel>";
		DomainXMLDigester.getInstance().digestDomainXML(new StringReader(xml));
		FloatDomainAttribute domainAttr = (FloatDomainAttribute) ((DomainClass) DomainXMLDigester.getInstance().getAllObjects().get(0)).getDomainAttribute("TestAttribute");
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, domainAttr.getPrecision());
	}
}
