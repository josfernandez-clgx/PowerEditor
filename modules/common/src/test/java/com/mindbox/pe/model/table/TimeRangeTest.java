package com.mindbox.pe.model.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class TimeRangeTest extends AbstractTestBase {

	@Test
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
