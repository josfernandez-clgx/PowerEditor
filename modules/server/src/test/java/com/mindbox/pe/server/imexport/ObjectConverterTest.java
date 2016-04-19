package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.server.ServerTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.server.ServerTestObjectMother.createParameterGrid;
import static com.mindbox.pe.server.ServerTestObjectMother.createUser;
import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.UserPassword;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;
import com.mindbox.pe.model.filter.NameFilterSpec;
import com.mindbox.pe.model.filter.PersistentFilterSpec;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.xsd.data.EntityDataElement;

/**
 * Tests for {@link com.mindbox.pe.server.imexport.ObjectConverter}.
 * 
 * @author Geneho Kim
 * 
 */
public class ObjectConverterTest extends AbstractTestWithTestConfig {

	private static class TestReplacementImpl implements ReplacementDateSynonymProvider {

		private DateSynonym replacement = createDateSynonym();

		public DateSynonym getReplacementDateSynonymForImport() throws ImportException {
			return replacement;
		}
	}

	private TestReplacementImpl testReplacementImpl;

	private boolean invokeHasSameData(List<Serializable> gridList1, List<Serializable> gridList2) throws Exception {
		Boolean result = (Boolean) ReflectionUtil.executeStaticPrivate(ObjectConverter.class, "hasSameData", new Class[] { List.class, List.class }, new Object[] { gridList1, gridList2 });
		return (result == null ? false : result.booleanValue());
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		GridTemplate template = new GridTemplate(1000, "template", TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template);
		ParameterTemplate paramTemplate = new ParameterTemplate(2000, "paramt", 9999, "desc");
		ParameterTemplateManager.getInstance().addParameterTemplate(paramTemplate);
		// This is to eliminate DB calls from ObjectConverter#getEffectiveDateSynonym and ObjectConverter#getExpirationDateSynonym
		DateSynonym dateSynonym = createDateSynonym();
		dateSynonym.setDate(getDate(2006, 1, 1, 0, 0, 0));
		DateSynonymManager.getInstance().insert(dateSynonym);
		dateSynonym = createDateSynonym();
		dateSynonym.setDate(getDate(2005, 10, 1, 0, 0, 0));
		DateSynonymManager.getInstance().insert(dateSynonym);
		this.testReplacementImpl = new TestReplacementImpl();
	}

	public void tearDown() throws Exception {
		GuidelineTemplateManager.getInstance().removeFromCache(1000);
		ParameterTemplateManager.getInstance().removeFromCache(2000);
		GridManager.getInstance().startLoading();
		DateSynonymManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testAddAndMergeGridContextWithEmptyGridListListAddToListForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(1, gridListList.size());
		assertTrue(gridListList.get(0) == gridList);
	}

	@Test
	public void testAddAndMergeGridContextWithEmptyGridListListAddToListForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(1, gridListList.size());
		assertTrue(gridListList.get(0) == gridList);
	}

	@Test
	public void testAddAndMergeGridContextWithNoSameDataAddToListForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);
		gridListList.add(gridList);

		gridList = new ArrayList<ProductGrid>();
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 200 });
		grid.setValue(1, 1, "value2");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(2, gridListList.size());
	}

	@Test
	public void testAddAndMergeGridContextWithNoSameDataAddToListForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		gridListList.add(gridList);

		gridList = new ArrayList<ParameterGrid>();
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value2");
		gridList.add(paramGrid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(2, gridListList.size());
	}

	@Test
	public void testAddAndMergeGridContextWithSameDataButNotMergeableContextsAddToListForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 20 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ProductGrid>();
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 200 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(2, gridListList.size());
	}

	@Test
	public void testAddAndMergeGridContextWithSameDataButNotMergeableContextsAddToListForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ParameterGrid>();
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 11 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		assertEquals(2, gridListList.size());
	}

	@Test
	public void testAddAndMergeGridContextWithSameDataIfFoundWithNullArgumentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(ObjectConverter.class, "addAndMergeGridContextWithSameDataIfFound", new Class[] { List.class, List.class }, new Object[] {
				null,
				new ArrayList<ParameterGrid>() });
		assertThrowsNullPointerException(ObjectConverter.class, "addAndMergeGridContextWithSameDataIfFound", new Class[] { List.class, List.class }, new Object[] {
				new ArrayList<ParameterGrid>(),
				null });
	}

	@Test
	public void testAddAndMergeGridContextWithSameDataUpdatesContextForGuidelineGrid() throws Exception {
		List<List<ProductGrid>> gridListList = new ArrayList<List<ProductGrid>>();

		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ProductGrid>();
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 200 });
		grid.setValue(1, 1, "value1");
		gridList.add(grid);

		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);
		assertEquals(1, gridListList.size());
		assertEquals(1, gridListList.get(0).size());
		grid = (ProductGrid) gridListList.get(0).get(0);
		assertArrayEqualsIgnoresOrder(new int[] { 100, 200 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
	}

	@Test
	public void testAddAndMergeGridContextWithSameDataUpdatesContextForParameterGrid() throws Exception {
		List<List<ParameterGrid>> gridListList = new ArrayList<List<ParameterGrid>>();

		List<ParameterGrid> gridList = new ArrayList<ParameterGrid>();
		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		gridList = new ArrayList<ParameterGrid>();
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 20 });
		paramGrid.setCellValues("value1");
		gridList.add(paramGrid);
		ObjectConverter.addAndMergeGridContextWithSameDataIfFound(gridListList, gridList);

		assertEquals(1, gridListList.size());
		assertEquals(1, gridListList.get(0).size());
		paramGrid = (ParameterGrid) gridListList.get(0).get(0);
		assertArrayEqualsIgnoresOrder(new int[] { 10, 20 }, paramGrid.getGenericEntityIDs(GenericEntityType.forName("channel")));
	}


	@Test
	public void testAsGenericCategoryForCategoryDigestWithNullCategoryDigestThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(null, false, new HashMap<String, Integer>(), null, testReplacementImpl);
			fail("Expected ImportException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}


	@Test
	public void testAsGenericCategoryForEntityWithNullEntityThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.asGenericCategory(GenericEntityType.forName("product"), null, false, new HashMap<String, Integer>(), testReplacementImpl);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test(expected = ImportException.class)
	public void testAsGenericCategoryForEntityWithNullGenericEntityTypeThrowsNullPointerException() throws Exception {
		ObjectConverter.asGenericCategory(null, new EntityDataElement.Entity(), false, new HashMap<String, Integer>(), testReplacementImpl);
	}

	@Test(expected = NullPointerException.class)
	public void testAsGenericCategoryForEntityWithNullIDMapThrowsNullPointerException() throws Exception {
		ObjectConverter.asGenericCategory(GenericEntityType.forName("product"), new EntityDataElement.Entity(), false, null, testReplacementImpl);
	}

	@Test
	public void testAsGuidelineGridListWithNullGridDigestThrowsNullPointerException() throws Exception {
		User user = createUser();
		try {
			ObjectConverter.asGuidelineGridList(null, null, false, user, null, null, testReplacementImpl);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testAsParameterGridListWithNullGridDigestThrowsNullPointerException() throws Exception {
		User user = createUser();
		assertThrowsNullPointerException(ObjectConverter.class, "asParameterGridList", new Class[] {
				com.mindbox.pe.xsd.data.GridDataElement.Grid.class,
				GuidelineContext[].class,
				User.class,
				Map.class,
				ReplacementDateSynonymProvider.class }, new Object[] { null, null, user, null, testReplacementImpl });
	}

	private void testContextsAreMergeable(GuidelineContext[] mergeToContext, GuidelineContext[] mergeFromContext, boolean expectedResult) throws Exception {
		Boolean result = (Boolean) ReflectionUtil.executeStaticPrivate(ObjectConverter.class, "contextsAreMergeable", new Class[] { GuidelineContext[].class, GuidelineContext[].class }, new Object[] {
				mergeToContext,
				mergeFromContext });
		assertNotNull(result);
		assertTrue(expectedResult == result.booleanValue());
	}

	@Test
	public void testContextsAreMergeableWithCategoryAndEntityOfDifferentTypeReturnsTrue() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c1.setIDs(new int[] { 1 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[1]);
		c2.setIDs(new int[] { 2 });
		testContextsAreMergeable(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, true);
	}

	@Test
	public void testContextsAreMergeableWithCategoryAndEntityOfSameTypeReturnsFalse() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c1.setIDs(new int[] { 1 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0]);
		c2.setIDs(new int[] { 2 });
		testContextsAreMergeable(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, false);
	}

	@Test
	public void testContextsAreMergeableWithDifferentCategoriesOfSameTypeReturnsTrue() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c1.setIDs(new int[] { 1 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.getAllGenericEntityTypes()[0].getCategoryType());
		c2.setIDs(new int[] { 2 });
		testContextsAreMergeable(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, true);
	}


	@Test
	public void testFetchContextWithNullGridDigestThrowsNullPointerException() throws Exception {
		try {
			ObjectConverter.fetchContext(null, false, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testHasSameDataHappyCaseForGuidelineGrids() throws Exception {
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setComments("comments1");
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		grid.setValue(1, 1, "value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(grid);
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments2");
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 200 });
		grid.setValue(1, 1, "value2");
		list1.add(grid);

		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments2");
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		grid.setValue(1, 1, "value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(grid);
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments1");
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 22 });
		grid.setValue(1, 1, "value1");
		list2.add(grid);
		assertTrue(invokeHasSameData(list1, list2));
		assertTrue(invokeHasSameData(list2, list1));
	}

	@Test
	public void testHasSameDataHappyCaseForParameterGrids() throws Exception {
		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 10 });
		paramGrid.setCellValues("value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(paramGrid);

		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 20 });
		paramGrid.setCellValues("value2");
		list1.add(paramGrid);

		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		paramGrid.setCellValues("value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(paramGrid);
		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setGenericEntityIDs(GenericEntityType.forName("investor"), new int[] { 22 });
		paramGrid.setCellValues("value1");
		list2.add(paramGrid);
		assertTrue(invokeHasSameData(list1, list2));
		assertTrue(invokeHasSameData(list2, list1));
	}

	@Test
	public void testHasSameDataWithDifferentGuidelineGridsReturnsFalse() throws Exception {
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setComments("comments");
		grid.setValue(1, 1, "value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(grid);
		grid = new ProductGrid(grid.getID() + 1, (GridTemplate) grid.getTemplate(), null, null);
		grid.setComments("comments");
		grid.setValue(1, 1, "value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(grid);
		assertFalse(invokeHasSameData(list1, list2));
		assertFalse(invokeHasSameData(list2, list1));
	}

	@Test
	public void testHasSameDataWithDifferentParameterGridsReturnsFalse() throws Exception {
		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setCellValues("value1");
		List<Serializable> list1 = new ArrayList<Serializable>();
		list1.add(paramGrid);

		paramGrid = new ParameterGrid(paramGrid.getID() + 1, paramGrid.getTemplateID(), null, null);
		paramGrid.setCellValues("value2");
		List<Serializable> list2 = new ArrayList<Serializable>();
		list2.add(paramGrid);
		assertFalse(invokeHasSameData(list1, list2));
		assertFalse(invokeHasSameData(list2, list1));
	}

	@Test
	public void testHasSameDataWithDifferentTypeGridsReturnsFalse() throws Exception {
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		attachGridTemplateColumn((GridTemplate) grid.getTemplate(), 1);
		grid.setValue(1, 1, "value");
		List<Serializable> guidelineList = new ArrayList<Serializable>();
		guidelineList.add(grid);

		ParameterGrid paramGrid = createParameterGrid();
		paramGrid.setCellValues("value");
		List<Serializable> paramList = new ArrayList<Serializable>();
		paramList.add(paramGrid);
		assertFalse(invokeHasSameData(guidelineList, paramList));
		assertFalse(invokeHasSameData(paramList, guidelineList));
	}

	@Test
	public void testHasSameDataWithEmptyListsReturnsTrue() throws Exception {
		assertTrue(invokeHasSameData(new ArrayList<Serializable>(), new ArrayList<Serializable>()));
	}

	@Test
	public void testHasSameDataWithNullArgumentThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(ObjectConverter.class, "hasSameData", new Class[] { List.class, List.class }, new Object[] { null, new ArrayList<ProductGrid>() });
		assertThrowsNullPointerException(ObjectConverter.class, "hasSameData", new Class[] { List.class, List.class }, new Object[] { new ArrayList<ProductGrid>(), null });
	}

	@Test
	public void testToFilterTypeStringWithEntityTypeFilterReturnsValidValue() throws Exception {
		NameFilterSpec<CBRCase> filter = new NameFilterSpec<CBRCase>(PeDataType.CBR_CASE, null, "name");
		assertEquals(PeDataType.CBR_CASE.toString(), ObjectConverter.toFilterTypeString(filter));
	}

	@Test
	public void testToFilterTypeStringWithGenericEntityFilterReturnsValidValue() throws Exception {
		GenericEntityFilterSpec filter = new GenericEntityFilterSpec(GenericEntityType.forName("product"), "name");
		assertEquals(GenericEntityType.forName("product").toString(), ObjectConverter.toFilterTypeString(filter));
	}

	@Test
	public void testToFilterTypeStringWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(ObjectConverter.class, "toFilterTypeString", new Class[] { PersistentFilterSpec.class });
	}

	@Test
	public void testValidateUserObjectThrowsImportExceptionIfUserIDNotProvided() throws Exception {
		User user = createUser();
		user.setUserID("");
		assertThrowsException(ObjectConverter.class, "validateUserObject", new Class[] { User.class }, new Object[] { user }, ImportException.class);
	}

	@Test
	public void testValidateUserObjectThrowsImportExceptionIfUserPasswordNotProvider() throws Exception {
		User user = createUser();
		user.setPasswordHistory(new ArrayList<UserPassword>());
		assertThrowsException(ObjectConverter.class, "validateUserObject", new Class[] { User.class }, new Object[] { user }, ImportException.class);
	}

	@Test
	public void testValidateUserObjectThrowsImportExceptionIfUserStatusNotProvider() throws Exception {
		User user = createUser();
		user.setStatus("");
		assertThrowsException(ObjectConverter.class, "validateUserObject", new Class[] { User.class }, new Object[] { user }, ImportException.class);
	}
}
