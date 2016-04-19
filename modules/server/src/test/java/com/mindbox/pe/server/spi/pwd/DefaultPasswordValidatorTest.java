package com.mindbox.pe.server.spi.pwd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.server.spi.PasswordValidatorProvider;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DefaultPasswordValidatorTest extends AbstractTestBase {

	private PasswordValidatorProvider validator;

	@Before
	public void setUp() throws Exception {
		validator = new DefaultPasswordValidator();
	}

	@Test
	public void testBlank() throws Exception {
		assertFalse(validator.isValidPassword(" ", "7215ee9c7d9dc229d2921a40e899ec5f", null));
	}

	@Test
	public void testDefaultDescriptionBlank() throws Exception {
		assertNotNull(validator.getDescription());
		assertEquals(validator.getDescription(), "");
	}

	@Test
	public void testEmpty() throws Exception {
		assertFalse(validator.isValidPassword("", "", null));
	}

	@Test
	public void testNonBlank() throws Exception {
		assertTrue(validator.isValidPassword("1", "c4ca4238a0b92382dcc509a6f75849b", null));
	}

	@Test
	public void testNull() throws Exception {
		assertFalse(validator.isValidPassword(null, null, null));
	}

}
