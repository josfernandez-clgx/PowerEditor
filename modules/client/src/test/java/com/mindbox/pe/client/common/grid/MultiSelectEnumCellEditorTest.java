package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValues;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.ListModel;

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

public class MultiSelectEnumCellEditorTest extends AbstractTestBase {

	private EnumValue[] enumVals;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private ListModel listModel;
	private MultiSelectEnumCellEditor editor;
	private DomainRetrieverProxy domainRetrieverProxyMock;

	@Before
	public void setUp() throws Exception {
		enumVals = createEnumValues(3);
		setUpEditor(false);
	}

	private void setUpEditor(boolean allowsBlank) throws Exception {
		domainRetrieverProxyMock = createMock(DomainRetrieverProxy.class);
		expect(domainRetrieverProxyMock.fetchAllDomainClasses()).andReturn(new DomainClass[0]);
		replay(domainRetrieverProxyMock);

		DomainModel.initInstance(domainRetrieverProxyMock);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.COLUMN);
		for (EnumValue ev : enumVals) {
			columnDataSpecDigest.addColumnEnumValue(ev.getDeployValue());
		}

		editor = new MultiSelectEnumCellEditor("Column", columnDataSpecDigest, false, new GridTableModel());
		listModel = (ListModel) ReflectionUtil.getPrivate(editor, "listModel");
	}

	@After
	public void tearDown() throws Exception {
		// Tear downs for MultiSelectEnumCellEditorTest
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
	}

	@Test
	public void testInitPopulatesListWithEnumValueObjects() throws Exception {
		assertEquals(enumVals.length, listModel.getSize());
		for (int i = 0; i < enumVals.length; i++) {
			assertTrue(listModel.getElementAt(i) instanceof EnumValue);
		}
	}
}
