package com.mindbox.pe.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractIDObjectTest extends AbstractTestBase {

	private static class IDObjectImpl extends AbstractIDObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1592661522099628263L;

		public IDObjectImpl() {
			super();
		}

		public IDObjectImpl(int id) {
			super(id);
		}
	}

	@Test
	public void testConstructorWithIDAcceptsAllInt() throws Exception {
		assertEquals(-1, new IDObjectImpl(-1).getID());
		assertEquals(0, new IDObjectImpl(0).getID());
		assertEquals(999999999, new IDObjectImpl(999999999).getID());
		assertEquals(-204391, new IDObjectImpl(-204391).getID());
	}

	@Test
	public void testDefaultConstructorSetsIDToUnassigned() throws Exception {
		assertEquals(Persistent.UNASSIGNED_ID, new IDObjectImpl().getID());
	}

	@Test
	public void testSetIdStringWithEmptyStringIsNoOp() throws Exception {
		IDObjectImpl impl = new IDObjectImpl(1);
		impl.setIdString("");
		assertEquals(1, impl.getID());
	}

	@Test
	public void testSetIdStringWithNonIntegerStringIsNoOp() throws Exception {
		IDObjectImpl impl = new IDObjectImpl(1);
		impl.setIdString("x2345U");
		assertEquals(1, impl.getID());
	}

	@Test
	public void testSetIdStringWithNullIsNoOp() throws Exception {
		IDObjectImpl impl = new IDObjectImpl(1);
		impl.setIdString(null);
		assertEquals(1, impl.getID());
	}

	@Test
	public void testSetIdStringWithValidStringSetsID() throws Exception {
		IDObjectImpl impl = new IDObjectImpl();
		impl.setIdString("2345");
		assertEquals(2345, impl.getID());
	}

}
