package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.server.ServerTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.xsd.data.EntityDataElement;
import com.mindbox.pe.xsd.data.EntityDataElement.Category;

public class ImportServiceTest extends AbstractTestWithTestConfig {

	private GenericCategory rootCategory;
	private ImportService importService;

	private void invokeBuildEntityIDMap(EntityDataElement entityDataElement, Map<String, Integer> entityIDMap) {
		ReflectionUtil.executePrivate(importService, "buildEntityIDMap", new Class[] { EntityDataElement.class, Map.class }, new Object[] { entityDataElement, entityIDMap });
	}

	private void invokeBuildEntityIDMapForNonMerge(EntityDataElement entityDataElement, Map<String, Integer> entityIDMap) {
		ReflectionUtil.executePrivate(new ImportService(), "buildEntityIDMapForNonMerge", new Class[] { EntityDataElement.class, Map.class }, new Object[] { entityDataElement, entityIDMap });
	}

	private Map<String, Integer> invokeBuildEntityIDMapWithNewMap(EntityDataElement entityDataElement) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		invokeBuildEntityIDMap(entityDataElement, map);
		return map;
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		// Set up for ImportServiceTest
		rootCategory = createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(rootCategory.getType(), rootCategory.getId(), rootCategory.getName());
		importService = new ImportService();
	}

	public void tearDown() throws Exception {
		// Tear downs for ImportServiceTest
		EntityManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testBuildEntityIDMapForEntityOfCategoryTypeWithNoParentIDUsesRootCategoryFromCache() throws Exception {
		final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity = new com.mindbox.pe.xsd.data.EntityDataElement.Entity();
		entity.setType("category");
		entity.setId(2);
		final EntityDataElement entityDataElement = new EntityDataElement();
		entityDataElement.getEntity().add(entity);

		final String expectedKey = ObjectConverter.asCategoryIDMapKey("product", entity.getId());
		final Map<String, Integer> idMap = invokeBuildEntityIDMapWithNewMap(entityDataElement);
		assertEquals(1, idMap.size());
		assertEquals(rootCategory.getID(), idMap.get(expectedKey).intValue());
	}

	/**
	 * This test requires that test/config/PowerEditorConfiguration.xml defines an entity named 'nocategory' which has
	 * no category defined for it.
	 */
	@Test
	public void testBuildEntityIDMapForNonMergeWithCategoryWithNoCategoryDefIsNoOp() throws Exception {
		// sanity check
		GenericEntityType type = GenericEntityType.forName("nocategory");
		assertNotNull(type);
		assertFalse(type.hasCategory());

		Category category = new Category();
		category.setId(1);
		category.setType(type.getName());

		final EntityDataElement entityDataElement = new EntityDataElement();
		entityDataElement.getCategory().add(category);

		Map<String, Integer> map = new HashMap<String, Integer>();
		invokeBuildEntityIDMapForNonMerge(entityDataElement, map);
		assertTrue(map.isEmpty());
	}

	@Test
	public void testImportDataXML_NoDataThrowsNoException() throws Exception {
		final ImportSpec importSpec = new ImportSpec("src/test/data/Empty-PowerEditor-Data.xml", "<powereditor-data></powereditor-data>", true);
		importService.importDataXML(importSpec, null);
		assertFalse(importService.getImportResult().hasError());
	}

	@Test
	public void testImportDataXMLWithEntitiesOnlySetIgnoresNonEntityElements() throws Exception {
		final ImportSpec importSpec = new ImportSpec("somefile.xml", "<powereditor-data><next-id-data><next-id id=\"10\"/></next-id-data></powereditor-data>", false);
		importService.importDataXML(importSpec, true, null);
	}
}
