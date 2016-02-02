package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

public class AbstractGenericEntitySearchFilterTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGenericEntitySearchFilterTest Tests");
		suite.addTestSuite(AbstractGenericEntitySearchFilterTest.class);
		return suite;
	}

	private static class GenericEntitySearchFilterImpl extends AbstractGenericEntitySearchFilter {
		protected GenericEntitySearchFilterImpl(GenericEntityType entityType) {
			super(entityType);
		}
	}

	public AbstractGenericEntitySearchFilterTest(String name) {
		super(name);
	}

	public void testConstructorWithNullThrowsNullPointerException() throws Exception {
		try {
			new GenericEntitySearchFilterImpl(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testIsAcceptableForPersistentWithNullThrowsNullPointerException() throws Exception {
		try {
			new GenericEntitySearchFilterImpl(entityType).isAcceptable(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testIsAcceptableForPersistentWithDiffTypeReturnsFalse() throws Exception {
		EntityTypeDefinition etDef = new EntityTypeDefinition();
		etDef.setName(TEST_ENTITY_TYPE_NAME + "1");
		etDef.setDisplayName("Test Entity2");
		etDef.setTypeID(TEST_ENTITY_TYPE_ID + 1);
		etDef.setCanClone("no");
		GenericEntityType entityType2 = GenericEntityType.makeInstance(etDef);

		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		assertFalse(new GenericEntitySearchFilterImpl(entityType).isAcceptable(entity));
	}

	public void testIsAcceptableForPersistentWithValidObjectReturnsTrue() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(new GenericEntitySearchFilterImpl(entityType).isAcceptable(entity));
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for AbstractGenericEntitySearchFilterTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractGenericEntitySearchFilterTest
		super.tearDown();
	}
}
