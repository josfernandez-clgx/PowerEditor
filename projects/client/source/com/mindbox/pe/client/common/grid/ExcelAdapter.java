package com.mindbox.pe.client.common.grid;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;

/**
 * Provides copy and paste functionality for grids.
 * @author Geneho Kim
 *
 */
public class ExcelAdapter implements ActionListener {

	private static final String NULL_VALUE = "null";
	private static final String COPY = "Copy";
	private static final String PASTE = "Paste";
	private static final String CUT = "Cut";

	private static final String YES = ClientUtil.getInstance().getLabel("label.yes");
	private static final String NO = ClientUtil.getInstance().getLabel("label.no");
	private static final String YES_TO_ALL = ClientUtil.getInstance().getLabel("label.yes.toAll");
	private static final String OPTIONS[] = new String[] { YES, NO, YES_TO_ALL };
	private static final String[] OPTION_Y_N = new String[] { YES, NO };

	public static final boolean isNullEmptyString(Object obj) {
		if (obj == null) {
			return true;
		}
		else if (NULL_VALUE.equals(obj)) {
			return true;
		}
		else if (obj instanceof String) {
			return ((String) obj).length() == 0;
		}
		else if (obj instanceof IntegerRange) {
			return ((IntegerRange) obj).getLowerValue() == null && ((IntegerRange) obj).getUpperValue() == null;
		}
		else if (obj instanceof FloatRange) {
			return ((FloatRange) obj).getLowerValue() == null && ((FloatRange) obj).getUpperValue() == null;
		}
		else {
			return false;
		}
	}

	private static final CategoryOrEntityValue convertToCategoryOrCellValue(String typeStr, String str, int columnNo)
			throws InputValidationException {
		GenericEntityType entityType = GenericEntityType.forName(typeStr);
		if (entityType == null) throw new IllegalArgumentException("No entity type of '" + typeStr + "' found");
		return convertToCategoryOrCellValue(entityType, str, columnNo);
	}

	/**
	 * Assumes <code>entityType</code> is not <code>null</code>. This is pasting from Excel so
	 * only the string name representation is known.
	 * @param entityType
	 * @param str the name of a category or entity
	 * @return CategoryOrEntityValue that corresponds with <code>str</code>
	 * @throws InputValidationException 
	 */
	private static final CategoryOrEntityValue convertToCategoryOrCellValue(GenericEntityType entityType, String str, int columnNo)
			throws InputValidationException {
		if (UtilBase.isEmpty(str)) return null;

		// TT 2105
		// (1) check for category and entity with the same name
		boolean categoryAndEntityHaveSameName = false;
		GenericCategory[] categories = EntityModelCacheFactory.getInstance().findGenericCategoryByName(entityType, str);
		List<GenericEntity> entities = EntityModelCacheFactory.getInstance().findAllGenericEntities(entityType, str);
		if ((entities != null && !entities.isEmpty()) && (categories != null && categories.length > 0)) {
			categoryAndEntityHaveSameName = true;
		}

		// (2) check for entity with the same name
		if (!categoryAndEntityHaveSameName && entities != null && !entities.isEmpty()) {
			if (entities.size() > 1) {
				ClientUtil.getInstance().showWarning(
						"msg.warning.paste.entity.name.duplicate",
						new Object[] { entityType.getDisplayName(), str, columnNo });
			}
			return new CategoryOrEntityValue(entities.get(0));
		}
		else {
			// if there are category and entity with the same name
			EntityTypeDefinition typeDef = ClientUtil.getEntityConfiguration().findEntityTypeDefinition(entityType);
			if (categoryAndEntityHaveSameName) {
				displayWarningPopup("msg.warning.paste.category.entity.duplicate", typeDef, str, columnNo);

				// treat as entity
				return new CategoryOrEntityValue(entities.get(0));
			}
			else if (categories != null && categories.length > 0) {
				// if there are multiple categories found and the entity type supports non-unique category names
				// display an error message. Otherwise allow paste.
				if (categories.length > 1) {
					displayWarningPopup("msg.warning.paste.categoryname.not.qualified", typeDef, str, columnNo);
				}

				return new CategoryOrEntityValue(entityType, false, categories[0].getID());
			}
		}

		throw new IllegalArgumentException(ClientUtil.getInstance().getMessage("msg.warning.no.category.entity.name", new Object[] { str }));
	}

	// TT 2105
	private static final void displayWarningPopup(String messageTag, EntityTypeDefinition typeDef, String cellValue, int columnNo)
			throws InputValidationException {

		String message = ClientUtil.getInstance().getMessage(messageTag, new Object[] { typeDef.getName(), cellValue, columnNo });
		int i1 = JOptionPane.showOptionDialog(
				ClientUtil.getApplet(),
				message,
				ClientUtil.getInstance().getLabel("d.title.error"),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				OPTION_Y_N,
				OPTION_Y_N[0]);
		if (i1 == 1) {
			throw new InputValidationException(message);
		}
	}

	private static final CategoryOrEntityValues convertToCategoryOrCellValues(String typeStr, String str, int columnNo)
			throws InputValidationException {
		GenericEntityType entityType = GenericEntityType.forName(typeStr);
		if (entityType == null) throw new IllegalArgumentException("No entity type of '" + entityType + "' found");
		if (UtilBase.isEmpty(str)) return null;
		EnumValues<String> enumValues = EnumValues.parseValue(str, true, null);
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.setSelectionExclusion(enumValues.isSelectionExclusion());
		for (int i = 0; i < enumValues.size(); i++) {
			try {
				CategoryOrEntityValue value = convertToCategoryOrCellValue(entityType, enumValues.get(i), columnNo);
				if (value != null) {
					values.add(value);
				}
			}
			catch (InputValidationException ex) {
				// skip pasting current value for a column
			}
		}
		return values;
	}

	public void setJTable(AbstractGridTable<?> gridtable) {
		gridTable = gridtable;
	}

	public ExcelAdapter(AbstractGridTable<?> gridtable) {
		gridTable = gridtable;
		gridTable.registerKeyboardAction(this, COPY, KeyStroke.getKeyStroke(KeyEvent.VK_C, 2, false), 0);
		gridTable.registerKeyboardAction(this, PASTE, KeyStroke.getKeyStroke(KeyEvent.VK_V, 2, false), 0);
		gridTable.registerKeyboardAction(this, CUT, KeyStroke.getKeyStroke(KeyEvent.VK_X, 2, false), 0);
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Pasting from clipboard into the PE.
	 */
	public synchronized void paste() {
		boolean showErrors = true;
		int selectedRowIndex = gridTable.getSelectedRows()[0];
		int selectedColumnIndex = gridTable.getSelectedColumn();
		if (selectedColumnIndex < 0) selectedColumnIndex = 0;
		try {
			String clipboardContent = (String) systemClipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
			StringTokenizer rowtokenizer = new StringTokenizer(clipboardContent, "\n");
			for (int pasteRowIndex = selectedRowIndex; rowtokenizer.hasMoreTokens(); pasteRowIndex++) {
				String rowString = rowtokenizer.nextToken();

				if (pasteRowIndex == gridTable.getRowCount()) {
					if (!gridTable.addRow(-1)) { // append a new row
						return; // table cannot add more rows to accomodate pasting
					}
				}
				String cellValueStr = null;
				String[] strs = rowString.split("\\t");
				int columnIndex = selectedColumnIndex;
				for (int i = 0; i < strs.length; i++) {
					cellValueStr = strs[i];
					if (cellValueStr.equals(NULL_VALUE)) {
						cellValueStr = "";
					}
					if (gridTable.getTemplateColumn(columnIndex + 1).getColumnDataSpecDigest().isRuleIDType()) {
						++columnIndex;
					}
					if (columnIndex < gridTable.getColumnCount()) {
						try {
							boolean writeInfo = gridTable.getTemplateColumn(columnIndex + 1).getColumnDataSpecDigest().getType().equals(
									ColumnDataSpecDigest.TYPE_INTEGER_RANGE);

							Object cellValueToUse;
							// GKIM: 2008-05-09
							// IF entity column, process for name/path resolution
							// Otherwise, use Template Column's convertToCellValue method which is used by Grid Loader
							// (part of TT 2186)
							if (gridTable.getTemplateColumn(columnIndex + 1).getColumnDataSpecDigest().getType().equals(
									ColumnDataSpecDigest.TYPE_ENTITY)) {
								if (gridTable.getTemplateColumn(columnIndex + 1).getColumnDataSpecDigest().isMultiSelectAllowed()) {
									cellValueToUse = convertToCategoryOrCellValues(
											gridTable.getTemplateColumn(columnIndex + 1).getColumnDataSpecDigest().getEntityType(),
											cellValueStr,
											columnIndex + 1);
								}
								else {
									cellValueToUse = convertToCategoryOrCellValue(
											gridTable.getTemplateColumn(columnIndex + 1).getColumnDataSpecDigest().getEntityType(),
											cellValueStr,
											columnIndex + 1);
								}
							}
							else {
								cellValueToUse = gridTable.getTemplateColumn(columnIndex + 1).convertToCellValue(
										cellValueStr,
										DomainModel.getInstance(),
										ClientUtil.getEnumerationSourceProxy());
								
								if (writeInfo) {
									ClientUtil.getLogger().info(
											String.format(
													"[paste] converted: cellValue [%s] to [%s] for row=%d,col=%d",
													cellValueStr,
													cellValueToUse,
													pasteRowIndex,
													columnIndex));
								}
							}
							gridTable.setAndValidateValueAt(cellValueToUse, pasteRowIndex, columnIndex);
						}
						// TT 2105
						catch (InputValidationException ex) {
							ClientUtil.getLogger().warn(
									"Error while pasting " + cellValueStr + " into " + pasteRowIndex + "," + columnIndex,
									ex);
							// skip pasting current column							
						}
						catch (Exception ex) {
							ClientUtil.getLogger().warn(
									"Error while pasting " + cellValueStr + " into " + pasteRowIndex + "," + columnIndex,
									ex);
							if (showErrors) {
								Object aobj[] = { cellValueStr, new Integer(columnIndex + 1) };
								String errMessage = ClientUtil.getInstance().getMessage("InvalidPasteValueMsg", aobj);
								int i1 = JOptionPane.showOptionDialog(
										ClientUtil.getApplet(),
										errMessage,
										ClientUtil.getInstance().getLabel("d.title.error"),
										JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.ERROR_MESSAGE,
										null,
										OPTIONS,
										OPTIONS[0]);
								switch (i1) {
								case 1:
									return;

								default:
									showErrors = false;
									break;

								case 0:
									break;
								}
							}
						}
					} // if
					++columnIndex;
				} // for 
			}
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	public void actionPerformed(ActionEvent actionevent) {
		if (actionevent.getActionCommand().compareTo(COPY) == 0) copy();
		if (actionevent.getActionCommand().compareTo(PASTE) == 0) paste();
		if (actionevent.getActionCommand().compareTo(CUT) == 0) cut();
	}

	public synchronized void cut() {
		int selectedRowCount = gridTable.getSelectedRowCount();
		int[] selectedRows = gridTable.getSelectedRows();
		if (validateSelectedRowsAndColumns(selectedRowCount, selectedRows)) {
			int[] selectedColumns = gridTable.getSelectedColumns();
			copy_internal(selectedRowCount, selectedRows, selectedColumns);
			// only remove the selected cells, not the entire row
			if (selectedColumns == null || selectedColumns.length == 0) {
				gridTable.removeRow();
			}
			else {
				for (int i = 0; i < selectedColumns.length; i++) {
					gridTable.setValueAt(null, selectedRows[0], selectedColumns[i]);
				}
			}
		}
	}

	public synchronized void copy() {
		int selectedRowCount = gridTable.getSelectedRowCount();
		int[] selectedRows = gridTable.getSelectedRows();
		if (validateSelectedRowsAndColumns(selectedRowCount, selectedRows)) {
			copy_internal(selectedRowCount, selectedRows, gridTable.getSelectedColumns());
		}
	}

	/**
	 * Checks if the specified selected rows and columns are valid.
	 * @param selectedRows
	 * @return <code>true</code> if operation should continue; <code>false</code>, otherwise
	 */
	private boolean validateSelectedRowsAndColumns(int selectedRowCount, int[] selectedRows) {
		if (selectedRows == null || selectedRows.length == 0) return false;

		if (selectedRowCount - 1 != selectedRows[selectedRows.length - 1] - selectedRows[0] || selectedRowCount != selectedRows.length) {
			String s = ClientUtil.getInstance().getMessage("InvalidCopySelectionMsg");
			JOptionPane.showMessageDialog(null, s, s, 0);
			return false;
		}
		return true;
	}

	private void copy_internal(int selectedRowCount, int[] selectedRows, int[] selectedColumns) {
		StringBuffer stringbuffer = new StringBuffer();
		int columnCount = gridTable.getColumnCount();
		if (selectedColumns == null || selectedColumns.length == 0) {
			for (int k = 0; k < selectedRowCount; k++) {
				if (k > 0) stringbuffer.append("\n");
				for (int col = 0; col < columnCount; col++) {
					if (!gridTable.getTemplateColumn(col + 1).getColumnDataSpecDigest().isRuleIDType()) {
						Object value = gridTable.getValueAt(selectedRows[k], col);
						stringbuffer.append(getClipboardValue(value));
						if (col < columnCount - 1) stringbuffer.append("\t");
					}
				}
			}
		}
		else {
			for (int i = 0; i < selectedColumns.length; i++) {
				if (i > 0) stringbuffer.append("\t");
				if (!gridTable.getTemplateColumn(selectedColumns[i] + 1).getColumnDataSpecDigest().isRuleIDType()) {
					Object value = gridTable.getValueAt(selectedRows[0], selectedColumns[i]);
					stringbuffer.append(getClipboardValue(value));
				}
			}
		}
		StringSelection selectionString = new StringSelection(stringbuffer.toString());
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		systemClipboard.setContents(selectionString, selectionString);
	}

	private String getClipboardValue(Object value) {
		if (isNullEmptyString(value)) {
			return NULL_VALUE;
		}
		else if (value instanceof Date) {
			return ClientUtil.inputDateFormat.format((Date) value);
		}
		else if (value instanceof CategoryOrEntityValue) {
			return getValueStringForClipboard((CategoryOrEntityValue) value);
		}
		else if (value instanceof CategoryOrEntityValues) {
			return getValueStringForClipboard((CategoryOrEntityValues) value);
		}
		else if (value instanceof EnumValues) {
			return MultiSelectEnumCellRenderer.toDisplayString((EnumValues<?>) value);
		}
		else if (value instanceof EnumValue) {
			return ((EnumValue) value).getDisplayLabel();
		}
		else {
			return value.toString();
		}
	}

	private static String getValueStringForClipboard(CategoryOrEntityValue value) {
		// handle entity type
		if (value.isForEntity()) {
			return CategoryEntitySingleSelectCellRenderer.getDisplayValue(value);
		}
		else {
			//TT 2100
			// handle entity type
			GenericCategory category = EntityModelCacheFactory.getInstance().getGenericCategory(value.getEntityType(), value.getId());
			// if this category has duplicated name with either another category or entity for the same type
			if (EntityModelCacheFactory.getInstance().hasDuplicatedCategoryOrEntityName(true, value.getEntityType(), category.getName())) {
				return EntityModelCacheFactory.getInstance().getMostRecentFullyQualifiedCategoryName(category);
			}
			else {
				return CategoryEntitySingleSelectCellRenderer.getDisplayValue(value);
			}
		}
	}

	private static String getValueStringForClipboard(CategoryOrEntityValues values) {
		StringBuffer buff = new StringBuffer();
		if (values.isSelectionExclusion()) {
			buff.append(EnumValues.OLD_EXCLUSION_PREFIX);
		}

		for (Iterator<CategoryOrEntityValue> iter = values.iterator(); iter.hasNext();) {
			CategoryOrEntityValue element = iter.next();
			// handle entity type
			if (element.isForEntity()) {
				buff.append(CategoryEntitySingleSelectCellRenderer.getDisplayValue(element));
			}
			else {
				//TT 2100
				// handle category type
				GenericCategory category = EntityModelCacheFactory.getInstance().getGenericCategory(
						element.getEntityType(),
						element.getId());
				// if this category has duplicated name with either another category or entity for the same type
				if (EntityModelCacheFactory.getInstance().hasDuplicatedCategoryOrEntityName(
						true,
						element.getEntityType(),
						category.getName())) {
					buff.append(EntityModelCacheFactory.getInstance().getMostRecentFullyQualifiedCategoryName(category));
				}
				else {
					buff.append(CategoryEntitySingleSelectCellRenderer.getDisplayValue(element));
				}
			}
			if (iter.hasNext()) buff.append(',');
		}
		return buff.toString();
	}

	private Clipboard systemClipboard;
	private AbstractGridTable<?> gridTable;

}
