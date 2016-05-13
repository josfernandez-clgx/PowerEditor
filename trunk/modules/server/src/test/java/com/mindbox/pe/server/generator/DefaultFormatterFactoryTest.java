package com.mindbox.pe.server.generator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DefaultFormatterFactoryTest {

	private DefaultFormatterFactory defaultFormatterFactory;

	@Test
	public void getCurrencyFormatterWithNoPrecisionHasNoGroupSymbols() throws Exception {
		assertEquals("10000000.00", defaultFormatterFactory.getCurrencyFormatter(null).format(10000000.0));
	}

	@Test
	public void getCurrencyFormatterWithNoPrecisionHasRoundsToTwoDecimals() throws Exception {
		assertEquals("100000.12", defaultFormatterFactory.getCurrencyFormatter(null).format(100000.123));
		assertEquals("100000.12", defaultFormatterFactory.getCurrencyFormatter(null).format(100000.125));
		assertEquals("100000.13", defaultFormatterFactory.getCurrencyFormatter(null).format(100000.126));
	}

	@Test
	public void getCurrentFormatterWithPrecisionHasNoGroupSymbols() throws Exception {
		assertEquals("10000000.000", defaultFormatterFactory.getCurrencyFormatter(3).format(10000000.0d));
	}

	@Test
	public void getFloatFormatterWithNoPrecisionHasNoGroupSymbols() throws Exception {
		assertEquals("10000000.0", defaultFormatterFactory.getFloatFormatter(null).format(10000000.0));
	}

	@Test
	public void getFloatFormatterWithNoPrecisionRetainAllDecimals() throws Exception {
		assertEquals("1000000.12345", defaultFormatterFactory.getFloatFormatter(null).format(1000000.12345));
	}

	@Test
	public void getFloatFormatterWithPrecisionHasNoGroupSymbols() throws Exception {
		assertEquals("10000000.0000", defaultFormatterFactory.getFloatFormatter(4).format(10000000.0d));
	}

	@Test
	public void getFloatFormatterWithPrecisionRoundsProperly() throws Exception {
		assertEquals("10000.1230", defaultFormatterFactory.getFloatFormatter(4).format(10000.123d));
		assertEquals("10000.1234", defaultFormatterFactory.getFloatFormatter(4).format(10000.12345d));
		assertEquals("10000.1235", defaultFormatterFactory.getFloatFormatter(4).format(10000.12346d));
	}

	@Before
	public void setUp() throws Exception {
		defaultFormatterFactory = new DefaultFormatterFactory(false);
	}
}
