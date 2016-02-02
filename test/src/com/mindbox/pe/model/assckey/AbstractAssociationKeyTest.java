package com.mindbox.pe.model.assckey;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

/**
 * Unit tests for {@link AbstractAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class AbstractAssociationKeyTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private static class TestImpl extends AbstractAssociationKey {
		public TestImpl(int associableID) {
			super(associableID);
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractAssociationKeyTest Tests");
		suite.addTestSuite(AbstractAssociationKeyTest.class);
		return suite;
	}

	private AbstractAssociationKey associationKey = null;

	public AbstractAssociationKeyTest(String name) {
		super(name);
	}

	public void testEqualsPositiveCaseReflexive() throws Exception {
		assertTrue(associationKey.equals(associationKey));
	}

	public void testEqualsPositiveCaseSymmetic() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(associationKey));
	}

	public void testEqualsPositiveCaseTransitive() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		AbstractAssociationKey key3 = new TestImpl(associationKey.getAssociableID());
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(key3));
		assertTrue(associationKey.equals(key3));
	}

	public void testEqualsPositiveCaseConsistent() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		AbstractAssociationKey key3 = new TestImpl(ObjectMother.createInt());
		int threshold = 4;
		for (int i = 0; i < threshold; i++) {
			assertTrue(associationKey.equals(key2));
			assertFalse(associationKey.equals(key3));
		}
	}

	public void testEqualsNegativeCaseWithNull() throws Exception {
		assertFalse(associationKey.equals(null));
	}

	public void testEqualsNegativeCaseWithDifferentType() throws Exception {
		assertFalse(associationKey.equals(String.valueOf(associationKey.getAssociableID())));
	}

	public void testEqualsNegativeCaseWithUnequalInstance() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(ObjectMother.createInt());
		assertFalse(associationKey.equals(key2));
		assertFalse(key2.equals(associationKey));
	}

	public void testHashCodeProducesSameResultOnEqualInstances() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		AbstractAssociationKey key3 = new TestImpl(associationKey.getAssociableID());
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());
	}

	protected void setUp() throws Exception {
		super.setUp();
		associationKey = new TestImpl(ObjectMother.createInt());
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractAssociationKeyTest
		super.tearDown();
	}
}
