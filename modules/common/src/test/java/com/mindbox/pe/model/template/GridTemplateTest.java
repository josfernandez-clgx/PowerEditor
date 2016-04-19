/*
 * Created on Feb 28, 2006
 *
 */
package com.mindbox.pe.model.template;

import static com.mindbox.pe.common.CommonTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * @since PowerEditor 5.0
 */
public class GridTemplateTest extends AbstractTestBase {

	private GridTemplate template;
	private GridTemplateColumn entityColumn;
	private GridTemplateColumn stringColumn;

	@Before
	public void setUp() throws Exception {
		template = createGridTemplate(createUsageType());
		stringColumn = createGridTemplateColumn(1, createUsageType());
		stringColumn.setDataSpecDigest(createColumnDataSpecDigest());

		entityColumn = createGridTemplateColumn(2, createUsageType());
		entityColumn.setDataSpecDigest(createEntityColumnDataSpecDigest("product", false, false, false));

		template.addGridTemplateColumn(stringColumn);
		template.addGridTemplateColumn(entityColumn);
	}

	public void tearDown() throws Exception {
		template = null;
	}

	@Test
	public void testGetEntityTypeColumns() throws Exception {
		List<GridTemplateColumn> entityCols = template.getEntityTypeColumns();
		assertNotNull(entityCols);
		assertTrue(entityCols.size() == 1);
		assertTrue(entityCols.get(0).equals(entityColumn));
	}
}
