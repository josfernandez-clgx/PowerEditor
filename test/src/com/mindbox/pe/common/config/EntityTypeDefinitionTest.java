package com.mindbox.pe.common.config;

import com.mindbox.pe.AbstractTestBase;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EntityTypeDefinitionTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityTypeDefinitionTest Tests");
		suite.addTestSuite(EntityTypeDefinitionTest.class);
		return suite;
	}
	
	private EntityTypeDefinition entityTypeDefinition = null;

	public EntityTypeDefinitionTest(String name) {
		super(name);
	}

	public void testConstructorSetsInvariantsPropertly() throws Exception {
		assertTrue(entityTypeDefinition.uniqueEntityNames());
		assertTrue(entityTypeDefinition.uniqueCategoryNames());
		assertTrue(entityTypeDefinition.canBelongToMultipleCategories());
		assertFalse(entityTypeDefinition.canClone());
		assertFalse(entityTypeDefinition.useInCompatibility());
		assertFalse(entityTypeDefinition.useInContext());
		assertFalse(entityTypeDefinition.useInMessageContext());	
	}
	
	public void testHasCategoryWithPositiveCategoryIDReturnsTrue() throws Exception {
		entityTypeDefinition.setCategoryType(1);
		assertTrue(entityTypeDefinition.hasCategory());
	}
	
	public void testHasCategoryWithNonPositiveCategoryIDReturnsFalse() throws Exception {
		entityTypeDefinition.setCategoryType(0);
		assertFalse(entityTypeDefinition.hasCategory());
		entityTypeDefinition.setCategoryType(-1);
		assertFalse(entityTypeDefinition.hasCategory());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		entityTypeDefinition = new EntityTypeDefinition();
	}

    public void testCanBelongToMultipleCategories() throws Exception {
        entityTypeDefinition.setCanBelongToMultipleCategories(true);
        assertTrue(entityTypeDefinition.canBelongToMultipleCategories());
        entityTypeDefinition.setCanBelongToMultipleCategories(false);
        assertFalse(entityTypeDefinition.canBelongToMultipleCategories());        
    }
    
    public void testUniqueCategoryNames() throws Exception {
        entityTypeDefinition.setUniqueCategoryNames(true);
        assertTrue(entityTypeDefinition.uniqueCategoryNames());
        entityTypeDefinition.setUniqueCategoryNames(false);
        assertFalse(entityTypeDefinition.uniqueCategoryNames());
    }
    
    
	protected void tearDown() throws Exception {
		// Tear downs for EntityTypeDefinitionTest
		super.tearDown();
	}
}
