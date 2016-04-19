package com.mindbox.pe.model.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.domain.FloatDomainAttribute;
import com.mindbox.pe.unittest.AbstractTestBase;

public class FloatDomainAttributeTest extends AbstractTestBase {

	private FloatDomainAttribute attr;

	@Before
	public void setUp() throws Exception {
		attr = new FloatDomainAttribute();
	}

	@Test
	public void testConstructionDefaultPrecision() throws Exception {
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, attr.getPrecision());
	}

	@Test
	public void testSetPrecisionHappyPath() throws Exception {
		assertTrue(0 != FloatDomainAttribute.DEFAULT_PRECISION); // sanity check
		attr.setPrecision(0);
		assertEquals(0, attr.getPrecision());
	}

	@Test
	public void testSetPrecisionIllegalValueDefaults() throws Exception {
		assertTrue(-1 < FloatDomainAttribute.MIN_PRECISION); // sanity check
		attr.setPrecision(203); // set precision to a non-default value
		attr.setPrecision(-1); // try setting it to an illegal value
		assertEquals(203, attr.getPrecision()); // setter was a no-op
	}
}
