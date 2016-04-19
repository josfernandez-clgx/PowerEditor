package com.mindbox.pe.server.enumsrc;

import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValuesAsList;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractEnumerationSourceTest extends AbstractTestBase {

	private static class EnumerationSourceImpl extends AbstractEnumerationSource {
		@Override
		protected void initParams(Map<String, String> paramMap) throws EnumSourceConfigException {
			// NO-OP
		}
	}

	private AbstractEnumerationSource enumerationSource;

	@Before
	public void setUp() throws Exception {
		enumerationSource = new EnumerationSourceImpl();
	}

	@Test
	public void testConstructorSetsInvariantsProperly() throws Exception {
		assertNull(enumerationSource.getName());
		assertFalse(enumerationSource.isSelectorSupported());
		assertFalse(enumerationSource.initialized);
	}

	@Test
	public void testGetAllEnumValuesB4InitThrowsIllegalStateException() throws Exception {
		assertThrowsException(enumerationSource, "getAllEnumValues", new Class[0], new Object[0], IllegalStateException.class);
	}

	@Test
	public void testGetAllEnumValuesHappyCase() throws Exception {
		enumerationSource.initialize("name", false, new HashMap<String, String>());
		enumerationSource.enumValueSelectorMap.put("key1", createEnumValuesAsList(2));
		enumerationSource.enumValueSelectorMap.put("key2", createEnumValuesAsList(3));

		assertEquals(5, enumerationSource.getAllEnumValues().size());
	}

	@Test
	public void testGetApplicableB4InitThrowsIllegalStateException() throws Exception {
		assertThrowsException(enumerationSource, "getApplicable", new Class[] { String.class }, new Object[] { "z" }, IllegalStateException.class);
	}

	@Test
	public void testGetApplicableHappyCase() throws Exception {
		enumerationSource.initialize("name", true, new HashMap<String, String>());

		String key = createString();
		List<EnumValue> enumValues = createEnumValuesAsList(2);
		enumerationSource.enumValueSelectorMap.put(key, enumValues);
		enumerationSource.enumValueSelectorMap.put(key + "x", createEnumValuesAsList(3));

		assertEquals(2, enumerationSource.getApplicable(key).size());
		assertEquals(enumValues.get(0), enumerationSource.getApplicable(key).get(0));
		assertEquals(enumValues.get(1), enumerationSource.getApplicable(key).get(1));
	}

	@Test
	public void testGetApplicableNoSelectorSupportThrowsUnsupportedOperationException() throws Exception {
		enumerationSource.initialize("name", false, new HashMap<String, String>());
		assertThrowsException(
				enumerationSource,
				"getApplicable",
				new Class[] { String.class },
				new Object[] { "z" },
				UnsupportedOperationException.class);
	}

	@Test
	public void testInitializeHappyCase() throws Exception {
		String name = createString();
		enumerationSource.initialize(name, true, new HashMap<String, String>());
		assertEquals(name, enumerationSource.getName());
		assertTrue(enumerationSource.isSelectorSupported());
		assertTrue(enumerationSource.initialized);
	}

	@Test
	public void testInitializeWithEmptyNameThrowsEnumSourceConfigException() throws Exception {
		assertThrowsException(enumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] { null, false,
				new HashMap<String, String>() }, EnumSourceConfigException.class);
		assertThrowsException(enumerationSource, "initialize", new Class[] { String.class, boolean.class, Map.class }, new Object[] { "", false,
				new HashMap<String, String>() }, EnumSourceConfigException.class);
	}
}
