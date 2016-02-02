package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class AbstractIDObjectTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractIDObjectTest Tests");
		suite.addTestSuite(AbstractIDObjectTest.class);
		return suite;
	}

	private static class IDObjectImpl extends AbstractIDObject {
		public IDObjectImpl(int id) {
			super(id);
		}

		public IDObjectImpl() {
			super();
		}
	}

	public AbstractIDObjectTest(String name) {
		super(name);
	}

	public void testDefaultConstructorSetsIDToUnassigned() throws Exception {
		assertEquals(Persistent.UNASSIGNED_ID, new IDObjectImpl().getID());
	}

	public void testConstructorWithIDAcceptsAllInt() throws Exception {
		assertEquals(-1, new IDObjectImpl(-1).getID());
		assertEquals(0, new IDObjectImpl(0).getID());
		assertEquals(999999999, new IDObjectImpl(999999999).getID());
		assertEquals(-204391, new IDObjectImpl(-204391).getID());
	}

	public void testSetIdStringWithValidStringSetsID() throws Exception {
		IDObjectImpl impl = new IDObjectImpl();
		impl.setIdString("2345");
		assertEquals(2345, impl.getID());
	}

	public void testSetIdStringWithNonIntegerStringIsNoOp() throws Exception {
		IDObjectImpl impl = new IDObjectImpl(1);
		impl.setIdString("x2345U");
		assertEquals(1, impl.getID());
	}

	public void testSetIdStringWithEmptyStringIsNoOp() throws Exception {
		IDObjectImpl impl = new IDObjectImpl(1);
		impl.setIdString("");
		assertEquals(1, impl.getID());
	}

	public void testSetIdStringWithNullIsNoOp() throws Exception {
		IDObjectImpl impl = new IDObjectImpl(1);
		impl.setIdString(null);
		assertEquals(1, impl.getID());
	}

}
