package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValues;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumCellRendererTest extends AbstractTestBase {
	private static final boolean ROW_SELECTED = true;
	private static final boolean ROW_NOT_SELECTED = false;

	private EnumValue[] enumVals;
	private EnumCellRenderer renderer;
	private JTable table;
	private JLabel label;
	private ColumnDataSpecDigest columnDataSpecDigest;
	private DomainRetrieverProxy domainRetrieverProxyMock;

	@Before
	public void setUp() throws Exception {
		domainRetrieverProxyMock = createMock(DomainRetrieverProxy.class);
		expect(domainRetrieverProxyMock.fetchAllDomainClasses()).andReturn(new DomainClass[0]);
		replay(domainRetrieverProxyMock);

		DomainModel.initInstance(domainRetrieverProxyMock);

		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		enumVals = createEnumValues(3);
		renderer = new EnumCellRenderer(columnDataSpecDigest);
		table = new JTable();
		label = (JLabel) renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
	}

	@After
	public void tearDown() throws Exception {
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
	}

	@Test
	public void testBackgroundNotSelectedToSelected() throws Exception {
		assertEquals(Color.white, label.getBackground());
		renderer.getTableCellRendererComponent(table, null, ROW_SELECTED, false, 0, 0);

		// Equals comparison to expected Color doesn't work because setBackground(...) may slightly change the RGB values for the platform...
		// assertEquals(PowerRendererSwingTheme.primary3, label.getBackground());

		// ...So, we have to be satisfied with the weaker assertion that the color is merely changed
		assertNotEquals(Color.white, label.getBackground());
	}

	@Test
	public void testBackgroundSelectedToNotSelected() throws Exception {
		renderer.getTableCellRendererComponent(table, null, ROW_SELECTED, false, 0, 0);
		assertNotEquals(Color.white, label.getBackground()); // sanity check

		renderer.getTableCellRendererComponent(table, null, ROW_NOT_SELECTED, false, 0, 0);
		assertEquals(Color.white, label.getBackground());
	}

	@Test
	public void testGetCellRendererComponentWithDisplayLabel() throws Exception {
		for (int i = 0; i < enumVals.length; i++) {
			renderer.getTableCellRendererComponent(table, enumVals[i].getDisplayLabel(), false, false, 0, 0);
			assertEquals(enumVals[i].getDisplayLabel(), label.getText());
		}
	}

	@Test
	public void testGetCellRendererComponentWithNotFoundStringHasTheString() throws Exception {
		renderer.getTableCellRendererComponent(table, "bogusString", false, false, 0, 0);
		assertEquals("bogusString", label.getText());
	}
}
