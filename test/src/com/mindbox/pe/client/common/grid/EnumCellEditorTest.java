package com.mindbox.pe.client.common.grid;

import javax.swing.JComboBox;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;

public class EnumCellEditorTest extends AbstractTestBase {
	private static final boolean BLANK_ALLOWED = true;
	private static final boolean BLANK_NOT_ALLOWED = false;


	public static TestSuite suite() {
		TestSuite suite = new TestSuite("EnumCellEditor Tests");
		suite.addTestSuite(EnumCellEditorTest.class);
		return suite;
	}

	private EnumValue[] enumVals;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private JComboBox combo;
	private EnumCellEditor editor;
	protected DomainRetrieverProxy domainRetrieverProxyMock;
	protected MockControl domainRetrieverProxyMockControl;
	private DomainClass dc;

	public EnumCellEditorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		enumVals = ObjectMother.createEnumValues(3);
		dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		for (EnumValue enumValue : enumVals) {
			dc.getDomainAttributes().get(0).addEnumValue(enumValue);
		}
		domainRetrieverProxyMockControl = MockControl.createControl(DomainRetrieverProxy.class);
		domainRetrieverProxyMock = (DomainRetrieverProxy) domainRetrieverProxyMockControl.getMock();
		domainRetrieverProxyMockControl.expectAndReturn(domainRetrieverProxyMock.fetchAllDomainClasses(), new DomainClass[] { dc });
		domainRetrieverProxyMockControl.replay();

		DomainModel.initInstance(domainRetrieverProxyMock);
		setUpEditor(BLANK_NOT_ALLOWED);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
	}
	
	public void testInit() throws Exception {
		assertEquals(1, editor.getClickCountToStart());
		assertEquals(enumVals.length, combo.getItemCount());
		for (int i = 0; i < enumVals.length; i++) {
			assertEquals(enumVals[i], combo.getItemAt(i));
		}
	}

	public void testInitPopulatesComboWithEnumValueObjects() throws Exception {
		for (int i = 0; i < enumVals.length; i++) {
			assertTrue(combo.getItemAt(i) instanceof EnumValue);
		}
	}

	public void testGetCellEditorComponentWithEnumValues() throws Exception {
		for (int i = 0; i < enumVals.length; i++) {
			editor.getTableCellEditorComponent(null, enumVals[i], false, 0, 0);
			assertEquals(enumVals[i], combo.getSelectedItem());
		}
	}

	public void testAllowsBlank_ComboHasBlankItem() throws Exception {
		setUpEditor(BLANK_ALLOWED);
		assertEquals(enumVals.length + 1, combo.getItemCount());
	}

	public void testGetCellEditorBlankValue() throws Exception {
		setUpEditor(BLANK_ALLOWED);
		editor.getTableCellEditorComponent(null, enumVals[0], false, 0, 0);
		assertNotEquals("", combo.getSelectedItem()); // sanity check

		editor.getTableCellEditorComponent(null, EnumValue.BLANK, false, 0, 0);
		assertEquals(EnumValue.BLANK, combo.getSelectedItem());
	}

	private void setUpEditor(boolean allowsBlank) {
		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());
		columnDataSpecDigest.setIsBlankAllowed(allowsBlank);
		columnDataSpecDigest.setIsEnumValueNeedSorted(false);

		GridTableModel tableModel = new GridTableModel();
		tableModel.setTemplate(ObjectMother.attachGridTemplateColumns(ObjectMother.createGridTemplate(ObjectMother.createUsageType()), 1));
		tableModel.getTemplate().getColumn(1).setDataSpecDigest(columnDataSpecDigest);
		editor = new EnumCellEditor(columnDataSpecDigest, tableModel.getTemplate().getColumn(1).getName(), false, tableModel);
		combo = (JComboBox) editor.getComponent();
	}
}
