package com.mindbox.pe.model;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;

public class ProductGridTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ProductGridTest Tests");
		suite.addTestSuite(ProductGridTest.class);
		return suite;
	}

	private ProductGrid grid;

	public ProductGridTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		grid = new ProductGrid(100, new GridTemplate(1, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setStatusChangeDate(getDate(2006, 5, 31)); // set to a date other than now
	}

	protected void tearDown() throws Exception {
		grid = null;
		config.resetConfiguration();
		super.tearDown();
	}

	public void testIsParameterGridRetunsFalse() throws Exception {
		assertFalse(grid.isParameterGrid());
	}

	public void testCopyOfWithNullSourceThrowsNullPointerException() throws Exception {
		try {
			ProductGrid.copyOf(null, new GridTemplate(2, "test2", TemplateUsageType.getAllInstances()[0]), null, null);
			fail("Excepted NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testCopyOfWithValidSourceCreatesClonedCopy() throws Exception {
		DateSynonym dateSynonym1 = new DateSynonym(100, "date1", "", new Date());
		DateSynonym dateSynonym2 = new DateSynonym(100, "date1", "", new Date());
		ProductGrid grid2 = ProductGrid.copyOf(
				grid,
				new GridTemplate(2, "test2", TemplateUsageType.getAllInstances()[0]),
				dateSynonym1,
				dateSynonym2);
		assertEquals(Persistent.UNASSIGNED_ID, grid2.getID());
		assertTrue(grid2.hasSameContext(grid));
		assertTrue(grid2.hasSameCellValues(grid));
		assertEquals(grid.getID(), grid2.getCloneOf());
		assertEquals(2, grid2.getTemplateID());
		assertEquals(grid2.getEffectiveDate().getDate(), dateSynonym1.getDate());
		assertEquals(grid2.getEffectiveDate().getDate(), dateSynonym1.getDate());
		assertEquals(grid.getStatus(), grid2.getStatus());
		assertNotEquals(grid.getStatusChangeDate(), grid2.getStatusChangeDate());
		assertEquals(grid.getComments(), grid2.getComments());
		assertEquals(grid.getNumRows(), grid2.getNumRows());
	}

}
