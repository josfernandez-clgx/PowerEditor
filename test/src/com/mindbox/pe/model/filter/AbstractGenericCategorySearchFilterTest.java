package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.Persistent;

public class AbstractGenericCategorySearchFilterTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGenericCategorySearchFilterTest Tests");
		suite.addTestSuite(AbstractGenericCategorySearchFilterTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractGenericCategorySearchFilter {

		protected TestImpl(int categoryType, boolean rootOnly) {
			super(categoryType, rootOnly);
		}
	}

	private AbstractGenericCategorySearchFilter filter;

	public AbstractGenericCategorySearchFilterTest(String name) {
		super(name);
	}

	public void testIsAcceptableWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(filter, "isAcceptable", new Class[] { Persistent.class });
	}

	public void testIsAcceptableUsesRootOnlyFlag() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(entityType);
		assertTrue(filter.isAcceptable(category));

		category.addParentKey(ObjectMother.createMutableTimedAssociationKey());
		assertFalse(filter.isAcceptable(category));

		assertTrue(new TestImpl(entityType.getCategoryType(), false).isAcceptable(category));
	}

	protected void setUp() throws Exception {
		super.setUp();
		filter = new TestImpl(entityType.getCategoryType(), true);
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractGenericCategorySearchFilterTest
		super.tearDown();
	}
}
