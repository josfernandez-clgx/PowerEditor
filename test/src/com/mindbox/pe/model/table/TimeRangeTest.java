package com.mindbox.pe.model.table;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class TimeRangeTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("TimeRangeTest Tests");
		suite.addTestSuite(TimeRangeTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public TimeRangeTest(String name) {
		super(name);
	}
	
	public void testCopyHappyCase() throws Exception {
		TimeRange timeRange = new TimeRange();
		timeRange.setLowerValueInclusive(true);
		timeRange.setUpperValueInclusive(false);
		timeRange.setLowerValue(500);
		timeRange.setUpperValue(4000);
		
		GridCellValue gridCellValue = timeRange.copy();
		assertTrue(gridCellValue.getClass() == TimeRange.class);
		assertTrue(((TimeRange) gridCellValue).isLowerValueInclusive());
		assertFalse(((TimeRange) gridCellValue).isUpperValueInclusive());
		assertEquals(500, ((TimeRange) gridCellValue).getFloor());
		assertEquals(4000, ((TimeRange) gridCellValue).getCeiling());
	}
}
