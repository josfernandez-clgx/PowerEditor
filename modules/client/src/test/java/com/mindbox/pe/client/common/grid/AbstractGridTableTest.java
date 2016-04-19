package com.mindbox.pe.client.common.grid;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.client.common.formatter.StringFormatter;
import com.mindbox.pe.client.common.formatter.SymbolDocumentFilter;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractGridTableTest extends AbstractTestBase {

	private static class TestGridTable extends AbstractGridTable<GridTemplate> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected TestGridTable() {
			super(null);
		}

		protected String getColumnTitle(int col) {
			return null;
		}

		protected AbstractTemplateColumn getTemplateColumn(int col) {
			return null;
		}

		protected int getTemplateColumnCount() {
			return 0;
		}
	}

	private static class TestTemplateColumn extends AbstractTemplateColumn {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private TestTemplateColumn() {
			super(0, "name", "desc", 10, null);
		}

		@SuppressWarnings("unused")
		public MessageConfiguration getMessageConfiguration() {
			return null;
		}
	}

	private AbstractGridTable<?> table;

	private AbstractTemplateColumn templateColumn;


	protected DomainRetrieverProxy domainRetrieverProxyMock;

	protected void replayAll() {
		replay(domainRetrieverProxyMock);
	}

	@Before
	public void setUp() throws Exception {
		table = new TestGridTable();
		templateColumn = new TestTemplateColumn();
		templateColumn.setDataSpecDigest(new ColumnDataSpecDigest());
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		domainRetrieverProxyMock = createMock(DomainRetrieverProxy.class);
		expect(domainRetrieverProxyMock.fetchAllDomainClasses()).andReturn(new DomainClass[0]);
		replayAll();

		DomainModel.initInstance(domainRetrieverProxyMock);
	}

	@After
	public void tearDown() throws Exception {
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
	}

	@Test
	public void testGetBooleanCellEditorBlankAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(true);
		testGetEditor(BooleanCellEditor.class);
	}

	@Test
	public void testGetBooleanCellEditorBlankNotAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(false);
		testGetEditor(null);
	}

	@Test
	public void testGetBooleanCellRendererBlankAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(true);
		testGetRenderer(BooleanCellRenderer.class);
	}

	@Test
	public void testGetBooleanCellRendererBlankNotAllowed() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		templateColumn.getColumnDataSpecDigest().setIsBlankAllowed(false);
		testGetRenderer(null);
	}

	@Test
	public void testGetCurrencyCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		testGetEditor(CurrencyCellEditor.class);
	}

	@Test
	public void testGetCurrencyCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY);
		testGetRenderer(CurrencyCellRenderer.class);
	}

	@Test
	public void testGetCurrencyRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		testGetEditor(FloatRangeCellEditor.class);
	}

	@Test
	public void testGetCurrencyRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);
		testGetRenderer(CurrencyRangeCellRenderer.class);
	}

	@Test
	public void testGetDateCellEditor() throws Exception {
	}

	@Test
	public void testGetDateCellRenderer() throws Exception {
	}

	@Test
	public void testGetDateRangeCellEditor() throws Exception {
	}

	@Test
	public void testGetDynamicStringCellEditor() throws Exception {
	}

	private void testGetEditor(Class<?> expectedReturnType) {
		if (expectedReturnType == null) {
			assertNull(table.getEditor(templateColumn));
		}
		else {
			assertEquals(expectedReturnType.getName(), table.getEditor(templateColumn).getClass().getName());
		}
	}

	@Test
	public void testGetFloatCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);
		testGetEditor(FloatCellEditor.class);
	}

	@Test
	public void testGetFloatCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);
		testGetRenderer(FloatCellRenderer.class);
	}

	@Test
	public void testGetFloatRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		testGetEditor(FloatRangeCellEditor.class);
	}

	@Test
	public void testGetFloatRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);
		testGetRenderer(FloatRangeCellRenderer.class);
	}

	@Test
	public void testGetIntegerCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		testGetEditor(null);
	}

	@Test
	public void testGetIntegerCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		testGetRenderer(IntegerCellRenderer.class);
	}

	@Test
	public void testGetIntegerRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		testGetEditor(IntegerRangeCellEditor.class);
	}

	@Test
	public void testGetIntegerRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);
		testGetRenderer(DefaultTableCellRenderer.class);
	}

	@Test
	public void testGetMultiSelectEnumCellEditor() throws Exception {
	}

	@Test
	public void testGetMultiSelectEnumCellRenderer() throws Exception {
	}

	private void testGetRenderer(Class<?> expectedReturnType) {
		if (expectedReturnType == null) {
			assertNull(table.getRenderer(templateColumn));
		}
		else {
			assertEquals(expectedReturnType.getName(), table.getRenderer(templateColumn).getClass().getName());
		}
	}

	@Test
	public void testGetSingleSelectEnumRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		testGetEditor(EnumCellEditor.class);
	}

	@Test
	public void testGetSingleSelectEnumRangeCellRenderer() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		testGetRenderer(EnumCellRenderer.class);
	}

	@Test
	public void testGetStringCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);
		testGetEditor(null);
	}

	@Test
	public void testGetSymbolCellEditor() throws Exception {
		ColumnDataSpecDigest colDataSpec = new ColumnDataSpecDigest();
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_SYMBOL);
		templateColumn.setDataSpecDigest(colDataSpec);

		DefaultCellEditor editor = (DefaultCellEditor) table.getEditor(templateColumn);

		StringFormatter formatter = (StringFormatter) ((JFormattedTextField) editor.getComponent()).getFormatter();
		assertEquals(SymbolDocumentFilter.class, ReflectionUtil.getPrivate(formatter, "documentFiler").getClass());
	}

	@Test
	public void testGetTimeRangeCellEditor() throws Exception {
		templateColumn.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_TIME_RANGE);
		testGetEditor(TimeRangeCellEditor.class);
	}
}
