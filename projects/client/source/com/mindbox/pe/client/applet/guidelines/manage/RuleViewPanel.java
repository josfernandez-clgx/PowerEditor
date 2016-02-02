/*
 * Created on 2004. 11. 1.
 *
 */
package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.rule.CellValueChangeListener;
import com.mindbox.pe.client.applet.template.rule.PowerEditPanel;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.grid.AbstractGridTableModel;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.rule.RuleDefinition;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
class RuleViewPanel extends PanelBase {

	public class RowSpinnerL implements ChangeListener {

		public void stateChanged(ChangeEvent arg0) {
			try {
				showRow_internal(getSelectedRow());
			}
			catch (InvalidDataException e) {
				e.printStackTrace();
			}
		}
	}

	private class RuleForColumnComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				selectRuleDef();
				showRow_internal(getSelectedRow());
			}
			catch (InvalidDataException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class RuleForColumnRadioL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				selectRuleDef();
				showRow_internal(getSelectedRow());
			}
			catch (InvalidDataException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class RuleForTemplateRadioL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				selectRuleDef();
				showRow_internal(getSelectedRow());
			}
			catch (InvalidDataException ex) {
				ex.printStackTrace();
			}
		}
	}


	private final JSpinner rowSpinner;
	private final PowerEditPanel pePanel;
	private GridTemplate template;
	private RuleDefinition ruleDef;
	private AbstractGridTableModel gridTableModel;
	private final SpinnerNumberModel spinnerModel;
	private final CellValueChangeListener cellValueChangeListener;
	private final RowSpinnerL rowSpinnerChangeListener;

	private final JRadioButton ruleForTemplateRadio, ruleForColumnRadio;
	private final JComboBox columnCombo;


	public RuleViewPanel(CellValueChangeListener cellValueChangeListener) {
		pePanel = new PowerEditPanel(true, null);
		spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
		rowSpinner = new JSpinner(spinnerModel);

		ruleForTemplateRadio = new JRadioButton("Template");
		ruleForColumnRadio = new JRadioButton("Column: ");
		columnCombo = new JComboBox(new String[] { " 1 - Column Name"});


		ButtonGroup ruleForGroup = new ButtonGroup();
		ruleForGroup.add(ruleForTemplateRadio);
		ruleForGroup.add(ruleForColumnRadio);

		initPanel();

		rowSpinnerChangeListener = new RowSpinnerL();
		rowSpinner.addChangeListener(rowSpinnerChangeListener);
		pePanel.setEnabled(true);
		this.cellValueChangeListener = cellValueChangeListener;
		pePanel.addCellValueChangeListener(cellValueChangeListener);

		ruleForTemplateRadio.addActionListener(new RuleForTemplateRadioL());
		ruleForColumnRadio.addActionListener(new RuleForColumnRadioL());
		columnCombo.addActionListener(new RuleForColumnComboL());

	}

	private void initPanel() {
		JPanel rowPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		rowPanel.add(new JLabel("Row:"));
		rowPanel.add(rowSpinner);

		rowPanel.add(new JLabel("   Rule and Message Scope: "));
		rowPanel.add(ruleForTemplateRadio);
		rowPanel.add(ruleForColumnRadio);
		rowPanel.add(columnCombo);


		setLayout(new BorderLayout());
		add(rowPanel, BorderLayout.NORTH);
		add(pePanel, BorderLayout.CENTER);
	}

	synchronized int getSelectedRow() {
		return ((Number) rowSpinner.getValue()).intValue();
	}

	synchronized void setGrid(GridTemplate template, AbstractGridTableModel model) throws InvalidDataException {
		this.template = template;
		this.gridTableModel = model;
		this.ruleDef = findInitialRuleDef();

		initRuleScopeWidgets();

		if (template != null && gridTableModel != null) {
			spinnerModel.setMaximum(new Integer(gridTableModel.getRowCount()));
			rowSpinner.removeChangeListener(rowSpinnerChangeListener);
			try {
				rowSpinner.setValue(new Integer(1));
				showRow_internal(1);
			}
			finally {
				rowSpinner.addChangeListener(rowSpinnerChangeListener);
			}
		}
		else {
			clearRule();
		}
	}

	synchronized void showRow(int row) throws InvalidDataException {
		if (row > 0) {
			rowSpinner.removeChangeListener(rowSpinnerChangeListener);
			try {
				rowSpinner.setValue(new Integer(row));
				showRow_internal(row);
			}
			finally {
				rowSpinner.addChangeListener(rowSpinnerChangeListener);
			}
		}
	}

	synchronized void refresh(int row) throws InvalidDataException {
		if (row > 0 && getSelectedRow() == row) {
			refresh_internal(row);
		}
	}

	@SuppressWarnings("unchecked")
	private void refresh_internal(int row) throws InvalidDataException {
		if (row > 0 && gridTableModel != null && gridTableModel.getRowCount() >= row) {
			pePanel.removeCellValueChangeListener(cellValueChangeListener);
			try {
				pePanel.setCellValues((List<Object>) gridTableModel.getDataVector().get(row - 1));
			}
			finally {
				pePanel.addCellValueChangeListener(cellValueChangeListener);
			}
		}
	}

	synchronized void rowsAdded() throws InvalidDataException {
		pePanel.removeCellValueChangeListener(cellValueChangeListener);
		try {
			spinnerModel.setMaximum(new Integer(gridTableModel.getRowCount()));
			showRow_internal((getSelectedRow() > 0 ? getSelectedRow() : 1));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			pePanel.addCellValueChangeListener(cellValueChangeListener);
		}
	}

	synchronized void rowsDeleted(int numRows) throws InvalidDataException {
		pePanel.removeCellValueChangeListener(cellValueChangeListener);
		rowSpinner.removeChangeListener(rowSpinnerChangeListener);
		try {
			int prevRow = getSelectedRow();
			int difference = ((Number) spinnerModel.getMaximum()).intValue() - numRows;
			spinnerModel.setMaximum(new Integer(Math.max(1, difference)));
			if (difference <= 0) {
				clearRule();
			}
			else if (prevRow <= difference) {
				rowSpinner.setValue(new Integer(prevRow));
				refresh_internal(prevRow);
			}
			else {
				rowSpinner.setValue(spinnerModel.getMaximum());
				refresh_internal(getSelectedRow());
			}
		}
		finally {
			pePanel.addCellValueChangeListener(cellValueChangeListener);
			rowSpinner.addChangeListener(rowSpinnerChangeListener);
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized void showRow_internal(int row) throws InvalidDataException {
		if (row < 1) return;
		if (template != null && gridTableModel != null) {
			pePanel.removeCellValueChangeListener(cellValueChangeListener);
			try {
				pePanel.setRule(template, ruleDef);
				if (gridTableModel.getRowCount() >= row) {
					pePanel.setCellValues((List<Object>) gridTableModel.getDataVector().get(row - 1));
				}
				else {
					pePanel.clearFields();
				}
			}
			finally {
				pePanel.addCellValueChangeListener(cellValueChangeListener);
			}
		}
	}

	private void clearRule() {
		//System.out.println(">>> RuleViewPanel.clearRule");
		pePanel.clearFields();
	}

	public void setEnabled(boolean enabled) {
		//super.setEnabled(enabled);
		pePanel.setEnabled(enabled);
	}

	private void initRuleScopeWidgets() {

		columnCombo.removeAllItems();
		boolean hasColumnRules = false;
		List<GridTemplateColumn> columnList = template.getColumns();
		if (columnList.size() > 0) {
			for (Iterator<GridTemplateColumn> iter = columnList.iterator(); iter.hasNext();) {
				GridTemplateColumn column = iter.next();
				if (column.getRuleDefinition() != null) {
					columnCombo.addItem(column.getID() + " " + column.getTitle());
					hasColumnRules = true;
				}
			}
		}

		columnCombo.setEnabled(true);
		ruleForColumnRadio.setEnabled(true);

		ruleForTemplateRadio.setSelected(true);


		if (template.getRuleDefinition() != null)
			ruleForTemplateRadio.setEnabled(true);
		else {
			ruleForTemplateRadio.setEnabled(false);
			ruleForTemplateRadio.setSelected(false);
		}
		if (hasColumnRules) {
			ruleForColumnRadio.setEnabled(true);
			columnCombo.setEnabled(true);
		}
		else {
			ruleForColumnRadio.setEnabled(false);
			columnCombo.setEnabled(false);
			ruleForColumnRadio.setSelected(false);
		}

	}

	private RuleDefinition findInitialRuleDef() {
		if (template.getRuleDefinition() != null) return template.getRuleDefinition();
		GridTemplateColumn selectedColumn = getSelectedColumn();
		if (selectedColumn != null) return selectedColumn.getRuleDefinition();
		return null;
	}

	private GridTemplateColumn getSelectedColumn() {
		String str = (String) columnCombo.getSelectedItem();
		if (str == null || str.length() == 0) {
			return null;
		}
		else {
			try {
				int colIndex = Integer.parseInt(str.substring(0, str.indexOf(" ")));
				return (GridTemplateColumn) template.getColumn(colIndex);
			}
			catch (NumberFormatException ex) {
				return null;
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
				return null;
			}
		}
	}

	private void selectRuleDef() {
		if (ruleForTemplateRadio.isSelected())
			ruleDef = template.getRuleDefinition();
		else if (ruleForColumnRadio.isSelected()) {
			GridTemplateColumn col = getSelectedColumn();
			if (col != null) {
				ruleDef = getSelectedColumn().getRuleDefinition();
			}
			else {
				ruleDef = null;
			}
		}
	}


}