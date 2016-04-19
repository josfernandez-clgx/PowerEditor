package com.mindbox.pe.client.common;

import static com.mindbox.pe.unittest.TestObjectMother.createInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.swing.DefaultComboBoxModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class RefreshableComboBoxModelTest extends AbstractTestBase {

	@Test
	public void testConstructorWithNullModelThrowsNullPointerException() throws Exception {
		try {
			new RefreshableComboBoxModel(null);
			fail("Excepted NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testChangesInBaseModelUpdatesItems() throws Exception {
		DefaultComboBoxModel baseModel = new DefaultComboBoxModel();
		RefreshableComboBoxModel refreshableComboBoxModel = new RefreshableComboBoxModel(baseModel);
		assertEquals(0, refreshableComboBoxModel.getSize());

		baseModel.addElement(createInteger());
		baseModel.addElement(createInteger());
		assertEquals(2, refreshableComboBoxModel.getSize());

		baseModel.removeElementAt(0);
		assertEquals(1, refreshableComboBoxModel.getSize());
	}

	@Test
	public void testClearingBaseModelRemovesAllItems() throws Exception {
		DefaultComboBoxModel baseModel = new DefaultComboBoxModel();
		baseModel.addElement(createInteger());
		baseModel.addElement(createInteger());

		RefreshableComboBoxModel refreshableComboBoxModel = new RefreshableComboBoxModel(baseModel);
		assertEquals(2, refreshableComboBoxModel.getSize());

		baseModel.removeAllElements();
		assertEquals(0, refreshableComboBoxModel.getSize());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}
