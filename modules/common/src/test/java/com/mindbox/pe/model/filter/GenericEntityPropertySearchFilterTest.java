package com.mindbox.pe.model.filter;

import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.GenericEntity;

public class GenericEntityPropertySearchFilterTest extends AbstractEntitySearchFilterTestBase {

	private GenericEntityPropertySearchFilter genericEntityPropertySearchFilter;
	private GenericEntity entity;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		genericEntityPropertySearchFilter = new GenericEntityPropertySearchFilter(entityType);
		entity = new GenericEntity(1, entityType, "name");
	}

	@Test
	public void testIsAcceptableForGenericEntityWithNoCriteriaReturnsTrue() throws Exception {
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		entity.setName("");
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
		entity.setParentID(100);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesDatePropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("created", getDate(2007, 5, 31, 6, 30, 30));

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("created", getDate(2007, 5, 31, 6, 31, 00));
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("created", getDate(2007, 5, 31, 6, 30, 30));
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesDoublePropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 1234.56789);

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 1234.5678);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 1234.56789);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesFalseBooleanPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("isBase", false);

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("isBase", true);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("isBase", false);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesFloatPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 0.2516f);

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 1.2516f);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 0.2516f);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesIntPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 50);

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 51);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 50);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesLongPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("property", 1234567890L);

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 123456789L);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("property", 1234567890L);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesStringPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("description", "value");

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("description", "val");
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("description", "has value");
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

	@Test
	public void testIsAcceptableUsesTrueBooleanPropertyProperly() throws Exception {
		genericEntityPropertySearchFilter.setPropertyCriterion("isBase", true);

		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("isBase", false);
		assertFalse(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));

		entity.setProperty("isBase", true);
		assertTrue(invokeIsAcceptable(genericEntityPropertySearchFilter, entity));
	}

}
