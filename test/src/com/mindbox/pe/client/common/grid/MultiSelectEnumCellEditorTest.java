package com.mindbox.pe.client.common.grid;

import javax.swing.ListModel;

import junit.framework.Test;
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

public class MultiSelectEnumCellEditorTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("MultiSelectEnumCellEditorTest Tests");
		suite.addTestSuite(MultiSelectEnumCellEditorTest.class);
		return suite;
	}

	private EnumValue[] enumVals;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private ListModel listModel;
	private MultiSelectEnumCellEditor editor;

	private DomainRetrieverProxy domainRetrieverProxyMock;
	private MockControl domainRetrieverProxyMockControl;

	public MultiSelectEnumCellEditorTest(String name) {
		super(name);
	}

	public void testInitPopulatesListWithEnumValueObjects() throws Exception {
		assertEquals(enumVals.length, listModel.getSize());
		for (int i = 0; i < enumVals.length; i++) {
			assertTrue(listModel.getElementAt(i) instanceof EnumValue);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		enumVals = ObjectMother.createEnumValues(3);
		setUpEditor(false);
	}

	protected void tearDown() throws Exception {
		// Tear downs for MultiSelectEnumCellEditorTest
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
		super.tearDown();
	}

	private void setUpEditor(boolean allowsBlank) throws Exception {
		domainRetrieverProxyMockControl = MockControl.createControl(DomainRetrieverProxy.class);
		domainRetrieverProxyMock = (DomainRetrieverProxy) domainRetrieverProxyMockControl.getMock();
		domainRetrieverProxyMockControl.expectAndReturn(domainRetrieverProxyMock.fetchAllDomainClasses(), new DomainClass[0]);
		domainRetrieverProxyMockControl.replay();

		DomainModel.initInstance(domainRetrieverProxyMock);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.COLUMN);
		for (EnumValue ev : enumVals) {
			columnDataSpecDigest.addColumnEnumValue(ev.getDeployValue());
		}

		editor = new MultiSelectEnumCellEditor("Column", columnDataSpecDigest, false, new GridTableModel());
		listModel = (ListModel) ReflectionUtil.getPrivate(editor, "listModel");
	}
}
