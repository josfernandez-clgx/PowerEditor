package com.mindbox.pe.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.SimpleEntityData;
import com.mindbox.pe.model.cbr.CBRCase;

public class AbstractPersistentFilterSpecTest extends AbstractTestWithGenericEntityType {

	private static class PersistentFilterSpecImpl<T extends Persistent> extends AbstractPersistentFilterSpec<T> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1972014074479063291L;

		protected PersistentFilterSpecImpl(PeDataType entityType, GenericEntityType genericEntityType, int filterID, String name) {
			super(entityType, genericEntityType, filterID, name);
		}

		protected PersistentFilterSpecImpl(PeDataType entityType, GenericEntityType genericEntityType, String name) {
			super(entityType, genericEntityType, name);
		}

		public SearchFilter<T> asSearchFilter() {
			return null;
		}

		public void setInvariants(Map<String, String> paramMap, Object helper) {
		}

		public String toParamString() {
			return null;
		}

	}

	@Test
	public void testConstructorAcceptsNullEntityType() throws Exception {
		new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, "name");
		new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, 4, "name");
	}

	@Test
	public void testConstructorAcceptsNullGenericEntityType() throws Exception {
		new PersistentFilterSpecImpl<CBRCase>(PeDataType.CBR_CASE, null, "name");
		new PersistentFilterSpecImpl<CBRCase>(PeDataType.CBR_CASE, null, 45, "name");
	}

	@Test
	public void testConstructorWithNotNullAndNotNullThrowsIllegalArgumentException() throws Exception {
		try {
			new PersistentFilterSpecImpl<CBRCase>(PeDataType.CBR_CASE, super.entityType, 1, "name");
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {

		}
		try {
			new PersistentFilterSpecImpl<CBRCase>(PeDataType.CBR_CASE, super.entityType, "name");
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {

		}
	}

	@Test
	public void testConstructorWithNullAndNullThrowsIllegalArgumentException() throws Exception {
		try {
			new PersistentFilterSpecImpl<SimpleEntityData>(null, null, 1, "name");
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {

		}
		try {
			new PersistentFilterSpecImpl<SimpleEntityData>(null, null, "name");
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {

		}
	}

	@Test
	public void testSetIsForGenericEntityWithNullEntityTypeReturnsTrue() throws Exception {
		assertTrue(new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, "name").isForGenericEntity());
		assertTrue(new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, 2, "name").isForGenericEntity());
	}

	@Test
	public void testSetIsForGenericEntityWithNullGenericEntityTypeReturnsFalse() throws Exception {
		assertFalse(new PersistentFilterSpecImpl<CBRCase>(PeDataType.CBR_CASE, null, "name").isForGenericEntity());
		assertFalse(new PersistentFilterSpecImpl<CBRCase>(PeDataType.CBR_CASE, null, 2, "name").isForGenericEntity());
	}

}
