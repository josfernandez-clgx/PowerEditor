package com.mindbox.pe.model.table;

import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class DynamicStringValueTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DynamicStringValueTest Tests");
		suite.addTestSuite(DynamicStringValueTest.class);
		return suite;
	}

	public DynamicStringValueTest(String name) {
		super(name);
	}

	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(DynamicStringValue.class);
	}

	public void testToStringReturnsStringValue() throws Exception {
		String str = "dynamic string text " + ObjectMother.createInt();
		DynamicStringValue dsValue = DynamicStringValue.parseValue(str);
		assertEquals(str, dsValue.toString());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
