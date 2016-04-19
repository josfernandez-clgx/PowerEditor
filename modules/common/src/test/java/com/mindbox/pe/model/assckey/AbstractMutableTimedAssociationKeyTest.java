package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for {@link AbstractMutableTimedAssociationKey}.
 * 
 * @author Geneho Kim
 * @since 5.1.0
 */
public class AbstractMutableTimedAssociationKeyTest extends AbstractTestBase {

	private static class TestImpl extends AbstractMutableTimedAssociationKey {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8037752233479707217L;

		public TestImpl(int id) {
			super(id);
		}

		public MutableTimedAssociationKey copy() {
			return null;
		}
	}

	private AbstractMutableTimedAssociationKey mutableTimedAssociationKey;

	@Before
	public void setUp() throws Exception {
		mutableTimedAssociationKey = new TestImpl(createInt());
	}

	@Test
	public void testHasEffectiveDateNegativeCase() throws Exception {
		assertFalse(mutableTimedAssociationKey.hasEffectiveDate());
	}

	@Test
	public void testHasEffectiveDatePositiveCase() throws Exception {
		mutableTimedAssociationKey.setEffectiveDate(createDateSynonym());
		assertTrue(mutableTimedAssociationKey.hasEffectiveDate());
	}

	@Test
	public void testHasExpirationDateNegativeCase() throws Exception {
		assertFalse(mutableTimedAssociationKey.hasExpirationDate());
	}

	@Test
	public void testHasExpirationDatePositiveCase() throws Exception {
		mutableTimedAssociationKey.setExpirationDate(createDateSynonym());
		assertTrue(mutableTimedAssociationKey.hasExpirationDate());
	}

}
