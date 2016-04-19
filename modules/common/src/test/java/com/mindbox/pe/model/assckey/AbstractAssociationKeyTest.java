package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for {@link AbstractAssociationKey}.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public class AbstractAssociationKeyTest extends AbstractTestBase {

	private static class TestImpl extends AbstractAssociationKey {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7188637040284957134L;

		public TestImpl(int associableID) {
			super(associableID);
		}
	}

	private AbstractAssociationKey associationKey = null;

	@Test
	public void testEqualsPositiveCaseReflexive() throws Exception {
		assertTrue(associationKey.equals(associationKey));
	}

	@Test
	public void testEqualsPositiveCaseSymmetic() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(associationKey));
	}

	@Test
	public void testEqualsPositiveCaseTransitive() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		AbstractAssociationKey key3 = new TestImpl(associationKey.getAssociableID());
		assertTrue(associationKey.equals(key2));
		assertTrue(key2.equals(key3));
		assertTrue(associationKey.equals(key3));
	}

	@Test
	public void testEqualsPositiveCaseConsistent() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		AbstractAssociationKey key3 = new TestImpl(createInt());
		int threshold = 4;
		for (int i = 0; i < threshold; i++) {
			assertTrue(associationKey.equals(key2));
			assertFalse(associationKey.equals(key3));
		}
	}

	@Test
	public void testEqualsNegativeCaseWithNull() throws Exception {
		assertFalse(associationKey.equals(null));
	}

	@Test
	public void testEqualsNegativeCaseWithDifferentType() throws Exception {
		assertFalse(associationKey.equals(String.valueOf(associationKey.getAssociableID())));
	}

	@Test
	public void testEqualsNegativeCaseWithUnequalInstance() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(createInt());
		assertFalse(associationKey.equals(key2));
		assertFalse(key2.equals(associationKey));
	}

	@Test
	public void testHashCodeProducesSameResultOnEqualInstances() throws Exception {
		AbstractAssociationKey key2 = new TestImpl(associationKey.getAssociableID());
		AbstractAssociationKey key3 = new TestImpl(associationKey.getAssociableID());
		assertEquals(associationKey.hashCode(), key2.hashCode());
		assertEquals(associationKey.hashCode(), key3.hashCode());
	}

	@Before
	public void setUp() throws Exception {
		associationKey = new TestImpl(createInt());
	}
}
