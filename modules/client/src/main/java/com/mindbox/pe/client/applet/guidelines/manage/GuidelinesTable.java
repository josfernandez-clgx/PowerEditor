package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.Component;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.GridSummary;

final class GuidelinesTable extends AbstractSortableTable<GuidelinesTableModel, GridSummary> {
	private class BooleanIconRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;
		private ImageIcon mOnIcon;
		private ImageIcon mOffIcon;

		public BooleanIconRenderer(String s, String s1) {
			mOnIcon = null;
			mOffIcon = null;
			setHorizontalAlignment(0);
			if (s != null) mOnIcon = ClientUtil.getInstance().makeImageIcon(s);
			if (s1 != null) mOffIcon = ClientUtil.getInstance().makeImageIcon(s1);
		}

		public Component getTableCellRendererComponent(JTable table, Object obj, boolean flag, boolean flag1, int i, int j) {
			Boolean boolean1 = (Boolean) obj;
			setIcon(boolean1.booleanValue() ? ((javax.swing.Icon) (mOnIcon)) : ((javax.swing.Icon) (mOffIcon)));
			if (flag)
				this.setBackground(table.getSelectionBackground());
			else
				this.setBackground(table.getBackground());
			return this;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private JButton editButton;
	private JButton viewButton;
	private JButton setFullContextButton;

	public GuidelinesTable(GuidelinesTableModel model, JButton editButton, JButton viewButton, JButton setFullContextButton) {
		super(model);
		this.editButton = editButton;
		this.viewButton = viewButton;
		this.setFullContextButton = setFullContextButton;
		initTable();
	}

	public void clearGuidelines() {
		getSelectionTableModel().clearDataList();
	}

	public GridSummary getSelectedGridSummary() {
		return getSelectedDataObject();
	}

	public int getSelectedTemplateID() {
		GridSummary summary = getSelectedDataObject();
		return summary == null ? -1 : summary.getTemplateID();
	}

	public void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		TableColumnModel tableColumnModel = getColumnModel();
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tablecolumn = tableColumnModel.getColumn(i);
			if (ClientUtil.getUserInterfaceConfig().getGuideline() != null && UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().getGuideline().isShowTemplateID(), false)) {
				switch (i) {
				case 0: // '\0'
					tablecolumn.setPreferredWidth(120);
					break;

				case 1: // '\0'
					tablecolumn.setPreferredWidth(40);
					break;

				case 2: // '\001'
					tablecolumn.setCellRenderer(new BooleanIconRenderer("image.box.filled", "image.box.empty"));
					break;

				case 3: // '\002'
					BooleanIconRenderer booleaniconrenderer = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer);
					break;

				case 4: // '\003'
					BooleanIconRenderer booleaniconrenderer1 = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer1);
					break;

				case 5: // '\004'
					BooleanIconRenderer booleaniconrenderer2 = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer2);
					break;
				case 6: // '\004'
					BooleanIconRenderer booleaniconrenderer3 = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer3);
					break;
				}
			}
			else {
				switch (i) {
				case 0: // '\0'
					tablecolumn.setPreferredWidth(140);
					break;

				case 1: // '\001'
					tablecolumn.setCellRenderer(new BooleanIconRenderer("image.box.filled", "image.box.empty"));
					break;

				case 2: // '\002'
					BooleanIconRenderer booleaniconrenderer = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer);
					break;

				case 3: // '\003'
					BooleanIconRenderer booleaniconrenderer1 = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer1);
					break;

				case 4: // '\004'
					BooleanIconRenderer booleaniconrenderer2 = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer2);
					break;
				case 5: // '\004'
					BooleanIconRenderer booleaniconrenderer3 = new BooleanIconRenderer("image.box.filled", "image.box.empty");
					tablecolumn.setCellRenderer(booleaniconrenderer3);
					break;
				}
			}
		}
	}

	protected void initTable() {
		setAutoResizeMode(2);
		setAutoCreateColumnsFromModel(false);
		setRowSelectionAllowed(true);
		setSelectionMode(0);
		setShowHorizontalLines(false);
		setRowHeight(16);
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent listselectionevent) {
				if (!listselectionevent.getValueIsAdjusting()) updateGuidelineButtons();
			}

		});
	}

	public void setGuidelines(List<GridSummary> list) {
		getSelectionTableModel().setDataList(list);
	}

	void updateGuidelineButtons() {
		editButton.setEnabled(false);
		viewButton.setEnabled(false);
		setFullContextButton.setEnabled(false);
		//setFullContextButton.setVisible(false);
		int i = getSelectedRow();
		if (i >= 0) {
			GridSummary gridsummary = getDateObjectAt(convertRowIndexToModel(i));
			if (gridsummary != null) {
				if (!gridsummary.isLocked() && gridsummary.isCommon() && gridsummary.isEditAllowed() && !gridsummary.isSubContext()) {
					editButton.setEnabled(true);
				}
				if (gridsummary.isCommon()) {
					viewButton.setEnabled(true);
				}
				if (gridsummary.isSubContext()) {
					setFullContextButton.setEnabled(true);
				}
			}
		}
	}

}
