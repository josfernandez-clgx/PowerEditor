/*
 * Created on Feb 28, 2006
 *
 */
package com.mindbox.pe.model.template;

import static com.mindbox.pe.common.CommonTestAssert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;
import com.mindbox.pe.xsd.config.RangeStyleType;

/**
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class GridTemplateColumnTest extends AbstractTestBase {

	private GridTemplateColumn column;

	@Before
	public void setUp() throws Exception {
		column = new GridTemplateColumn();
		column.setID(1);
		column.setTitle("Column One");
	}

	@Test
	public void testGetColumnMessageFragmentNullReturns() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");
		assertNull(column.getMessageFragmentDigest(enumValues));

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(MessageConfigType.ENUM);
		cmd.setCellSelection(CellSelectionType.DEFAULT);
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

	@Test
	public void testGetMessageConfiguration() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(MessageConfigType.ENUM);
		cmd.setCellSelection(CellSelectionType.DEFAULT);
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

	@Test
	public void testGetColumnMessageFragmentForEnum() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("First Item");

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(MessageConfigType.ENUM);
		cmd.setCellSelection(CellSelectionType.DEFAULT);
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
		cmd2.setType(MessageConfigType.ENUM);
		cmd2.setCellSelection(CellSelectionType.ENUM_EXCLUDE_SINGLE);
		cmd2.setText("excluded enum message text: " + System.currentTimeMillis());
		cmd2.setEnumDelimiter("enum-delim-2");
		cmd2.setEnumFinalDelimiter("final enum & delim & 2");
		cmd2.setEnumPrefix("enum-prefix-2");
		column.addColumnMessageFragment(cmd2);
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd2, column.getMessageFragmentDigest(enumValues));
		enumValues.add("more values2");
		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd, column.getMessageFragmentDigest(enumValues));

		cmd2.setCellSelection(CellSelectionType.ENUM_EXCLUDE_MULTIPLE);
		assertEquals("Enum message fragment not equal for exclusion,multi-select", cmd2, column.getMessageFragmentDigest(enumValues));
		enumValues.setSelectionExclusion(false);
		assertEquals("Enum message fragment not equal for exclusion,single-select", cmd, column.getMessageFragmentDigest(enumValues));

		// check null returns
	}

	@Test
	public void testGetColumnMessageFragmentForRange() throws Exception {
		IntegerRange rangeValue = new IntegerRange();
		rangeValue.setUpperValue(new Integer(100));
		rangeValue.setLowerValue(new Integer(10));
		assertNull(column.getMessageFragmentDigest(rangeValue));

		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(MessageConfigType.RANGE);
		cmd.setText("range message text: " + System.currentTimeMillis());
		cmd.setRangeStyle(RangeStyleType.SYMBOLIC);
		column.addColumnMessageFragment(cmd);
		assertEquals("Enum message fragment not equal for range style", cmd, column.getMessageFragmentDigest(rangeValue));
	}

	@Test
	public void testUpdateColumnMessageFragmentText() throws Exception {
		ColumnMessageFragmentDigest cmd = new ColumnMessageFragmentDigest();
		cmd.setType(MessageConfigType.ENUM);
		cmd.setCellSelection(CellSelectionType.DEFAULT);
		cmd.setText("enum message text: " + System.currentTimeMillis());
		cmd.setEnumDelimiter("enum-delim");
		cmd.setEnumFinalDelimiter("final enum & delim");
		cmd.setEnumPrefix("enum-prefix");
		column.addColumnMessageFragment(cmd);

		ColumnMessageFragmentDigest cmd2 = new ColumnMessageFragmentDigest();
		cmd2.setType(MessageConfigType.ENUM);
		cmd2.setCellSelection(CellSelectionType.DEFAULT);
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
		// TODO: DAB null in locale parameter
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
