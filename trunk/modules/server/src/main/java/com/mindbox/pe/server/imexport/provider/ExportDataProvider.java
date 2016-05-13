package com.mindbox.pe.server.imexport.provider;

import java.util.List;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.imexport.ExportException;
import com.mindbox.pe.server.model.User;
/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public interface ExportDataProvider {

	/**
	 * Gets date synonym of the specified id from server cache.
	 * @param id the id of the date synonym to retrieve
	 * @return date synonym; <code>null</code>, if not found
	 * @throws ExportException on error
	 * @since PowerEditor 4.2.0
	 */
	DateSynonym getDateSynonym(int id) throws ExportException;
	
	List<GridTemplate> getAllGuidelineTemplates() throws ExportException;
	
	List<ProductGrid> getGuidelineGrids(int templateID) throws ExportException;
	
	List<ParameterTemplate> getAllParameterTemplates() throws ExportException;
	
	List<ParameterGrid> getParameterGrids(int templateID) throws ExportException;
	
	Privilege[] getAllPrivileges() throws ExportException;
	
	Role[] getAllRoles() throws ExportException;
	
	User[] getAllUsers() throws ExportException;

	String getCellValue(AbstractGrid<?> grid, int row, int col, String defaultValue) throws ExportException;	
}
