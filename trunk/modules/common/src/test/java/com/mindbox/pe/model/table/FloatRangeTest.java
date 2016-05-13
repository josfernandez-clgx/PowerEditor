package com.mindbox.pe.model.table;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class FloatRangeTest extends AbstractTestBase {

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(FloatRange.class);
	}

	@Test
	public void testIsEmptyNegativeCase() throws Exception {
		FloatRange range = FloatRange.parseValue("(0.1234-0.9590]");
		assertFalse(range.isEmpty());
		range.setLowerValue(null);
		assertFalse(range.isEmpty());
		range.setLowerValue(range.getUpperValue());
		range.setUpperValue(null);
		assertFalse(range.isEmpty());
	}

	@Test
	public void testIsEmptyPositiveCase() throws Exception {
		FloatRange range = new FloatRange();
		assertTrue(range.isEmpty());
	}

	@Test
	public void testIsForDateHappyCase() throws Exception {
		FloatRange range = new FloatRange();
		assertFalse(range.isForDate());
	}

	@Test
	public void testParseValueLargeValues() throws Exception {
		FloatRange range = FloatRange.parseValue("(2000000.0-4.2E7]");
		assertFalse(range.isLowerValueInclusive());
		assertTrue(range.isUpperValueInclusive());
		assertEquals(Float.floatToIntBits(2000000.0f), Float.floatToIntBits(range.getLowerValue().floatValue()));
		assertEquals(Float.floatToIntBits(42000000.0f), Float.floatToIntBits(range.getUpperValue().floatValue()));
	}

	@Test
	public void testParseValueLargeValues2() throws Exception {
		FloatRange range = FloatRange.parseValue("[1.25E7-4.05E8)");
		assertTrue(range.isLowerValueInclusive());
		assertFalse(range.isUpperValueInclusive());
		assertEquals(Float.floatToIntBits(12500000.0f), Float.floatToIntBits(range.getLowerValue().floatValue()));
		assertEquals(Float.floatToIntBits(405000000.0f), Float.floatToIntBits(range.getUpperValue().floatValue()));
	}

	@Test
	public void testParseValueParsesCeilingOnly() throws Exception {
		FloatRange range = FloatRange.parseValue("[-2500000)");
		assertFalse(range.isUpperValueInclusive());
		assertEquals(Float.floatToIntBits(2500000.0f), Float.floatToIntBits(range.getUpperValue().floatValue()));
		assertNull(range.getLowerValue());
	}

	@Test
	public void testParseValueParsesFloorAndCeiling() throws Exception {
		FloatRange range = FloatRange.parseValue("(0.1234-0.9590]");
		assertFalse(range.isLowerValueInclusive());
		assertTrue(range.isUpperValueInclusive());
		assertEquals(Float.floatToIntBits(0.1234f), Float.floatToIntBits(range.getLowerValue().floatValue()));
		assertEquals(Float.floatToIntBits(0.9590f), Float.floatToIntBits(range.getUpperValue().floatValue()));
	}

	@Test
	public void testParseValueParsesFloorOnly() throws Exception {
		FloatRange range = FloatRange.parseValue("[20.25-]");
		assertTrue(range.isLowerValueInclusive());
		assertEquals(Float.floatToIntBits(20.25f), Float.floatToIntBits(range.getLowerValue().floatValue()));
		assertNull(range.getUpperValue());
	}

	@Test
	public void testParseValueWithEmptyRangeStrReturnsEmptyRange() throws Exception {
		FloatRange range = FloatRange.parseValue("[-]");
		assertNotNull(range);
		assertNull(range.getLowerValue());
		assertNull(range.getUpperValue());
	}

	@Test
	public void testParseValueWithEmptyStringReturnsEmptyRange() throws Exception {
		FloatRange range = FloatRange.parseValue("");
		assertNotNull(range);
		assertNull(range.getLowerValue());
		assertNull(range.getUpperValue());
	}

	@Test
	public void testParseValueWithNullReturnsEmptyRange() throws Exception {
		FloatRange range = FloatRange.parseValue(null);
		assertNotNull(range);
		assertNull(range.getLowerValue());
		assertNull(range.getUpperValue());
	}

	@Test
	public void testRepresentsSingleValueNegativeCase() throws Exception {
		FloatRange range = FloatRange.parseValue("(0.1234-0.9590]");
		assertFalse(range.representsSingleValue());
	}

	@Test
	public void testRepresentsSingleValuePositiveCase() throws Exception {
		FloatRange range = FloatRange.parseValue("(0.1234-0.9590]");
		range.setLowerValue(range.getUpperValue());
		assertTrue(range.representsSingleValue());
	}

	@Test
	public void testRepresentsSingleValueReturnsFalseIfIsEmpty() throws Exception {
		FloatRange range = new FloatRange();
		assertFalse(range.representsSingleValue());
	}

	@Test
	public void testValueOfHappyCaseReturnsDouble() throws Exception {
		FloatRange floatRange = new FloatRange();
		Object obj = floatRange.valueOf("12.34");
		assertTrue(obj instanceof Double);
		assertEquals(Float.floatToIntBits(12.34f), Float.floatToIntBits(((Number) obj).floatValue()));
	}

	@Test
	public void testValueOfWithEmptyStringReturnsNull() throws Exception {
		FloatRange floatRange = new FloatRange();
		assertNull(floatRange.valueOf(""));
	}

	@Test
	public void testValueOfWithNonNumericStringThrowsException() throws Exception {
		FloatRange floatRange = new FloatRange();
		assertThrowsException(floatRange, "valueOf", new Class[] { String.class }, new Object[] { "34,00" }, Exception.class);
	}

	@Test
	public void testValueOfWithNullReturnsNull() throws Exception {
		FloatRange floatRange = new FloatRange();
		assertNull(floatRange.valueOf(null));
	}
}
