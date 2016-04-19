package com.mindbox.pe.model.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.unittest.AbstractTestBase;

public class ActivationsComparatorTest extends AbstractTestBase {

	private ActivationsComparator comparator;

	@Before
	public void setUp() throws Exception {
		this.comparator = ActivationsComparator.getInstance();
	}

	/**
	 * @throws Exception later/more recent is more
	 */
	@Test
	public void testCompareLaterIsMore() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "1", "desc1", new Date(System.currentTimeMillis() - 10000));
		DateSynonym ds2 = new DateSynonym(2, "2", "desc2", new Date(System.currentTimeMillis()));
		DateSynonym ds3 = new DateSynonym(3, "3", "desc2", new Date(System.currentTimeMillis() + 10000));
		ParameterGrid grid1 = new ParameterGrid(1, 1, ds1, ds3);
		ParameterGrid grid2 = new ParameterGrid(2, 1, ds1, ds2);
		assertTrue(0 > comparator.compare(grid1, grid2));
		assertTrue(0 < comparator.compare(grid2, grid1));

		grid1.setEffectiveDate(ds2);
		assertTrue(0 > comparator.compare(grid1, grid2));
		assertTrue(0 < comparator.compare(grid2, grid1));

		grid1 = new ParameterGrid(1, 1, ds1, ds3);
		grid2 = new ParameterGrid(2, 1, ds2, ds3);
		assertTrue(0 < comparator.compare(grid1, grid2));
		assertTrue(0 > comparator.compare(grid2, grid1));
	}

	@Test
	public void testCompareNoDatesComparedToSomeDate() throws Exception {
		DateSynonym ds = new DateSynonym(1, "1", "desc", new Date());
		ParameterGrid grid1 = new ParameterGrid(1, 1, null, null);
		ParameterGrid grid2 = new ParameterGrid(2, 1, ds, null);

		assertTrue(0 < comparator.compare(grid1, grid2));
		assertTrue(0 > comparator.compare(grid2, grid1));

		grid2 = new ParameterGrid(2, 1, null, ds);
		assertTrue(0 > comparator.compare(grid1, grid2));
		assertTrue(0 < comparator.compare(grid2, grid1));

		grid2 = new ParameterGrid(2, 1, ds, new DateSynonym(1, "1", "desc", new Date()));
		assertTrue(0 < comparator.compare(grid1, grid2));
		assertTrue(0 > comparator.compare(grid2, grid1));
	}

	@Test
	public void testCompareOpenDateIsLessThanSomeDate() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "1", "desc1", new Date(System.currentTimeMillis() - 10000));
		DateSynonym ds2 = new DateSynonym(2, "2", "desc2", new Date(System.currentTimeMillis()));
		DateSynonym ds3 = new DateSynonym(3, "3", "desc2", new Date(System.currentTimeMillis() + 10000));
		ParameterGrid grid1 = new ParameterGrid(1, 1, ds2, null);
		ParameterGrid grid2 = new ParameterGrid(2, 1, ds2, ds3);
		assertTrue(0 > comparator.compare(grid1, grid2));
		assertTrue(0 < comparator.compare(grid2, grid1));

		grid2.setEffectiveDate(ds1);
		assertTrue(0 > comparator.compare(grid1, grid2));
		assertTrue(0 < comparator.compare(grid2, grid1));

		grid1 = new ParameterGrid(1, 1, null, ds3);
		assertTrue(0 < comparator.compare(grid1, grid2));
		assertTrue(0 > comparator.compare(grid2, grid1));
	}

	/**
	 * @throws Exception Earliest/most recent should be more than older dates. So null effective dates are older than
	 *             non-null effective dates.
	 */
	@Test
	public void testCompareOpenEffectiveDateIsLessThanNonNullEffectiveDate() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "1", "desc1", new Date());
		DateSynonym ds2 = new DateSynonym(2, "2", "desc2", new Date(System.currentTimeMillis() + 10000));
		ParameterGrid grid1 = new ParameterGrid(1, 1, ds1, null);
		ParameterGrid grid2 = new ParameterGrid(2, 1, null, ds2);
		assertTrue(0 > comparator.compare(grid1, grid2));
		assertTrue(0 < comparator.compare(grid2, grid1));
	}

	@Test
	public void testCompareWithNoDatesReturnsZero() throws Exception {
		ParameterGrid grid1 = new ParameterGrid(1, 1, null, null);
		ParameterGrid grid2 = new ParameterGrid(2, 1, null, null);
		assertEquals(0, comparator.compare(grid1, grid2));
	}

	@Test
	public void testCompareWithNullsReturnsZero() throws Exception {
		assertEquals(0, comparator.compare(null, null));
	}

	@Test
	public void testCompareWithOneNullThrowsNullPointerException() throws Exception {
		ParameterGrid grid = new ParameterGrid(1, 1, null, null);
		try {
			comparator.compare(grid, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
		try {
			comparator.compare(null, grid);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}
}
