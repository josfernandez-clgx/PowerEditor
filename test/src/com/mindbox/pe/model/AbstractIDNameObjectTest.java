package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class AbstractIDNameObjectTest extends AbstractTestBase {

	private static class IDNameObjectImpl extends AbstractIDNameObject {
		public IDNameObjectImpl(String name) {
			super(name);
		}

		public IDNameObjectImpl(int id, String name) {
			super(id, name);
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractIDNameObjectTest Tests");
		suite.addTestSuite(AbstractIDNameObjectTest.class);
		return suite;
	}

	public AbstractIDNameObjectTest(String name) {
		super(name);
	}

	public void testConstructorWithNullNameThrowsNullPointerException() throws Exception {
		try {
			new IDNameObjectImpl(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {

		}
		try {
			new IDNameObjectImpl(1, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {

		}
	}

	public void testSetNameWithNullThrowsNullPointerException() throws Exception {
		AbstractIDNameObject idNameobj = new IDNameObjectImpl(1,"name");
		try {
			idNameobj.setName(null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {

		}
	}
}
