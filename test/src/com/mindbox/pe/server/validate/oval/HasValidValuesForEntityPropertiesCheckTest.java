package com.mindbox.pe.server.validate.oval;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.oval.context.ClassContext;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.imexport.digest.Entity;
import com.mindbox.pe.server.imexport.digest.Property;

public class HasValidValuesForEntityPropertiesCheckTest extends AbstractTestWithGenericEntityType {

	public static Test suite() {
		TestSuite suite = new TestSuite("UniqueNameConstraintCheckTest Tests");
		suite.addTestSuite(HasValidValuesForEntityPropertiesCheckTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private HasValidValuesForEntityPropertiesCheck hasValidValuesForEntityPropertiesCheck;

	public HasValidValuesForEntityPropertiesCheckTest(String name) {
		super(name);
	}

	public void testIsSatisfiedPositiveCaseWithNullValue() throws Exception {
		assertTrue(hasValidValuesForEntityPropertiesCheck.isSatisfied(null, null, null, null));
	}

	public void testIsSatisfiedPositiveCaseWithInvalidEntityType() throws Exception {
		Entity entity = ObjectMother.createEntity("xyz");
		assertTrue(hasValidValuesForEntityPropertiesCheck.isSatisfied(entity, entity, new ClassContext(Entity.class), null));
	}

	public void testIsSatisfiedPositiveCaseWithEntityWithNoProperties() throws Exception {
		Entity entity = ObjectMother.createEntity(entityType.getName());
		Property property = new Property();
		property.setName("name");
		property.setValue(ObjectMother.createString());
		entity.addObject(property);
		assertTrue(hasValidValuesForEntityPropertiesCheck.isSatisfied(entity, entity, new ClassContext(Entity.class), null));
	}

	public void testIsSatisfiedPositiveCaseForUnsupportedValidatedObjectType() throws Exception {
		assertTrue(hasValidValuesForEntityPropertiesCheck.isSatisfied(Boolean.TRUE, Boolean.TRUE, null, null));
	}

	public void testIsSatisfiedNegativeCaseForBooleanPropertyType() throws Exception {
		Entity entity = ObjectMother.createEntity(entityType.getName());
		Property property = new Property();
		property.setName("boolean-property");
		property.setValue(ObjectMother.createString());
		entity.addObject(property);
		assertFalse(hasValidValuesForEntityPropertiesCheck.isSatisfied(entity, entity, new ClassContext(Entity.class), null));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.hasValidValuesForEntityPropertiesCheck = new HasValidValuesForEntityPropertiesCheck();
	}
}
