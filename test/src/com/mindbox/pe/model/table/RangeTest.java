package com.mindbox.pe.model.table;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class RangeTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(RangeTest.class.getName());
		suite.addTestSuite(RangeTest.class);
		return suite;
	}

	public RangeTest(String name) {
		super(name);
	}
	
	public void testEquals() throws Exception {
		AbstractRange ir = ObjectMother.createIntegerRange(1,3);
		
		assertTrue(ir.equals(ir));
		assertFalse(ir.equals(null));
		assertFalse(ir.equals(new Object()));
		
		AbstractRange other = ObjectMother.createIntegerRange(1,3);
		
		// same values
		assertTrue(ir.equals(other));
		assertTrue(other.equals(ir));
		
		// diff lower
		((IntegerRange) other).setLowerValue(new Integer(ir.getFloor().intValue()+1));
		assertFalse(ir.equals(other));
		assertFalse(other.equals(ir));
		((IntegerRange) other).setLowerValue((Integer) ir.getFloor());
		
		// diff upper
		((IntegerRange) other).setUpperValue(new Integer(ir.getCeiling().intValue()+1));
		assertFalse(ir.equals(other));
		assertFalse(other.equals(ir));
		((IntegerRange) other).setUpperValue((Integer) ir.getCeiling());
		
		// diff lower inclusive
		((IntegerRange) other).setLowerValueInclusive(!ir.isLowerValueInclusive());
		assertFalse(ir.equals(other));
		assertFalse(other.equals(ir));
		((IntegerRange) other).setLowerValueInclusive(ir.isLowerValueInclusive());
		
		// diff upper inclusive
		((IntegerRange) other).setUpperValueInclusive(!ir.isUpperValueInclusive());
		assertFalse(ir.equals(other));
		assertFalse(other.equals(ir));
	}
}
