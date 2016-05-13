package com.mindbox.pe.server.validate.oval;

import static com.mindbox.pe.server.ServerTestObjectMother.createGenericCategory;
import static com.mindbox.pe.server.ServerTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

public class UniqueNameConstraintCheckTest extends AbstractTestWithGenericEntityType {

	private UniqueNameConstraintCheck uniqueNameConstraintCheck;

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
		this.uniqueNameConstraintCheck = new UniqueNameConstraintCheck();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(ConfigurationManager.class, "instance", null);
	}

	@Test
	public void testIsValidNegativeCaseForUnsupportedValidatedObjectType() throws Exception {
		assertFalse(uniqueNameConstraintCheck.isValid("validatedObject", "valueToValidate", null, null, null));
	}

	@Test
	public void testIsValidNegativeCaseForUnsupportedValueToValidateType() throws Exception {
		assertFalse(uniqueNameConstraintCheck.isValid(createGenericEntity(entityType), Boolean.TRUE, null, null, null));
	}

	@Test
	public void testIsValidNegativeCaseWithGenericEntityForUniqueEntityNamesSetToTrue() throws Exception {
		entityTypeDefinition.setUniqueEntityNames(Boolean.TRUE);
		GenericEntity entity = createGenericEntity(entityType);
		EntityManager.getInstance().addGenericEntity(entity.getID() + 1, entityType.getID(), entity.getName(), -1, entity.getPropertyMap());
		assertFalse(uniqueNameConstraintCheck.isValid(entity, entity.getName(), null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithGenericCategory() throws Exception {
		GenericCategory category = createGenericCategory(entityType);
		assertTrue(uniqueNameConstraintCheck.isValid(category, category.getName(), null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithGenericEntity() throws Exception {
		GenericEntity entity = createGenericEntity(entityType);
		assertTrue(uniqueNameConstraintCheck.isValid(entity, entity.getName(), null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithGenericEntityForUniqueEntityNamesSetToFalse() throws Exception {
		entityTypeDefinition.setUniqueEntityNames(Boolean.FALSE);
		GenericEntity entity = createGenericEntity(entityType);
		EntityManager.getInstance().addGenericEntity(entity.getID() + 1, entityType.getID(), entity.getName(), -1, entity.getPropertyMap());
		assertTrue(uniqueNameConstraintCheck.isValid(entity, entity.getName(), null, null, null));
	}

	@Test
	public void testIsValidPositiveCaseWithNullValue() throws Exception {
		assertTrue(uniqueNameConstraintCheck.isValid(null, null, null, null, null));
	}
}
