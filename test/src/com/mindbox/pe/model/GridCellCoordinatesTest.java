package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.GridCellCoordinates.GridCellCoordinatesComparator;

public class GridCellCoordinatesTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(GridCellCoordinatesTest.class.getName());
		suite.addTestSuite(GridCellCoordinatesTest.class);
		return suite;
	}

	public GridCellCoordinatesTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
	}
	
	public void testConstructorNegativeRow() throws Exception {
		try {
			new GridCellCoordinates(-1, 1);
			fail("Expected " + IllegalArgumentException.class.getName());
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	public void testConstructorNegativeColumn() throws Exception {
		try {
			new GridCellCoordinates(1, -1);
			fail("Expected " + IllegalArgumentException.class.getName());
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	public void testEquals() throws Exception {
		GridCellCoordinates coord = new GridCellCoordinates(1, 1);
		GridCellCoordinates coordLesserRow = new GridCellCoordinates(0, 1);
		GridCellCoordinates coordGreaterRow = new GridCellCoordinates(2, 1);
		GridCellCoordinates coordLesserColumn = new GridCellCoordinates(1, 0);
		GridCellCoordinates coordGreaterColumn = new GridCellCoordinates(1, 2);
		GridCellCoordinates coordDiffDesc = new GridCellCoordinates(1, 1, "Desc");
		
		assertTrue(coord.equals(coord));
		assertFalse(coord.equals(null));
		assertFalse(coord.equals(new Object()));
		assertFalse(coord.equals(coordLesserRow));
		assertFalse(coord.equals(coordGreaterRow));
		assertFalse(coord.equals(coordLesserColumn));
		assertFalse(coord.equals(coordGreaterColumn));
		assertTrue(coord.equals(coordDiffDesc));
	}
	
	public void testRowFirstComparatorNullObject1() throws Exception {
		GridCellCoordinatesComparator comparator = new GridCellCoordinates.RowFirstComparator();
		try {
			comparator.compare(null, new GridCellCoordinates(1, 1));
			fail("Expected " + NullPointerException.class.getName());
		} catch (NullPointerException e) {
			// pass
		}
	}
	
	public void testRowFirstComparatorNullObject2() throws Exception {
		GridCellCoordinatesComparator comparator = new GridCellCoordinates.RowFirstComparator();
		try {
			comparator.compare(new GridCellCoordinates(1, 1), null);
			fail("Expected " + NullPointerException.class.getName());
		} catch (NullPointerException e) {
			// pass
		}
	}

	
// Note: no longer valid; types are checked in Java 1.6
//	public void testRowFirstComparatorObject1WrongType() throws Exception {
//		GridCellCoordinatesComparator comparator = new GridCellCoordinates.RowFirstComparator();
//		try {
//			comparator.compare(new Object(), new GridCellCoordinates(1, 1));
//			fail("Expected " + ClassCastException.class.getName());
//		} catch (ClassCastException e) {
//			// pass
//		}
//	}
//	
//	public void testRowFirstComparatorObject2WrongType() throws Exception {
//		GridCellCoordinatesComparator comparator = new GridCellCoordinates.RowFirstComparator();
//		try {
//			comparator.compare(new GridCellCoordinates(1, 1), new Object());
//			fail("Expected " + ClassCastException.class.getName());
//		} catch (ClassCastException e) {
//			// pass
//		}
//	}
	
	public void testRowFirstComparator() throws Exception {
		GridCellCoordinatesComparator comparator = new GridCellCoordinates.RowFirstComparator();
		GridCellCoordinates coord = new GridCellCoordinates(1, 1);
		GridCellCoordinates coordSame = new GridCellCoordinates(1, 1);
		GridCellCoordinates coordSameDiffDesc = new GridCellCoordinates(1, 1, "Desc");
		GridCellCoordinates coordRowLesser = new GridCellCoordinates(0, 1);
		GridCellCoordinates coordRowGreater = new GridCellCoordinates(2, 1);
		GridCellCoordinates coordColumnLesser = new GridCellCoordinates(1, 0);
		GridCellCoordinates coordColumnGreater = new GridCellCoordinates(1, 2);
		
		assertTrue(0 == comparator.compare(coord, coord));
		assertTrue(0 == comparator.compare(coord, coordSame));
		assertTrue(0 == comparator.compare(coord, coordSameDiffDesc));
		assertTrue(0 < comparator.compare(coord, coordRowLesser));
		assertTrue(0 > comparator.compare(coord, coordRowGreater));
		assertTrue(0 < comparator.compare(coord, coordColumnLesser));
		assertTrue(0 > comparator.compare(coord, coordColumnGreater));
	}
	
	public void testColumnFirstComparatorNullObject1() throws Exception {
		GridCellCoordinatesComparator comparator = new GridCellCoordinates.ColumnFirstComparator();
		try {
			comparator.compare(null, new GridCellCoordinates(1, 1));
			fail("Expected " + ClassCastException.class.getName());
		} catch (ClassCastException e) {
			// pass
		}
	}
	
	public void testColumnFirstComparatorNullObject2() throws Exception {
		GridCellCoordinatesComparator comparator = new GridCellCoordinates.ColumnFirstComparator();
		try {
			comparator.compare(new GridCellCoordinates(1, 1), null);
			fail("Expected " + ClassCastException.class.getName());
		} catch (ClassCastException e) {
			// pass
		}
	}
	
// Note: no longer valid; types are checked in Java 1.6
//	public void testColumnFirstComparatorObject1WrongType() throws Exception {
//		GridCellCoordinatesComparator comparator = new GridCellCoordinates.ColumnFirstComparator();
//		try {
//			comparator.compare(new Object(), new GridCellCoordinates(1, 1));
//			fail("Expected " + ClassCastException.class.getName());
//		} catch (ClassCastException e) {
//			// pass
//		}
//	}
//	
//	public void testColumnFirstComparatorObject2WrongType() throws Exception {
//		GridCellCoordinatesComparator comparator = new GridCellCoordinates.ColumnFirstComparator();
//		try {
//			comparator.compare(new GridCellCoordinates(1, 1), new Object());
//			fail("Expected " + ClassCastException.class.getName());
//		} catch (ClassCastException e) {
//			// pass
//		}
//	}
	
	public void testColumnFirstComparator() throws Exception {
		GridCellCoordinatesComparator comparator = new GridCellCoordinates.ColumnFirstComparator();
		GridCellCoordinates coord = new GridCellCoordinates(1, 1);
		GridCellCoordinates coordSame = new GridCellCoordinates(1, 1);
		GridCellCoordinates coordSameDiffDesc = new GridCellCoordinates(1, 1, "Desc");
		GridCellCoordinates coordRowLesser = new GridCellCoordinates(0, 1);
		GridCellCoordinates coordRowGreater = new GridCellCoordinates(2, 1);
		GridCellCoordinates coordColumnLesser = new GridCellCoordinates(1, 0);
		GridCellCoordinates coordColumnGreater = new GridCellCoordinates(1, 2);
		
		assertTrue(0 == comparator.compare(coord, coord));
		assertTrue(0 == comparator.compare(coord, coordSame));
		assertTrue(0 == comparator.compare(coord, coordSameDiffDesc));
		assertTrue(0 < comparator.compare(coord, coordRowLesser));
		assertTrue(0 > comparator.compare(coord, coordRowGreater));
		assertTrue(0 < comparator.compare(coord, coordColumnLesser));
		assertTrue(0 > comparator.compare(coord, coordColumnGreater));
	}
}
