package com.mindbox.pe.server.imexport;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.junit.Test;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;

public class ExportServiceTest extends AbstractTestWithTestConfig {

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		DateSynonymManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
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
		final String results = sw.toString();
		final int index1 = results.indexOf("<enum-value>test value 1</enum-value>");
		final int index2 = results.indexOf("<enum-value>test value 2</enum-value>");
		assertTrue(index1 > 0);
		assertTrue(index2 > index1);
	}
}
