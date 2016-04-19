package com.mindbox.pe.client.common;

import java.awt.Component;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.template.GridTemplateColumn;

public class CheckList extends JList {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private class CheckListRenderer extends JCheckBox implements ListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1) {
			this.setEnabled(jlist.isEnabled());
			setSelected(flag);
			this.setFont(jlist.getFont());
			if (i % 2 != 0) {
				this.setBackground(UIManager.getColor("List.selectionBackground"));
			}
			else {
				this.setBackground(UIManager.getColor("List.background"));
			}
			setText(getListText(obj));
			return this;
		}

		public CheckListRenderer() {
			this.setBackground(UIManager.getColor("List.background"));
			this.setForeground(UIManager.getColor("List.foreground"));
		}
	}

	private class ToggleSelectionModel extends DefaultListSelectionModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		public void setValueIsAdjusting(boolean flag) {
			if (!flag) gestureStarted = false;
		}

		public void setSelectionInterval(int i, int j) {
			if (this.isSelectedIndex(i) && !gestureStarted)
				super.removeSelectionInterval(i, j);
			else
				super.addSelectionInterval(i, j);
			gestureStarted = true;
		}

		boolean gestureStarted;

		ToggleSelectionModel() {
			gestureStarted = false;
		}
	}

	public CheckList() {
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellRenderer(new CheckListRenderer());
		setSelectionModel(new ToggleSelectionModel());
		setBorder(new EtchedBorder());
	}

	public void selectAll() {
		super.setSelectionInterval(0, getModel().getSize() - 1);
	}

	/**
	 * Override this to customize text displayed for each item in this list.
	 * @param obj 
	 * @return string representation of <code>obj</code>
	 */
	protected String getListText(Object obj) {
		return (obj instanceof GridTemplateColumn ? ((GridTemplateColumn) obj).getTitle() : ((obj instanceof Privilege) ? ((Privilege) obj).getDisplayString() : ((obj instanceof IDNameObject)
				? ((IDNameObject) obj).getName()
				: ((obj instanceof TemplateUsageType) ? ((TemplateUsageType) obj).getDisplayName() : obj.toString()))));
	}
}