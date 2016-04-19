package com.mindbox.pe.common;

import static com.mindbox.pe.common.CommonTestObjectMother.createEntityTypeDefinition;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;

import org.junit.Before;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.EntityType;

public abstract class AbstractTestWithGenericEntityType extends AbstractTestBase {

	protected int genericCategoryTypeId;
	protected int genericEntityTypeId;
	protected EntityType entityTypeDefinition;
	protected GenericEntityType entityType;

	@Before
	public void setUp() throws Exception {
		genericCategoryTypeId = createInt();
		genericEntityTypeId = createInt();
		entityTypeDefinition = createEntityTypeDefinition(genericEntityTypeId, genericCategoryTypeId);
		entityType = GenericEntityType.makeInstance(entityTypeDefinition);
	}
}
