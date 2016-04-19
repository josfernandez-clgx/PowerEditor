package com.mindbox.pe.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.xsd.config.EntityType;

public class AbstractGenericEntitySearchFilterTest extends AbstractTestWithGenericEntityType {

	private static class GenericEntitySearchFilterImpl extends AbstractGenericEntitySearchFilter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3946910389295955248L;

		protected GenericEntitySearchFilterImpl(GenericEntityType entityType) {
			super(entityType);
		}
	}

	@Test
	public void testConstructorWithNullThrowsNullPointerException() throws Exception {
		try {
			new GenericEntitySearchFilterImpl(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testIsAcceptableForPersistentWithDiffTypeReturnsFalse() throws Exception {
		EntityType etDef = new EntityType();
		etDef.setName(entityType.getName() + "1");
		etDef.setDisplayName("Test Entity2");
		etDef.setTypeID(entityType.getID() + 1);
		etDef.setCategoryType(entityType.getID() + 2);
		etDef.setCanClone(Boolean.FALSE);
		GenericEntityType entityType2 = GenericEntityType.makeInstance(etDef);

		GenericEntity entity = new GenericEntity(1, entityType2, "name");
		assertFalse(new GenericEntitySearchFilterImpl(entityType).isAcceptable(entity));
	}

	@Test
	public void testIsAcceptableForPersistentWithNullThrowsNullPointerException() throws Exception {
		try {
			new GenericEntitySearchFilterImpl(entityType).isAcceptable(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testIsAcceptableForPersistentWithValidObjectReturnsTrue() throws Exception {
		GenericEntity entity = new GenericEntity(1, entityType, "name");
		assertTrue(new GenericEntitySearchFilterImpl(entityType).isAcceptable(entity));
	}

}
