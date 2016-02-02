package com.mindbox.pe.model;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class FloatDomainAttributeTest extends AbstractTestBase {
	private FloatDomainAttribute attr;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FloatDomainAttributeTest.class
				.getName());
		suite.addTestSuite(FloatDomainAttributeTest.class);
		return suite;
	}

	public FloatDomainAttributeTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		attr = new FloatDomainAttribute();
	}
	
	public void testConstructionDefaultPrecision() throws Exception {
		assertEquals(FloatDomainAttribute.DEFAULT_PRECISION, attr.getPrecision());
	}
	
	public void testSetPrecisionHappyPath() throws Exception {
		assertTrue(0 != FloatDomainAttribute.DEFAULT_PRECISION); // sanity check
		attr.setPrecision(0);
		assertEquals(0, attr.getPrecision());
	}
	
	public void testSetPrecisionIllegalValueDefaults() throws Exception {
		assertTrue(-1 < FloatDomainAttribute.MIN_PRECISION); // sanity check
		attr.setPrecision(203); // set precision to a non-default value
		attr.setPrecision(-1);  // try setting it to an illegal value
		assertEquals(203, attr.getPrecision()); // setter was a no-op
	}
}
