package com.mindbox.pe.model.table;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EnumValue;

public class EnumValuesTest extends AbstractTestBase {


	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EnumValuesTest Tests");
		suite.addTestSuite(EnumValuesTest.class);
		return suite;
	}

	private EnumValues<EnumValue> enumValues = null;
	private EnumValues<String> enumValuesForString = null;

	public EnumValuesTest(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	public void testCopyHappyCase() throws Exception {
		enumValues.setSelectionExclusion(true);
		enumValues.add(ObjectMother.createEnumValue());
		EnumValues<EnumValue> copy = (EnumValues<EnumValue>) enumValues.copy();
		assertFalse(enumValues == copy);
		assertTrue(enumValues.equals(copy));
	}

	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(EnumValues.class);
	}

	public void testParseValueSkipsEmptyTokens() throws Exception {
		EnumValues<String> values = EnumValues.parseValue("one,,two, ,three", false, null);
		assertEquals(3, values.size());
		assertTrue(values.contains("one"));
		assertTrue(values.contains("two"));
		assertTrue(values.contains("three"));
	}

	public void testGetEnumAsStringWithInvalidIndexThrowsIndexOutOfBoundsException() throws Exception {
		try {
			enumValuesForString.getEnumValueAsString(0);
			fail("Expected IndexOutOfBoundsException not thrown");
		}
		catch (IndexOutOfBoundsException e) {
			// expected
		}
		enumValuesForString.add("str");
		try {
			assertNull(enumValuesForString.getEnumValueAsString(1));
		}
		catch (IndexOutOfBoundsException e) {
			// expected
		}
	}

	public void testIsExclusionEnumValueWithStringEnumValue() throws Exception {
		String exclusionEnumVal = "%~%[enum1";
		String notExclusionEnumVal = "enum2";
		assertTrue(EnumValues.isExclusionEnumValueString(exclusionEnumVal, true, Arrays.asList(new EnumValue[] {
				ObjectMother.createEnumValue(),
				ObjectMother.createEnumValue() })));
		assertFalse(EnumValues.isExclusionEnumValueString(notExclusionEnumVal, true, Arrays.asList(new EnumValue[] {
				ObjectMother.createEnumValue(),
				ObjectMother.createEnumValue() })));
	}

	public void testIsExclusionEnumValueWithEnumValueObject() throws Exception {
		List<EnumValue> enumValues = Arrays.asList(ObjectMother.createEnumValues(2));
		String exclusionEnumVal = EnumValues.EXCLUSION_PREFIX + ((EnumValue) enumValues.get(0)).getDisplayLabel();
		String notExclusionEnumVal = ((EnumValue) enumValues.get(1)).getDisplayLabel();
		assertTrue(EnumValues.isExclusionEnumValueString(exclusionEnumVal, true, enumValues));
		assertFalse(EnumValues.isExclusionEnumValueString(notExclusionEnumVal, true, enumValues));
	}

	public void testGetEnumAsStringWithStringValueReturnsSameString() throws Exception {
		String str = String.valueOf(System.currentTimeMillis());
		enumValuesForString.add(str);
		assertEquals(str, enumValuesForString.getEnumValueAsString(0));
	}

	public void testGetEnumAsStringWithEnumValueReturnsDeployID() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		enumValues.add(enumValue);
		assertEquals(String.valueOf(enumValue.getDeployID()), enumValues.getEnumValueAsString(0));
	}

	public void testToStringReturnsDeployIDsForEnumValue() throws Exception {
		EnumValue enumValue1 = ObjectMother.createEnumValue();
		EnumValue enumValue2 = ObjectMother.createEnumValue();
		enumValues.add(enumValue1);
		enumValues.add(enumValue2);
		assertEquals(enumValue1.getDeployID().toString() + ',' + enumValue2.getDeployID().toString(), enumValues.toString());
	}

	public void testToStringReturnsDisplayLabelsForStringValue() throws Exception {
		enumValuesForString.add("value1");
		enumValuesForString.add("another value");
		assertEquals("value1,another value", enumValuesForString.toString());
	}

	public void testToStringHappyCaseWithEmptyEnumValues() throws Exception {
		assertEquals("", enumValues.toString());
	}

	public void testToStringHappyCaseWithEmptyEnumValuesWithExclusion() throws Exception {
		enumValues.setSelectionExclusion(true);
		assertEquals("", enumValues.toString());
	}

	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(enumValues.equals(null));
	}

	public void testEqualsWithNonCompatibleTypeReturnsFalse() throws Exception {
		assertFalse(enumValues.equals("bogusValue"));
	}

	public void testEqualsWithExclusionMismatchReturnsFalse() throws Exception {
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.setSelectionExclusion(true);
		assertFalse(enumValues.equals(values1));
		assertFalse(values1.equals(enumValues));
	}

	public void testEqualsWithSizeMismatchReturnsFalse() throws Exception {
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.add(ObjectMother.createEnumValue());
		assertFalse(enumValues.equals(values1));
		assertFalse(values1.equals(enumValues));
	}

	public void testEqualsWithElementMissmatchReturnsFalse() throws Exception {
		enumValues.add(ObjectMother.createEnumValue());
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.add(ObjectMother.createEnumValue());
		assertFalse(enumValues.equals(values1));
		assertFalse(values1.equals(enumValues));
	}

	public void testEqualsHappyCase() throws Exception {
		enumValues.add(ObjectMother.createEnumValue());
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.add(enumValues.get(0));
		assertTrue(enumValues.equals(values1));
		assertTrue(values1.equals(enumValues));

		enumValues.setSelectionExclusion(true);
		values1.setSelectionExclusion(true);
		assertTrue(enumValues.equals(values1));
		assertTrue(values1.equals(enumValues));
	}

	protected void setUp() throws Exception {
		super.setUp();
		enumValues = ObjectMother.createEnumValues();
		enumValuesForString = new EnumValues<String>();
	}

	protected void tearDown() throws Exception {
		enumValues.clear();
		enumValues = null;
		super.tearDown();
	}
}
