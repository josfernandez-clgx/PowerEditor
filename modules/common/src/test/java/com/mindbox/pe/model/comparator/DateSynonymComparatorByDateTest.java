package com.mindbox.pe.model.comparator;

import static com.mindbox.pe.common.CommonTestObjectMother.createDateSynonym;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.unittest.AbstractTestBase;

public class DateSynonymComparatorByDateTest extends AbstractTestBase {

	@Test
	public void testCompareToWithNullAndNullReturnsZero() {
		assertEquals(0, DateSynonymComparatorByDate.getInstance().compare(null, null));
	}

	@Test
	public void testCompareToWithNullAndNotNullReturnsPositive() {
		assertTrue(0 < DateSynonymComparatorByDate.getInstance().compare(null, createDateSynonym()));
	}

	@Test
	public void testCompareToWithNotNullAndNullReturnsNegative() {
		assertTrue(0 > DateSynonymComparatorByDate.getInstance().compare(createDateSynonym(), null));
	}

	@Test
	public void testCompareToWithHappyCaseForEquality() {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(ds1.getDate());
		assertEquals(0, DateSynonymComparatorByDate.getInstance().compare(ds1, ds2));
		assertEquals(0, DateSynonymComparatorByDate.getInstance().compare(ds2, ds1));
	}

	@Test
	public void testCompareToHappyCaseForNonEquality() {
		DateSynonym ds1 = createDateSynonym();
		ds1.setDate(getDate(2005, 10, 10, 00, 00, 00));
		DateSynonym ds2 = createDateSynonym();
		ds2.setDate(getDate(2005, 10, 10, 00, 30, 00));
		assertTrue(0 > DateSynonymComparatorByDate.getInstance().compare(ds1, ds2));
		assertTrue(0 < DateSynonymComparatorByDate.getInstance().compare(ds2, ds1));
	}

}
