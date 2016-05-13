package com.mindbox.pe.model.filter;

import static com.mindbox.pe.common.CommonTestObjectMother.createGenericCategory;
import static com.mindbox.pe.common.CommonTestObjectMother.createMutableTimedAssociationKey;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.Persistent;

public class AbstractGenericCategorySearchFilterTest extends AbstractTestWithGenericEntityType {

	private static class TestImpl extends AbstractGenericCategorySearchFilter {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3909586213578250388L;

		protected TestImpl(int categoryType, boolean rootOnly) {
			super(categoryType, rootOnly);
		}
	}

	private AbstractGenericCategorySearchFilter filter;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		filter = new TestImpl(entityType.getCategoryType(), true);
	}

	@Test
	public void testIsAcceptableUsesRootOnlyFlag() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		assertTrue(filter.isAcceptable(category));

		category.addParentKey(createMutableTimedAssociationKey());
		assertFalse(filter.isAcceptable(category));

		assertTrue(new TestImpl(entityType.getCategoryType(), false).isAcceptable(category));
	}

	@Test
	public void testIsAcceptableWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(filter, "isAcceptable", new Class[] { Persistent.class });
	}
}
