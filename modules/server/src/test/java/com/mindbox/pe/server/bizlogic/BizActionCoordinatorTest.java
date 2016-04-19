package com.mindbox.pe.server.bizlogic;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.SimpleEntityData;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;

public class BizActionCoordinatorTest extends AbstractTestWithTestConfig {

	public void setUp() throws Exception {
		super.setUp();
		ParameterTemplateManager.getInstance().addParameterTemplate(new ParameterTemplate(1, "param template", 99, "desc"));
		config.initServer("src/test/config/PowerEditorConfiguration-NoProgram.xml");
	}

	public void tearDown() throws Exception {
		// Tear downs for BizActionCoordinatorTest
		ParameterTemplateManager.getInstance().removeFromCache(1);
		ParameterManager.getInstance().startLoading();
		GridManager.getInstance().startLoading();
		super.tearDown();
		config.resetConfiguration();
	}

	@Test
	public void testExtractIDsWithEmptyArrayReturnsEmptyArray() throws Exception {
		assertEquals(0, BizActionCoordinator.extractIDs(new Persistent[0]).length);
	}

	@Test
	public void testExtractIDsWithNullReturnsNull() throws Exception {
		assertNull(BizActionCoordinator.extractIDs(null));
	}

	@Test
	public void testExtractIDsWithValidArrayReturnsCorrectIDs() throws Exception {
		assertArrayEqualsIgnoresOrder(
				new int[] { 1, 2, 3 },
				BizActionCoordinator.extractIDs(new Persistent[] { new SimpleEntityData(1, "name1"), new SimpleEntityData(3, "name3"), new SimpleEntityData(2, "name2") }));
	}

	@Test
	public void testIsDateSynonymInUseNegativeCase() throws Exception {
		ProductGrid productGrid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		GridManager.getInstance().addProductGrid(productGrid);
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");

		DateSynonym ds = createDateSynonym();
		assertFalse(BizActionCoordinator.getInstance().isDateSynonymInUse(ds));
	}

	@Test
	public void testIsDateSynonymInUsePositiveCase() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		DateSynonym ds = createDateSynonym();
		grid.setEffectiveDate(ds);
		grid.setExpirationDate(null);
		assertTrue(BizActionCoordinator.getInstance().isDateSynonymInUse(ds));

		grid.setEffectiveDate(null);

		ProductGrid productGrid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		GridManager.getInstance().addProductGrid(productGrid);
		productGrid.setEffectiveDate(ds);
		assertTrue(BizActionCoordinator.getInstance().isDateSynonymInUse(ds));
	}

	@Test
	public void testIsDateSynonymInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(BizActionCoordinator.getInstance(), "isDateSynonymInUse", new Class[] { DateSynonym.class });
	}
}
