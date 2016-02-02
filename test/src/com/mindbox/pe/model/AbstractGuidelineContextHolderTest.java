package com.mindbox.pe.model;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.common.AbstractGuidelineContextHolder;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;

public class AbstractGuidelineContextHolderTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractGuidelineContextHolderTest Tests");
		suite.addTestSuite(AbstractGuidelineContextHolderTest.class);
		return suite;
	}

	/**
	 * 
	 * Helper class to test abstract super class. This must NOT override parent's methods.
	 */
	private static class GuidelineContextHolderImpl extends AbstractGuidelineContextHolder {

		protected EntityConfiguration getEntityConfiguration() {
			return ConfigurationManager.getInstance().getEntityConfiguration();
		}

		protected GenericEntity getGenericEntity(GenericEntityType type, int id) {
			return EntityManager.getInstance().getEntity(type, id);
		}

		protected GenericCategory getGenericCategory(int genericCategoryType, int categoryID) {
			return EntityManager.getInstance().getGenericCategory(genericCategoryType, categoryID);
		}

	}

	private GenericEntityType productType, programType;

	private AbstractGuidelineContextHolder contextHolder;

	public AbstractGuidelineContextHolderTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();

		productType = GenericEntityType.forName("product");
		programType = GenericEntityType.forName("program");

		// add test entities
		Map<String, Object> emptyMap = new HashMap<String, Object>();
		EntityManager.getInstance().startLoading();
		EntityManager.getInstance().addGenericEntity(4, GenericEntityType.forName("channel").getID(), "Channel1", -1, emptyMap);
		EntityManager.getInstance().addGenericEntity(3, GenericEntityType.forName("investor").getID(), "Investor3",  -1, emptyMap);

		EntityManager.getInstance().addGenericEntity(1, productType.getID(), "Product 1", -1, emptyMap);
		EntityManager.getInstance().addGenericEntity(2, productType.getID(), "Product 2", 1, emptyMap);
		EntityManager.getInstance().addGenericEntityCategory(10, 11, "Test Category");
		EntityManager.getInstance().addGenericEntityToCategory(11, 10, 1, productType.getID(), -1, -1);

		EntityManager.getInstance().addGenericEntity(1, programType.getID(), "Program 1", -1, emptyMap);
		EntityManager.getInstance().addGenericEntityCategory(20, 22, "Test Category");
		EntityManager.getInstance().addGenericEntityToCategory(22, 20, 1, programType.getID(), -1, -1);
		
		EntityManager.getInstance().addGenericEntityCategory(30, 1000, "Test Channel Category");
		EntityManager.getInstance().addGenericEntityCategory(40, 1000, "Test Investor Category");
		EntityManager.getInstance().addGenericEntityCategory(50, 1000, "Test Branch Category");

		// finishLoading accesses DB if an entity has no categories; make sure each entity has at least one category
		EntityManager.getInstance().finishLoading();

		contextHolder = new GuidelineContextHolderImpl();
		
		assertTrue(EntityManager.getInstance().hasGenericEntity(GenericEntityType.forName("channel"), 4));
		assertTrue(EntityManager.getInstance().hasGenericEntity(GenericEntityType.forName("investor"), 3));
		assertNotNull(EntityManager.getInstance().getEntity(GenericEntityType.forName("investor"), 3));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		EntityManager.getInstance().startLoading();
		productType = null;
		programType = null;
		contextHolder = null;
	}

	public void testGetGuidelineContextsWithTwoGenericEntities() throws Exception {
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(productType, 2) });
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(programType, 1) });

		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == GenericEntityType.forName("product") && contexts[i].getIDs()[0] == 2)
					|| (contexts[i].getGenericEntityType() == programType && contexts[i].getIDs()[0] == 1));
		}
	}

	public void testGetGuidelineContextsWithChannelAndProductGenericEntity() throws Exception {
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(productType, 2) });
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(GenericEntityType.forName("channel"), 4) });

		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == GenericEntityType.forName("product") && contexts[i].getIDs()[0] == 2)
					|| (contexts[i].getGenericEntityType() == GenericEntityType.forName("channel") && contexts[i].getIDs()[0] == 4));
		}
	}

	public void testGetGuidelineContextsWithChannelAndInvestor() throws Exception {
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(GenericEntityType.forName("investor"), 3) });
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(GenericEntityType.forName("channel"), 4) });

		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == GenericEntityType.forName("investor") && contexts[i].getIDs()[0] == 3)
					|| (contexts[i].getGenericEntityType() == GenericEntityType.forName("channel") && contexts[i].getIDs()[0] == 4));
		}
	}

	public void testGetGuidelineContextsWithTwoGenericCategories() throws Exception {
		contextHolder.addContext(new GenericCategory[] { EntityManager.getInstance().getGenericCategory(10, 11) });
		contextHolder.addContext(new GenericCategory[] { EntityManager.getInstance().getGenericCategory(20, 22) });
		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericCategoryType() == 10 && contexts[i].getIDs()[0] == 11)
					|| (contexts[i].getGenericCategoryType() == 20 && contexts[i].getIDs()[0] == 22));

		}
	}

	public void testGetGuidelineContextsWithGenericCategoryAndProductGenericEntity() throws Exception {
		contextHolder.addContext(new GenericEntity[] { EntityManager.getInstance().getEntity(productType, 2) });
		contextHolder.addContext(new GenericCategory[] { EntityManager.getInstance().getGenericCategory(20, 22) });
		GuidelineContext[] contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == GenericEntityType.forName("product") && contexts[i].getIDs()[0] == 2)
					|| (contexts[i].getGenericCategoryType() == 20 && contexts[i].getIDs()[0] == 22));

		}
	}

	public void testSetContextElemensWithTwoGenericEntities() throws Exception {
		GuidelineContext c1 = new GuidelineContext(productType);
		c1.setIDs(new int[] { 2 });
		GuidelineContext c2 = new GuidelineContext(programType);
		c2.setIDs(new int[] { 1 });
		GuidelineContext[] contexts = new GuidelineContext[] { c1, c2 };
		contextHolder.setContextElemens(contexts);

		contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericEntityType() == GenericEntityType.forName("product") && contexts[i].getIDs()[0] == 2)
					|| (contexts[i].getGenericEntityType() == programType && contexts[i].getIDs()[0] == 1));
		}
	}

	public void testSetContextElemensWithTwoGenericCategories() throws Exception {
		GuidelineContext c1 = new GuidelineContext(10);
		c1.setIDs(new int[] { 11 });
		GuidelineContext c2 = new GuidelineContext(20);
		c2.setIDs(new int[] { 22 });
		GuidelineContext[] contexts = new GuidelineContext[] { c1, c2 };
		contextHolder.setContextElemens(contexts);

		contexts = contextHolder.getGuidelineContexts();
		assertEquals(2, contexts.length);
		for (int i = 0; i < contexts.length; i++) {
			assertEquals(1, contexts[i].getIDs().length);
			assertTrue((contexts[i].getGenericCategoryType() == 10 && contexts[i].getIDs()[0] == 11)
					|| (contexts[i].getGenericCategoryType() == 20 && contexts[i].getIDs()[0] == 22));

		}
	}
}
