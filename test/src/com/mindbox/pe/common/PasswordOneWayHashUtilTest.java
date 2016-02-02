package com.mindbox.pe.common;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class PasswordOneWayHashUtilTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(PasswordOneWayHashUtilTest.class.getName());
		suite.addTestSuite(PasswordOneWayHashUtilTest.class);
		return suite;
	}

	public PasswordOneWayHashUtilTest(String name) {
		super(name);
	}

	public void testConvertToOneWayHashWithInvalidAlgorithmThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				PasswordOneWayHashUtil.class,
				"convertToOneWayHash",
				new Class[] { String.class, String.class },
				new Object[] { "", PasswordOneWayHashUtil.HASH_ALGORITHM_MD5 + "x" },
				UnsupportedOperationException.class);
	}

	public void testConvertToOneWayHashWithNullAlgorithmThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				PasswordOneWayHashUtil.class,
				"convertToOneWayHash",
				new Class[] { String.class, String.class },
				new Object[] { "", null });
	}

	public void testConvertToOneWayHashWithNull() throws Exception {
		String pwdAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash(null, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		assertNull(pwdAsOneWayHash);
	}

	public void testConvertToOneWayHashWithEmptyString() throws Exception {
		String pwdAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash("", PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		assertNull(pwdAsOneWayHash);
	}

	public void testConvertToOneWayHashHappyCase() throws Exception {
		String clearText = "demo";
		String pwdAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash(clearText, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		assertEquals("fe1ce2a7fbac8fafaed7c982a4e229", pwdAsOneWayHash);
	}
}
