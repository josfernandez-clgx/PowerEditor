package com.mindbox.pe.client.common.grid;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.table.DefaultTableCellRenderer;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.client.common.formatter.StringFormatter;
import com.mindbox.pe.client.common.formatter.SymbolDocumentFilter;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.GridTemplate;

public class AbstractGridTableTest extends AbstractTestBase {
	private AbstractGridTable<?> table;
	private AbstractTemplateColumn templateColumn;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(AbstractGridTableTest.class.getName());
		suite.addTestSuite(AbstractGridTableTest.class);
		return suite;
	}

	protected DomainRetrieverProxy domainRetrieverProxyMock;
	protected MockControl domainRetrieverProxyMockControl;

	public AbstractGridTableTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		table = new TestGridTable();
		templateColumn = new TestTemplateColumn();
		templateColumn.setDataSpecDigest(new ColumnDataSpecDigest());
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		domainRetrieverProxyMockControl = MockControl.createControl(DomainRetrieverProxy.class);
		domainRetrieverProxyMock = (DomainRetrieverProxy) domainRetrieverProxyMockControl.getMock();
		domainRetrieverProxyMockControl.expectAndReturn(domainRetrieverProxyMock.fetchAllDomainClasses(), new DomainClass[0]);
		domainRetrieverProxyMockControl.replay();

		DomainModel.initInstance(domainRetrieverProxyMock);
	}

	@Override
	protected void tearDown() throws Exception {
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
		super.tearDown();
	}

	public void testGetBooleanCellRendererBlankAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(true);
		testGetRenderer(BooleanCellRenderer.class);
	}

	public void testGetBooleanCellRendererBlankNotAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(false);
		testGetRenderer(null);
	}

	public void testGetCurrencyRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		testGetRenderer(CurrencyRangeCellRenderer.class);
	}

	public void testGetCurrencyCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		testGetRenderer(CurrencyCellRenderer.class);
	}

	public void testGetFloatRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		testGetRenderer(FloatRangeCellRenderer.class);
	}

	public void testGetFloatCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);
		testGetRenderer(FloatCellRenderer.class);
	}

	public void testGetDateCellRenderer() throws Exception {
	}

	public void testGetIntegerCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		testGetRenderer(IntegerCellRenderer.class);
	}

	public void testGetIntegerRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		testGetRenderer(DefaultTableCellRenderer.class);
	}

	public void testGetMultiSelectEnumCellRenderer() throws Exception {
	}

	public void testGetSingleSelectEnumRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		testGetRenderer(EnumCellRenderer.class);
	}

	public void testGetSymbolCellEditor() throws Exception {
		ColumnDataSpecDigest colDataSpec = new ColumnDataSpecDigest();
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_SYMBOL);
		templateColumn.setDataSpecDigest(colDataSpec);

		DefaultCellEditor editor = (DefaultCellEditor) table.getEditor(templateColumn);

		StringFormatter formatter = (StringFormatter) ((JFormattedTextField) editor.getComponent()).getFormatter();
		assertEquals(SymbolDocumentFilter.class, ReflectionUtil.getPrivate(formatter, "documentFiler").getClass());
	}

	public void testGetBooleanCellEditorBlankAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(true);
		testGetEditor(BooleanCellEditor.class);
	}

	public void testGetBooleanCellEditorBlankNotAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(false);
		testGetEditor(null);//, new BooleanDataSpec(false));
	}

	public void testGetMultiSelectEnumCellEditor() throws Exception {
	}

	public void testGetSingleSelectEnumRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		testGetEditor(EnumCellEditor.class);
	}

	public void testGetDateRangeCellEditor() throws Exception {
	}

	public void testGetDateCellEditor() throws Exception {
	}

	public void testGetTimeRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_TIME_RANGE);
		testGetEditor(TimeRangeCellEditor.class);
	}

	public void testGetIntegerRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		testGetEditor(IntegerRangeCellEditor.class);
	}

	public void testGetCurrencyRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		testGetEditor(FloatRangeCellEditor.class);
	}

	public void testGetFloatRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		testGetEditor(FloatRangeCellEditor.class);
	}

	public void testGetCurrencyCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		testGetEditor(CurrencyCellEditor.class);
	}

	public void testGetFloatCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);
		testGetEditor(FloatCellEditor.class);
	}

	public void testGetDynamicStringCellEditor() throws Exception {
	}

	public void testGetIntegerCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		testGetEditor(null);
	}

	public void testGetStringCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);
		testGetEditor(null);
	}

	private void testGetRenderer(Class<?> expectedReturnType) {
		if (expectedReturnType == null) {
			assertNull(table.getRenderer(templateColumn));
		}
		else {
			assertEquals(expectedReturnType.getName(), table.getRenderer(templateColumn).getClass().getName());
		}
	}

	private void testGetEditor(Class<?> expectedReturnType) {
		if (expectedReturnType == null) {
			assertNull(table.getEditor(templateColumn));
		}
		else {
			assertEquals(expectedReturnType.getName(), table.getEditor(templateColumn).getClass().getName());
		}
	}

	private static class TestGridTable extends AbstractGridTable<GridTemplate> {
		protected TestGridTable() {
			super(null);
		}

		protected AbstractTemplateColumn getTemplateColumn(int col) {
			return null;
		}

		protected int getTemplateColumnCount() {
			return 0;
		}

		protected String getColumnTitle(int col) {
			return null;
		}
	}

	private static class TestTemplateColumn extends AbstractTemplateColumn {
		private TestTemplateColumn() {
			super(0, "name", "desc", 10, null);
		}

		@SuppressWarnings("unused")
		public MessageConfiguration getMessageConfiguration() {
			return null;
		}
	}
}
