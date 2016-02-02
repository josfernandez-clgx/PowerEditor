package com.mindbox.pe.server.cache;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.TemplateUsageType;

public class GuidelineTemplateManagerTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineTemplateManagerTest Tests");
		suite.addTestSuite(GuidelineTemplateManagerTest.class);
		return suite;
	}

	private GridTemplate template;

	public GuidelineTemplateManagerTest(String name) {
		super(name);
	}

	public void testGetTemplatesWithEntityListColumnWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GuidelineTemplateManager.getInstance(), "getTemplatesWithEntityListColumn", new Class[]
			{ GenericEntityType.class, boolean.class}, new Object[]
			{ null, Boolean.TRUE});
	}

	public void testGetTemplatesWithEntityListColumnWithNoAppropriateTemplateReturnsEmptyList() throws Exception {
		ObjectMother.attachGridTemplateColumn(template, 1);
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		assertEquals(0, GuidelineTemplateManager.getInstance().getTemplatesWithEntityListColumn(GenericEntityType.forName("product"), true).size());
	}

	public void testGetTemplatesWithEntityListColumnHappyCase() throws Exception {
		ObjectMother.attachGridTemplateColumns(template, 1);
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		GridTemplate template2 = ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template2);
		ObjectMother.attachGridTemplateColumns(template2, 1);
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template2.getColumn(1));
		template2.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template2.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template2.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template2.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(false);

		GridTemplate template3 = ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template3);
		ObjectMother.attachGridTemplateColumns(template3, 1);
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template3.getColumn(1));
		template3.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template3.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template3.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		template3.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);

		GridTemplate template4 = ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template4);
		ObjectMother.attachGridTemplateColumns(template4, 1);
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template4.getColumn(1));
		template4.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template4.getColumn(1).getColumnDataSpecDigest().setEntityType("channel");
		template4.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template4.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(false);

		List<GridTemplate> list = GuidelineTemplateManager.getInstance().getTemplatesWithEntityListColumn(GenericEntityType.forName("product"), false);
		assertEquals(1, list.size());
		assertEquals(template2.getID(), list.get(0).getID());

		list = GuidelineTemplateManager.getInstance().getTemplatesWithEntityListColumn(GenericEntityType.forName("product"), true);
		assertEquals(1, list.size());
		assertEquals(template3.getID(), list.get(0).getID());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		template = ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template);
	}

	protected void tearDown() throws Exception {
		GuidelineTemplateManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}
}
