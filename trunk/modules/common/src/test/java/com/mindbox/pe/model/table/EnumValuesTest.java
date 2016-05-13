package com.mindbox.pe.model.table;

import static com.mindbox.pe.common.CommonTestObjectMother.createEnumValue;
import static com.mindbox.pe.common.CommonTestObjectMother.createEnumValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumValuesTest extends AbstractTestBase {

	private EnumValues<EnumValue> enumValues = null;
	private EnumValues<String> enumValuesForString = null;

	@Before
	public void setUp() throws Exception {
		enumValues = createEnumValues();
		enumValuesForString = new EnumValues<String>();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCopyHappyCase() throws Exception {
		enumValues.setSelectionExclusion(true);
		enumValues.add(createEnumValue());
		EnumValues<EnumValue> copy = (EnumValues<EnumValue>) enumValues.copy();
		assertFalse(enumValues == copy);
		assertTrue(enumValues.equals(copy));
	}

	@Test
	public void testEqualsHappyCase() throws Exception {
		enumValues.add(createEnumValue());
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.add(enumValues.get(0));
		assertTrue(enumValues.equals(values1));
		assertTrue(values1.equals(enumValues));

		enumValues.setSelectionExclusion(true);
		values1.setSelectionExclusion(true);
		assertTrue(enumValues.equals(values1));
		assertTrue(values1.equals(enumValues));
	}

	@Test
	public void testEqualsWithElementMissmatchReturnsFalse() throws Exception {
		enumValues.add(createEnumValue());
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.add(createEnumValue());
		assertFalse(enumValues.equals(values1));
		assertFalse(values1.equals(enumValues));
	}

	@Test
	public void testEqualsWithExclusionMismatchReturnsFalse() throws Exception {
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.setSelectionExclusion(true);
		assertFalse(enumValues.equals(values1));
		assertFalse(values1.equals(enumValues));
	}

	@Test
	public void testEqualsWithNonCompatibleTypeReturnsFalse() throws Exception {
		assertFalse(enumValues.equals("bogusValue"));
	}

	@Test
	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(enumValues.equals(null));
	}

	@Test
	public void testEqualsWithSizeMismatchReturnsFalse() throws Exception {
		EnumValues<EnumValue> values1 = new EnumValues<EnumValue>();
		values1.add(createEnumValue());
		assertFalse(enumValues.equals(values1));
		assertFalse(values1.equals(enumValues));
	}

	@Test
	public void testGetEnumAsStringWithEnumValueReturnsDeployID() throws Exception {
		EnumValue enumValue = createEnumValue();
		enumValues.add(enumValue);
		assertEquals(String.valueOf(enumValue.getDeployID()), enumValues.getEnumValueAsString(0));
	}

	@Test
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

	@Test
	public void testGetEnumAsStringWithStringValueReturnsSameString() throws Exception {
		String str = String.valueOf(System.currentTimeMillis());
		enumValuesForString.add(str);
		assertEquals(str, enumValuesForString.getEnumValueAsString(0));
	}

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(EnumValues.class);
	}

	@Test
	public void testIsExclusionEnumValueWithEnumValueObject() throws Exception {
		List<EnumValue> enumValues = Arrays.asList(createEnumValues(2));
		String exclusionEnumVal = EnumValues.EXCLUSION_PREFIX + ((EnumValue) enumValues.get(0)).getDisplayLabel();
		String notExclusionEnumVal = ((EnumValue) enumValues.get(1)).getDisplayLabel();
		assertTrue(EnumValues.isExclusionEnumValueString(exclusionEnumVal, true, enumValues));
		assertFalse(EnumValues.isExclusionEnumValueString(notExclusionEnumVal, true, enumValues));
	}

	@Test
	public void testIsExclusionEnumValueWithStringEnumValue() throws Exception {
		String exclusionEnumVal = "%~%[enum1";
		String notExclusionEnumVal = "enum2";
		assertTrue(EnumValues.isExclusionEnumValueString(
				exclusionEnumVal,
				true,
				Arrays.asList(new EnumValue[] { createEnumValue(), createEnumValue() })));
		assertFalse(EnumValues.isExclusionEnumValueString(
				notExclusionEnumVal,
				true,
				Arrays.asList(new EnumValue[] { createEnumValue(), createEnumValue() })));
	}

	@Test
	public void testParseValueSkipsEmptyTokens() throws Exception {
		EnumValues<String> values = EnumValues.parseValue("one,,two, ,three", false, null);
		assertEquals(3, values.size());
		assertTrue(values.contains("one"));
		assertTrue(values.contains("two"));
		assertTrue(values.contains("three"));
	}

	@Test
	public void testToStringHappyCaseWithEmptyEnumValues() throws Exception {
		assertEquals("", enumValues.toString());
	}

	@Test
	public void testToStringHappyCaseWithEmptyEnumValuesWithExclusion() throws Exception {
		enumValues.setSelectionExclusion(true);
		assertEquals("", enumValues.toString());
	}

	@Test
	public void testToStringReturnsDeployIDsForEnumValue() throws Exception {
		EnumValue enumValue1 = createEnumValue();
		EnumValue enumValue2 = createEnumValue();
		enumValues.add(enumValue1);
		enumValues.add(enumValue2);
		assertEquals(enumValue1.getDeployID().toString() + ',' + enumValue2.getDeployID().toString(), enumValues.toString());
	}

	@Test
	public void testToStringReturnsDisplayLabelsForStringValue() throws Exception {
		enumValuesForString.add("value1");
		enumValuesForString.add("another value");
		assertEquals("value1,another value", enumValuesForString.toString());
	}
}
