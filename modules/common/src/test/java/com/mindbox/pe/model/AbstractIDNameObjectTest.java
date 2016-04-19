package com.mindbox.pe.model;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractIDNameObjectTest extends AbstractTestBase {

	private static class IDNameObjectImpl extends AbstractIDNameObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4879982745876833482L;

		public IDNameObjectImpl(String name) {
			super(name);
		}

		public IDNameObjectImpl(int id, String name) {
			super(id, name);
		}
	}

	@Test
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

	@Test(expected = NullPointerException.class)
	public void testSetNameWithNullThrowsNullPointerException() throws Exception {
		AbstractIDNameObject idNameobj = new IDNameObjectImpl(1, "name");
		idNameobj.setName(null);
	}
}
