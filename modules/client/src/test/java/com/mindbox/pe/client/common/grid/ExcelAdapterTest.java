package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestAssert.assertContains;
import static com.mindbox.pe.client.ClientTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.attachEnumValue;
import static com.mindbox.pe.client.ClientTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.client.ClientTestObjectMother.attachGridTemplateColumns;
import static com.mindbox.pe.client.ClientTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValue;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValues;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValuesAsList;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static com.mindbox.pe.client.ClientTestObjectMother.createGridTemplate;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;

public class ExcelAdapterTest extends AbstractClientTestBase {


	protected void assertClipboardEquals(String expected) throws Exception {
		String clipboardContent = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(excelAdapter).getTransferData(DataFlavor.stringFlavor);
		assertEquals(expected, clipboardContent);
	}

	private GridTable gridTable;
	private ExcelAdapter excelAdapter;


	@Test
	public void testCopyForRuleIDColumnDoesNotCopyRuleIDColumns() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals("cell-1,2");
	}

	@Test
	public void testPasteForIntegerRange_HappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		gridTable.setTemplate(gridTable.getTemplate());
		gridTable.addRow(0);

		String newValue1 = "[640-659]";
		String newValue2 = "[640-659]";
		String newValue3 = "[660-679]";
		String newValue4 = "[640-659]";
		String newValue5 = "[700-719]";
		setClipboard(newValue1 + "\t\n" + newValue2 + "\t\n" + newValue3 + "\t\n" + newValue4 + "\t\n" + newValue5);

		gridTable.clearSelection();
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.paste();

		assertEquals(newValue1, gridTable.getValueAt(0, 0).toString());
		assertEquals(newValue2, gridTable.getValueAt(1, 0).toString());
		assertEquals(newValue3, gridTable.getValueAt(2, 0).toString());
		assertEquals(newValue4, gridTable.getValueAt(3, 0).toString());
		assertEquals(newValue5, gridTable.getValueAt(4, 0).toString());
	}

	@Test
	public void testPasteForRuleIDColumnAsFirstColumnHappyCaseWithRowSelection() throws Exception {
		attachGridTemplateColumn(gridTable.getTemplate(), 3);
		attachColumnDataSpecDigest(gridTable.getTemplate().getColumn(3));
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		gridTable.setTemplate(gridTable.getTemplate());
		Long ruleID = new Long(createInt());
		String newValue1 = "newvalue-" + createInt();
		String newValue2 = "newvalue-" + createInt();

		setClipboard(newValue1 + "\t" + newValue2);
		gridTable.addRow(0);
		gridTable.setValueAt(ruleID, 0, 0);
		gridTable.setValueAt("cell-1,1", 0, 1);
		gridTable.setValueAt("cell-1,2", 0, 2);
		gridTable.clearSelection();
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals(ruleID, gridTable.getValueAt(0, 0));
		assertEquals(newValue1, gridTable.getValueAt(0, 1));
		assertEquals(newValue2, gridTable.getValueAt(0, 2));
	}

	@Test
	public void testPasteForRuleIDColumnAsFirstColumnHappyCaseWithFirstCellSelection() throws Exception {
		attachGridTemplateColumn(gridTable.getTemplate(), 3);
		attachColumnDataSpecDigest(gridTable.getTemplate().getColumn(3));
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		gridTable.setTemplate(gridTable.getTemplate());
		Long ruleID = new Long(createInt());
		String newValue1 = "newvalue-" + createInt();
		String newValue2 = "newvalue-" + createInt();

		setClipboard(newValue1 + "\t" + newValue2);
		gridTable.addRow(0);
		gridTable.setValueAt(ruleID, 0, 0);
		gridTable.setValueAt("cell-1,1", 0, 1);
		gridTable.setValueAt("cell-1,2", 0, 2);
		gridTable.clearSelection();
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals(ruleID, gridTable.getValueAt(0, 0));
		assertEquals(newValue1, gridTable.getValueAt(0, 1));
		assertEquals(newValue2, gridTable.getValueAt(0, 2));
	}

	@Test
	public void testPasteForRuleIDColumnAsFirstColumnHappyCaseWithSecondCellSelection() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long ruleID = new Long(createInt());
		String newValue = "newvalue-" + createInt();

		setClipboard(newValue);
		gridTable.setValueAt(ruleID, 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(1, 1);
		excelAdapter.paste();
		assertEquals(ruleID, gridTable.getValueAt(0, 0));
		assertEquals(newValue, gridTable.getValueAt(0, 1));
	}

	@Test
	public void testPasteForRuleIDColumnAsMiddleColumnHappyCaseWithRowSelection() throws Exception {
		attachGridTemplateColumn(gridTable.getTemplate(), 3);
		attachColumnDataSpecDigest(gridTable.getTemplate().getColumn(3));
		gridTable.getTemplate().getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		gridTable.setTemplate(gridTable.getTemplate());
		Long ruleID = new Long(createInt());
		String newValue1 = "newvalue-" + createInt();
		String newValue2 = "newvalue-" + createInt();

		setClipboard(newValue1 + "\t" + newValue2);
		gridTable.addRow(0);
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt(ruleID, 0, 1);
		gridTable.setValueAt("cell-1,2", 0, 2);
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals(newValue1, gridTable.getValueAt(0, 0));
		assertEquals(ruleID, gridTable.getValueAt(0, 1));
		assertEquals(newValue2, gridTable.getValueAt(0, 2));
	}

	@Test
	public void testPasteForRuleIDColumnAsMiddleColumnHappyCaseWithCellSelection() throws Exception {
		attachGridTemplateColumn(gridTable.getTemplate(), 3);
		attachColumnDataSpecDigest(gridTable.getTemplate().getColumn(3));
		gridTable.getTemplate().getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		gridTable.setTemplate(gridTable.getTemplate());
		Long ruleID = new Long(createInt());
		String newValue1 = "newvalue-" + createInt();
		String newValue2 = "newvalue-" + createInt();

		setClipboard(newValue1 + "\t" + newValue2);
		gridTable.addRow(0);
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt(ruleID, 0, 1);
		gridTable.setValueAt("cell-1,2", 0, 2);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals(newValue1, gridTable.getValueAt(0, 0));
		assertEquals(ruleID, gridTable.getValueAt(0, 1));
		assertEquals(newValue2, gridTable.getValueAt(0, 2));
	}

	@Test
	public void testPasteForRuleIDColumnAsLastColumnHappyCaseWithRowSelection() throws Exception {
		gridTable.getTemplate().getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long ruleID = new Long(createInt());
		String newValue = "newvalue-" + createInt();

		setClipboard(newValue);
		gridTable.setValueAt("cell-1,2", 0, 0);
		gridTable.setValueAt(ruleID, 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals(ruleID, gridTable.getValueAt(0, 1));
		assertEquals(newValue, gridTable.getValueAt(0, 0));
	}

	@Test
	public void testPasteForRuleIDColumnAsLastColumnHappyCaseWithCellSelection() throws Exception {
		gridTable.getTemplate().getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long ruleID = new Long(createInt());
		String newValue = "newvalue-" + createInt();

		setClipboard(newValue);
		gridTable.setValueAt("cell-1,2", 0, 0);
		gridTable.setValueAt(ruleID, 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals(ruleID, gridTable.getValueAt(0, 1));
		assertEquals(newValue, gridTable.getValueAt(0, 0));
	}

	@Test
	public void testPasteForDynamicStringColumnSetsDynamicStringObject() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING);
		String str = "dynamic string text " + createInt();
		setClipboard(str);

		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		DynamicStringValue dsValue = (DynamicStringValue) gridTable.getValueAt(0, 0);
		assertEquals(str, dsValue.toString());
	}

	@Test
	public void testPasteWithSingleValueHappyCase() throws Exception {
		setClipboard("new-111");
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals("new-111", gridTable.getValueAt(0, 0));
	}

	@Test
	public void testPasteWithMultipleValuesHappyCase() throws Exception {
		setClipboard("new-11\nnew-21");
		gridTable.addRow(0);
		gridTable.addRow(0);
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setValueAt("cell-2,1", 1, 0);
		gridTable.setValueAt("cell-2,2", 1, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(1, 1);
		excelAdapter.paste();
		assertEquals("new-11", gridTable.getValueAt(0, 1));
		assertEquals("new-21", gridTable.getValueAt(1, 1));
	}

	@Test
	public void testPasteWithSingleSelectEnumValueHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setEnumSourceType(EnumSourceType.COLUMN);
		List<EnumValue> enumValues = createEnumValuesAsList(3);
		for (EnumValue ev : enumValues) {
			gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().addColumnEnumValue(ev.getDisplayLabel());
		}
		setClipboard(enumValues.get(0).getDisplayLabel());

		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		EnumValue enumValue = (EnumValue) gridTable.getValueAt(0, 0);
		assertEquals(enumValues.get(0).getDisplayLabel(), enumValue.getDeployValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPasteWithMultiSelectEnumValuesHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		List<EnumValue> enumValueList = createEnumValuesAsList(3);
		for (EnumValue ev : enumValueList) {
			gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().addColumnEnumValue(ev.getDisplayLabel());
		}

		EnumValues<EnumValue> enumValues = new EnumValues<EnumValue>();
		enumValues.add(enumValueList.get(0));
		enumValues.add(enumValueList.get(1));
		setClipboard(MultiSelectEnumCellRenderer.toDisplayString(enumValues));

		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();

		EnumValues<EnumValue> evFromTable = (EnumValues<EnumValue>) gridTable.getValueAt(0, 0);
		assertEquals(2, evFromTable.size());
		assertFalse(evFromTable.isSelectionExclusion());

		// try with exclusion set to true
		enumValues = new EnumValues<EnumValue>();
		enumValues.setSelectionExclusion(true);
		enumValues.add(enumValueList.get(2));
		setClipboard(MultiSelectEnumCellRenderer.toDisplayString(enumValues));

		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();

		evFromTable = (EnumValues<EnumValue>) gridTable.getValueAt(0, 0);
		assertEquals(1, evFromTable.size());
		assertTrue(evFromTable.isSelectionExclusion());
		assertEquals(enumValueList.get(2).getDisplayLabel(), evFromTable.get(0).getDisplayLabel());
	}

	@Test
	public void testPasteWithExclusionOverwriteNonExclusionWithSameEnumValue() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		final List<EnumValue> enumValueList = createEnumValuesAsList(3);
		for (final EnumValue enumValue : enumValueList) {
			gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().addColumnEnumValue(enumValue.getDisplayLabel());
		}
		EnumValues<EnumValue> enumValues = new EnumValues<EnumValue>();
		enumValues.setSelectionExclusion(false);
		enumValues.add(enumValueList.get(0));
		gridTable.setValueAt(enumValues, 0, 0);

		enumValues = new EnumValues<EnumValue>();
		enumValues.setSelectionExclusion(true);
		enumValues.add(enumValueList.get(0));
		setClipboard(MultiSelectEnumCellRenderer.toDisplayString(enumValues));

		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertTrue(((EnumValues<?>) gridTable.getValueAt(0, 0)).isSelectionExclusion());
	}

	@Test
	public void testPasteWithSingleSelectEntityListForEntityHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, false, false));
		GenericEntity entity = createGenericEntity(entityType1);
		EntityModelCacheFactory.getInstance().add(entity);
		setClipboard(entity.getName());
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertTrue(gridTable.getValueAt(0, 0) instanceof CategoryOrEntityValue);
		assertTrue(((CategoryOrEntityValue) gridTable.getValueAt(0, 0)).isForEntity());
		assertEquals(entity.getId(), ((CategoryOrEntityValue) gridTable.getValueAt(0, 0)).getId());
		EntityModelCacheFactory.getInstance().remove(entity);
	}

	@Test
	public void testPasteWithSingleSelectEntityListForCategoryHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), false, true, false));
		GenericCategory category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		setClipboard(category.getName());
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertTrue(gridTable.getValueAt(0, 0) instanceof CategoryOrEntityValue);
		assertFalse(((CategoryOrEntityValue) gridTable.getValueAt(0, 0)).isForEntity());
		assertEquals(category.getId(), ((CategoryOrEntityValue) gridTable.getValueAt(0, 0)).getId());
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
	}

	@Test
	public void testPasteWithSingleSelectForFullyQualifiedCategoryPathHappyCase() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType1);
		rootCategory.setName("root");
		EntityModelCacheFactory.getInstance().addGenericCategory(rootCategory);
		GenericCategory childCategory = createGenericCategory(entityType1);
		childCategory.setName("child");
		EntityModelCacheFactory.getInstance().addGenericCategory(childCategory);
		MutableTimedAssociationKey childToParentKey = new DefaultMutableTimedAssociationKey(rootCategory.getId(), null, null);
		MutableTimedAssociationKey parentToChildKey = new DefaultMutableTimedAssociationKey(childCategory.getId(), null, null);
		childCategory.addParentKey(childToParentKey);
		rootCategory.addChildAssociation(parentToChildKey);

		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), false, true, false));
		setClipboard("root" + Constants.CATEGORY_PATH_DELIMITER + "child");
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertTrue(gridTable.getValueAt(0, 0) instanceof CategoryOrEntityValue);
		assertFalse(((CategoryOrEntityValue) gridTable.getValueAt(0, 0)).isForEntity());
		assertEquals(childCategory.getId(), ((CategoryOrEntityValue) gridTable.getValueAt(0, 0)).getId());
		EntityModelCacheFactory.getInstance().removeGenericCategory(rootCategory);
		EntityModelCacheFactory.getInstance().removeGenericCategory(childCategory);
	}

	@Test
	public void testPasteWithMultiSelectEntityListForEntitiesHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, true));
		GenericEntity entity1 = createGenericEntity(entityType1);
		GenericEntity entity2 = createGenericEntity(entityType1);
		EntityModelCacheFactory.getInstance().add(entity1);
		EntityModelCacheFactory.getInstance().add(entity2);
		setClipboard(EnumValues.OLD_EXCLUSION_PREFIX + entity1.getName() + ',' + entity2.getName());
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		CategoryOrEntityValues cellValue = (CategoryOrEntityValues) gridTable.getValueAt(0, 0);
		assertTrue(cellValue.isSelectionExclusion());
		assertEquals(2, cellValue.size());
		assertContains(entity1, cellValue);
		assertContains(entity2, cellValue);
		EntityModelCacheFactory.getInstance().remove(entity1);
		EntityModelCacheFactory.getInstance().remove(entity2);
	}

	@Test
	public void testPasteWithMultiSelectEntityListForEntitiesSkipsEmptyStrings() throws Exception {
		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, true));
		GenericEntity entity1 = createGenericEntity(entityType1);
		GenericEntity entity2 = createGenericEntity(entityType1);
		EntityModelCacheFactory.getInstance().add(entity1);
		EntityModelCacheFactory.getInstance().add(entity2);
		setClipboard(entity1.getName() + ",," + entity2.getName());
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		CategoryOrEntityValues cellValue = (CategoryOrEntityValues) gridTable.getValueAt(0, 0);
		assertFalse(cellValue.isSelectionExclusion());
		assertEquals(2, cellValue.size());
		assertContains(entity1, cellValue);
		assertContains(entity2, cellValue);
		EntityModelCacheFactory.getInstance().remove(entity1);
		EntityModelCacheFactory.getInstance().remove(entity2);
	}

	@Test
	public void testPasteWithMultiSelectEntityListForCategoriesHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, true));
		GenericCategory category1 = createGenericCategory(entityType1);
		GenericCategory category2 = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category2);
		setClipboard(category1.getName() + ',' + category2.getName());
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		CategoryOrEntityValues cellValue = (CategoryOrEntityValues) gridTable.getValueAt(0, 0);
		assertFalse(cellValue.isSelectionExclusion());
		assertEquals(2, cellValue.size());
		assertContains(category1, cellValue);
		assertContains(category2, cellValue);
		EntityModelCacheFactory.getInstance().removeGenericCategory(category1);
		EntityModelCacheFactory.getInstance().removeGenericCategory(category2);
	}

	@Test
	public void testPasteWithMultiSelectEntityListForCategoriesSkipsEmptyStrings() throws Exception {
		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, true));
		GenericCategory category1 = createGenericCategory(entityType1);
		GenericCategory category2 = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category2);
		setClipboard(category1.getName() + ",," + category2.getName());
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		CategoryOrEntityValues cellValue = (CategoryOrEntityValues) gridTable.getValueAt(0, 0);
		assertFalse(cellValue.isSelectionExclusion());
		assertEquals(2, cellValue.size());
		assertContains(category1, cellValue);
		assertContains(category2, cellValue);
		EntityModelCacheFactory.getInstance().removeGenericCategory(category1);
		EntityModelCacheFactory.getInstance().removeGenericCategory(category2);
	}

	@Test
	public void testPasteHandlesEmptyCellValues() throws Exception {
		setClipboard("\tnew-111");
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.paste();
		assertEquals("new-111", gridTable.getValueAt(0, 1));
	}

	@Test
	public void testCutWithNoSelectionHappyCase() throws Exception {
		// clear clipboard content
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);

		setClipboard("12345");
		excelAdapter.copy();
		assertClipboardEquals("12345");
		assertEquals("cell-1,1", gridTable.getValueAt(0, 0));
		assertEquals("cell-1,2", gridTable.getValueAt(0, 1));
	}

	@Test
	public void testCutWithSingleCellSelectionHappyCase() throws Exception {
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsBlankAllowed(true);
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.cut();
		assertClipboardEquals("cell-1,1");
		assertNull(gridTable.getValueAt(0, 0));
	}

	@Test
	public void testCutWithRowSelectionHappyCase() throws Exception {
		// start with only one cell
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.cut();
		assertClipboardEquals("cell-1,1	cell-1,2");
		assertNull(gridTable.getValueAt(0, 0));
		assertNull(gridTable.getValueAt(0, 1));
		assertEquals(0, gridTable.getRowCount());
	}

	@Test
	public void testCopyWithNoSelectionDoesNotUpdateClipboard() throws Exception {
		// clear clipboard content
		setClipboard("12345");
		excelAdapter.copy();
		assertClipboardEquals("12345");
	}

	@Test
	public void testCopyWithSingleCellSelectionSetsClipboardWithCellValue() throws Exception {
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals("cell-1,1");
	}

	@Test
	public void testCopyWithRowSelectionSetsClipboardWithRowValues() throws Exception {
		gridTable.setValueAt("cell-1,1", 0, 0);
		gridTable.setValueAt("cell-1,2", 0, 1);
		gridTable.setRowSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals("cell-1,1	cell-1,2");
	}

	@Test
	public void testCopyWithSingleSelectEntityListForEntitySetsClipbaordWithName() throws Exception {
		GenericEntity entity = createGenericEntity(entityType1);
		EntityModelCacheFactory.getInstance().add(entity);
		CategoryOrEntityValue value = new CategoryOrEntityValue(entity);

		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, false));
		gridTable.addRow(0);
		gridTable.setValueAt(value, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals(entity.getName());
		EntityModelCacheFactory.getInstance().remove(entity);
	}

	@Test
	public void testCopyWithSingleSelectEntityListForCategorySetsClipbaordWithName() throws Exception {
		GenericCategory category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		CategoryOrEntityValue value = new CategoryOrEntityValue(entityType1, false, category.getID());

		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, false));
		gridTable.addRow(0);
		gridTable.setValueAt(value, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals(category.getName());
	}

	/**
	 * This doesnt generate the fully qualified path because the child name is unique
	 * @throws Exception
	 */
	@Test
	public void testCopyWithForCategorySetsClipbaordWithNonFullyQualifiedName() throws Exception {
		GenericCategory rootCategory = createGenericCategory(entityType1);
		rootCategory.setName("root");
		EntityModelCacheFactory.getInstance().addGenericCategory(rootCategory);
		GenericCategory childCategory = createGenericCategory(entityType1);
		childCategory.setName("child");
		EntityModelCacheFactory.getInstance().addGenericCategory(childCategory);
		MutableTimedAssociationKey childToParentKey = new DefaultMutableTimedAssociationKey(rootCategory.getId(), null, null);
		MutableTimedAssociationKey parentToChildKey = new DefaultMutableTimedAssociationKey(childCategory.getId(), null, null);
		childCategory.addParentKey(childToParentKey);
		rootCategory.addChildAssociation(parentToChildKey);

		CategoryOrEntityValue value = new CategoryOrEntityValue(entityType1, false, childCategory.getID());

		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, false));
		gridTable.addRow(0);
		gridTable.setValueAt(value, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals(childCategory.getName());
	}


	@Test
	public void testCopyWithMultiSelectEntityListForEntitySetsClipbaordWithName() throws Exception {
		GenericEntity entity = createGenericEntity(entityType1);
		EntityModelCacheFactory.getInstance().add(entity);
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(entity));

		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, false));
		gridTable.addRow(0);
		gridTable.setValueAt(value, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals(entity.getName());
	}

	@Test
	public void testCopyWithMultiSingleSelectEntityListForCategorySetsClipbaordWithName() throws Exception {
		GenericCategory category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.setSelectionExclusion(true);
		value.add(new CategoryOrEntityValue(entityType1, false, category.getID()));

		gridTable.getTemplate().getColumn(1).setDataSpecDigest(createEntityColumnDataSpecDigest(entityType1.getName(), true, true, false));
		gridTable.addRow(0);
		gridTable.setValueAt(value, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals(EnumValues.OLD_EXCLUSION_PREFIX + category.getName());
	}

	@Test
	public void testCopyWithEnumValueSetsClipbaordWithDisplayLabel() throws Exception {
		EnumValue enumValue = createEnumValue();
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		gridTable.addRow(0);
		gridTable.setValueAt(enumValue, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		assertClipboardEquals(enumValue.getDisplayLabel());
	}

	@Test
	public void testCopyWithEnumValuesSetsClipbaordWithDisplayLabel() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		gridTable.getTemplate().getColumn(1).getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		gridTable.addRow(0);
		gridTable.setValueAt(enumValues, 0, 0);
		gridTable.setRowSelectionInterval(0, 0);
		gridTable.setColumnSelectionInterval(0, 0);
		excelAdapter.copy();
		String expected = ((EnumValue) enumValues.get(0)).getDisplayLabel() + "," + ((EnumValue) enumValues.get(1)).getDisplayLabel();
		assertClipboardEquals(expected);
	}

	protected void setClipboard(String value) throws Exception {
		StringSelection stringSelection = new StringSelection(value);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// clear cache
		Map<?, ?> map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryMap", Map.class);
		map.clear();
		GridTemplate template = attachGridTemplateColumns(createGridTemplate(TemplateUsageType.getAllInstances()[0]), 2);
		attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(2));
		gridTable = new GridTable();
		gridTable.setTemplate(template);
		gridTable.addRow(0);
		excelAdapter = new ExcelAdapter(gridTable);
	}

	@After
	public void tearDown() throws Exception {
		if (gridTable.getRowCount() > 0) {
			gridTable.setRowSelectionInterval(0, gridTable.getRowCount() - 1);
			gridTable.removeRow();
		}
		super.tearDown();
	}
}
