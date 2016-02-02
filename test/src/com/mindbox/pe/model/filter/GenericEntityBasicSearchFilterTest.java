package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.model.GenericEntity;

public class GenericEntityBasicSearchFilterTest extends AbstractEntitySearchFilterTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityBasicSearchFilterTest Tests");
		suite.addTestSuite(GenericEntityBasicSearchFilterTest.class);
		return suite;
	}


	private GenericEntityBasicSearchFilter genericEntityBasicSearchFilter;

	public GenericEntityBasicSearchFilterTest(String name) {
		super(name);
	}

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

	public void testIsAcceptableForGenericEntityWithNegativeParentCriterionReturnsTrue() throws Exception {
		genericEntityBasicSearchFilter.setParentIDCriteria(-1);

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(100);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

	public void testIsAcceptableForGenericEntityWithEmptyNameCriterionReturnsTrue() throws Exception {
		genericEntityBasicSearchFilter.setNameCriterion("");

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(100);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

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

	public void testIsAcceptableForGenericEntityWithParentCriterionReturnsTrueForMatchingNameOnly() throws Exception {
		genericEntityBasicSearchFilter.setParentIDCriteria(0);

		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertFalse(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
		entity.setParentID(0);
		assertTrue(invokeIsAcceptable(genericEntityBasicSearchFilter, entity));
	}

	protected void setUp() throws Exception {
		super.setUp();
		genericEntityBasicSearchFilter = new GenericEntityBasicSearchFilter(entityType);
	}

	protected void tearDown() throws Exception {
		genericEntityBasicSearchFilter = null;
		super.tearDown();
	}
}
