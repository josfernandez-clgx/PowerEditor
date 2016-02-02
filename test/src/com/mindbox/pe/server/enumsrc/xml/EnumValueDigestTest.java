package com.mindbox.pe.server.enumsrc.xml;

import java.io.StringReader;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EnumValue;

public class EnumValueDigestTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("EnumValueDigestTest Tests");
		suite.addTestSuite(EnumValueDigestTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public EnumValueDigestTest(String name) {
		super(name);
	}

	public void testConstructorSetsInvariantsProperly() throws Exception {
		assertFalse(new EnumValueDigest().isInactive());
	}

	public void testAsEnumValueHappyCase() throws Exception {
		EnumValueDigest digest = new EnumValueDigest();
		digest.setValue(ObjectMother.createString() + "value");
		digest.setInactive(true);
		digest.setSelectorValue(null);
		digest.setDisplayLabel(ObjectMother.createString() + "disp");

		EnumValue enumValue = digest.asEnumValue();
		assertEquals(digest.getValue(), enumValue.getDeployValue());
		assertEquals(digest.getDisplayLabel(), enumValue.getDisplayLabel());
		assertEquals(digest.isInactive(), !enumValue.isActive());
		assertFalse(enumValue.hasDeployID());
	}

	public void testGetDisplayLabelReturnsValueIfEmpty() throws Exception {
		EnumValueDigest digest = new EnumValueDigest();
		digest.setValue(ObjectMother.createString() + "value");
		assertEquals(digest.getValue(), digest.getDisplayLabel());
		digest.setDisplayLabel(" ");
		assertEquals(digest.getValue(), digest.getDisplayLabel());
	}

	public void testHasSelectorValuePositiveCase() throws Exception {
		EnumValueDigest digest = new EnumValueDigest();
		digest.setSelectorValue(ObjectMother.createString());
		assertTrue(digest.hasSelectorValue());
	}

	public void testHasSelectorValueNegativeCase() throws Exception {
		EnumValueDigest digest = new EnumValueDigest();
		assertFalse(digest.hasSelectorValue());
		digest.setSelectorValue(" ");
		assertFalse(digest.hasSelectorValue());
	}

	public void testParseEnumValuesHappyCase() throws Exception {
		String testStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PowerEditorEnumeration>"
				+ "<EnumValue><Value>value1</Value><DisplayLabel>Disp 1</DisplayLabel><Inactive>true</Inactive></EnumValue>"
				+ "<EnumValue><Value>value2</Value><DisplayLabel>Disp 2</DisplayLabel><SelectorValue>SV</SelectorValue></EnumValue>"
				+ "</PowerEditorEnumeration>";

		List<EnumValueDigest> list = EnumValueDigest.parseEnumValues(new StringReader(testStr));

		assertEquals(2, list.size());
		assertEquals("value1", list.get(0).getValue());
		assertEquals("Disp 1", list.get(0).getDisplayLabel());
		assertTrue(list.get(0).isInactive());
		assertNull(list.get(0).getSelectorValue());
		assertEquals("value2", list.get(1).getValue());
		assertEquals("Disp 2", list.get(1).getDisplayLabel());
		assertFalse(list.get(1).isInactive());
		assertEquals("SV", list.get(1).getSelectorValue());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Logger.getLogger("org.apache.commons.digester").setLevel(Level.INFO);
	}
}
