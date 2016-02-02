package com.mindbox.pe.model.assckey;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllModelAsscKeyTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllModelAsscKeyTestSuite Tests");
		suite.addTest(AbstractAssociationKeyTest.suite());
		suite.addTest(AbstractMutableTimedAssociationKeySetTest.suite());
		suite.addTest(AbstractMutableTimedAssociationKeyTest.suite());
		suite.addTest(AbstractTimedAssociationKeyTest.suite());
		suite.addTest(DefaultChildAssociationKeySetTest.suite());
		suite.addTest(DefaultParentAssociationKeySetTest.suite());
		suite.addTest(GenericEntityAssociationKeyTest.suite());
		return suite;
	}

}
