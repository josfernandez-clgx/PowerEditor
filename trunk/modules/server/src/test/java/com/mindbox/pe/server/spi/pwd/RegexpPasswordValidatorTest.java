package com.mindbox.pe.server.spi.pwd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.spi.PasswordValidatorProvider;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RegexpPasswordValidatorTest extends AbstractTestBase {

	private static final String TOO_SHORT_PWD = "Aa123";
	private static final String TOO_SHORT_PWD_MD5 = "46315d1d58cae3d8df137cd2ad9c4a70";
	private static final String ONE_PATTERNS_MATCHED = "AAAAAA";
	private static final String ONE_PATTERNS_MATCHED_MD5 = "36d04a9d74392c727b1a9bf97a7bcbac";
	private static final String TWO_PATTERNS_MATCHED = "AA1234";
	private static final String TWO_PATTERNS_MATCHED_MD5 = "5b6cc0a07cd7a9af8791e7504312b475";
	private static final String THREE_PATTERNS_MATCHED = "Aa1234";
	private static final String THREE_PATTERNS_MATCHED_MD5 = "e267cfcd18461ce93867eca67c59f41";
	private static final String FOUR_PATTERNS_MATCHED = "Aa123!";
	private static final String FOUR_PATTERNS_MATCHED_MD5 = "675278e2a74b19cfa0a5f766424c67cc";
	private static final String VALID_PWD = THREE_PATTERNS_MATCHED;
	private static final String VALID_PWD_MD5 = THREE_PATTERNS_MATCHED_MD5;

	private PasswordValidatorProvider validator;

	@Before
	public void setUp() throws Exception {
		validator = new RegexpPasswordValidator();
		ReflectionUtil.setPrivate(validator, "minLength", new Integer(6));
		ReflectionUtil.setPrivate(validator, "minRegexpMatch", new Integer(3));
		ReflectionUtil.setPrivate(validator, "minRegexpMatchSet", new Boolean(true));
		ReflectionUtil.executePrivate(validator, "setRegexp", new Class[] { String.class }, new Object[] { ".*([\\p{Upper}]).*" });
		ReflectionUtil.executePrivate(validator, "setRegexp", new Class[] { String.class }, new Object[] { ".*([\\p{Lower}]).*" });
		ReflectionUtil.executePrivate(validator, "setRegexp", new Class[] { String.class }, new Object[] { ".*([\\p{Digit}]).*" });
		ReflectionUtil.executePrivate(validator, "setRegexp", new Class[] { String.class }, new Object[] { ".*([\\p{Punct}]).*" });
	}

	@Test
	public void test6CharMin() throws Exception {
		assertFalse(validator.isValidPassword(null, null, null));
		assertFalse(validator.isValidPassword(TOO_SHORT_PWD, TOO_SHORT_PWD_MD5, null));
		assertTrue(validator.isValidPassword(VALID_PWD, VALID_PWD_MD5, null));
	}

	@Test
	public void testRequiredChars() throws Exception {
		assertFalse(validator.isValidPassword(ONE_PATTERNS_MATCHED, ONE_PATTERNS_MATCHED_MD5, null));
		assertFalse(validator.isValidPassword(TWO_PATTERNS_MATCHED, TWO_PATTERNS_MATCHED_MD5, null));
		assertTrue(validator.isValidPassword(THREE_PATTERNS_MATCHED, THREE_PATTERNS_MATCHED_MD5, null));
		assertTrue(validator.isValidPassword(FOUR_PATTERNS_MATCHED, FOUR_PATTERNS_MATCHED_MD5, null));
	}

	@Test
	public void testReused() throws Exception {
		//	testng reuse with one password in password history
		assertFalse(validator.isValidPassword(VALID_PWD, VALID_PWD_MD5, new String[] { VALID_PWD_MD5 }));
		// testng reuse with more than one password in password history
		assertFalse(validator.isValidPassword(VALID_PWD, THREE_PATTERNS_MATCHED_MD5, new String[] { VALID_PWD_MD5, THREE_PATTERNS_MATCHED_MD5 }));

		assertTrue(validator.isValidPassword(VALID_PWD, VALID_PWD_MD5, null)); // null OK
		assertTrue(validator.isValidPassword(VALID_PWD, VALID_PWD_MD5, new String[] {})); // zero length OK

		assertFalse(VALID_PWD.equals(FOUR_PATTERNS_MATCHED)); // sanity check
		assertTrue(validator.isValidPassword(VALID_PWD, VALID_PWD_MD5, new String[] { FOUR_PATTERNS_MATCHED }));
	}
}
