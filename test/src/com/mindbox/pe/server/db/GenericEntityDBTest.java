/*
 * Created on 2004. 4. 15.
 *
 */
package com.mindbox.pe.server.db;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;

/**
 * Generic Update DB operation test case.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 * @deprecated disabled until fixed
 */
public class GenericEntityDBTest extends DBTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite(GenericEntityDBTest.class);
		suite.setName("Generic Entity DB Loader/Updater Test");
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	private EntityConfiguration entityConfig = null;
	private GenericEntityType entityType = null;
	private GenericEntity entity = null;
	private User user = null;

	public GenericEntityDBTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.populateServerCache();
		
		entityConfig = ConfigurationManager.getInstance().getEntityConfiguration();
		entityType = GenericEntityType.makeInstance(entityConfig.findEntityTypeDefinition(7));

		entity = new GenericEntity(-1, entityType, "GETest");
		entity.setParentID(-1);
		entity.setProperty("description", "test-description");
		entity.setProperty("code", "test-code");
		
		user = SecurityCacheManager.getInstance().getUser("demo");
	}

	public void testGenericEntityInsertTest() throws Exception {
		logger.info(">>> testGenericEntityInsertTest");
		
		int newID = BizActionCoordinator.getInstance().save(entity, true, user);
		entity.setID(newID);

		logger.info("testGenericEntityInsertTest: new entity saved...");

		// check cache

		assertTrue(
			"Generic entity " + entity + " not in the cache",
			EntityManager.getInstance().hasGenericEntity(entityType, newID));

		logger.info("testGenericEntityInsertTest: passed new entity cache check");
		
		// check DB 
		config.refreshServerCache();

		assertTrue(
			"Generic entity not inserted in the DB",
			EntityManager.getInstance().hasGenericEntity(entityType, newID));

		logger.info("testGenericEntityInsertTest: passed new entity DB check");

		// check update
		entity.setName("test-updated-name");
		entity.setProperty("description", "test-desc-updated");
		entity.setProperty("isBase", true);
		entity.setProperty("code", "test-code-updated");

		BizActionCoordinator.getInstance().save(entity, true, user);

		logger.info("testGenericEntityInsertTest: entity updated...");

		// check cache for update

		GenericEntity cachedEntity = EntityManager.getInstance().getEntity(entityType, newID);
		assertNotNull(cachedEntity);

		assertEquals(entity, cachedEntity);
		cachedEntity = null;

		logger.info("testGenericEntityInsertTest: passed update entity cache check");

		// check DB for update
		config.refreshServerCache();
		cachedEntity = EntityManager.getInstance().getEntity(entityType, newID);
		assertNotNull(cachedEntity);

		assertEquals(entity, cachedEntity);

		logger.info("testGenericEntityInsertTest: passed update entity DB check");
		
		// delete entity
		BizActionCoordinator.getInstance().deleteGenericEntity(entityType, newID, user);
		
		logger.info("testGenericEntityInsertTest: entity deleted...");

		// check cache for deletion
		assertFalse("Entity not removed from cache", EntityManager.getInstance().hasGenericEntity(entityType, newID));

		logger.info("testGenericEntityInsertTest: passed delete entity cache check");

		// check DB for update
		config.refreshServerCache();
		assertFalse("Entity not removed from cache", EntityManager.getInstance().hasGenericEntity(entityType, newID));

		logger.info("testGenericEntityInsertTest: passed delete entity DB check");
		
		logger.info("<<< testGenericEntityInsertTest");
	}
}
