package com.mindbox.pe.client.common.selection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.table.IDNameObjectSelectionTable;
import com.mindbox.pe.client.common.table.IDNameObjectSelectionTableModel;
import com.mindbox.pe.model.SimpleEntityData;

public class IDNameObjectSelectionPanelTest extends AbstractClientTestBase {

	private static class IDNameObjectSelectionPanelImpl extends IDNameObjectSelectionPanel<SimpleEntityData, ButtonPanel> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2465304435237858084L;
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

	@Test
	public void testAddForPersistentWithNullThrowsNullPointerException() throws Exception {
		try {
			idNameObjectSelectionPanel.add((SimpleEntityData) null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
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

	@Before
	public void setUp() throws Exception {
		super.setUp();
		IDNameObjectSelectionTableModel<SimpleEntityData> idNameObjectSelectionTableModel = new IDNameObjectSelectionTableModel<SimpleEntityData>(new String[] { "column1" });
		IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>, SimpleEntityData> idNameObjectSelectionTable = new IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>, SimpleEntityData>(
				idNameObjectSelectionTableModel,
				false);
		idNameObjectSelectionPanel = new IDNameObjectSelectionPanelImpl(idNameObjectSelectionTable);
	}

	@After
	public void tearDown() throws Exception {
		idNameObjectSelectionPanel = null;
		super.tearDown();
	}
}
