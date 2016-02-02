/*
 * Created on 2004. 3. 4.
 *
 */
package com.mindbox.pe.common.format;

import com.mindbox.pe.common.format.CurrencyFormatterTest;
import com.mindbox.pe.common.format.CurrencyRangeFormatterTest;
import com.mindbox.pe.common.format.FloatFormatterTest;
import com.mindbox.pe.common.format.FloatRangeFormatterTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllCommonFormatTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCommonFormatTests.class.getName());
		suite.addTest(FloatFormatterTest.suite());
		suite.addTest(CurrencyFormatterTest.suite());
		suite.addTest(FloatRangeFormatterTest.suite());
		suite.addTest(CurrencyRangeFormatterTest.suite());
		return suite;
	}

}
