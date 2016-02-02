package com.mindbox.pe.client.common.selection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.table.IDNameObjectSelectionTable;
import com.mindbox.pe.client.common.table.IDNameObjectSelectionTableModel;
import com.mindbox.pe.model.SimpleEntityData;

public class IDNameObjectSelectionPanelTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("IDNameObjectSelectionPanelTest Tests");
		suite.addTestSuite(IDNameObjectSelectionPanelTest.class);
		return suite;
	}

	private static class IDNameObjectSelectionPanelImpl extends IDNameObjectSelectionPanel<SimpleEntityData, ButtonPanel> {

		private boolean setEnabledSelectionAwaresCalled = false;

		public IDNameObjectSelectionPanelImpl(IDNameObjectSelectionTable<?, SimpleEntityData> selectionTable) {
			super(IDNameObjectSelectionPanelTest.class.getName(), selectionTable, false);
		}

		public void setEnabledSelectionAwares(boolean enabled) {
			setEnabledSelectionAwaresCalled = true;
		}

		boolean isSetEnabledSelectionAwaresCalled() {
			return setEnabledSelectionAwaresCalled;
		}

		void resetEnabledSelectionAwaresCalled() {
			setEnabledSelectionAwaresCalled = false;
		}

		protected void createButtonPanel() {
			this.buttonPanel = new ButtonPanel(new JButton[0], 0);
		}

	}

	private IDNameObjectSelectionPanelImpl idNameObjectSelectionPanel;

	public IDNameObjectSelectionPanelTest(String name) {
		super(name);
	}

	public void testAddForPersistentWithNullThrowsNullPointerException() throws Exception {
		try {
			idNameObjectSelectionPanel.add((SimpleEntityData) null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testAddForPersistentWithValidValueDisablesSelectionListener() throws Exception {
		SimpleEntityData entity = new SimpleEntityData(100, "name");
		List<SimpleEntityData> list = new ArrayList<SimpleEntityData>();
		list.add(entity);
		idNameObjectSelectionPanel.populate(list);
		idNameObjectSelectionPanel.selectEntity(100);
		idNameObjectSelectionPanel.resetEnabledSelectionAwaresCalled();

		entity = new SimpleEntityData(2, "name");
		idNameObjectSelectionPanel.add(entity);
		Thread.sleep(100);
		assertFalse(idNameObjectSelectionPanel.isSetEnabledSelectionAwaresCalled());
	}

	protected void setUp() throws Exception {
		super.setUp();
		IDNameObjectSelectionTableModel<SimpleEntityData> idNameObjectSelectionTableModel = new IDNameObjectSelectionTableModel<SimpleEntityData>(
				new String[] { "column1" });
		IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>, SimpleEntityData> idNameObjectSelectionTable = new IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>, SimpleEntityData>(
				idNameObjectSelectionTableModel,
				false);
		idNameObjectSelectionPanel = new IDNameObjectSelectionPanelImpl(idNameObjectSelectionTable);
	}

	protected void tearDown() throws Exception {
		idNameObjectSelectionPanel = null;
		super.tearDown();
	}
}
