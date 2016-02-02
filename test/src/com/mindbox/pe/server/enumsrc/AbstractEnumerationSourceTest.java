package com.mindbox.pe.server.enumsrc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EnumValue;

public class AbstractEnumerationSourceTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractEnumerationSourceTest Tests");
		suite.addTestSuite(AbstractEnumerationSourceTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private static class EnumerationSourceImpl extends AbstractEnumerationSource {
		@Override
		protected void initParams(Map<String, String> paramMap) throws EnumSourceConfigException {
			// NO-OP
		}
	}

	public AbstractEnumerationSourceTest(String name) {
		super(name);
	}

	public void testConstructorSetsInvariantsProperly() throws Exception {
		assertNull(enumerationSource.getName());
		assertFalse(enumerationSource.isSelectorSupported());
		assertFalse(enumerationSource.initialized);
	}

	public void testInitializeHappyCase() throws Exception {
		String name = ObjectMother.createString();
		enumerationSource.initialize(name, true, new HashMap<String, String>());
		assertEquals(name, enumerationSource.getName());
		assertTrue(enumerationSource.isSelectorSupported());
		assertTrue(enumerationSource.initialized);
	}

	public void testInitializeWithEmptyNameThrowsEnumSourceConfigException() throws Exception {
		assertThrowsException(enumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] {
				null,
				false,
				new HashMap<String, String>() }, EnumSourceConfigException.class);
		assertThrowsException(enumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] {
				"",
				false,
				new HashMap<String, String>() }, EnumSourceConfigException.class);
	}

	public void testGetAllEnumValuesHappyCase() throws Exception {
		enumerationSource.initialize("name", false, new HashMap<String, String>());
		enumerationSource.enumValueSelectorMap.put("key1", ObjectMother.createEnumValuesAsList(2));
		enumerationSource.enumValueSelectorMap.put("key2", ObjectMother.createEnumValuesAsList(3));

		assertEquals(5, enumerationSource.getAllEnumValues().size());
	}

	public void testGetAllEnumValuesB4InitThrowsIllegalStateException() throws Exception {
		assertThrowsException(enumerationSource, "getAllEnumValues", new Class[0], new Object[0], IllegalStateException.class);
	}

	public void testGetApplicableHappyCase() throws Exception {
		enumerationSource.initialize("name", true, new HashMap<String, String>());

		String key = ObjectMother.createString();
		List<EnumValue> enumValues = ObjectMother.createEnumValuesAsList(2);
		enumerationSource.enumValueSelectorMap.put(key, enumValues);
		enumerationSource.enumValueSelectorMap.put(key + "x", ObjectMother.createEnumValuesAsList(3));

		assertEquals(2, enumerationSource.getApplicable(key).size());
		assertEquals(enumValues.get(0), enumerationSource.getApplicable(key).get(0));
		assertEquals(enumValues.get(1), enumerationSource.getApplicable(key).get(1));
	}

	public void testGetApplicableB4InitThrowsIllegalStateException() throws Exception {
		assertThrowsException(
				enumerationSource,
				"getApplicable",
				new Class[] { String.class },
				new Object[] { "z" },
				IllegalStateException.class);
	}

	public void testGetApplicableNoSelectorSupportThrowsUnsupportedOperationException() throws Exception {
		enumerationSource.initialize("name", false, new HashMap<String, String>());
		assertThrowsException(
				enumerationSource,
				"getApplicable",
				new Class[] { String.class },
				new Object[] { "z" },
				UnsupportedOperationException.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		enumerationSource = new EnumerationSourceImpl();
	}

	private AbstractEnumerationSource enumerationSource;
}
