package com.mindbox.pe.model.grid;

import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.grid.GridValueContainer;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;

public class GridValueContainerTest extends AbstractTestBase {

	private GridValueContainer<GridTemplateColumn> gridValueContainer;
	private GridTemplate template;

	@Before
	public void setUp() throws Exception {
		//		config.initServer();
		template = new GridTemplate(1, "test", createUsageType());
		template.addColumn(createGridTemplateColumn(1, template.getUsageType()));
		gridValueContainer = new GridValueContainer<GridTemplateColumn>(template);
	}

	@Test
	public void testHasSameRowNegativeCaseWithIncorrectRow() throws Exception {
		gridValueContainer.setNumRows(0);
		assertFalse(gridValueContainer.hasSameRow(1, null, 1, null));
	}

	@Test
	public void testHasSameRowPositiveCaseWithNullVsEmpytString() throws Exception {
		gridValueContainer.setNumRows(1);
		gridValueContainer.setValue(1, 1, null);
		GridValueContainer<GridTemplateColumn> valueContainable = new GridValueContainer<GridTemplateColumn>(template);
		valueContainable.setNumRows(1);
		valueContainable.setValue(1, 1, "");

		assertTrue(gridValueContainer.hasSameRow(1, new String[] { template.getColumn(1).getName() }, 1, valueContainable));
	}
}
