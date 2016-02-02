package com.mindbox.pe.server.validate.oval;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.server.cache.EntityManager;

public class UniqueNameConstraintCheckTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("UniqueNameConstraintCheckTest Tests");
		suite.addTestSuite(UniqueNameConstraintCheckTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private UniqueNameConstraintCheck uniqueNameConstraintCheck;

	public UniqueNameConstraintCheckTest(String name) {
		super(name);
	}

	public void testIsValidPositiveCaseWithNullValue() throws Exception {
		assertTrue(uniqueNameConstraintCheck.isValid(null, null, null, null, null));
	}

	public void testIsValidPositiveCaseWithGenericEntity() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		assertTrue(uniqueNameConstraintCheck.isValid(entity, entity.getName(), null, null, null));
	}

	public void testIsValidPositiveCaseWithGenericEntityForUniqueEntityNamesSetToFalse() throws Exception {
		entityTypeDefinition.setUniqueEntityNames(false);
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		EntityManager.getInstance().addGenericEntity(entity.getID() + 1, entityType.getID(), entity.getName(), -1, entity.getPropertyMap());
		assertTrue(uniqueNameConstraintCheck.isValid(entity, entity.getName(), null, null, null));
	}

	public void testIsValidPositiveCaseWithGenericCategory() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(entityType);
		assertTrue(uniqueNameConstraintCheck.isValid(category, category.getName(), null, null, null));
	}

	public void testIsValidNegativeCaseForUnsupportedValueToValidateType() throws Exception {
		assertFalse(uniqueNameConstraintCheck.isValid(ObjectMother.createGenericEntity(entityType), Boolean.TRUE, null, null, null));
	}

	public void testIsValidNegativeCaseForUnsupportedValidatedObjectType() throws Exception {
		assertFalse(uniqueNameConstraintCheck.isValid("validatedObject", "valueToValidate", null, null, null));
	}

	public void testIsValidNegativeCaseWithGenericEntityForUniqueEntityNamesSetToTrue() throws Exception {
		entityTypeDefinition.setUniqueEntityNames(true);
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		EntityManager.getInstance().addGenericEntity(entity.getID() + 1, entityType.getID(), entity.getName(), -1, entity.getPropertyMap());
		assertFalse(uniqueNameConstraintCheck.isValid(entity, entity.getName(), null, null, null));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.uniqueNameConstraintCheck = new UniqueNameConstraintCheck();
	}
}
