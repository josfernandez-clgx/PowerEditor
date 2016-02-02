package com.mindbox.pe;

import java.util.Map;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericEntityType;

public abstract class AbstractTestWithGenericEntityType extends AbstractTestBase {

	protected static final String TEST_ENTITY_TYPE_NAME = "test";

	protected static final int TEST_ENTITY_TYPE_ID = 5;
	protected static final int TEST_CATEGORY_TYPE_ID = 40;

	protected GenericEntityType entityType;
	protected EntityTypeDefinition entityTypeDefinition;

	protected AbstractTestWithGenericEntityType(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		entityTypeDefinition = new EntityTypeDefinition();
		entityTypeDefinition.setName(TEST_ENTITY_TYPE_NAME);
		entityTypeDefinition.setDisplayName("Test Entity");
		entityTypeDefinition.setTypeID(TEST_ENTITY_TYPE_ID);
		entityTypeDefinition.setCanClone("yes");
		entityTypeDefinition.setCategoryType(TEST_CATEGORY_TYPE_ID);
		EntityPropertyDefinition propDefinition = new EntityPropertyDefinition();
		propDefinition.setName("string-property");
		propDefinition.setDisplayName("String Property");
		propDefinition.setIsRequired("true");
		propDefinition.setType(ConfigUtil.PROPERTY_TYPE_STRING);
		entityTypeDefinition.addPropertyDefinition(propDefinition);
		propDefinition = new EntityPropertyDefinition();
		propDefinition.setName("integer-property");
		propDefinition.setDisplayName("Integer Property");
		propDefinition.setIsRequired("false");
		propDefinition.setType(ConfigUtil.PROPERTY_TYPE_INT);
		entityTypeDefinition.addPropertyDefinition(propDefinition);
		propDefinition = new EntityPropertyDefinition();
		propDefinition.setName("boolean-property");
		propDefinition.setDisplayName("Boolean Property");
		propDefinition.setIsRequired("false");
		propDefinition.setType(ConfigUtil.PROPERTY_TYPE_BOOLEAN);
		entityTypeDefinition.addPropertyDefinition(propDefinition);
		entityType = GenericEntityType.makeInstance(entityTypeDefinition);
	}

	protected void tearDown() throws Exception {
		clearGenericEntityTypeCache();
		super.tearDown();
	}
	
	@SuppressWarnings("unchecked")
	protected final void clearGenericEntityTypeCache() throws Exception {
		((Map)ReflectionUtil.getStaticPrivate(GenericEntityType.class, "instanceMap")).clear();
	}
}
