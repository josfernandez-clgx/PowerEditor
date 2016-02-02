package com.mindbox.pe.server.spi.db;

import java.util.Map;
import java.util.Set;

import com.mindbox.pe.model.DateSynonym;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public interface EntityDataHolder {

    /**
     * 
     * @param categoryType
     * @param categoryID
     * @param categoryName
     * @param parent
     * @since PowerEditor 3.1.0
     */
    void addGenericEntityCategory(int categoryType, int categoryID, String categoryName);
    
    /**
     * Adds the specified parent relationship to the specified category.
     * @param categoryType category type
     * @param categoryID category id
     * @param parentID new parent id
     * @param effectiveDateID effective date id
     * @param expirationDateID expiration date id
     */
    void addParentAssociation(int categoryType, int categoryID, int parentID, int effectiveDateID, int expirationDateID);
    
    /**
     * 
     * @param categoryID
     * @param categoryType
     * @param entityID
     * @param entityType
     * @param effectiveDateID effective date id
     * @param expirationDateID expiration date id
     * @since PowerEditor 3.1.0
     */
    void addGenericEntityToCategory(int categoryID, int categoryType, int entityID, int entityType, int effectiveDateID, int expirationDateID);
    
    /**
     * 
     * @param categoryIDs
     * @param categoryType
     * @param entityID
     * @param entityType
     * @param effectiveDateID effective date id
     * @param expirationDateID expiration date id
     * @since PowerEditor 3.1.0
     */
    void addGenericEntityToCategories(int[] categoryIDs, int categoryType, int entityID, int entityType, int effectiveDateID, int expirationDateID);
    
    /**
     * Adds the specified generic entity.
     * 
     * @param entityID
     * @param entityType
     * @param name
     * @param propertyMap
     * @since 3.0.0
     */
    void addGenericEntity(int entityID, int entityType, String name, int parentID, Map<String,Object> propertyMap);

    /**
     * Adds the specified entity compatibility information.
     * 
     * @param entityType1
     * @param entityID1
     * @param entityType2
     * @param entityID2
     * @param effectiveDate
     * @param expirationDate
     * @since 3.0.0
     */
    void addEntityCompatibility(
            int entityType1,
            int entityID1,
            int entityType2,
            int entityID2,
            DateSynonym effectiveDate,
            DateSynonym expirationDate);
    
	/**
	 * Gets a set of {@link DateSynonym} instances used in category-category relationship chanages for the specified category type.
	 * Note that this only looks at parent assocation keys since child associations are driven by parent associations.
	 * @param categoryType category type
	 * @return a set of {@link DateSynonym} instances
	 */
   Set<DateSynonym> getDateSynonymsForChangesInCategoryRelationships(int categoryType);

}