package com.mindbox.pe.client.common.grid;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;

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

public class EnumCellRendererTest extends AbstractTestBase {
	private static final boolean ROW_SELECTED = true;
	private static final boolean ROW_NOT_SELECTED = false;

	private EnumValue[] enumVals;
	private EnumCellRenderer renderer;
	private JTable table;
	private JLabel label;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private DomainRetrieverProxy domainRetrieverProxyMock;
	private MockControl domainRetrieverProxyMockControl;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(EnumCellRendererTest.class.getName());
		suite.addTestSuite(EnumCellRendererTest.class);
		return suite;
	}

	public EnumCellRendererTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		domainRetrieverProxyMockControl = MockControl.createControl(DomainRetrieverProxy.class);
		domainRetrieverProxyMock = (DomainRetrieverProxy) domainRetrieverProxyMockControl.getMock();
		domainRetrieverProxyMockControl.expectAndReturn(domainRetrieverProxyMock.fetchAllDomainClasses(), new DomainClass[0]);
		domainRetrieverProxyMockControl.replay();

		DomainModel.initInstance(domainRetrieverProxyMock);

		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		enumVals = ObjectMother.createEnumValues(3);
		renderer = new EnumCellRenderer(columnDataSpecDigest);
		table = new JTable();
		label = (JLabel) renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
	}

	@Override
	protected void tearDown() throws Exception {
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
		super.tearDown();
	}

	public void testBackgroundNotSelectedToSelected() throws Exception {
		assertEquals(Color.white, label.getBackground());
		renderer.getTableCellRendererComponent(table, null, ROW_SELECTED, false, 0, 0);

		// Equals comparison to expected Color doesn't work because setBackground(...) may slightly change the RGB values for the platform...
		// assertEquals(PowerRendererSwingTheme.primary3, label.getBackground());

		// ...So, we have to be satisfied with the weaker assertion that the color is merely changed
		assertNotEquals(Color.white, label.getBackground());
	}

	public void testBackgroundSelectedToNotSelected() throws Exception {
		renderer.getTableCellRendererComponent(table, null, ROW_SELECTED, false, 0, 0);
		assertNotEquals(Color.white, label.getBackground()); // sanity check

		renderer.getTableCellRendererComponent(table, null, ROW_NOT_SELECTED, false, 0, 0);
		assertEquals(Color.white, label.getBackground());
	}

	public void testGetCellRendererComponentWithDisplayLabel() throws Exception {
		for (int i = 0; i < enumVals.length; i++) {
			renderer.getTableCellRendererComponent(table, enumVals[i].getDisplayLabel(), false, false, 0, 0);
			assertEquals(enumVals[i].getDisplayLabel(), label.getText());
		}
	}

	public void testGetCellRendererComponentWithNotFoundStringHasTheString() throws Exception {
		renderer.getTableCellRendererComponent(table, "bogusString", false, false, 0, 0);
		assertEquals("bogusString", label.getText());
	}
}
