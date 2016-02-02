package com.mindbox.pe.client.common;

import javax.swing.DefaultComboBoxModel;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class RefreshableComboBoxModelTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RefreshableComboBoxModelTest Tests");
		suite.addTestSuite(RefreshableComboBoxModelTest.class);
		return suite;
	}

	public RefreshableComboBoxModelTest(String name) {
		super(name);
	}

	public void testConstructorWithNullModelThrowsNullPointerException() throws Exception {
		try {
			new RefreshableComboBoxModel(null);
			fail("Excepted NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testChangesInBaseModelUpdatesItems() throws Exception {
		DefaultComboBoxModel baseModel = new DefaultComboBoxModel();
		RefreshableComboBoxModel refreshableComboBoxModel = new RefreshableComboBoxModel(baseModel);
		assertEquals(0, refreshableComboBoxModel.getSize());

		baseModel.addElement(ObjectMother.createInteger());
		baseModel.addElement(ObjectMother.createInteger());
		assertEquals(2, refreshableComboBoxModel.getSize());

		baseModel.removeElementAt(0);
		assertEquals(1, refreshableComboBoxModel.getSize());
	}

	public void testClearingBaseModelRemovesAllItems() throws Exception {
		DefaultComboBoxModel baseModel = new DefaultComboBoxModel();
		baseModel.addElement(ObjectMother.createInteger());
		baseModel.addElement(ObjectMother.createInteger());

		RefreshableComboBoxModel refreshableComboBoxModel = new RefreshableComboBoxModel(baseModel);
		assertEquals(2, refreshableComboBoxModel.getSize());
		
		baseModel.removeAllElements();
		assertEquals(0, refreshableComboBoxModel.getSize());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
