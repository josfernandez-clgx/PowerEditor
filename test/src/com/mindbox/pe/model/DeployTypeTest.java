package com.mindbox.pe.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class DeployTypeTest extends AbstractTestBase {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(DeployTypeTest.class.getName());
		suite.addTestSuite(DeployTypeTest.class);
		return suite;
	}

	public DeployTypeTest(String name) {
		super(name);
	}

	public void testNotValidValues() throws Exception {
		Set<DeployType> notValidValues = new HashSet<DeployType>(Arrays.asList(new DeployType[]{DeployType.CODE, DeployType.RELATIONSHIP}));
		Set<DeployType> intersection = new HashSet<DeployType>(Arrays.asList(DeployType.VALID_VALUES));
		intersection.retainAll(notValidValues);
		assertEquals("Invalid DeployTypes in VALID_VALUES: " + intersection, 0, intersection.size());
	}
}
