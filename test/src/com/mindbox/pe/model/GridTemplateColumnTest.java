/*
 * Created on Feb 28, 2006
 *
 */
package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.AbstractMessageKeyList;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.AbstractTestBase;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class GridTemplateColumnTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GridTemplateColumn Tests");
		suite.addTestSuite(GridTemplateColumnTest.class);
		return suite;
	}

	private GridTemplateColumn column;

	public GridTemplateColumnTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		column = new GridTemplateColumn();
		column.setID(1);
		column.setTitle("Column One");
	}

	public void tearDown() throws Exception {
		column = null;
	}

	public void testGetColumnMessageFragmentNullReturns() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");
		assertNull(column.getMessageFragmentDigest(enumValues));

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(AbstractMessageKeyList.ENUM_KEY);
		cmd.setCellSelection("default");
		cmd.setText("enum message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		column.addColumnMessageFragment(cmd);

		assertNotNull(column.getMessageFragmentDigest(enumValues));
		assertNull(column.getMessageFragmentDigest("string value"));

		IntegerRange rangeValue = new IntegerRange();
		rangeValue.setUpperValue(new Integer(100));
		rangeValue.setLowerValue(new Integer(10));
		assertNull(column.getMessageFragmentDigest(rangeValue));
	}

	public void testGetMessageConfiguration() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(AbstractMessageKeyList.ENUM_KEY);
		cmd.setCellSelection("default");
		cmd.setText("enum message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		column.addColumnMessageFragment(cmd);

		boolean isExclusion = enumValues.isSelectionExclusion();
		boolean isMultiSelect = enumValues.size() > 1;

		MessageConfiguration messageConfig = column.getMessageConfiguration();
		assertEquals("Enum delimiter mismatch", cmd.getEnumDelimiter(), messageConfig.getEnumDelimiter(isExclusion, isMultiSelect));
		assertEquals("Enum final delimiter mismatch", cmd.getEnumFinalDelimiter(), messageConfig.getEnumFinalDelimiter(isExclusion, isMultiSelect));
		assertEquals("Enum prefix mismatch", cmd.getEnumPrefix(), messageConfig.getEnumPrefix(isExclusion, isMultiSelect));

		assertEquals("Enum delimiter mismatch", cmd.getEnumDelimiter(), messageConfig.getEnumDelimiter(true, true));
		assertEquals("Enum final delimiter mismatch", cmd.getEnumFinalDelimiter(), messageConfig.getEnumFinalDelimiter(true, true));
		assertEquals("Enum prefix mismatch", cmd.getEnumPrefix(), messageConfig.getEnumPrefix(true, true));

		assertEquals("Enum delimiter mismatch", cmd.getEnumDelimiter(), messageConfig.getEnumDelimiter(false, true));
		assertEquals("Enum final delimiter mismatch", cmd.getEnumFinalDelimiter(), messageConfig.getEnumFinalDelimiter(false, true));
		assertEquals("Enum prefix mismatch", cmd.getEnumPrefix(), messageConfig.getEnumPrefix(false, true));
	}

	public void testGetColumnMessageFragmentForEnum() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(AbstractMessageKeyList.ENUM_KEY);
		cmd.setCellSelection("default");
		cmd.setText("enum message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		column.addColumnMessageFragment(cmd);

		assertEquals("Enum message fragment not equal for inclusion,single-select", cmd, column.getMessageFragmentDigest(enumValues));
		enumValues.add("More Value");
		assertEquals("Enum message fragment not equal for inclusion,multi-select", cmd, column.getMessageFragmentDigest(enumValues));
		enumValues.setSelectionExclusion(true);
		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd, column.getMessageFragmentDigest(enumValues));
		enumValues.clear();
		enumValues.add("Some Value");
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd, column.getMessageFragmentDigest(enumValues));

		ColumnMessageFragmentDigest cmd2 = new ColumnMessageFragmentDigest();
		cmd2.setType(AbstractMessageKeyList.ENUM_KEY);
		cmd2.setCellSelection(AbstractMessageKeyList.TYPE_EXCLUDE_SINGLE_KEY);
		cmd2.setText("excluded enum message text: " + System.currentTimeMillis());
		cmd2.setEnumDelimiter("enum-delim-2");
		cmd2.setEnumFinalDelimiter("final enum & delim & 2");
		cmd2.setEnumPrefix("enum-prefix-2");
		column.addColumnMessageFragment(cmd2);
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd2, column.getMessageFragmentDigest(enumValues));
		enumValues.add("more values2");
		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd, column.getMessageFragmentDigest(enumValues));

		cmd2.setCellSelection(AbstractMessageKeyList.TYPE_EXCLUDE_MULTIPLE_KEY);
		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd2, column.getMessageFragmentDigest(enumValues));
		enumValues.setSelectionExclusion(false);
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd, column.getMessageFragmentDigest(enumValues));

		// check null returns
	}

	public void testGetColumnMessageFragmentForRange() throws Exception {
		IntegerRange rangeValue = new IntegerRange();
		rangeValue.setUpperValue(new Integer(100));
		rangeValue.setLowerValue(new Integer(10));
		assertNull(column.getMessageFragmentDigest(rangeValue));

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(AbstractMessageKeyList.TYPE_RANGE_KEY);
		cmd.setText("range message text: " + System.currentTimeMillis());
		cmd.setRangeStyle(AbstractMessageKeyList.RANGE_SYMBOLIC_KEY);
		column.addColumnMessageFragment(cmd);
		assertEquals("Enum message fragment not equal for range style", cmd, column.getMessageFragmentDigest(rangeValue));
	}
	
	public void testUpdateColumnMessageFragmentText() throws Exception {
		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(AbstractMessageKeyList.ENUM_KEY);
		cmd.setCellSelection("default");
		cmd.setText("enum message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		column.addColumnMessageFragment(cmd);
		
		ColumnMessageFragmentDigest cmd2 = new ColumnMessageFragmentDigest();
		cmd2.setType(AbstractMessageKeyList.ENUM_KEY);
		cmd2.setCellSelection("default");
		cmd2.setText("enum message text: " + System.currentTimeMillis());
		cmd2.setEnumDelimiter("enum-delim-2");
		cmd2.setEnumFinalDelimiter("final enum & delim-2");
		cmd2.setEnumPrefix("enum-prefix-2");
		column.updateColumnMessageFragmentText(cmd2);
		
		// verify message configuration is updated
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");
		boolean isExclusion = enumValues.isSelectionExclusion();
		boolean isMultiSelect = enumValues.size() > 1;

		MessageConfiguration messageConfig = column.getMessageConfiguration();
		assertEquals("Enum delimiter mismatch", cmd2.getEnumDelimiter(), messageConfig.getEnumDelimiter(isExclusion, isMultiSelect));
		assertEquals("Enum final delimiter mismatch", cmd2.getEnumFinalDelimiter(), messageConfig.getEnumFinalDelimiter(isExclusion, isMultiSelect));
		assertEquals("Enum prefix mismatch", cmd2.getEnumPrefix(), messageConfig.getEnumPrefix(isExclusion, isMultiSelect));
		
		// verify message text is updated
		assertEquals(cmd2.getText(), column.getUnparsedMessage(enumValues));
		
		// verify message fragment digest is updated
		cmd = column.getMessageFragmentDigest(enumValues);
		assertEquals(cmd2.getType(), cmd.getType());
		assertEquals(cmd2.getCellSelection(), cmd.getCellSelection());
		assertEquals(cmd2.getEnumDelimiter(), cmd.getEnumDelimiter());
		assertEquals(cmd2.getEnumFinalDelimiter(), cmd.getEnumFinalDelimiter());
		assertEquals(cmd2.getEnumPrefix(), cmd.getEnumPrefix());
		assertEquals(cmd2.getText(), cmd.getText());
	}
}
