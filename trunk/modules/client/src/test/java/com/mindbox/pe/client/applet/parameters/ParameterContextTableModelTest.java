package com.mindbox.pe.client.applet.parameters;

import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.grid.ParameterGrid;


public class ParameterContextTableModelTest extends AbstractClientTestBase {

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		final int numberOfEntities = ClientUtil.getEntityConfigHelper().getEntityTypeDefinitions().size();
		final ParameterContextTableModel model = new ParameterContextTableModel();

		final Date effdate = getDate(2006, 10, 10);
		final Date expdate = getDate(2006, 11, 11);
		final DateSynonym ds1 = new DateSynonym(1, "ds1", "ds1", effdate);
		final DateSynonym ds2 = new DateSynonym(1, "ds2", "ds2", expdate);

		final ParameterGrid data = new ParameterGrid(0, 0, ds1, ds2);

		model.addParameterGrid(data);

		assertEquals(ds1.getName(), model.getValueAt(0, numberOfEntities));
		assertEquals(ds2.getName(), model.getValueAt(0, numberOfEntities + 1));
	}
}
