package com.mindbox.pe.model.comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GenericCategory;

public class GenericCategoryComparatorTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericCategoryComparatorTest Tests");
		suite.addTestSuite(GenericCategoryComparatorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public GenericCategoryComparatorTest(String name) {
		super(name);
	}

	public void testCompareForNameSortNegativeHappyCase() throws Exception {
		GenericCategory category1 = ObjectMother.createGenericCategory(entityType);
		GenericCategory category2 = ObjectMother.createGenericCategory(entityType);
		category2.setName(category1.getName() + "2");
		assertTrue(GenericCategoryComparator.getSortByNameInstance().compare(category1, category2) < 0);
	}

	public void testCompareForNameSortPositiveHappyCase() throws Exception {
		GenericCategory category1 = ObjectMother.createGenericCategory(entityType);
		GenericCategory category2 = ObjectMother.createGenericCategory(entityType);
		category2.setName(category1.getName() + "2");
		assertTrue(GenericCategoryComparator.getSortByNameInstance().compare(category2, category1) > 0);
	}

	public void testCompareForNameSortZeroHappyCase() throws Exception {
		GenericCategory category1 = ObjectMother.createGenericCategory(entityType);
		GenericCategory category2 = ObjectMother.createGenericCategory(entityType);
		category2.setName(category1.getName());
		assertEquals(0, GenericCategoryComparator.getSortByNameInstance().compare(category2, category1));
	}
}
