package com.mindbox.pe.server.generator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;

public class GuidelineParamsProducerTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineParamsProducerTest Tests");
		suite.addTestSuite(GuidelineParamsProducerTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	protected static void assertContainsParamWith(List<GuidelineGenerateParams> paramList, int rowNo, DateSynonym effDate,
			DateSynonym expDate, Object cellValue, boolean spansMultiple) {
		int count = 0;
		for (GuidelineGenerateParams params : paramList) {
			if (params.getRowNum() == rowNo && UtilBase.isSame(effDate, params.getSunrise())
					&& UtilBase.isSame(expDate, params.getSunset()) && params.getRowData().contains(cellValue)
					&& (spansMultiple == params.spansMultipleActivations())) {
				++count;
			}
		}
		assertTrue("No parameter with row " + rowNo + " and " + effDate + ", " + expDate + " with " + cellValue + " found", count > 0);
		assertEquals("More than one param found", 1, count);
	}

	@Deprecated
	protected static void assertContainsParamWith(List<GuidelineGenerateParams> paramList, int rowNo, DateSynonym effDate,
			DateSynonym expDate) {
		for (GuidelineGenerateParams params : paramList) {
			if (params.getRowNum() == rowNo && UtilBase.isSame(effDate, params.getSunrise())
					&& UtilBase.isSame(expDate, params.getSunset())) {
				return;
			}
		}
		fail("No parameter with row " + rowNo + " and " + effDate + ", " + expDate + " found");
	}


	public GuidelineParamsProducerTest(String name) {
		super(name);
	}

	public void testGroupByContextHappyCaseWithSingleContextNoGap() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setExpirationDate(ds);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(ds);
		gridList.add(grid2);

		List<List<ProductGrid>> groupList = invokeGroupByContext(gridList);
		assertEquals(1, groupList.size());
		assertEquals(2, groupList.get(0).size());
	}

	public void testGroupByContextHappyCaseWithMultipleContextNoGap() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setExpirationDate(ds);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(ds);
		grid2.setExpirationDate(ds2);
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.addGenericCategoryID(GenericEntityType.getAllGenericEntityTypes()[0], ObjectMother.createInt());
		grid3.setNumRows(maxRows);
		grid3.setEffectiveDate(ds);
		gridList.add(grid3);

		List<List<ProductGrid>> groupList = invokeGroupByContext(gridList);
		assertEquals(2, groupList.size());
		assertEquals(2, groupList.get(0).size());
		assertEquals(1, groupList.get(1).size());
		assertEquals(grid3, groupList.get(1).get(0));
	}

	public void testGroupByContextHappyCaseWithMultipleContextWithGap() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setExpirationDate(ds);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.addGenericCategoryID(GenericEntityType.getAllGenericEntityTypes()[0], ObjectMother.createInt());
		grid3.setNumRows(maxRows);
		grid3.setEffectiveDate(ds2);
		gridList.add(grid3);

		List<List<ProductGrid>> groupList = invokeGroupByContext(gridList);
		assertEquals(3, groupList.size());
		assertEquals(1, groupList.get(0).size());
		assertEquals(1, groupList.get(1).size());
		assertEquals(1, groupList.get(2).size());
	}

	public void testGroupByContextHappyCaseWithSingleContextGapSimpleCase() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setExpirationDate(ds);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		List<List<ProductGrid>> groupList = invokeGroupByContext(gridList);
		assertEquals(2, groupList.size());
		assertEquals(1, groupList.get(0).size());
		assertEquals(1, groupList.get(1).size());
	}

	public void testGroupByContextHappyCaseWithSingleContextGap() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		DateSynonym dsCopy = ObjectMother.createDateSynonym();
		dsCopy.setDate(ds.getDate());
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setExpirationDate(ds);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(dsCopy);
		grid2.setExpirationDate(ds2);
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.setNumRows(maxRows);
		grid3.setEffectiveDate(ObjectMother.createDateSynonym());
		gridList.add(grid3);

		List<List<ProductGrid>> groupList = invokeGroupByContext(gridList);
		assertEquals(2, groupList.size());
		assertEquals(2, groupList.get(0).size());
		assertEquals(1, groupList.get(1).size());
		assertEquals(grid3, groupList.get(1).get(0));
	}

	public void testGroupByContextHappyCaseWithSingleContextMultipleGap() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setExpirationDate(ds);
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(ds2);
		grid2.setExpirationDate(ObjectMother.createDateSynonym());
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.setNumRows(maxRows);
		grid3.setEffectiveDate(ObjectMother.createDateSynonym());
		gridList.add(grid3);

		List<List<ProductGrid>> groupList = invokeGroupByContext(gridList);
		assertEquals(3, groupList.size());
		assertEquals(1, groupList.get(0).size());
		assertEquals(1, groupList.get(1).size());
		assertEquals(1, groupList.get(2).size());
	}

	@SuppressWarnings("unchecked")
	private List<List<ProductGrid>> invokeGroupByContext(List<ProductGrid> grids) throws Exception {
		return (List<List<ProductGrid>>) ReflectionUtil.executeStaticPrivate(
				GuidelineParamsProducer.class,
				"groupByContext",
				new Class[] { List.class },
				new Object[] { grids });
	}

	public void testGenerateProductGenParmsHappyCaseWithSingleGrid() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setEffectiveDate(ds);
		gridList.add(grid1);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();

		assertEquals(5, paramList.size());
		for (GuidelineGenerateParams params : paramList) {
			assertEquals(ds, params.getSunrise());
			assertNull(params.getSunset());
			assertFalse(params.spansMultipleActivations());
		}
	}

	public void testGenerateProductGenParmsHappyCaseWithAllSameRows() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid1.setNumRows(maxRows);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 2, "value22");
		grid1.setValue(3, 2, "value32");
		grid1.setValue(4, 1, "value41");
		grid1.setValue(5, 2, "value52");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setEffectiveDate(ds2);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 2, "value22");
		grid2.setValue(3, 2, "value32");
		grid2.setValue(4, 1, "value41");
		grid2.setValue(5, 2, "value52");
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(maxRows, paramList.size());

		for (GuidelineGenerateParams params : paramList) {
			assertEquals(ds1, params.getSunrise());
			assertNull(params.getSunset());
			assertTrue(params.spansMultipleActivations());
		}
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRows() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		int maxRows = 5;
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(maxRows);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 3, "value13");
		grid1.setValue(2, 2, "value20");
		grid1.setValue(3, 2, "value32");
		grid1.setValue(4, 1, "value41");
		grid1.setValue(5, 1, "value51");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(maxRows);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 2, "value22");
		grid2.setValue(3, 2, "value32");
		grid2.setValue(4, 1, "value41");
		grid2.setValue(5, 2, "value52");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(8, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, ds2, "value13", false);
		assertContainsParamWith(paramList, 2, ds1, ds2, "value20", false);
		assertContainsParamWith(paramList, 3, ds1, null, "value32", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value41", true);
		assertContainsParamWith(paramList, 5, ds1, ds2, "value51", false);
		assertContainsParamWith(paramList, 1, ds2, null, "value11", false);
		assertContainsParamWith(paramList, 2, ds2, null, "value22", false);
		assertContainsParamWith(paramList, 5, ds2, null, "value52", false);
	}

	public void testGenerateProductGenParmsHappyCaseForComplexCase1() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		DateSynonym ds3 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(4);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value4");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(5);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value22");
		grid2.setValue(3, 1, "newvalue2");
		grid2.setValue(4, 1, "value3");
		grid2.setValue(5, 1, "value4");
		grid2.setEffectiveDate(ds2);
		grid2.setExpirationDate(ds3);
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.setNumRows(7);
		grid3.setValue(1, 1, "value11");
		grid3.setValue(2, 1, "value22");
		grid3.setValue(3, 1, "newvalue2");
		grid3.setValue(4, 1, "newvalue31");
		grid3.setValue(5, 1, "newvalue32");
		grid3.setValue(6, 1, "value3");
		grid3.setValue(7, 1, "value4");
		grid3.setEffectiveDate(ds3);
		gridList.add(grid3);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();

		assertEquals(7, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 3, ds2, null, "newvalue2", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value4", true);
		assertContainsParamWith(paramList, 4, ds3, null, "newvalue31", false);
		assertContainsParamWith(paramList, 5, ds3, null, "newvalue32", false);
	}

	public void testGenerateProductGenParmsHappyCaseForComplexCase2() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(7);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value4");
		grid1.setValue(5, 1, "value5");
		grid1.setValue(6, 1, "value6");
		grid1.setValue(7, 1, "value7");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(9);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value22");
		grid2.setValue(3, 1, "newvalue33");
		grid2.setValue(4, 1, "newvalue44");
		grid2.setValue(5, 1, "value3");
		grid2.setValue(6, 1, "value4");
		grid2.setValue(7, 1, "value5-mod");
		grid2.setValue(8, 1, "value6-mod");
		grid2.setValue(9, 1, "value7");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();

		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value4", true);
		assertContainsParamWith(paramList, 5, ds1, ds2, "value5", false);
		assertContainsParamWith(paramList, 6, ds1, ds2, "value6", false);
		assertContainsParamWith(paramList, 7, ds1, null, "value7", true);
		assertContainsParamWith(paramList, 3, ds2, null, "newvalue33", false);
		assertContainsParamWith(paramList, 4, ds2, null, "newvalue44", false);
		assertContainsParamWith(paramList, 7, ds2, null, "value5-mod", false);
		assertContainsParamWith(paramList, 8, ds2, null, "value6-mod", false);
		assertEquals(11, paramList.size());
	}

	public void testGenerateProductGenParmsHappyCaseForComplexCase3() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		DateSynonym ds3 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(7);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value4");
		grid1.setValue(5, 1, "value5");
		grid1.setValue(6, 1, "value6");
		grid1.setValue(7, 1, "value7");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(9);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value22");
		grid2.setValue(3, 1, "newvalue33");
		grid2.setValue(4, 1, "newvalue44");
		grid2.setValue(5, 1, "value3");
		grid2.setValue(6, 1, "value4");
		grid2.setValue(7, 1, "value5-mod");
		grid2.setValue(8, 1, "value6-mod");
		grid2.setValue(9, 1, "value7");
		grid2.setEffectiveDate(ds2);
		grid2.setExpirationDate(ds3);
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.setNumRows(9);
		grid3.setValue(1, 1, "value11");
		grid3.setValue(2, 1, "value22");
		grid3.setValue(3, 1, "newvalue33");
		grid3.setValue(4, 1, "newvalue44");
		grid3.setValue(5, 1, "value3");
		grid3.setValue(6, 1, "value4");
		grid3.setValue(7, 1, "value5-mod");
		grid3.setValue(8, 1, "value6-mod");
		grid3.setValue(9, 1, "value7-mod");
		grid3.setEffectiveDate(ds3);
		gridList.add(grid3);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();

		assertEquals(12, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value4", true);
		assertContainsParamWith(paramList, 5, ds1, ds2, "value5", false);
		assertContainsParamWith(paramList, 6, ds1, ds2, "value6", false);
		assertContainsParamWith(paramList, 7, ds1, ds3, "value7", true);
		assertContainsParamWith(paramList, 3, ds2, null, "newvalue33", true);
		assertContainsParamWith(paramList, 4, ds2, null, "newvalue44", true);
		assertContainsParamWith(paramList, 7, ds2, null, "value5-mod", true);
		assertContainsParamWith(paramList, 8, ds2, null, "value6-mod", true);
		assertContainsParamWith(paramList, 9, ds3, null, "value7-mod", false);
	}

	public void testGenerateProductGenParmsHappyCaseWithSeveralGrids() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		DateSynonym ds3 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(3);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 2, "value22");
		grid1.setValue(3, 2, "value32");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(3);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 2, "value22");
		grid2.setValue(3, 2, "value33");
		grid2.setEffectiveDate(ds2);
		grid2.setExpirationDate(ds3);
		gridList.add(grid2);
		ProductGrid grid3 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid3.setNumRows(3);
		grid3.setValue(1, 1, "value11");
		grid3.setValue(2, 2, "value24");
		grid3.setValue(3, 2, "value33");
		grid3.setEffectiveDate(ds3);
		gridList.add(grid3);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(5, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, ds3, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, ds2, "value32", false);
		assertContainsParamWith(paramList, 3, ds2, null, "value33", true);
		assertContainsParamWith(paramList, 2, ds3, null, "value24", false);
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRowsAndInsertedRowsMiddle() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(3);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(4);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "newvalue");
		grid2.setValue(3, 1, "value22");
		grid2.setValue(4, 1, "value3-mod");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 2, ds2, null, "newvalue", false);
		assertContainsParamWith(paramList, 3, ds1, ds2, "value3", false);
		assertContainsParamWith(paramList, 4, ds2, null, "value3-mod", false);
		assertEquals(5, paramList.size());
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRowsAndInsertedRowsFront() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(5);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value41");
		grid1.setValue(5, 1, "value52");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(7);
		grid2.setValue(1, 1, "newvalue1");
		grid2.setValue(2, 1, "newvalue2");
		grid2.setValue(3, 1, "value11");
		grid2.setValue(4, 1, "value22");
		grid2.setValue(5, 1, "value3");
		grid2.setValue(6, 1, "value41");
		grid2.setValue(7, 1, "value52");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(7, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value41", true);
		assertContainsParamWith(paramList, 5, ds1, null, "value52", true);
		assertContainsParamWith(paramList, 1, ds2, null, "newvalue1", false);
		assertContainsParamWith(paramList, 2, ds2, null, "newvalue2", false);
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRowsAndInsertedRowsEnd() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(5);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "valueX");
		grid1.setValue(4, 1, "value41");
		grid1.setValue(5, 1, "value52");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(6);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value22");
		grid2.setValue(3, 1, "valueX");
		grid2.setValue(4, 1, "value41");
		grid2.setValue(5, 1, "value52");
		grid2.setValue(6, 1, "newvalue");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(6, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, null, "valueX", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value41", true);
		assertContainsParamWith(paramList, 5, ds1, null, "value52", true);
		assertContainsParamWith(paramList, 6, ds2, null, "newvalue", false);
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRowsAndRemovedRowsMiddle() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(4);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value4");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(3);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value3");
		grid2.setValue(3, 1, "value4");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(4, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, ds2, "value22", false);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value4", true);
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRowsAndRemovedRowsFront() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(4);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value4");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(2);
		grid2.setValue(1, 1, "value3");
		grid2.setValue(2, 1, "value4");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(4, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, ds2, "value11", false);
		assertContainsParamWith(paramList, 2, ds1, ds2, "value22", false);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 4, ds1, null, "value4", true);
	}

	public void testGenerateProductGenParmsHappyCaseWithDiffRowsAndRemovedRowsEnd() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(4);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "value3");
		grid1.setValue(4, 1, "value4");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(3);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value22");
		grid2.setValue(3, 1, "value3");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(4, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, null, "value22", true);
		assertContainsParamWith(paramList, 3, ds1, null, "value3", true);
		assertContainsParamWith(paramList, 4, ds1, ds2, "value4", false);
	}

	public void testGenerateProductGenParmsHappyCaseWithAddedRows() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(2);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value00");
		grid1.setValue(2, 1, "value21");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(3);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value21");
		grid2.setValue(3, 2, "value32");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(4, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, ds2, "value00", false);
		assertContainsParamWith(paramList, 2, ds1, null, "value21", true);
		assertContainsParamWith(paramList, 1, ds2, null, "value11", false);
		assertContainsParamWith(paramList, 3, ds2, null, "value32", false);
	}

	public void testGenerateProductGenParmsHappyCaseWithRemovedRows() throws Exception {
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(ObjectMother.attachGridTemplateColumns(
				ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]),
				4));
		grid1.setNumRows(3);
		grid1.setEffectiveDate(ds1);
		grid1.setExpirationDate(ds2);
		grid1.setValue(1, 1, "value11");
		grid1.setValue(2, 1, "value22");
		grid1.setValue(3, 1, "valueX");
		gridList.add(grid1);
		ProductGrid grid2 = ObjectMother.createGuidelineGrid((GridTemplate) grid1.getTemplate());
		grid2.setNumRows(2);
		grid2.setValue(1, 1, "value11");
		grid2.setValue(2, 1, "value2-X");
		grid2.setEffectiveDate(ds2);
		gridList.add(grid2);

		MockControl mockControl = MockControl.createControl(GeneratorErrorContainer.class);
		GeneratorErrorContainer generatorErrorContainer = (GeneratorErrorContainer) mockControl.getMock();
		mockControl.replay();

		List<GuidelineGenerateParams> paramList = new GuidelineParamsProducer(generatorErrorContainer).generateProductGenParms(
				"DRAFT",
				(GridTemplate) grid1.getTemplate(),
				gridList,
				null);
		mockControl.verify();
		assertEquals(4, paramList.size());
		assertContainsParamWith(paramList, 1, ds1, null, "value11", true);
		assertContainsParamWith(paramList, 2, ds1, ds2, "value22", false);
		assertContainsParamWith(paramList, 3, ds1, ds2, "valueX", false);
		assertContainsParamWith(paramList, 2, ds2, null, "value2-X", false);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
}
