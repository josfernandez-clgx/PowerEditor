package com.mindbox.pe.client.common;

import com.mindbox.pe.client.common.context.AllClientCommonContextTestSuite;
import com.mindbox.pe.client.common.formatter.AllClientCommonFormatterTests;
import com.mindbox.pe.client.common.grid.AllClientCommonGridTests;
import com.mindbox.pe.client.common.selection.AllClientCommonSelectionTests;
import com.mindbox.pe.client.common.table.AllClientCommonTableTestSuite;
import com.mindbox.pe.client.common.tree.AllClientCommonTreeTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllClientCommonTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Common Tests");
		suite.addTest(DateSynonymComboBoxTest.suite());
		suite.addTest(FloatTextFieldTest.suite());
		suite.addTest(EnumValueCellRendererTest.suite());
		suite.addTest(RefreshableComboBoxModelTest.suite());
		suite.addTest(AllClientCommonContextTestSuite.suite());
		suite.addTest(AllClientCommonFormatterTests.suite());
		suite.addTest(AllClientCommonGridTests.suite());
		suite.addTest(AllClientCommonSelectionTests.suite());
		suite.addTest(AllClientCommonTableTestSuite.suite());
		suite.addTest(AllClientCommonTreeTestSuite.suite());
		return suite;
	}
}
