package com.mindbox.pe.server.cache;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.attachGridTemplateColumns;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class GuidelineTemplateManagerTest extends AbstractTestWithTestConfig {

	private GridTemplate template;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		template = createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template);
	}

	public void tearDown() throws Exception {
		GuidelineTemplateManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testGetTemplatesWithEntityListColumnHappyCase() throws Exception {
		attachGridTemplateColumns(template, 1);
		attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		GridTemplate template2 = createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template2);
		attachGridTemplateColumns(template2, 1);
		attachColumnDataSpecDigest((GridTemplateColumn) template2.getColumn(1));
		template2.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template2.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template2.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template2.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(false);

		GridTemplate template3 = createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template3);
		attachGridTemplateColumns(template3, 1);
		attachColumnDataSpecDigest((GridTemplateColumn) template3.getColumn(1));
		template3.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template3.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template3.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		template3.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);

		GridTemplate template4 = createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template4);
		attachGridTemplateColumns(template4, 1);
		attachColumnDataSpecDigest((GridTemplateColumn) template4.getColumn(1));
		template4.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template4.getColumn(1).getColumnDataSpecDigest().setEntityType("channel");
		template4.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template4.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(false);

		List<GridTemplate> list = GuidelineTemplateManager.getInstance()
				.getTemplatesWithEntityListColumn(GenericEntityType.forName("product"), false);
		assertEquals(1, list.size());
		assertEquals(template2.getID(), list.get(0).getID());

		list = GuidelineTemplateManager.getInstance().getTemplatesWithEntityListColumn(GenericEntityType.forName("product"), true);
		assertEquals(1, list.size());
		assertEquals(template3.getID(), list.get(0).getID());
	}

	@Test
	public void testGetTemplatesWithEntityListColumnWithNoAppropriateTemplateReturnsEmptyList() throws Exception {
		attachGridTemplateColumn(template, 1);
		attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		assertEquals(0, GuidelineTemplateManager.getInstance().getTemplatesWithEntityListColumn(GenericEntityType.forName("product"), true).size());
	}

	@Test
	public void testGetTemplatesWithEntityListColumnWithNullTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(GuidelineTemplateManager.getInstance(), "getTemplatesWithEntityListColumn", new Class[] {
				GenericEntityType.class, boolean.class }, new Object[] { null, Boolean.TRUE });
	}
}
