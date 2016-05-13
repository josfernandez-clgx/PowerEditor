package com.mindbox.pe.common.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class CurrencyRangeFormatterTest extends AbstractTestBase {

	private static final int TEST_PRECISION = 2;

	private CurrencyRangeFormatter formatter;


	@Before
	public void setUp() throws Exception {
		formatter = new CurrencyRangeFormatter(TEST_PRECISION);
	}

	@Test
	public void testDecorateEmpty() throws Exception {
		assertNull(formatter.decorate(null));
		assertEquals("", formatter.decorate(""));
	}

	@Test
	public void testDecoratePositiveLo_PositiveHi() throws Exception {
		assertEquals("($1.23-$4.56)", formatter.decorate("(1.23-4.56)"));
	}

	@Test
	public void testDecorateNegativeLo_PositiveHi() throws Exception {
		assertEquals("($-1.23-$4.56)", formatter.decorate("(-1.23-4.56)"));
	}

	@Test
	public void testDecoratePositiveLo_NegativeHi() throws Exception {
		assertEquals("($1.23-$-4.56)", formatter.decorate("(1.23--4.56)"));
	}

	@Test
	public void testDecorateNegativeLo_NegativeHi() throws Exception {
		assertEquals("($-1.23-$-4.56)", formatter.decorate("(-1.23--4.56)"));
	}

	@Test
	public void testDecoratePositiveLo_EmptyHi() throws Exception {
		assertEquals("($1.23- ", formatter.decorate("(1.23- "));
	}

	@Test
	public void testDecorateNegativeLo_EmptyHi() throws Exception {
		assertEquals("($-1.23- ", formatter.decorate("(-1.23- "));
	}

	@Test
	public void testDecorateEmptyLo_PositiveHi() throws Exception {
		assertEquals(" -$4.56)", formatter.decorate(" -4.56)"));
	}

	@Test
	public void testDecorateEmptyLo_NegativeHi() throws Exception {
		assertEquals(" -$-4.56)", formatter.decorate(" --4.56)"));
	}
}
