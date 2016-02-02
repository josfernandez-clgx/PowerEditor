package com.mindbox.pe.client.common.grid;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;

public class CellValidatorTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CellValidatorTest Tests");
		suite.addTestSuite(CellValidatorTest.class);
		return suite;
	}

	public CellValidatorTest(String name) {
		super(name);
	}
	
	public void testValidateValueForSingleSelectEnumDataSpecWorksWithCustomEnums() throws Exception {
		List<String> enumValueList = new ArrayList<String>();
		enumValueList.add("Enum1");
		enumValueList.add("Enum2");
		enumValueList.add("Enum3");
		ColumnDataSpecDigest columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setIsBlankAllowed(true);
		assertTrue(CellValidator.validateValue("Enum2", columnDataSpecDigest));
	}

	// TODO Kim, 2008-05-28: Add Tests for more scenarios/ other methods
	
	protected void setUp() throws Exception {
		super.setUp();
		// Set up for CellValidatorTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for CellValidatorTest
		super.tearDown();
	}
}
