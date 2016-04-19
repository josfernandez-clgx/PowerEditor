package com.mindbox.pe.server;

import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.config.EntityType;

public abstract class AbstractTestWithGenericEntityType extends AbstractTestBase {

	protected static final String TEST_ENTITY_TYPE_NAME = "test";

	protected static final int TEST_ENTITY_TYPE_ID = 5;
	protected static final int TEST_CATEGORY_TYPE_ID = 40;

	protected GenericEntityType entityType;
	protected EntityType entityTypeDefinition;

	@Before
	public void setUp() throws Exception {
		entityTypeDefinition = new EntityType();
		entityTypeDefinition.setName(TEST_ENTITY_TYPE_NAME);
		entityTypeDefinition.setDisplayName("Test Entity");
		entityTypeDefinition.setTypeID(TEST_ENTITY_TYPE_ID);
		entityTypeDefinition.setCanClone(Boolean.TRUE);
		entityTypeDefinition.setCategoryType(TEST_CATEGORY_TYPE_ID);
		EntityProperty propDefinition = new EntityProperty();
		propDefinition.setName("string-property");
		propDefinition.setDisplayName("String Property");
		propDefinition.setIsRequired(Boolean.TRUE);
		propDefinition.setType(EntityPropertyType.STRING);
		entityTypeDefinition.getEntityProperty().add(propDefinition);
		propDefinition = new EntityProperty();
		propDefinition.setName("integer-property");
		propDefinition.setDisplayName("Integer Property");
		propDefinition.setIsRequired(Boolean.FALSE);
		propDefinition.setType(EntityPropertyType.INTEGER);
		entityTypeDefinition.getEntityProperty().add(propDefinition);
		propDefinition = new EntityProperty();
		propDefinition.setName("boolean-property");
		propDefinition.setDisplayName("Boolean Property");
		propDefinition.setIsRequired(Boolean.FALSE);
		propDefinition.setType(EntityPropertyType.BOOLEAN);
		entityTypeDefinition.getEntityProperty().add(propDefinition);
		entityType = GenericEntityType.makeInstance(entityTypeDefinition);
	}

	@After
	public void tearDown() throws Exception {
		clearGenericEntityTypeCache();
	}

	@SuppressWarnings("rawtypes")
	protected final void clearGenericEntityTypeCache() throws Exception {
		((Map) ReflectionUtil.getStaticPrivate(GenericEntityType.class, "instanceMap")).clear();
	}
}
