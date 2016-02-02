package com.mindbox.pe.model.filter;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.SimpleEntityData;

public class AbstractPersistentFilterSpecTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractPersistentFilterSpecTest Tests");
		suite.addTestSuite(AbstractPersistentFilterSpecTest.class);
		return suite;
	}

	private static class PersistentFilterSpecImpl<T extends Persistent> extends AbstractPersistentFilterSpec<T> {

		protected PersistentFilterSpecImpl(EntityType entityType, GenericEntityType genericEntityType, String name) {
			super(entityType, genericEntityType, name);
		}

		protected PersistentFilterSpecImpl(EntityType entityType, GenericEntityType genericEntityType, int filterID, String name) {
			super(entityType, genericEntityType, filterID, name);
		}

		public SearchFilter<T> asSearchFilter() {
			return null;
		}

		public String toParamString() {
			return null;
		}

		public void setInvariants(Map<String, String> paramMap, Object helper) {
		}

	}

	public AbstractPersistentFilterSpecTest(String name) {
		super(name);
	}

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

	public void testConstructorWithNotNullAndNotNullThrowsIllegalArgumentException() throws Exception {
		try {
			new PersistentFilterSpecImpl<CBRCase>(EntityType.CBR_CASE, super.entityType, 1, "name");
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {

		}
		try {
			new PersistentFilterSpecImpl<CBRCase>(EntityType.CBR_CASE, super.entityType, "name");
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {

		}
	}

	public void testConstructorAcceptsNullEntityType() throws Exception {
		new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, "name");
		new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, 4, "name");
	}

	public void testConstructorAcceptsNullGenericEntityType() throws Exception {
		new PersistentFilterSpecImpl<CBRCase>(EntityType.CBR_CASE, null, "name");
		new PersistentFilterSpecImpl<CBRCase>(EntityType.CBR_CASE, null, 45, "name");
	}

	public void testSetIsForGenericEntityWithNullGenericEntityTypeReturnsFalse() throws Exception {
		assertFalse(new PersistentFilterSpecImpl<CBRCase>(EntityType.CBR_CASE, null, "name").isForGenericEntity());
		assertFalse(new PersistentFilterSpecImpl<CBRCase>(EntityType.CBR_CASE, null, 2, "name").isForGenericEntity());
	}

	public void testSetIsForGenericEntityWithNullEntityTypeReturnsTrue() throws Exception {
		assertTrue(new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, "name").isForGenericEntity());
		assertTrue(new PersistentFilterSpecImpl<GenericEntity>(null, super.entityType, 2, "name").isForGenericEntity());
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for AbstractPersistentFilterSpecTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractPersistentFilterSpecTest
		super.tearDown();
	}
}
