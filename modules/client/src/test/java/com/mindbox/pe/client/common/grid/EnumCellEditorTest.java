package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.client.ClientTestObjectMother.attachGridTemplateColumns;
import static com.mindbox.pe.client.ClientTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createDomainClass;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValues;
import static com.mindbox.pe.client.ClientTestObjectMother.createGridTemplate;
import static com.mindbox.pe.client.ClientTestObjectMother.createUsageType;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JComboBox;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumCellEditorTest extends AbstractTestBase {
	private static final boolean BLANK_ALLOWED = true;
	private static final boolean BLANK_NOT_ALLOWED = false;

	private EnumValue[] enumVals;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private JComboBox combo;
	private EnumCellEditor editor;
	protected DomainRetrieverProxy domainRetrieverProxyMock;
	private DomainClass dc;

	@Before
	public void setUp() throws Exception {
		enumVals = createEnumValues(3);
		dc = attachDomainAttributes(createDomainClass(), 1);
		for (EnumValue enumValue : enumVals) {
			dc.getDomainAttributes().get(0).addEnumValue(enumValue);
		}
		domainRetrieverProxyMock = createMock(DomainRetrieverProxy.class);
		expect(domainRetrieverProxyMock.fetchAllDomainClasses()).andReturn(new DomainClass[] { dc });
		replay(domainRetrieverProxyMock);

		DomainModel.initInstance(domainRetrieverProxyMock);
		setUpEditor(BLANK_NOT_ALLOWED);
	}

	@After
	public void tearDown() throws Exception {
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
	}

	@Test
	public void testInit() throws Exception {
		assertEquals(1, editor.getClickCountToStart());
		assertEquals(enumVals.length, combo.getItemCount());
		for (int i = 0; i < enumVals.length; i++) {
			assertEquals(enumVals[i], combo.getItemAt(i));
		}
	}

	@Test
	public void testInitPopulatesComboWithEnumValueObjects() throws Exception {
		for (int i = 0; i < enumVals.length; i++) {
			assertTrue(combo.getItemAt(i) instanceof EnumValue);
		}
	}

	@Test
	public void testGetCellEditorComponentWithEnumValues() throws Exception {
		for (int i = 0; i < enumVals.length; i++) {
			editor.getTableCellEditorComponent(null, enumVals[i], false, 0, 0);
			assertEquals(enumVals[i], combo.getSelectedItem());
		}
	}

	@Test
	public void testAllowsBlank_ComboHasBlankItem() throws Exception {
		setUpEditor(BLANK_ALLOWED);
		assertEquals(enumVals.length + 1, combo.getItemCount());
	}

	@Test
	public void testGetCellEditorBlankValue() throws Exception {
		setUpEditor(BLANK_ALLOWED);
		editor.getTableCellEditorComponent(null, enumVals[0], false, 0, 0);
		assertNotEquals("Sanity check failed; combo has a selected value", "", combo.getSelectedItem());

		editor.getTableCellEditorComponent(null, EnumValue.BLANK, false, 0, 0);
		assertEquals(EnumValue.BLANK, combo.getSelectedItem());
	}

	private void setUpEditor(boolean allowsBlank) {
		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());
		columnDataSpecDigest.setIsBlankAllowed(allowsBlank);
		columnDataSpecDigest.setIsEnumValueNeedSorted(false);

		GridTableModel tableModel = new GridTableModel();
		tableModel.setTemplate(attachGridTemplateColumns(createGridTemplate(createUsageType()), 1));
		tableModel.getTemplate().getColumn(1).setDataSpecDigest(columnDataSpecDigest);
		editor = new EnumCellEditor(columnDataSpecDigest, tableModel.getTemplate().getColumn(1).getName(), false, tableModel);
		combo = (JComboBox) editor.getComponent();
	}
}
