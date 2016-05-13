package com.mindbox.pe.common;

import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class PasswordOneWayHashUtilTest extends AbstractTestBase {

	@Test
	public void testConvertToOneWayHashWithInvalidAlgorithmThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				PasswordOneWayHashUtil.class,
				"convertToOneWayHash",
				new Class[] { String.class, String.class },
				new Object[] { "", PasswordOneWayHashUtil.HASH_ALGORITHM_MD5 + "x" },
				UnsupportedOperationException.class);
	}

	@Test
	public void testConvertToOneWayHashWithNullAlgorithmThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(PasswordOneWayHashUtil.class, "convertToOneWayHash", new Class[] { String.class, String.class }, new Object[] { "", null });
	}

	@Test
	public void testConvertToOneWayHashWithNull() throws Exception {
		String pwdAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash(null, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		assertNull(pwdAsOneWayHash);
	}

	@Test
	public void testConvertToOneWayHashWithEmptyString() throws Exception {
		String pwdAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash("", PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		assertNull(pwdAsOneWayHash);
	}

	@Test
	public void testConvertToOneWayHashHappyCase() throws Exception {
		String clearText = "demo";
		String pwdAsOneWayHash = PasswordOneWayHashUtil.convertToOneWayHash(clearText, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
		assertEquals("fe1ce2a7fbac8fafaed7c982a4e229", pwdAsOneWayHash);
	}
}
