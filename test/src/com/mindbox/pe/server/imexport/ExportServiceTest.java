package com.mindbox.pe.server.imexport;

import java.io.StringWriter;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.imexport.digest.ActivationDates;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.ImportXMLDigester;
import com.mindbox.pe.server.imexport.digest.Parent;

public class ExportServiceTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ExportServiceTest Tests");
		suite.addTestSuite(ExportServiceTest.class);
		return suite;
	}

	private StringWriter stringWriter;

	public ExportServiceTest(String name) {
		super(name);
	}

	// TODO Kim 8/12/2006: add tests for template entity columns

	public void testExportTemplateColumnEnumValue() throws Exception {
		GridTemplateColumn column = new GridTemplateColumn(1, "col1", "Column 1", 100, TemplateUsageType.valueOf("Global-Qualify"));
		ColumnDataSpecDigest cdsd = new ColumnDataSpecDigest();
		cdsd.setIsBlankAllowed(true);
		cdsd.setIsMultiSelectAllowed(true);
		cdsd.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		cdsd.setEnumSourceType(EnumSourceType.COLUMN);
		cdsd.addColumnEnumValue("test value 1");
		cdsd.addColumnEnumValue("test value 2");
		column.setDataSpecDigest(cdsd);
		GridTemplate template = new GridTemplate(1, "test1", TemplateUsageType.valueOf("Global-Qualify"));
		template.addGridTemplateColumn(column);
		template.setComment("comment");
		GuidelineTemplateManager.getInstance().addTemplate(template);

		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeTemplates(true);
		filter.addGuidelineTemplateID(template.getId());
		StringWriter sw = new StringWriter();
		ExportService.getInstance().export(sw, filter, config.getUserID());
		String results = sw.toString();
		String expectedResults = "<enum-value>test value 1</enum-value>" + System.getProperty("line.separator")
				+ "            <enum-value>test value 2</enum-value>";
		assertTrue(results.indexOf(expectedResults) != -1);
	}

	public void testExportDateSynonyms() throws Exception {
		DateSynonym ds = new DateSynonym(1, "Test 1 Name", "Test 1 Desc", getDate(2006, 10, 10));
		DateSynonymManager.getInstance().insert(ds);
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeDateSynonyms(true);
		StringWriter sw = new StringWriter();
		ExportService.getInstance().export(sw, filter, config.getUserID());
		String results = sw.toString();
		String expectedResults = "<date-data>" + System.getProperty("line.separator")
				+ "    <DateElement id=\"1\" name=\"Test 1 Name\" date=\"2006-10-10T00:00:00\" description=\"Test 1 Desc\"/>"
				+ System.getProperty("line.separator") + "  </date-data>";

		assertTrue(results.indexOf(expectedResults) != -1);
	}

	public void testCategoryHasMultipleParentAssociations() throws Exception {
		// mock out the DB id generation stuff, tantential to the purpose of the test
		getMockDbIdGenerator().nextFilterID();
		getMockDbIdGeneratorControl().setReturnValue(1);

		getMockDbIdGenerator().nextGridID();
		getMockDbIdGeneratorControl().setReturnValue(2);

		getMockDbIdGenerator().nextSequentialID();
		getMockDbIdGeneratorControl().setReturnValue(3);

		getMockDbIdGeneratorControl().expectAndReturn(getMockDbIdGenerator().nextAuditID(), 4);
		getMockDbIdGeneratorControl().expectAndReturn(getMockDbIdGenerator().nextRuleID(), 5);

		replay();

		// set up 3 categories, 2 as parents of the same child at different times
		GenericEntityType productEntityType = GenericEntityType.forName("product");
		int productCategoryType = productEntityType.getCategoryType();

		GenericCategory parent1 = ObjectMother.createGenericCategory(productEntityType);
		EntityManager.getInstance().addGenericEntityCategory(productCategoryType, parent1.getID(), parent1.getName());

		GenericCategory parent2 = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(productCategoryType, parent2.getID(), parent2.getName());

		GenericCategory child = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(productCategoryType, child.getID(), child.getName());

		DateSynonym ds1 = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(ds1);
		DateSynonym ds2 = ObjectMother.createDateSynonym();
		DateSynonymManager.getInstance().insert(ds2);

		EntityManager.getInstance().addParentAssociation(productCategoryType, child.getID(), parent1.getID(), ds1.getID(), ds2.getID());
		EntityManager.getInstance().addParentAssociation(productCategoryType, child.getID(), parent2.getID(), ds2.getID(), -1);

		// here's the real test, export just entity data...
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeEntities(true);
		ExportService.getInstance().export(stringWriter, filter, config.getUserID());

		verify();

		// ...and verify results
		DigestedObjectHolder result = ImportXMLDigester.getInstance().digestImportXML(stringWriter.toString());

		List<CategoryDigest> categories = result.getObjects(CategoryDigest.class);
		assertEquals(3, categories.size());

		CategoryDigest digestedCategory1 = categories.get(0);

		// assertDigestedCategory(productEntityType, parent1, digestedCategory1, 0); commented out temporarily during 5.1. alpha.  see note in ExportService.writeGenericCategoryTag
		assertDigestedCategory(productEntityType, parent1, digestedCategory1, 1);

		CategoryDigest digestedCategory2 = categories.get(1);
		// assertDigestedCategory(productEntityType, parent2, digestedCategory2, 0); commented out temporarily during 5.1. alpha.  see note in ExportService.writeGenericCategoryTag
		assertDigestedCategory(productEntityType, parent2, digestedCategory2, 1);

		CategoryDigest digestedCategory3 = categories.get(2);
		assertDigestedCategory(productEntityType, child, digestedCategory3, 2);
		assertParent((Parent) digestedCategory3.getParents().get(0), parent1, parent2, ds1, ds2);
		assertParent((Parent) digestedCategory3.getParents().get(1), parent1, parent2, ds1, ds2);
	}

	private void assertDigestedCategory(GenericEntityType entityType, GenericCategory exportedCategory, CategoryDigest digestedCategory,
			int expectedParentCount) {
		assertEquals(exportedCategory.getID(), digestedCategory.getId());
		assertEquals(entityType.getName(), digestedCategory.getType());
		assertEquals(expectedParentCount, digestedCategory.getParents().size());
	}

	private void assertParent(Parent digestedParent, GenericCategory parent1, GenericCategory parent2, DateSynonym ds1, DateSynonym ds2) {
		if (digestedParent.getId() == parent1.getID()) {
			assertEquals(ds1.getID(), digestedParent.getActivationDates().getEffectiveDateID());
			assertEquals(ds2.getID(), digestedParent.getActivationDates().getExpirationDateID());
		}
		else if (digestedParent.getId() == parent2.getID()) {
			assertEquals(ds2.getID(), digestedParent.getActivationDates().getEffectiveDateID());
			assertEquals(ActivationDates.UNSPECIFIED_ID, digestedParent.getActivationDates().getExpirationDateID());
		}
		else {
			throw new IllegalStateException("unexpected digested parent id: " + digestedParent.getId());
		}
	}

	//	private void invokeWriteFiltersOnExportWriter(List<PersistentFilterSpec> filterList) throws Exception {
	//		ReflectionUtil.executePrivate(getExportWriter(), "writeFilters", new Class[] { List.class }, new Object[] { filterList });
	//	}
	//
	//	private void invokeWriteOpenTagOnExportWriter(String tag) throws Exception {
	//		ReflectionUtil.executePrivate(getExportWriter(), "writeOpenTag", new Class[] { String.class }, new Object[] { tag });
	//	}
	//
	//	private void invokeWriteCloseTagOnExportWriter() throws Exception {
	//		ReflectionUtil.executePrivate(getExportWriter(), "writeCloseTag", new Class[0], new Object[0]);
	//	}

	//	@SuppressWarnings("unchecked")
	//	private Object getExportWriter() throws Exception {
	//		if (exportWriterInstance == null) {
	//			Class c = Class.forName(ExportService.class.getName() + "$ExportWriter");
	//			exportWriterInstance = c.getDeclaredConstructor(new Class[] { ExportService.class, Writer.class, ExportDataProvider.class }).newInstance(
	//					new Object[] { ExportService.getInstance(), stringWriter, exportDataProvider });
	//		}
	//		return exportWriterInstance;
	//	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		stringWriter = new StringWriter();
	}

	protected void tearDown() throws Exception {
		DateSynonymManager.getInstance().startLoading();
		stringWriter = null;
		config.resetConfiguration();
		super.tearDown();
	}
}
