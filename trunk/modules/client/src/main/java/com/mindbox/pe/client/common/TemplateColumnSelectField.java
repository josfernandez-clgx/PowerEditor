package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
public final class TemplateColumnSelectField extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final class FindColL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			findColumnButton.setEnabled(false);
			try {
				List<String> colVals = new ArrayList<String>();
				for (int i = 1; i <= template.getNumColumns(); i++) {
					AbstractTemplateColumn column = template.getColumn(i);
					if (!forLHS || !column.getColumnDataSpecDigest().isRuleIDType()) {
						boolean colOK = false;
						if (genericDataTypes == null)
							colOK = true;
						else {
							int colGenericDataType = DataTypeCompatibilityValidator.getGenericDataType(column.getColumnDataSpecDigest().getType());
							for (int j = 0; j < genericDataTypes.length; j++)
								if (colGenericDataType == genericDataTypes[j]) {
									colOK = true;
									break;
								}
						}
						// if forMembershipOperator is set, make sure only enum & entity (both single and multi-select) columns appear)
						if (colOK) {
							if (forEntityTestFunctionOperator && column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
								colVals.add(i + " " + template.getColumn(i).getTitle());
							}
							else if (!forMembershipOperator || (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST))) {
								colVals.add(i + " " + template.getColumn(i).getTitle());
							}

						}
					}
				}
				String[] columnValues = (String[]) colVals.toArray(new String[colVals.size()]);
				if (columnValues.length == 0) {
					ClientUtil.getInstance().showWarning("msg.warning.no.matching.columns");
				}
				else {
					String value = (String) JOptionPane.showInputDialog(ClientUtil.getApplet(), null, "Select Column", JOptionPane.PLAIN_MESSAGE, null, columnValues, null);
					if (value != null) {
						int colIndex = Integer.parseInt(value.substring(0, value.indexOf(" ")));
						setValue_internal(colIndex);
					}
				}
			}
			finally {
				findColumnButton.setEnabled(true);
			}
		}
	}

	private final JButton findColumnButton;
	private final JTextField columnField;
	private final GridTemplate template;
	private ColumnReference reference;
	private int[] genericDataTypes = null;
	private boolean forMembershipOperator = false;
	private boolean forEntityTestFunctionOperator = false;
	private final boolean forLHS;

	public TemplateColumnSelectField(GridTemplate template, boolean forLHS) {
		if (template == null) throw new NullPointerException("template cannot be null");
		this.template = template;
		this.forLHS = forLHS;
		findColumnButton = UIFactory.createButton("", "image.btn.find.column", new FindColL(), null);

		columnField = new JTextField();
		columnField.setEditable(false);

		initPanel();
	}

	private void initPanel() {
		setLayout(new BorderLayout(2, 2));
		add(columnField, BorderLayout.CENTER);
		add(findColumnButton, BorderLayout.EAST);
	}

	private void refreshText() {
		columnField.setText(reference == null ? "" : "Column " + reference.getColumnNo() + ": " + template.getColumn(reference.getColumnNo()).getTitle());
	}

	public ColumnReference getValue() {
		return reference;
	}

	public void setValue(ColumnReference ref) {
		this.reference = ref;
		refreshText();
	}

	private void setValue_internal(int col) {
		if (reference == null) {
			reference = RuleElementFactory.getInstance().createColumnReference(col);
		}
		else {
			reference.setColumnNo(col);
		}
		refreshText();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		findColumnButton.setEnabled(enabled);
		columnField.setEnabled(enabled);
	}

	public void setGenericDataTypes(int[] genericDataTypes) {
		this.genericDataTypes = genericDataTypes;
	}

	public void setForMembershipOperator(boolean forMembershipOperator) {
		this.forMembershipOperator = forMembershipOperator;
	}

	public void setForEntityTestFunctionOperator(boolean forEntityTestFunctionOperator) {
		this.forEntityTestFunctionOperator = forEntityTestFunctionOperator;
	}

}