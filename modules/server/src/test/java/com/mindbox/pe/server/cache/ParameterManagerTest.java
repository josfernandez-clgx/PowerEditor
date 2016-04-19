package com.mindbox.pe.server.cache;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotMemberOf;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.config.ConfigurationManager;

public class ParameterManagerTest extends AbstractTestWithTestConfig {

	@SuppressWarnings("unchecked")
	private List<GuidelineReportData> invokeToGuidelineReportData(ParameterTemplate template, List<ParameterGrid> gridList) {
		return (List<GuidelineReportData>) ReflectionUtil.executeStaticPrivate(ParameterManager.class, "toGuidelineReportData", new Class[] { ParameterTemplate.class, List.class }, new Object[] {
				template,
				gridList });
	}

	public void setUp() throws Exception {
		super.setUp();
		super.config.initServer();
		ParameterTemplateManager.getInstance().addParameterTemplate(new ParameterTemplate(1, "param template", 99, "desc"));
	}

	public void tearDown() throws Exception {
		ParameterTemplateManager.getInstance().removeFromCache(1);
		ParameterManager.getInstance().startLoading();
		super.tearDown();
	}

	@Test
	public void testGetGridsWithInvalidEntityIDReturnsEmptyList() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		List<ParameterGrid> list = ParameterManager.getInstance().getGrids(GenericEntityType.forName("product"), 12343);
		assertEquals(0, list.size());
	}

	@Test
	public void testGetGridsWithValidEntityReturnsCorrectList() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterManager.getInstance().addParameterGrid(1001, 1, "", 0, null, null, "Draft");
		ParameterManager.getInstance().addParameterGrid(1002, 1, "", 0, null, null, "Draft");

		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		grid.setStatus("SomeStatus");
		grid.addGenericEntityID(GenericEntityType.forName("product"), 200);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 201);
		grid = ParameterManager.getInstance().getGrid(1001);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 201);
		grid.addGenericCategoryID(GenericEntityType.forName("product"), 200);
		grid = ParameterManager.getInstance().getGrid(1001);
		grid.addGenericEntityID(GenericEntityType.forName("channel"), 200);

		List<ParameterGrid> list = ParameterManager.getInstance().getGrids(GenericEntityType.forName("product"), 200);
		assertEquals(1, list.size());
		assertEquals(1000, list.get(0).getID());
	}

	@Test
	public void testIsInUseNegativeCase() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");

		DateSynonym ds = createDateSynonym();
		assertFalse(ParameterManager.getInstance().isInUse(ds));
	}

	@Test
	public void testIsInUsePositiveCase() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		DateSynonym ds = createDateSynonym();
		grid.setEffectiveDate(ds);
		grid.setExpirationDate(null);
		assertTrue(ParameterManager.getInstance().isInUse(ds));

		grid.setEffectiveDate(null);
		grid.setExpirationDate(ds);
		assertTrue(ParameterManager.getInstance().isInUse(ds));
	}

	@Test
	public void testIsInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(ParameterManager.getInstance(), "isInUse", new Class[] { DateSynonym.class });
	}

	@Test
	public void testRemoveCategoryFromAllContext() throws Exception {
		GenericEntityType type = ConfigurationManager.getInstance().getEntityConfigHelper().findEntityTypeForCategoryType(20);
		assertNotNull(type);

		int categoryToRemove = 100;
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterManager.getInstance().addGridContext(1000, 20, categoryToRemove);
		ParameterManager.getInstance().addParameterGrid(1001, 1, "", 0, null, null, "Draft");
		ParameterManager.getInstance().addGridContext(1001, 20, 101);

		ParameterManager.getInstance().removeCategoryFromAllContext(20, categoryToRemove);

		List<ParameterGrid> list = ParameterManager.getInstance().getGrids(1);
		for (Iterator<ParameterGrid> iter = list.iterator(); iter.hasNext();) {
			ParameterGrid grid = iter.next();
			assertNotMemberOf("Grid " + grid.getID() + " still has category", categoryToRemove, grid.getGenericCategoryIDs(type));
		}
	}

	@Test
	public void testToGuidelineReportDataWithEmptyGridListReturnsEmptyList() throws Exception {
		List<GuidelineReportData> list = invokeToGuidelineReportData(ParameterTemplateManager.getInstance().getTemplate(1), new ArrayList<ParameterGrid>());
		assertEquals(0, list.size());
	}

	@Test
	public void testToGuidelineReportDataWithNullGridListThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(ParameterManager.class, "toGuidelineReportData", new Class[] { ParameterTemplate.class, List.class }, new Object[] {
				ParameterTemplateManager.getInstance().getTemplate(1),
				null });
	}

	@Test
	public void testToGuidelineReportDataWithNullTemplateThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(ParameterManager.class, "toGuidelineReportData", new Class[] { ParameterTemplate.class, List.class }, new Object[] {
				null,
				new ArrayList<GuidelineReportData>() });
	}

	@Test
	public void testToGuidelineReportDataWithValidGridSetStatus() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		grid.setStatus("SomeStatus");
		List<ParameterGrid> list = new ArrayList<ParameterGrid>();
		list.add(grid);
		List<GuidelineReportData> grdList = invokeToGuidelineReportData(ParameterTemplateManager.getInstance().getTemplate(1), list);
		assertEquals(1, list.size());
		assertEquals("SomeStatus", grdList.get(0).getStatus());
	}

	@Test
	public void testUpdateGridContextTakesInvalidGridID() throws Exception {
		try {
			ParameterManager.getInstance().updateGridContext(123985, new GuidelineContext[0]);
		}
		catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
	}

	@Test
	public void testUpdateGridContextWithNullContextClearsContext() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		grid.setStatus("SomeStatus");
		grid.addGenericEntityID(GenericEntityType.forName("product"), 200);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 201);

		ParameterManager.getInstance().updateGridContext(1000, null);
		assertTrue(grid.isContextEmpty());
	}

	@Test
	public void testUpdateGridContextWithValidGridIDUpdateContext() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		grid.setStatus("SomeStatus");
		grid.addGenericEntityID(GenericEntityType.forName("product"), 200);
		grid.addGenericEntityID(GenericEntityType.forName("product"), 201);

		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("channel").getCategoryType());
		context.setIDs(new int[] { 11, 22 });
		ParameterManager.getInstance().updateGridContext(1000, new GuidelineContext[] { context });
		assertArrayEqualsIgnoresOrder(new int[] { 11, 22 }, grid.getGenericCategoryIDs(GenericEntityType.forName("channel")));
		assertFalse(grid.hasGenericEntityContext(GenericEntityType.forName("channel")));
	}

}
