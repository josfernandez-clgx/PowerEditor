package com.mindbox.pe.client.common.dialog;

import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Date;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.common.ReflectionUtil;


public class MDateDateFieldTest extends AbstractClientTestBase {

	/**
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		Date date = getDate(2006, 10, 10, 10, 11, 12);
		MDateDateField mdateField = new MDateDateField(false, false, true);
		mdateField.setValue(date);
		assertEquals(mdateField.getDate(), date);
		assertEquals(mdateField.formatToString(mdateField.getDate()), "10/10/2006 10:11:12");
		assertEquals(mdateField.formatToDate("10/10/2006 10:11:12"), mdateField.getDate());


		mdateField = new MDateDateField(false, true, false);
		mdateField.setValue(date);
		assertNotEquals(mdateField.getDate(), date);
		assertEquals(mdateField.getDate(), getDate(2006, 10, 10));
		assertEquals(mdateField.formatToString(mdateField.getDate()), "10/10/2006");
		assertEquals(mdateField.formatToDate("10/10/2006"), mdateField.getDate());

		mdateField = new MDateDateField(true, true);
		mdateField.setValue(date);
		mdateField.setEnabled(false);
		assertFalse(((JTextField) ReflectionUtil.getPrivate(mdateField, "timeField")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(mdateField, "clearButton")).isEnabled());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
