package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.model.GenericEntity;

public class GenericEntityPropertySearchFilterTest extends AbstractEntitySearchFilterTestBase {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityPropertySearchFilterTest Tests");
		suite.addTestSuite(GenericEntityPropertySearchFilterTest.class);
		return suite;
	}

	private GenericEntityPropertySearchFilter genericEntityPropertySearchFilter;
	private GenericEntity entity;
	
	public GenericEntityPropertySearchFilterTest(String name) {
		super(name);
	}

	public void testIsAcceptableUsesFalseBooleanPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("isBase", false);
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("isBase", true);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("isBase", false);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesTrueBooleanPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("isBase", true);
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("isBase", false);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("isBase", true);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesIntPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 50);
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 51);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("property", 50);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesLongPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 1234567890L);
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 123456789L);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("property", 1234567890L);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesFloatPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 0.2516f);
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 1.2516f);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("property", 0.2516f);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesDoublePropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 1234.56789);
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 1234.5678);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("property", 1234.56789);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesDatePropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("created", getDate(2007,5,31,6,30,30));
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("created", getDate(2007,5,31,6,31,00));
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("created", getDate(2007,5,31,6,30,30));
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableUsesStringPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("description", "value");
		
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("description", "val");
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		
		entity.setProperty("description", "has value");
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}
	
	public void testIsAcceptableForGenericEntityWithNoCriteriaReturnsTrue() throws Exception {
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		entity.setParentID(100);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	
	protected void setUp() throws Exception {
		super.setUp();
		genericEntityPropertySearchFilter = new GenericEntityPropertySearchFilter(entityType);
		entity = new GenericEntity(1, entityType, "name");
	}

	protected void tearDown() throws Exception {
		entity = null;
		genericEntityPropertySearchFilter = null;
		super.tearDown();
	}
}
