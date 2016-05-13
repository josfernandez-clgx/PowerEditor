/*
 * Created on 2004. 10. 6.
 */
package com.mindbox.pe.client.common.tab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * PowerEditor tab for managing consistent UI.
 * Use this instead of <code>javax.swing.JTabbedPane</code>. Note {@link #setModel(SingleSelectionModel)} method
 * of this does nothing, as the behavior of this depends on the model.
 * <p>
 * Any component you add to this, using one of <code>add</code> methods, <code>addTab</code> methods, 
 * or <code>insertTab</code> methods, must implement {@link PowerEditorTabPanel},
 * in order for the component to be notified when a tab change occurs.
 * <p>
 * When a tab change is detected, 
 * <ol>
 * <li>Check if the component of the previous tab (the tab that was visible when a tab change is detected)
 *     implements {@link PowerEditorTabPanel}. If not, skip to Step 4.</li>
 * <li>{@link PowerEditorTabPanel#hasUnsavedChanges()} is called on the component.
 *     If it returns <code>false</code>, skip to Step 4.
 * </li>
 * <li>{@link PowerEditorTabPanel#saveChanges()} is called on the component.
 * If it throws an exception, tab change will not occur.
 * </li>
 * <li>Tab selection changes, as usual.</li>
 * </ol>
 * <b>Usage</b><br/>
 * Use this class as you would <code>javax.swing.JTabbedPane</code>.
 * Just make sure the component you add as a tab to this implements {@link PowerEditorTabPanel}.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 * @see PowerEditorTabPanel
 */
public class PowerEditorTab extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static class Test {

		public static void main(String[] args) throws Exception {
			MetalLookAndFeel.setCurrentTheme(PowerEditorSwingTheme.getInstance());
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

			JFrame frame = new JFrame("New Guideline (Global-Qualify)");
			frame.setBounds(200, 200, 800, 600);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
			p1.add(new JLabel("Row: "));
			JSpinner spinner = new JSpinner();
			spinner.setValue(new Integer(1));
			p1.add(spinner);
			p1.add(new JButton("New Guideline"));

			JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			p2.add(new JButton("OK"));
			p2.add(new JButton("Cancel"));

			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(p1, BorderLayout.NORTH);
			frame.getContentPane().add(p2, BorderLayout.SOUTH);
			frame.setVisible(true);
		}
	}


	private class SelectionModel extends DefaultSingleSelectionModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		protected void fireStateChanged() {
			super.fireStateChanged();
		}

		public void setSelectedIndex(int arg0) {
			if (processTabChange()) {
				super.setSelectedIndex(arg0);
			}
		}
	}

	public PowerEditorTab() {
		super();
		addListener();
	}

	public PowerEditorTab(int tabPlacement) {
		super(tabPlacement);
		addListener();
	}

	public PowerEditorTab(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		addListener();
	}

	private void addListener() {
		//getModel().addChangeListener(new TabChangeL());
		super.setModel(new SelectionModel());
	}

	/**
	 * This does nothing.
	 */
	public final void setModel(SingleSelectionModel arg0) {
	}

	/**
	 * Gets the PowerEditorTabPanel if it is contained in the selected tab.
	 * If the select tab contains an instance of this, this calls
	 * this methond on the instance to obtain the PowerEditorTabPanel.
	 * @return PowerEditorTabPanel if it is contained in the selected tab;
	 *         <code>null</code>, otherwise
	 */
	public final PowerEditorTabPanel getSelectedPowerEditorTabPanel() {
		if (getTabCount() > 0) {
			Component component = getSelectedComponent();
			if (component == null) return null;
			if (component instanceof PowerEditorTabPanel) {
				return (PowerEditorTabPanel) component;
			}
			else if (component instanceof PowerEditorTab) {
				return ((PowerEditorTab) component).getSelectedPowerEditorTabPanel();
			}
		}
		return null;
	}

	/**
	 * Tests if tab can be changed
	 * @return <code>true</code> if tab change can be made; <code>false</code>, otherwise
	 */
	private boolean processTabChange() {
		PowerEditorTabPanel tabPanel = getSelectedPowerEditorTabPanel();
		if (tabPanel != null) {
			boolean hasChanges = tabPanel.hasUnsavedChanges();
			if (hasChanges) {
				Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
				if (result == null) {
					return false;
				}
				else if (result.booleanValue()) {
					try {
						tabPanel.saveChanges();
						return true;
					}
					catch (CanceledException e) {
						return false;
					}
					catch (Exception ex) {
						ClientUtil.handleRuntimeException(ex);
						return false;
					}
				}
				else {
					tabPanel.discardChanges();
					return true;
				}
			}
		}
		return true;
	}
}