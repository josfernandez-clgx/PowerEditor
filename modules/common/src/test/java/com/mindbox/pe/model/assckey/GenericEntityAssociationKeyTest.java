package com.mindbox.pe.model.assckey;

import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * Unit tests for {@link GenericEntityAssociationKey}.
 */
public class GenericEntityAssociationKeyTest extends AbstractTestBase {

	@Test
	public void testGetIDReturnsZero() throws Exception {
		GenericEntityAssociationKey data = new GenericEntityAssociationKey(null, (int) System.currentTimeMillis(), null, null);
		assertEquals(0, data.getID());
	}

	@Test
	public void testSetEffectiveDates() throws Exception {
		DateSynonym eff = DateSynonym.createUnnamedInstance(getDate(2006, 1, 1));
		DateSynonym exp = DateSynonym.createUnnamedInstance(getDate(2006, 9, 1));

		GenericEntityAssociationKey data = new GenericEntityAssociationKey(null, (int) System.currentTimeMillis(), null, null);
		data.setEffectiveDate(eff);
		data.setExpirationDate(exp);
		assertEquals(data.getEffectiveDate(), eff);
		assertEquals(data.getExpirationDate(), exp);
	}

}
