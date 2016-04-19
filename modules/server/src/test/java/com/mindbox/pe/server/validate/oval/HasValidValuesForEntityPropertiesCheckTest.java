package com.mindbox.pe.server.validate.oval;

import static org.junit.Assert.assertTrue;

import java.io.FileReader;

import org.junit.After;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

public class HasValidValuesForEntityPropertiesCheckTest extends AbstractTestWithGenericEntityType {

	private HasValidValuesForEntityPropertiesCheck hasValidValuesForEntityPropertiesCheck;

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

		this.hasValidValuesForEntityPropertiesCheck = new HasValidValuesForEntityPropertiesCheck();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(ConfigurationManager.class, "instance", null);
	}

	@Test
	public void testIsSatisfiedPositiveCaseForUnsupportedValidatedObjectType() throws Exception {
		assertTrue(hasValidValuesForEntityPropertiesCheck.isSatisfied(Boolean.TRUE, Boolean.TRUE, null, null));
	}

	@Test
	public void testIsSatisfiedPositiveCaseWithNullValue() throws Exception {
		assertTrue(hasValidValuesForEntityPropertiesCheck.isSatisfied(null, null, null, null));
	}
}
