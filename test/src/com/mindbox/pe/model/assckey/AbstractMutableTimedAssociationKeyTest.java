package com.mindbox.pe.model.assckey;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

/**
 * Unit tests for {@link AbstractMutableTimedAssociationKey}.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class AbstractMutableTimedAssociationKeyTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractMutableTimedAssociationKeyTest Tests");
		suite.addTestSuite(AbstractMutableTimedAssociationKeyTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractMutableTimedAssociationKey {
		public TestImpl(int id) {
			super(id);
		}

		public MutableTimedAssociationKey copy() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private AbstractMutableTimedAssociationKey mutableTimedAssociationKey;
	
	public AbstractMutableTimedAssociationKeyTest(String name) {
		super(name);
	}

	public void testHasEffectiveDatePositiveCase() throws Exception {
		mutableTimedAssociationKey.setEffectiveDate(ObjectMother.createDateSynonym());
		assertTrue(mutableTimedAssociationKey.hasEffectiveDate());
	}
	
	public void testHasEffectiveDateNegativeCase() throws Exception {
		assertFalse(mutableTimedAssociationKey.hasEffectiveDate());
	}
	
	public void testHasExpirationDatePositiveCase() throws Exception {
		mutableTimedAssociationKey.setExpirationDate(ObjectMother.createDateSynonym());
		assertTrue(mutableTimedAssociationKey.hasExpirationDate());
	}
	
	public void testHasExpirationDateNegativeCase() throws Exception {
		assertFalse(mutableTimedAssociationKey.hasExpirationDate());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mutableTimedAssociationKey = new TestImpl(ObjectMother.createInt());
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractMutableTimedAssociationKeyTest
		super.tearDown();
	}
}
