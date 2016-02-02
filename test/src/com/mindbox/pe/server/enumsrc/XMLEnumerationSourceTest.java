package com.mindbox.pe.server.enumsrc;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class XMLEnumerationSourceTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("XMLEnumerationSourceTest Tests");
		suite.addTestSuite(XMLEnumerationSourceTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public XMLEnumerationSourceTest(String name) {
		super(name);
	}

	public void testInitParamWithNoFileNameThrowsEnumSourceConfigException() throws Exception {
		assertThrowsException(xmlEnumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] {
				"name",
				false,
				new HashMap<String, String>() }, EnumSourceConfigException.class);
	}

	public void testInitParamWithNotFoundFileThrowsEnumSourceConfigException() throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(XMLEnumerationSource.PARAM_XML_FILE, "test/data-x/abc1234.xml");
		assertThrowsException(xmlEnumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] {
				"name",
				false,
				new HashMap<String, String>() }, EnumSourceConfigException.class);
	}

	public void testInitParamHappyCaseWithSelector() throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(XMLEnumerationSource.PARAM_XML_FILE, "test/data/XMLEnumeration-Sample-County.xml");
		xmlEnumerationSource.initialize("name", true, paramMap);
		
		assertEquals(2, xmlEnumerationSource.getAllEnumValues().size());
		assertEquals(2, xmlEnumerationSource.getApplicable("GA").size());
		assertEquals("forsyth", xmlEnumerationSource.getApplicable("GA").get(0).getDeployValue());
		assertEquals("Fulton", xmlEnumerationSource.getApplicable("GA").get(1).getDisplayLabel());
	}

	public void testInitParamHappyCaseWithoutSelector() throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(XMLEnumerationSource.PARAM_XML_FILE, "test/data/XMLEnumeration-Sample-State.xml");
		xmlEnumerationSource.initialize("name", false, paramMap);
		
		assertEquals(2, xmlEnumerationSource.getAllEnumValues().size());
		assertEquals("GA", xmlEnumerationSource.getAllEnumValues().get(0).getDeployValue());
		assertEquals("Virginia", xmlEnumerationSource.getAllEnumValues().get(1).getDisplayLabel());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		xmlEnumerationSource = new XMLEnumerationSource();
	}

	private XMLEnumerationSource xmlEnumerationSource;
}
