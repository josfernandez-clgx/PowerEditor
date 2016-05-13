package com.mindbox.pe.client.common.rowheader;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

class RowHeaderRenderer extends DefaultTableCellRenderer implements ListCellRenderer<Object> {
	private static final long serialVersionUID = -3951228734910107454L;

	protected Border noFocusBorder;
	protected Border focusBorder;

	public RowHeaderRenderer() {
		setOpaque(true);
		setBorder(noFocusBorder);
		setAlignmentX(0.5F);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> jlist, Object obj, int i, boolean flag, boolean flag1) {
		if (jlist != null) {
			if (flag) {
				setBackground(jlist.getSelectionBackground());
				setForeground(jlist.getSelectionForeground());
			}
			else {
				setBackground(jlist.getBackground());
				setForeground(jlist.getForeground());
			}
			setFont(jlist.getFont());
			setEnabled(jlist.isEnabled());
		}
		else {
			setBackground(UIManager.getColor("TableHeader.background"));
			setForeground(UIManager.getColor("TableHeader.foreground"));
			setFont(UIManager.getFont("TableHeader.font"));
			setEnabled(true);
		}
		if (flag1)
			setBorder(focusBorder);
		else
			setBorder(noFocusBorder);
		setValue(obj);
		return this;
	}

	@Override
	public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
		if (jtable != null) {
			if (flag) {
				setBackground(jtable.getSelectionBackground());
				setForeground(jtable.getSelectionForeground());
			}
			else {
				setBackground(jtable.getBackground());
				setForeground(jtable.getForeground());
			}
			setFont(jtable.getFont());
			setEnabled(jtable.isEnabled());
		}
		else {
			setBackground(UIManager.getColor("TableHeader.background"));
			setForeground(UIManager.getColor("TableHeader.foreground"));
			setFont(UIManager.getFont("TableHeader.font"));
			setEnabled(true);
		}
		if (flag1)
			setBorder(focusBorder);
		else
			setBorder(noFocusBorder);
		setValue(obj);
		return this;
	}

	@Override
	public void updateUI() {
		super.updateUI();
		Border border = UIManager.getBorder("TableHeader.cellBorder");
		Border border1 = UIManager.getBorder("Table.focusCellHighlightBorder");
		focusBorder = new javax.swing.plaf.BorderUIResource.CompoundBorderUIResource(border, border1);
		Insets insets = border1.getBorderInsets(this);
		noFocusBorder = new javax.swing.plaf.BorderUIResource.CompoundBorderUIResource(
				border,
				BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
	}
}
