package com.mindbox.pe.model.grid;

import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;

public class ProductGridTest extends AbstractTestBase {

	private ProductGrid grid;
	private TemplateUsageType usageType;

	@Before
	public void setUp() throws Exception {
		usageType = createUsageType();
		grid = new ProductGrid(100, new GridTemplate(1, "test", usageType), null, null);
		grid.setStatusChangeDate(getDate(2006, 5, 31)); // set to a date other than now
	}

	@Test
	public void testIsParameterGridRetunsFalse() throws Exception {
		assertFalse(grid.isParameterGrid());
	}

	@Test(expected = NullPointerException.class)
	public void testCopyOfWithNullSourceThrowsNullPointerException() throws Exception {
		ProductGrid.copyOf(null, new GridTemplate(2, "test2", usageType), null, null);
	}

	@Test
	public void testCopyOfWithValidSourceCreatesClonedCopy() throws Exception {
		DateSynonym dateSynonym1 = new DateSynonym(100, "date1", "", new Date());
		DateSynonym dateSynonym2 = new DateSynonym(100, "date1", "", new Date());
		ProductGrid grid2 = ProductGrid.copyOf(grid, new GridTemplate(2, "test2", usageType), dateSynonym1, dateSynonym2);
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
