package com.mindbox.pe.model.table;

import static com.mindbox.pe.common.CommonTestObjectMother.createIntegerRange;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class RangeTest extends AbstractTestBase {

	@Test
	public void testEquals() throws Exception {
		AbstractRange ir = createIntegerRange(1, 3);

		assertTrue(ir.equals(ir));
		assertFalse(ir.equals(null));
		assertFalse(ir.equals(new Object()));

		AbstractRange other = createIntegerRange(1, 3);

		// same values
		assertTrue(ir.equals(other));
		assertTrue(other.equals(ir));

		// diff lower
		((IntegerRange) other).setLowerValue(new Integer(ir.getFloor().intValue() + 1));
		assertFalse(ir.equals(other));
		assertFalse(other.equals(ir));
		((IntegerRange) other).setLowerValue((Integer) ir.getFloor());

		// diff upper
		((IntegerRange) other).setUpperValue(new Integer(ir.getCeiling().intValue() + 1));
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
