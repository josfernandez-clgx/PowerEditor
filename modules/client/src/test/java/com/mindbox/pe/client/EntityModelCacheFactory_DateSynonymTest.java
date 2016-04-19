package com.mindbox.pe.client;

import static com.mindbox.pe.client.ClientTestObjectMother.createDateSynonym;
import static org.junit.Assert.assertEquals;

import javax.swing.ComboBoxModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EntityModelCacheFactory_DateSynonymTest extends AbstractTestBase {

	@Test(expected = NullPointerException.class)
	public void testAddDateSynonymWithNullThrowsNullPointerException() throws Exception {
		EntityModelCacheFactory.getInstance().addDateSynonym(null);
	}

	@Test
	public void testAddDateSynonymWithNamedDateSynonymUpdatesCachedComboModels() throws Exception {
		ComboBoxModel modelWithEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(true);
		ComboBoxModel modelWithNoEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(false);

		DateSynonym ds = createDateSynonym();
		EntityModelCacheFactory.getInstance().addDateSynonym(ds);

		assertEquals(2, modelWithEmptyValue.getSize());
		assertEquals(1, modelWithNoEmptyValue.getSize());
		assertEquals(2, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(true).getSize());
		assertEquals(1, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(false).getSize());
	}

	@Test
	public void testResortNamedDateSynonymsUpdatesCachedComboModels() throws Exception {
		ComboBoxModel modelWithEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(true);
		ComboBoxModel modelWithNoEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(false);

		DateSynonym ds = createDateSynonym();
		invokeResortNamedDateSynonyms(new DateSynonym[] { ds });

		assertEquals(2, modelWithEmptyValue.getSize());
		assertEquals(1, modelWithNoEmptyValue.getSize());
		assertEquals(2, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(true).getSize());
		assertEquals(1, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(false).getSize());
	}

	private void invokeResortNamedDateSynonyms(DateSynonym[] dateSynonyms) {
		ReflectionUtil.executePrivate(EntityModelCacheFactory.getInstance(), "resortNamedDateSynonyms", new Class[] { DateSynonym[].class }, new Object[] { dateSynonyms });
	}


	@Before
	public void setUp() throws Exception {
		ClientTestUtil.prepEntityModelCacheFactoryDateSynonymCache();
	}

	@After
	public void tearDown() throws Exception {
		ClientTestUtil.clearEntityModelCacheFactoryDateSynonymCache();
	}

}
