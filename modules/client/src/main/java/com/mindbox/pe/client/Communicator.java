package com.mindbox.pe.client;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.ExternalEnumSourceDetail;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.IntegerPair;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.report.AbstractReportSpec;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Communicator is responsible for handling communication with the server.
 * @author Gene Kim
 * @author MindBox
 * @since 1.0
 */
public interface Communicator {

	void bulkSaveGridData(List<GuidelineReportData> data, String status, DateSynonym eff, DateSynonym exp) throws ServerException;

	boolean checkNameForUniqueness(PeDataType entityType, String name) throws ServerException;

	void clearFailedLoginCounter(final String userID) throws ServerException;

	int clone(GenericEntity object, boolean copyPolicies, boolean lock) throws ServerException;

	/**
	 * Clone a case base and its sub-objects
	 * @param oldCaseBaseID oldCaseBaseID
	 * @param newCaseBaseName newCaseBaseName
	 * @return The new case base id.
	 * @throws ServerException
	 * @since 4.1.0
	 */
	int cloneCaseBases(int oldCaseBaseID, String newCaseBaseName) throws ServerException;

	/**
	 * Clones all guideline activations for the specified template for the new template id.
	 * @param oldTemplateID oldTemplateID
	 * @param newTemplateID newTemplateID
	 * @throws ServerException
	 * @since 4.0.0
	 */
	void cloneGuidelines(int oldTemplateID, int newTemplateID) throws ServerException;

	/**
	 * 
	 * @param data data
	 * @throws ServerException
	 * @since 3.0.0
	 */
	void delete(GenericEntityCompatibilityData data) throws ServerException;

	/**
	 * @since 3.0.0
	 */
	void delete(int entityID, GenericEntityType type) throws ServerException;

	void delete(int entityID, PeDataType entityType) throws ServerException;

	/**
	 * 
	 * @param categoryType categoryType
	 * @param categoryID categoryID
	 * @throws ServerException
	 * @since 3.1.0
	 */
	void deleteGenericCategory(int categoryType, int categoryID) throws ServerException;

	/**
	 * Removes the specified template.
	 * It <code>deleteGuidelines</code> is <code>true</code>, this delete all guidelines for the template as well.
	 * @param templateID templateID
	 * @param deleteGuidelines deleteGuidelines
	 * @throws ServerException on error
	 * @since 4.3.7
	 */
	void deleteTemplate(int templateID, boolean deleteGuidelines) throws ServerException;

	void deleteUser(UserData user) throws ServerException;

	DeployResponse deploy(GuidelineReportFilter filter, boolean exportPolicies) throws ServerException;

	void enableUser(String userID) throws ServerException;

	/**
	 * This is called when export data is to be written on client machine
	 * @param filter filter
	 * @return byte array which contains the file to be written
	 * @throws ServerException
	 */
	byte[] exportDataToClient(GuidelineReportFilter filter) throws ServerException;

	/**
	 * This is called when export data is to be written on server machine
	 * @param filter filter
	 * @param filename the filename to save
	 * @return int
	 * @throws ServerException
	 */
	// TODO Kim: remove this; and do this on the server
	int exportDataToServer(GuidelineReportFilter filter, String filename) throws ServerException;

	Persistent fetch(int entityID, PeDataType entityType, boolean lockEntity) throws ServerException;

	/**
	 * 
	 * @param entityType1 entityType1
	 * @param entityType2 entityType2
	 * @return list of the specified compatibility data
	 * @throws ServerException
	 * @since 3.0.0
	 */
	List<GenericEntityCompatibilityData> fetchCompatibilityData(GenericEntityType entityType1, GenericEntityType entityType2) throws ServerException;

	/**
	 * Retrieves custom report names.
	 * @return a list of report names
	 * @throws ServerException on error
	 */
	List<String> fetchCustomReportNames() throws ServerException;

	/**
	 * Fetchs the full context for the specified subcontext of the specified template.
	 * @param templateID the template id
	 * @param subContext sub-context
	 * @return full context
	 * @throws ServerException on error
	 * @since 4.2.0
	 */
	GuidelineContext[] fetchFullContext(int templateID, GuidelineContext[] subContext) throws ServerException;

	GridDataResponse fetchGridData(int templateID, GuidelineContext[] contexts) throws ServerException;

	List<GridSummary> fetchGridSummaries(TemplateUsageType usageType, GuidelineContext[] contexts) throws ServerException;

	/**
	 * Gets parameters for the specified template
	 * @param templateID the id of the template
	 * @return list of the parameters for the specified template
	 * @since 2.2.0
	 */
	List<ParameterGrid> fetchParameters(int templateID) throws ServerException;

	List<GridTemplate> fetchTemplateSummaries(TemplateUsageType s) throws ServerException;

	/**
	 * Gets 
	 * @param templateID templateID
	 * @param cutoverDate cutoverDate
	 * @return list of two lists of instances of {@link com.mindbox.pe.model.GuidelineReportData}; 
	 *         first list contains guidelines to cut over; second those to remain as is
	 * @throws ServerException
	 * @since 4.2.0
	 */
	List<List<GuidelineReportData>> findCutoverGuidelines(int templateID, DateSynonym cutoverDate) throws ServerException;

	/**
	 * Generates policy summary report.
	 * @param reportSpec reportSpec
	 * @param guidelines guidelines
	 * @return generate report content as byte array
	 * @throws ServerException
	 */
	byte[] generatePolicySummaryReport(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException;

	/**
	 * Generates reports of the specified guidelines and returns the URL of the report to display.
	 * @param reportSpec report specification
	 * @param guidelines guidelines
	 * @return report URL
	 * @throws ServerException on error generating the report
	 */
	String generateReportURL(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException;

	List<EnumValue> getAllEnumValuesFromEnumerationSource(String sourceName) throws ServerException;

	List<EnumValue> getApplicableEnumValuesFromEnumerationSource(String sourceName, String selectorValue) throws ServerException;

	String getDeployErrorString(int runID) throws ServerException;

	byte[] getDomainDefintionXML() throws ServerException;

	List<ExternalEnumSourceDetail> getEnumerationSourceDetails() throws ServerException;

	long getNextRuleID() throws ServerException;

	boolean hasDeployRule(int templateID, int columnID) throws ServerException;

	/**
	 * Tests if the specified template has any guidelines.
	 * @param templateID templateID
	 * @return <code>true</code> if template with the specified id has at least one guideline; <code>false</code>, otherwise
	 * @throws ServerException on error
	 * @since 4.3.7
	 */
	boolean hasGuidelines(int templateID) throws ServerException;

	/**
	 * Import items from the specified files.
	 * @param importSpec import specification
	 * @return ImportResult class
	 * @throws ServerException on server error
	 * @throws IOException on I/O error
	 */
	public ImportResult importData(ImportSpec importSpec) throws ServerException, IOException;

	boolean isExistingCompatibility(GenericEntityType type1, int id1, GenericEntityType type2, int id2) throws ServerException;

	boolean isInUse(DateSynonym dateSynonym) throws ServerException;

	/**
	 * @since 3.0.0
	 */
	void lock(int entityID, GenericEntityType type) throws ServerException;

	void lock(int entityID, PeDataType entityType) throws ServerException;

	void lockGrid(int templateID, GuidelineContext[] contexts) throws ServerException;

	void lockUser(String userID) throws ServerException;

	void logout() throws ServerException;

	/**
	 * Makes a new version of template for the specified cutover date.
	 * Guidelines for the old template that is activate on the cutover date will be cut over to the new template.
	 * That is, their expiration date is set to the cutover date and a new guideline is created with activation date
	 * of the cutover date and the expiration date of the guideline.
	 * @param oldTemplateID source template id
	 * @param newVersion a new version template
	 * @param cutoverDate the cutover date
	 * @return new template ID
	 * @throws ServerException on error
	 * @since 4.2.0
	 */
	int makeNewVersion(int oldTemplateID, GridTemplate newVersion, DateSynonym cutoverDate, List<GuidelineReportData> guidelinesToCutOver) throws ServerException;

	/**
	 * Refresh PowerEditor configuration, including all cached objects.
	 * @return <code>null</code> on success; error message, otherwise
	 * @throws ServerException on error
	 * @since 4.5.0
	 */
	String reloadConfiguration() throws ServerException;

	/**
	 * Reloads user data from the data source
	 * @return a list of {@link UserData} objects
	 * @throws ServerException on error
	 * @since 4.5.0
	 */
	List<UserData> reloadUserData() throws ServerException;

	void replace(DateSynonym[] toBeReplaced, DateSynonym replacement) throws ServerException;

	GenerateStats retrieveDeployStats(int i) throws ServerException;

	/**
	 * Equivalent to <code>save(object, lock, true)</code>.
	 * @param object object
	 * @param lock lock
	 * @return id
	 * @throws ServerException
	 */
	int save(Persistent object, boolean lock) throws ServerException;

	int save(Persistent object, boolean lock, boolean validate) throws ServerException;

	Map<IntegerPair, Integer> saveGridData(int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids) throws ServerException;

	<T extends Persistent> List<T> search(SearchFilter<T> searchFilter) throws ServerException;

	void unlock(int entityID, GenericEntityType entityType) throws ServerException;

	void unlock(int entityID, PeDataType entityType) throws ServerException;

	void unlockGrid(int templateID, GuidelineContext[] contexts) throws ServerException;

	void unlockUser(String userID) throws ServerException;

	/**
	 * Updates contexts of the specified grids to the new contexts.
	 * @param templateID template id of the grids' template
	 * @param grids grids of which context to update
	 * @param newContexts new contexts
	 * @throws ServerException on error
	 * @since 4.2.0
	 */
	void updateGridContext(int templateID, List<ProductGrid> grids, GuidelineContext[] newContexts) throws ServerException;

	/**
	 * Validates that the new date for the given date synonym is valid.
	 * @param dateSynonymId date synonym id
	 * @param newDate new date
	 * @return empty list if the date if valid; non-empty list of would-be-invalid guidelines, otherwise
	 * @throws ServerException on error
	 * @since 5.9.1
	 */
	List<GuidelineReportData> validateDateSynonymDateChange(int dateSynonymId, Date newDate) throws ServerException;

}
