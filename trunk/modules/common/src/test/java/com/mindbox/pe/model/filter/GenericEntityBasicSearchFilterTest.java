package com.mindbox.pe.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.GenericEntity;

public class GenericEntityBasicSearchFilterTest extends AbstractEntitySearchFilterTestBase {

	private GenericEntityBasicSearchFilter genericEntityBasicSearchFilter;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		genericEntityBasicSearchFilter = new GenericEntityBasicSearchFilter(entityType);
	}

	@Test
	public void testIsAcceptableForGenericEntityWithEmptyNameCriterionReturnsTrue() throws Exception {
		genericEntityBasicSearchFilter.setNameCriterion("");

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(100);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

	@Test
	public void testIsAcceptableForGenericEntityWithNameCriterionReturnsTrueForMatchingNameOnly() throws Exception {
		genericEntityBasicSearchFilter.setNameCriterion("name");

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("205oks Name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("aslw-nAmE-sldkm20slkgm");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("nam-ame");
		assertFalse(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

	@Test
	public void testIsAcceptableForGenericEntityWithNegativeParentCriterionReturnsTrue() throws Exception {
		genericEntityBasicSearchFilter.setParentIDCriteria(-1);

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(100);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

	@Test
	public void testIsAcceptableForGenericEntityWithNoCriteriaReturnsTrue() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(-1);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(200);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

	@Test
	public void testIsAcceptableForGenericEntityWithParentCriterionReturnsTrueForMatchingNameOnly() throws Exception {
		genericEntityBasicSearchFilter.setParentIDCriteria(0);

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertFalse(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(0);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

}
