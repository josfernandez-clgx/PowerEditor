package com.mindbox.pe.model;

import static com.mindbox.pe.common.CommonTestObjectMother.createGenericCategory;
import static com.mindbox.pe.common.CommonTestObjectMother.createGenericEntity;
import static com.mindbox.pe.common.CommonTestObjectMother.createGenericEntityType;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractGuidelineContextHolder;
import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.EntityType;


public class AbstractGuidelineContextHolderTest extends AbstractTestBase {

	/**
	 * 
	 * Helper class to test abstract super class. This must NOT override parent's methods.
	 */
	private class GuidelineContextHolderImpl extends AbstractGuidelineContextHolder {

		protected EntityConfigHelper getEntityConfiguration() {
			return entityConfigHelper;
		}

		protected GenericCategory getGenericCategory(int genericCategoryType, int categoryID) {
			if (genericCategoryType == genericEntityType1.getCategoryType()) return category1;
			if (genericCategoryType == genericEntityType2.getCategoryType()) return category2;
			return null;
		}

		protected GenericEntity getGenericEntity(GenericEntityType type, int id) {
			if (type == genericEntityType1) return entity1;
			if (type == genericEntityType2) return entity2;
			if (type == genericEntityType3) return entity3;
			if (type == genericEntityType4) return entity4;
			return null;
		}
	}

	private GenericEntityType genericEntityType1;
	private GenericEntityType genericEntityType2;
	private GenericEntityType genericEntityType3;
	private GenericEntityType genericEntityType4;
	private EntityConfigHelper entityConfigHelper;
	private AbstractGuidelineContextHolder contextHolder;
	private GenericCategory category1;
	private GenericCategory category2;
	private GenericEntity entity1;
	private GenericEntity entity2;
	private GenericEntity entity3;
	private GenericEntity entity4;

	@Before
	public void setUp() throws Exception {
		genericEntityType1 = createGenericEntityType(createInt(), createInt());
		genericEntityType2 = createGenericEntityType(createInt(), createInt());
		genericEntityType3 = createGenericEntityType(createInt(), createInt());
		genericEntityType4 = createGenericEntityType(createInt(), createInt());

		EntityConfig entityConfig = new EntityConfig();

		EntityType entityTypeDefinition = new EntityType();
		entityTypeDefinition.setCategoryType(genericEntityType1.getCategoryType());
		entityTypeDefinition.setTypeID(genericEntityType1.getID());
		entityConfig.getEntityType().add(entityTypeDefinition);

		entityTypeDefinition = new EntityType();
		entityTypeDefinition.setCategoryType(genericEntityType2.getCategoryType());
		entityTypeDefinition.setTypeID(genericEntityType2.getID());
		entityConfig.getEntityType().add(entityTypeDefinition);

		CategoryType categoryType = new CategoryType();
		categoryType.setName("category for " + genericEntityType1.getName());
		categoryType.setTypeID(genericEntityType1.getCategoryType());
		entityConfig.getCategoryType().add(categoryType);

		categoryType = new CategoryType();
		categoryType.setName("category for " + genericEntityType2.getName());
		categoryType.setTypeID(genericEntityType2.getCategoryType());
		entityConfig.getCategoryType().add(categoryType);

		entityConfigHelper = new EntityConfigHelper(entityConfig);

		category1 = createGenericCategory(genericEntityType1);
		category2 = createGenericCategory(genericEntityType2);
		entity1 = createGenericEntity(genericEntityType1);
		entity2 = createGenericEntity(genericEntityType2);
		entity3 = createGenericEntity(genericEntityType3);
		entity4 = createGenericEntity(genericEntityType4);

		contextHolder = new GuidelineContextHolderImpl();
	}

	@Test
	public void testGetGuidelineContextsWithChannelAndInvestor() throws Exception {
		contextHolder.addContext(new GenericEntity[] { entity2 });
		contextHolder.addContext(new GenericEntity[] { entity1 });

		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == genericEntityType2 && contexts[i].getIDs()[0] == entity2.getId())
					|| (contexts[i].getGenericEntityType() == genericEntityType1 && contexts[i].getIDs()[0] == entity1.getId()));
		}
	}

	@Test
	public void testGetGuidelineContextsWithChannelAndProductGenericEntity() throws Exception {
		contextHolder.addContext(new GenericEntity[] { entity3 });
		contextHolder.addContext(new GenericEntity[] { entity1 });

		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == genericEntityType3 && contexts[i].getIDs()[0] == entity3.getId())
					|| (contexts[i].getGenericEntityType() == genericEntityType1 && contexts[i].getIDs()[0] == entity1.getId()));
		}
	}

	@Test
	public void testGetGuidelineContextsWithGenericCategoryAndProductGenericEntity() throws Exception {
		contextHolder.addContext(new GenericEntity[] { entity3 });
		contextHolder.addContext(new GenericCategory[] { category1 });
		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == genericEntityType3 && contexts[i].getIDs()[0] == entity3.getId())
					|| (contexts[i].getGenericCategoryType() == genericEntityType1.getCategoryType() && contexts[i].getIDs()[0] == category1.getId()));

		}
	}

	@Test
	public void testGetGuidelineContextsWithTwoGenericCategories() throws Exception {
		contextHolder.addContext(new GenericCategory[] { category1 });
		contextHolder.addContext(new GenericCategory[] { category2 });
		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericCategoryType() == genericEntityType1.getCategoryType() && contexts[i].getIDs()[0] == category1.getId())
					|| (contexts[i].getGenericCategoryType() == genericEntityType2.getCategoryType() && contexts[i].getIDs()[0] == category2.getId()));

		}
	}

	@Test
	public void testGetGuidelineContextsWithTwoGenericEntities() throws Exception {
		contextHolder.addContext(new GenericEntity[] { entity3 });
		contextHolder.addContext(new GenericEntity[] { entity4 });

		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == genericEntityType3 && contexts[i].getIDs()[0] == entity3.getId())
					|| (contexts[i].getGenericEntityType() == genericEntityType4 && contexts[i].getIDs()[0] == entity4.getId()));
		}
	}

	@Test
	public void testSetContextElemensWithTwoGenericCategories() throws Exception {
		GuidelineContext c1 = new GuidelineContext(genericEntityType1.getCategoryType());
		c1.setIDs(new int[] { category1.getId() });
		GuidelineContext c2 = new GuidelineContext(genericEntityType2.getCategoryType());
		c2.setIDs(new int[] { category2.getId() });
		GuidelineContext[] contexts = new GuidelineContext[] { c1, c2 };
		contextHolder.setContextElemens(contexts);

		contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericCategoryType() == genericEntityType1.getCategoryType() && contexts[i].getIDs()[0] == category1.getId())
					|| (contexts[i].getGenericCategoryType() == genericEntityType2.getCategoryType() && contexts[i].getIDs()[0] == category2.getId()));

		}
	}

	@Test
	public void testSetContextElemensWithTwoGenericEntities() throws Exception {
		GuidelineContext c1 = new GuidelineContext(genericEntityType3);
		c1.setIDs(new int[] { entity3.getId() });
		GuidelineContext c2 = new GuidelineContext(genericEntityType4);
		c2.setIDs(new int[] { entity4.getId() });
		GuidelineContext[] contexts = new GuidelineContext[] { c1, c2 };
		contextHolder.setContextElemens(contexts);

		contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == genericEntityType3 && contexts[i].getIDs()[0] == entity3.getId())
					|| (contexts[i].getGenericEntityType() == genericEntityType4 && contexts[i].getIDs()[0] == entity4.getId()));
		}
	}
}
