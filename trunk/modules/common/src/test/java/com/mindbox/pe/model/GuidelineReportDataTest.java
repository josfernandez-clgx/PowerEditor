package com.mindbox.pe.model;

import static com.mindbox.pe.common.CommonTestObjectMother.createParameterGrid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class GuidelineReportDataTest extends AbstractTestBase {

	private GuidelineReportData data;

	@Test
	public void testAll() throws Exception {
		data = new GuidelineReportData(1, "test", createParameterGrid(), true);
		assertNull(data.getMatchingRowNumbers());
		data.setMatchingRowNumbers("1,2,3");
		assertEquals(data.getMatchingRowNumbers(), "1,2,3");
	}

}
