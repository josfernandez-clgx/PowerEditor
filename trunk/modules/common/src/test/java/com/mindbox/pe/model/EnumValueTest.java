package com.mindbox.pe.model;

import static com.mindbox.pe.common.CommonTestObjectMother.createEnumValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumValueTest extends AbstractTestBase {

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(EnumValue.class);
	}

	@Test
	public void testConstructorSetsInvariantsPropertly() throws Exception {
		EnumValue enumValue = new EnumValue();
		assertNull(enumValue.getDeployID());
		assertTrue(enumValue.isActive());
	}

	@Test
	public void testSetInactivePositiveCase() throws Exception {
		EnumValue enumValue = new EnumValue();
		enumValue.setInactive(null);
		assertTrue(enumValue.isActive());

		enumValue.setInactive("No");
		assertTrue(enumValue.isActive());

		enumValue.setInactive("no");
		assertTrue(enumValue.isActive());

		enumValue.setInactive("false");
		assertTrue(enumValue.isActive());

		enumValue.setInactive("FALSE");
		assertTrue(enumValue.isActive());
	}

	@Test
	public void testSetInactiveNegativeCase() throws Exception {
		EnumValue enumValue = new EnumValue();
		enumValue.setInactive("Yes");
		assertFalse(enumValue.isActive());

		enumValue.setInactive("yes");
		assertFalse(enumValue.isActive());

		enumValue.setInactive("true");
		assertFalse(enumValue.isActive());

		enumValue.setInactive("tRUE");
		assertFalse(enumValue.isActive());
	}

	@Test
	public void testToStringReturnsDeployID() throws Exception {
		EnumValue enumValue = createEnumValue();
		assertEquals(enumValue.getDeployID().toString(), enumValue.toString());
	}

	@Test
	public void testBLANKToStringReturnsEmptyString() throws Exception {
		assertEquals("", EnumValue.BLANK.toString());
	}

	@Test
	public void testBLANKDisplayValueIsASingleSpaceCharacter() throws Exception {
		assertEquals(String.valueOf(' '), EnumValue.BLANK.getDisplayLabel());
	}

	@Test
	public void testEquals() throws Exception {
		EnumValue enumVal = createEnumValue();

		assertTrue(enumVal.equals(enumVal));
		assertFalse(enumVal.equals(null));
		assertFalse(enumVal.equals(EnumValue.BLANK));
		assertFalse(enumVal.equals(new Object()));

		EnumValue other = new EnumValue();

		// same prop values
		other.setDeployID(enumVal.getDeployID());
		other.setDeployValue(enumVal.getDeployValue());
		other.setDisplayLabel(enumVal.getDisplayLabel());
		other.setInactive(enumVal.getInactive());
		assertTrue(enumVal.equals(other));
		assertTrue(other.equals(enumVal));

		// diff id
		other.setDeployID(new Integer(enumVal.getDeployID().intValue() + 1));
		assertFalse(enumVal.equals(other));
		assertFalse(other.equals(enumVal));
		other.setDeployID(enumVal.getDeployID());

		// diff deploy val
		other.setDeployValue(enumVal.getDeployValue() + "changed");
		assertFalse(enumVal.equals(other));
		assertFalse(other.equals(enumVal));
		other.setDeployValue(enumVal.getDeployValue());

		// diff display val
		other.setDisplayLabel(enumVal.getDisplayLabel() + "changed");
		assertFalse(enumVal.equals(other));
		assertFalse(other.equals(enumVal));
		other.setDisplayLabel(enumVal.getDisplayLabel());

		// diff active
		other.setInactive(String.valueOf(enumVal.isActive()));
		assertFalse(enumVal.equals(other));
		assertFalse(other.equals(enumVal));
	}

	@Test
	public void testEqualsOverridable() throws Exception {
		EnumValue enumVal = createEnumValue();
		EnumValue childWithSameValues = new EnumValueSubclass();
		childWithSameValues.setDeployID(enumVal.getDeployID());
		childWithSameValues.setDeployValue(enumVal.getDeployValue());
		childWithSameValues.setDisplayLabel(enumVal.getDisplayLabel());
		childWithSameValues.setInactive(String.valueOf(!enumVal.isActive()));

		// To see this test fail, substitute this line in EnumValue.equals...
		//    if (!o.getClass().getName().equals(this.getClass().getName())) {
		// with this commonly seen implementation line...
		//    if (!(o instanceof EnumValue))

		assertFalse(enumVal.equals(childWithSameValues));
		assertFalse(childWithSameValues.equals(enumVal));
	}

	private final class EnumValueSubclass extends EnumValue {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8770227897485185730L;
		private int var;

		// This is not necessary for the test to run, but is an example of how to override
		// EnumValue.equals.  Note that 'instanceof' is used only because this subclass
		// is final (otherwise, o.getClass().getName().equals(this.getClass().getName() should be used).
		public boolean equals(Object o) {
			return (o instanceof EnumValueSubclass) && (((EnumValueSubclass) o).var == var);
		}
		// also, hashCode() should be overriden as well.
	}
}
