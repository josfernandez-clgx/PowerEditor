package com.mindbox.pe.server.generator.value.rhscolref;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllServerGeneratorValueRhsColRefTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("AllServerGeneratorValueRhsColRefTests Tests");
		suite.addTest(EnumValueHelperTest.suite());
		suite.addTest(EnumValuesHelperTest.suite());
		suite.addTest(RHSColRefWriteValueHelperFactoryTest.suite());
		return suite;
	}

}
