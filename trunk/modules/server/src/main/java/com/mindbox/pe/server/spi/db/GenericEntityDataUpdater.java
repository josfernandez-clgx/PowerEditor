/*
 * Created on 2004. 2. 24.
 *  
 */
package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;
import java.util.Map;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

/**
 * Updates generic entity data.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public interface GenericEntityDataUpdater {

    /**
     * 
     * @param id
     * @param name
     * @param parent
     * @param categoryType
     * @throws SQLException
     * @since PowerEditor 3.1.0
     */
    void addCategory(int id, String name, int categoryType, MutableTimedAssociationKey[] parentAssocations) throws SQLException;
    
    void updateCategory(int id, String name, int categoryType, MutableTimedAssociationKey[] parentAssocations) throws SQLException;
    
    void deleteGenericEntity(int id, int type) throws SQLException;

    void insertGenericEntity(int id, int type, String name, int parentID, Map<String,Object> propertyMap, int categoryType, MutableTimedAssociationKey[] categoryAssociations) throws SQLException;

    void updateGenericEntity(int id, int type, String name, int parentID, Map<String,Object> propertyMap, int categoryType, MutableTimedAssociationKey[] categoryAssociations) throws SQLException;

    void insertEntityCompatibility(
            int entityType1,
            int entityID1,
            int entityType2,
            int entityID2,
            DateSynonym effectiveDate,
            DateSynonym expirationDate) throws SQLException;

    void updateEntityCompatibility(
            int entityType1,
            int entityID1,
            int entityType2,
            int entityID2,
            DateSynonym effectiveDate,
            DateSynonym expirationDate) throws SQLException;

    void deleteEntityCompatibility(int entityType1, int entityID1, int entityType2, int entityID2) throws SQLException;
    
    void deleteAllEntityCompatibility(int entityType1, int entityID1) throws SQLException;

}