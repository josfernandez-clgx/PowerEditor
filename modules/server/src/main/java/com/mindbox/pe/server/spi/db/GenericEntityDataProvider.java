/*
 * Created on 2004. 2. 24.
 *
 */
package com.mindbox.pe.server.spi.db;

import java.sql.SQLException;

/**
 * Provider of generic entity data.
 * A part of PEDBC framework.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public interface GenericEntityDataProvider {

	/**
	 * 
	 * @param entityDataHolder entityDataHolder
	 * @throws SQLException on error
	 * @since 3.1.0
	 */
	void loadCategories(EntityDataHolder entityDataHolder) throws SQLException;

	/**
	 * 
	 * @param entityDataHolder entityDataHolder
	 * @throws SQLException on error
	 * @since 5.1.0
	 */
	void loadCategoryParents(EntityDataHolder entityDataHolder) throws SQLException;

	void loadEntityCompaitilityMatrix(EntityDataHolder entityDataHolder) throws SQLException;

	/**
	 * 
	 * @param entityDataHolder entityDataHolder
	 * @throws SQLException on error
	 * @since 3.1.0
	 */
	void loadEntityToCategories(EntityDataHolder entityDataHolder) throws SQLException;

	void loadGenericEntities(EntityDataHolder entityDataHolder) throws SQLException;
}
