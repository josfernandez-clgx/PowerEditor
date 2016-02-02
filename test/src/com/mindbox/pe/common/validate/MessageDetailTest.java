package com.mindbox.pe.common.validate;

import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class MessageDetailTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("MessageDetailTest Tests");
		suite.addTestSuite(MessageDetailTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public MessageDetailTest(String name) {
		super(name);
	}
	
	public void testMessageDetailIsSerializable() throws Exception {
		assertTrue(Serializable.class.isAssignableFrom(MessageDetail.class));
	}
}
