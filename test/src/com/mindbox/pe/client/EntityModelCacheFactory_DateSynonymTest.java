package com.mindbox.pe.client;

import javax.swing.ComboBoxModel;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;

public class EntityModelCacheFactory_DateSynonymTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityModelCacheFactory_DateSynonymTest Tests");
		suite.addTestSuite(EntityModelCacheFactory_DateSynonymTest.class);
		return suite;
	}

	public EntityModelCacheFactory_DateSynonymTest(String name) {
		super(name);
	}

	public void testAddDateSynonymWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(EntityModelCacheFactory.getInstance(), "addDateSynonym", new Class[]
			{ DateSynonym.class});
	}

	public void testAddDateSynonymWithNamedDateSynonymUpdatesCachedComboModels() throws Exception {
		ComboBoxModel modelWithEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(true);
		ComboBoxModel modelWithNoEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(false);

		DateSynonym ds = ObjectMother.createDateSynonym();
		EntityModelCacheFactory.getInstance().addDateSynonym(ds);

		assertEquals(2, modelWithEmptyValue.getSize());
		assertEquals(1, modelWithNoEmptyValue.getSize());
		assertEquals(2, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(true).getSize());
		assertEquals(1, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(false).getSize());
	}

	public void testResortNamedDateSynonymsUpdatesCachedComboModels() throws Exception {
		ComboBoxModel modelWithEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(true);
		ComboBoxModel modelWithNoEmptyValue = EntityModelCacheFactory.getInstance().createDateSynonymComboModel(false);

		DateSynonym ds = ObjectMother.createDateSynonym();
		invokeResortNamedDateSynonyms(new DateSynonym[]{ds});
		
		assertEquals(2, modelWithEmptyValue.getSize());
		assertEquals(1, modelWithNoEmptyValue.getSize());
		assertEquals(2, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(true).getSize());
		assertEquals(1, EntityModelCacheFactory.getInstance().getDateSynonymComboModel(false).getSize());
	}

	private void invokeResortNamedDateSynonyms(DateSynonym[] dateSynonyms) {
		ReflectionUtil.executePrivate(EntityModelCacheFactory.getInstance(), "resortNamedDateSynonyms", new Class[]
			{ DateSynonym[].class}, new Object[]
			{ dateSynonyms});
	}

	protected void setUp() throws Exception {
		super.setUp();
		ClientTestUtil.prepEntityModelCacheFactoryDateSynonymCache();
	}

	protected void tearDown() throws Exception {
		ClientTestUtil.clearEntityModelCacheFactoryDateSynonymCache();
		super.tearDown();
	}

}
