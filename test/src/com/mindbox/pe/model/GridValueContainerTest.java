package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;

public class GridValueContainerTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GridValueContainerTest Tests");
		suite.addTestSuite(GridValueContainerTest.class);
		return suite;
	}

	private GridValueContainer<GridTemplateColumn> gridValueContainer;
	private GridTemplate template;

	public GridValueContainerTest(String name) {
		super(name);
	}

	public void testHasSameRowNegativeCaseWithIncorrectRow() throws Exception {
		gridValueContainer.setNumRows(0);
		assertFalse(gridValueContainer.hasSameRow(1, null, 1, null));
	}

	public void testHasSameRowPositiveCaseWithNullVsEmpytString() throws Exception {
		gridValueContainer.setNumRows(1);
		gridValueContainer.setValue(1, 1, null);
		GridValueContainer<GridTemplateColumn> valueContainable = new GridValueContainer<GridTemplateColumn>(template);
		valueContainable.setNumRows(1);
		valueContainable.setValue(1, 1, "");

		assertTrue(gridValueContainer.hasSameRow(1, new String[] { template.getColumn(1).getName() }, 1, valueContainable));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		template = new GridTemplate(1, "test", TemplateUsageType.getAllInstances()[0]);
		template.addColumn(ObjectMother.createGridTemplateColumn(1, template.getUsageType()));
		gridValueContainer = new GridValueContainer<GridTemplateColumn>(template);
	}
}
