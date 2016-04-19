package com.mindbox.pe.model.comparator;

import static com.mindbox.pe.common.CommonTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.GenericCategory;

public class GenericCategoryComparatorTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testCompareForNameSortNegativeHappyCase() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category2.setName(category1.getName() + "2");
		assertTrue(GenericCategoryComparator.getSortByNameInstance().compare(category1, category2) < 0);
	}

	@Test
	public void testCompareForNameSortPositiveHappyCase() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category2.setName(category1.getName() + "2");
		assertTrue(GenericCategoryComparator.getSortByNameInstance().compare(category2, category1) > 0);
	}

	@Test
	public void testCompareForNameSortZeroHappyCase() throws Exception {
		GenericCategory category1 = createGenericCategory(entityType);
		GenericCategory category2 = createGenericCategory(entityType);
		category2.setName(category1.getName());
		assertEquals(0, GenericCategoryComparator.getSortByNameInstance().compare(category2, category1));
	}
}
