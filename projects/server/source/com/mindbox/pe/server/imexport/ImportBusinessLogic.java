package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.imexport.digest.NextIDSeed;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.server.parser.jtb.rule.ParseException;

/**
 * Provides utility methods for importing various data.
 * 
 * @author kim
 * @see BizActionCoordinator
 */
public interface ImportBusinessLogic {

	void deleteAllEntityCompatibility(GenericEntityType type, int entityID) throws ServletActionException;

	void importActionTypeDefinition(ActionTypeDefinition actionDef, boolean merge, Map<String, Integer> actionIDMap, User user,
			boolean updateIfExist) throws ServletActionException, ParseException;

	void importCategory(GenericCategory category, boolean merge, User user) throws ImportException, DataValidationFailedException;

	void importCBRAttribute(CBRAttribute attribute, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws ImportException;

	void importCBRCase(CBRCase cbrCase, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws ImportException;

	void importCBRCaseBase(CBRCaseBase caseBase, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws ImportException;

	int importDateSynonym(DateSynonym dateSynonym, User user) throws ImportException, DataValidationFailedException;

	void importEntity(GenericEntity entity, boolean merge, User user) throws ImportException, DataValidationFailedException;

	void importEntityCompatibilityData(GenericEntityCompatibilityData compData, User user) throws ImportException,
			DataValidationFailedException;

	void importGridData(int templateID, List<ProductGrid> grids, User user) throws ImportException;

	void importNextID(NextIDSeed nextIDSeed) throws ImportException, DataValidationFailedException;

	void importParameterGrid(ParameterGrid parameterGrid, User user) throws ImportException;

	void importRole(Role role, List<String> unknownPrivsForRole, User user) throws ImportException;

	void importTemplate(GridTemplate template, boolean merge, Map<Integer, Integer> idMap, User user) throws ServletActionException;

	void importTestTypeDefinition(TestTypeDefinition testDef, boolean merge, Map<String, Integer> actionIDMap, User user,
			boolean updateIfExist) throws ServletActionException, ParseException;

	void importUser(User userToSave, User requester) throws ImportException;

	void updateRootCategoryDuringImport(String type, String newName, User user) throws ImportException;

	void validateTemplateForImport(GridTemplate template, boolean checkColumnNames, boolean merge) throws ImportException;
}
