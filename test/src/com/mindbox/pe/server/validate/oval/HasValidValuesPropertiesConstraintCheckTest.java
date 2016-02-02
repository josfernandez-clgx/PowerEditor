package com.mindbox.pe.server.validate.oval;

import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GenericEntity;

public class HasValidValuesPropertiesConstraintCheckTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("UniqueNameConstraintCheckTest Tests");
		suite.addTestSuite(HasValidValuesPropertiesConstraintCheckTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private HasValidValuesForPropertiesConstraintCheck hasValidValuesForPropertiesConstraintCheck;

	public HasValidValuesPropertiesConstraintCheckTest(String name) {
		super(name);
	}

	public void testIsValidPositiveCaseWithNullValue() throws Exception {
		assertTrue(hasValidValuesForPropertiesConstraintCheck.isValid(null, null, null, null, null));
	}

	public void testIsValidPositiveCaseWithInvalidEntityType() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(null);
		assertTrue(hasValidValuesForPropertiesConstraintCheck.isValid(entity, new HashMap<String, Object>(), null, null, null));
	}

	public void testIsValidPositiveCaseWithEntityWithNoProperties() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(entityType);
		assertTrue(hasValidValuesForPropertiesConstraintCheck.isValid(entity, new HashMap<String, Object>(), null, null, null));
	}

	public void testIsValidNegativeCaseForUnsupportedValueToValidateType() throws Exception {
		assertFalse(hasValidValuesForPropertiesConstraintCheck.isValid(
				ObjectMother.createGenericEntity(entityType),
				Boolean.TRUE,
				null,
				null,
				null));
	}

	public void testIsValidNegativeCaseForUnsupportedValidatedObjectType() throws Exception {
		assertFalse(hasValidValuesForPropertiesConstraintCheck.isValid("validatedObject", new HashMap<String, Object>(), null, null, null));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.hasValidValuesForPropertiesConstraintCheck = new HasValidValuesForPropertiesConstraintCheck();
	}
}
