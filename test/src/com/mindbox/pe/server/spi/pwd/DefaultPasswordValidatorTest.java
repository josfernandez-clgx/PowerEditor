package com.mindbox.pe.server.spi.pwd;

import com.mindbox.pe.server.spi.PasswordValidatorProvider;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DefaultPasswordValidatorTest extends TestCase {
	private PasswordValidatorProvider validator;
	
	public static Test suite() {
		TestSuite suite = new TestSuite(DefaultPasswordValidatorTest.class.getName());
		suite.addTestSuite(DefaultPasswordValidatorTest.class);
		return suite;
	}
	
	public DefaultPasswordValidatorTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		validator = new DefaultPasswordValidator();
	}
	
	public void testNull() throws Exception {
		assertFalse(validator.isValidPassword(null,null, null));
	}
	
	public void testEmpty() throws Exception {
		assertFalse(validator.isValidPassword("","", null));
	}
	
	public void testBlank() throws Exception {
		assertFalse(validator.isValidPassword(" ","7215ee9c7d9dc229d2921a40e899ec5f", null));
	}
	
	public void testNonBlank() throws Exception {
		assertTrue(validator.isValidPassword("1","c4ca4238a0b92382dcc509a6f75849b", null));
	}
    
    public void testDefaultDescriptionBlank() throws Exception {
        assertNotNull(validator.getDescription());
        assertEquals(validator.getDescription(), "");
    }
    
}
