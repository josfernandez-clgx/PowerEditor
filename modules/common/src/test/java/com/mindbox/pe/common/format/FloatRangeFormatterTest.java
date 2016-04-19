package com.mindbox.pe.common.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.unittest.AbstractTestBase;


public class FloatRangeFormatterTest extends AbstractTestBase {

	private static final int TEST_PRECISION = 2;
	private static final Double posVal = new Double("1.23");
	private static final Double negVal = new Double("-1.23");

	private FloatRangeFormatter formatter;
	private FloatRange range;

	@Before
	public void setUp() throws Exception {
		formatter = new FloatRangeFormatter(TEST_PRECISION);

		range = new FloatRange();
		range.setLowerValue(posVal);
		range.setUpperValue(posVal);
	}

	@Test
	public void testDecorateEmpty() throws Exception {
		assertNull(formatter.decorate(null));
		assertEquals("", formatter.decorate(""));
		assertEquals("any string", formatter.decorate("any string"));
	}

	@Test
	public void testFormatNull() throws Exception {
		assertEquals("", formatter.format(null));
	}

	@Test
	public void testFormatNullLo_NullHi() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(null);

		assertEquals("", formatter.format(range));
	}

	@Test
	public void testFormatInclusive() throws Exception {
		range.setLowerValueInclusive(true);
		range.setUpperValueInclusive(true);

		assertEquals("[1.23-1.23]", formatter.format(range));
	}

	@Test
	public void testFormatExclusive() throws Exception {
		range.setLowerValueInclusive(false);
		range.setUpperValueInclusive(false);

		assertEquals("(1.23-1.23)", formatter.format(range));
	}

	@Test
	public void testFormatPositiveLo_PositiveHi() throws Exception {
		assertEquals("[1.23-1.23]", formatter.format(range));
	}

	@Test
	public void testFormatNegativeLo_PositiveHi() throws Exception {
		range.setLowerValue(negVal);
		assertEquals("[-1.23-1.23]", formatter.format(range));
	}

	@Test
	public void testFormatNegativeLo_NegativeHi() throws Exception {
		range.setLowerValue(negVal);
		range.setUpperValue(negVal);
		assertEquals("[-1.23--1.23]", formatter.format(range));
	}

	@Test
	public void testFormatPositiveLo_EmptyHi() throws Exception {
		range.setUpperValue(null);
		assertEquals("[1.23- ", formatter.format(range));
	}

	@Test
	public void testFormatNegativeLo_EmptyHi() throws Exception {
		range.setLowerValue(negVal);
		range.setUpperValue(null);
		assertEquals("[-1.23- ", formatter.format(range));
	}

	@Test
	public void testFormatEmptyLo_PositiveHi() throws Exception {
		range.setLowerValue(null);
		assertEquals(" -1.23]", formatter.format(range));
	}

	@Test
	public void testFormatEmptyLo_NegativeHi() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(negVal);
		assertEquals(" --1.23]", formatter.format(range));
	}
}
