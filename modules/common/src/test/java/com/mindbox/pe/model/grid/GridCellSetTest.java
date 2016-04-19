package com.mindbox.pe.model.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.grid.GridCellCoordinates;
import com.mindbox.pe.model.grid.GridCellSet;
import com.mindbox.pe.unittest.AbstractTestBase;

public class GridCellSetTest extends AbstractTestBase {

	private GridCellSet cells;
	private GridCellCoordinates coords;

	@Before
	public void setUp() throws Exception {
		cells = new GridCellSet(new GridCellCoordinates.RowFirstComparator());
		coords = new GridCellCoordinates(0, 0);
	}

	@Test
	public void testAdd() throws Exception {
		assertFalse(cells.add(null));
		assertTrue(cells.add(coords));
		assertFalse(cells.add(coords));
	}

	@Test
	public void testRemove() throws Exception {
		cells.add(coords);
		assertFalse(cells.remove(null));
		assertTrue(cells.remove(coords));
		assertFalse(cells.remove(coords));
	}

	@Test
	public void testContains() throws Exception {
		cells.add(coords);
		assertFalse(cells.contains(null));
		assertTrue(cells.contains(coords));
	}

	@Test
	public void testGet() throws Exception {
		assertNull(cells.get(1, 1));
		cells.add(coords);
		assertSame(coords, cells.get(coords.getRow(), coords.getColumn()));
	}

	@Test
	public void testGetColumns() throws Exception {
		assertTrue(cells.getColumnIndexes().isEmpty());

		// 4 cells with 3 distinct rows, 2 distinct columns
		cells.add(new GridCellCoordinates(1, 2));
		cells.add(new GridCellCoordinates(1, 3));
		cells.add(new GridCellCoordinates(2, 3));
		cells.add(new GridCellCoordinates(3, 2));

		SortedSet<Integer> expectedColumns = new TreeSet<Integer>();
		expectedColumns.add(new Integer(2));
		expectedColumns.add(new Integer(3));
		assertEquals(expectedColumns, cells.getColumnIndexes());
	}

	@Test
	public void testGetRows() throws Exception {
		assertTrue(cells.getRowIndexes().isEmpty());

		// 4 cells with 3 distinct rows, 2 distinct columns
		cells.add(new GridCellCoordinates(1, 2));
		cells.add(new GridCellCoordinates(1, 3));
		cells.add(new GridCellCoordinates(2, 3));
		cells.add(new GridCellCoordinates(3, 2));

		SortedSet<Integer> expectedRows = new TreeSet<Integer>();
		expectedRows.add(new Integer(1));
		expectedRows.add(new Integer(2));
		expectedRows.add(new Integer(3));
		assertEquals(expectedRows, cells.getRowIndexes());
	}

	@Test
	public void testEquals() throws Exception {
		GridCellCoordinates greaterRow = new GridCellCoordinates(2, 2);
		GridCellCoordinates greaterColumn = new GridCellCoordinates(1, 3);
		cells.add(greaterRow);
		cells.add(greaterColumn);

		GridCellSet same = new GridCellSet(new GridCellCoordinates.RowFirstComparator());
		same.add(greaterRow);
		same.add(greaterColumn);

		GridCellSet diffComparator = new GridCellSet(new GridCellCoordinates.ColumnFirstComparator());
		diffComparator.add(greaterRow);
		diffComparator.add(greaterColumn);

		GridCellSet diffContents = new GridCellSet(new GridCellCoordinates.RowFirstComparator());
		diffContents.add(greaterRow);

		assertTrue(cells.equals(cells));
		assertTrue(cells.equals(same));
		assertTrue(cells.equals(diffComparator)); // because it is a Set, TreeSet.equals doesn't consider order only size and membership
		assertFalse(cells.equals(null));
		assertFalse(cells.equals(new Object()));
		assertFalse(cells.equals(diffContents));
	}
}
