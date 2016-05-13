package com.mindbox.pe.server.enumsrc;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class XMLEnumerationSourceTest extends AbstractTestBase {

	private XMLEnumerationSource xmlEnumerationSource;

	@Before
	public void setUp() throws Exception {
		xmlEnumerationSource = new XMLEnumerationSource();
	}

	@Test
	public void testInitParamHappyCaseWithoutSelector() throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(XMLEnumerationSource.PARAM_XML_FILE, "src/test/data/XMLEnumeration-Sample-State.xml");
		xmlEnumerationSource.initialize("name", false, paramMap);

		assertEquals(2, xmlEnumerationSource.getAllEnumValues().size());
		assertEquals("GA", xmlEnumerationSource.getAllEnumValues().get(0).getDeployValue());
		assertEquals("Virginia", xmlEnumerationSource.getAllEnumValues().get(1).getDisplayLabel());
	}

	@Test
	public void testInitParamHappyCaseWithSelector() throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(XMLEnumerationSource.PARAM_XML_FILE, "src/test/data/XMLEnumeration-Sample-County.xml");
		xmlEnumerationSource.initialize("name", true, paramMap);

		assertEquals(2, xmlEnumerationSource.getAllEnumValues().size());
		assertEquals(2, xmlEnumerationSource.getApplicable("GA").size());
		assertEquals("forsyth", xmlEnumerationSource.getApplicable("GA").get(0).getDeployValue());
		assertEquals("Fulton", xmlEnumerationSource.getApplicable("GA").get(1).getDisplayLabel());
	}

	@Test
	public void testInitParamWithNoFileNameThrowsEnumSourceConfigException() throws Exception {
		assertThrowsException(xmlEnumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] { "name",
				false, new HashMap<String, String>() }, EnumSourceConfigException.class);
	}

	@Test
	public void testInitParamWithNotFoundFileThrowsEnumSourceConfigException() throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(XMLEnumerationSource.PARAM_XML_FILE, "src/test/data-x/abc1234.xml");
		assertThrowsException(xmlEnumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] { "name",
				false, new HashMap<String, String>() }, EnumSourceConfigException.class);
	}
}
