package com.mindbox.pe.common.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;


public class CurrencyFormatterTest extends AbstractTestBase {

	private static final int TEST_PRECISION = 2;

	private CurrencyFormatter formatter;

	@Before
	public void setUp() throws Exception {
		formatter = new CurrencyFormatter(TEST_PRECISION);
	}

	@Test
	public void testDecorateNull() throws Exception {
		assertNull(formatter.decorate(null));
	}

	@Test
	public void testDecorateNonempty() throws Exception {
		assertEquals("$any string", formatter.decorate("any string"));
	}
}
