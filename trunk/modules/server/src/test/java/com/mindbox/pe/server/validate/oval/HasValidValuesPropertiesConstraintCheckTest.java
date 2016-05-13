package com.mindbox.pe.server.validate.oval;

import static com.mindbox.pe.server.ServerTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

public class HasValidValuesPropertiesConstraintCheckTest extends AbstractTestWithGenericEntityType {

	private HasValidValuesForPropertiesConstraintCheck hasValidValuesForPropertiesConstraintCheck;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		final CategoryType categoryType = new CategoryType();
		categoryType.setName("category-" + entityType.getName());
		categoryType.setTypeID(entityTypeDefinition.getCategoryType());
		categoryType.setShowInSelectionTable(Boolean.FALSE);

		final PowerEditorConfiguration powerEditorConfiguration = XmlUtil.unmarshal(new FileReader("src/test/config/PowerEditorConfiguration-NoProgram.xml"), PowerEditorConfiguration.class);
		powerEditorConfiguration.setEntityConfig(new EntityConfig());
		powerEditorConfiguration.getEntityConfig().getEntityType().add(entityTypeDefinition);
		powerEditorConfiguration.getEntityConfig().getCategoryType().add(categoryType);

		ConfigurationManager.initialize("1.0", "b1", powerEditorConfiguration, "src/test/config/PowerEditorConfiguration-NoProgram.xml");

		this.hasValidValuesForPropertiesConstraintCheck = new HasValidValuesForPropertiesConstraintCheck();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(ConfigurationManager.class, "instance", null);
	}

	@Test
	public void testIsValidNegativeCaseForUnsupportedValidatedObjectType() throws Exception {
		assertFalse(hasValidValuesForPropertiesConstraintCheck.isValid("validatedObject", new HashMap<String, Object>(), null, null, null));
	}

	@Test
	public void testIsValidNegativeCaseForUnsupportedValueToValidateType() throws Exception {
		assertFalse(hasValidValuesForPropertiesConstraintCheck.isValid(createGenericEntity(entityType), Boolean.TRUE, null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithEntityWithNoProperties() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		assertTrue(hasValidValuesForPropertiesConstraintCheck.isValid(entity, new HashMap<String, Object>(), null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithInvalidEntityType() throws Exception {
		GenericEntity entity = createGenericEntity(null);
		assertTrue(hasValidValuesForPropertiesConstraintCheck.isValid(entity, new HashMap<String, Object>(), null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithNullValue() throws Exception {
		assertTrue(hasValidValuesForPropertiesConstraintCheck.isValid(null, null, null, null, null));
	}
}
