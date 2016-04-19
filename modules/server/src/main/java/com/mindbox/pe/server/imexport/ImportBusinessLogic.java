package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.DateDataElement.DateElement;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement.GuidelineAction;
import com.mindbox.pe.xsd.data.NextIDDataElement.NextId;
import com.mindbox.pe.xsd.data.TestConditionDataElement.TestCondition;
import com.mindbox.server.parser.jtb.rule.ParseException;

/**
 * Provides utility methods for importing various data.
 * 
 * @author kim
 * @see BizActionCoordinator
 */
public interface ImportBusinessLogic {

	void deleteAllEntityCompatibility(GenericEntityType type, int entityID) throws ServletActionException;

	void importCategory(GenericCategory category, boolean merge, User user) throws ImportException, DataValidationFailedException;

	void importCBRAttribute(CBRAttributeElement attribute, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws DataValidationFailedException, ImportException;

	void importCBRCase(CBRCaseElement cbrCase, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws DataValidationFailedException, ImportException;

	void importCBRCaseBase(CBRCaseBaseElement caseBase, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws DataValidationFailedException, ImportException;

	int importDateSynonym(DateElement dateSynonym, boolean merge, User user) throws ImportException, DataValidationFailedException;

	void importEntity(GenericEntity entity, boolean merge, User user) throws ImportException, DataValidationFailedException;

	void importEntityCompatibilityData(GenericEntityCompatibilityData compData, User user) throws ImportException, DataValidationFailedException;

	void importGridData(int templateID, List<ProductGrid> grids, User user) throws ImportException;

	void importGuidelineAction(GuidelineAction actionDef, boolean merge, Map<String, Integer> actionIDMap, User user, boolean updateIfExist) throws DataValidationFailedException,
			ServletActionException, ParseException;

	void importNextID(NextId nextId) throws ImportException, DataValidationFailedException;

	void importParameterGrid(ParameterGrid parameterGrid, User user) throws ImportException;

	void importRole(Role role, List<String> unknownPrivsForRole, User user) throws ImportException;

	void importTemplate(GridTemplate template, boolean merge, Map<Integer, Integer> idMap, User user) throws ServletActionException;

	void importTestCondition(TestCondition testCondition, boolean merge, Map<String, Integer> actionIDMap, User user, boolean updateIfExist) throws DataValidationFailedException,
			ServletActionException, ParseException;

	void importUser(User userToSave, User requester) throws ImportException;

	void updateRootCategoryDuringImport(String type, String newName, User user) throws ImportException;

	void validateTemplateForImport(GridTemplate template, boolean checkColumnNames, boolean merge) throws ImportException;
}
