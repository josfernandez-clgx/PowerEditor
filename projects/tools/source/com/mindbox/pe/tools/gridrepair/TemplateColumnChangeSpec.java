/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.gridrepair;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface TemplateColumnChangeSpec {

	/**
	 * Gets the id of the template changed.
	 * @return template id
	 */
	int getTemplateID();
	
	/**
	 * Gets a list of removed columns.
	 * @return list of removed columns
	 */
	int[] removedColumns();
	
	/**
	 * Gets a list of positions where new columns are added.
	 * O position means a column was added at the beginning, making old column 1 column 2. 
	 * 3 position means a new column was added <b>after</b> column 3, making old column 4, column 5.
	 * @return positions of new columns
	 */
	int[] addedColumnPositions();

}
