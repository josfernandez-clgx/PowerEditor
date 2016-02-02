package com.mindbox.pe.server.config;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;

public class PasswordTest extends AbstractTestWithTestConfig {
	private static final String CLEAR_TEXT_PWD = "myPassword";
	private static final String ENCRYPTED_PWD = "WK36ajhPXs0SGfWo4LR+YA==";
	private static final String EMPTY_STRING_ENCRYPTED = "0MmF4lZJiSM=";
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(PasswordTest.class.getName());
		suite.addTestSuite(PasswordTest.class);
		return suite;
	}

	public PasswordTest(String name) {
		super(name);
	}
	
	public void testConstructEncryptedHappyPath() throws Exception {
		Password pwd = Password.fromEncryptedString(ENCRYPTED_PWD);
		assertEquals(CLEAR_TEXT_PWD, pwd.getClearText());
		assertEquals(ENCRYPTED_PWD, pwd.getEncrypted());
	}
	
	public void testConstructClearTextHappyPath() throws Exception {
		Password pwd = Password.fromClearText(CLEAR_TEXT_PWD);
		assertEquals(CLEAR_TEXT_PWD, pwd.getClearText());
		assertEquals(ENCRYPTED_PWD, pwd.getEncrypted());
	}
	
	public void testConstructNullEncrypted() throws Exception {
		try {
			Password.fromEncryptedString(null);
			fail("Expected " + NullPointerException.class.getName());
		} catch (NullPointerException e) {
			// pass
		}
	}
	
	public void testConstructNullClearText() throws Exception {
		try {
			Password.fromClearText(null);
			fail("Expected " + NullPointerException.class.getName());
		} catch (NullPointerException e) {
			// pass
		}
	}
	
	public void testConstructEmptyEncrypted() throws Exception {
		Password pwd = Password.fromEncryptedString("");
		assertEquals("", pwd.getEncrypted());
		
		String expectedClearText = "";
		assertEquals(expectedClearText, pwd.getClearText());
	}
	
	public void testConstructEmptyClearText() throws Exception {
		Password pwd = Password.fromClearText("");
		assertEquals("", pwd.getClearText());
		assertEquals(EMPTY_STRING_ENCRYPTED, pwd.getEncrypted());
	}
}
