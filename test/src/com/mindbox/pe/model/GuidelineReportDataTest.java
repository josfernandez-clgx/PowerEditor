package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;

public class GuidelineReportDataTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineReportDataTest Tests");
		suite.addTestSuite(GuidelineReportDataTest.class);
		return suite;
	}

	private GuidelineReportData data;

	public GuidelineReportDataTest(String name) {
		super(name);
	}

	public void testAll() throws Exception {
        data = new GuidelineReportData(1, "test", ObjectMother.createParameterGrid(), true);
        assertNull(data.getMatchingRowNumbers());
        data.setMatchingRowNumbers("1,2,3");
        assertEquals(data.getMatchingRowNumbers(), "1,2,3");
	}
    
	protected void setUp() throws Exception {
        super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
