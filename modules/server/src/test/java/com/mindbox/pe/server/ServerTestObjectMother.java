package com.mindbox.pe.server;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.TestObjectMother.getNextUniqueDate;
import static com.mindbox.pe.unittest.TestObjectMother.getNextUniqueId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.communication.DateSynonymInUseRequest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBRCaseClass;
import com.mindbox.pe.model.cbr.CBRScoringFunction;
import com.mindbox.pe.model.cbr.CBRValueRange;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.model.DomainClassLinkPattern;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.MutableAuditEvent;
import com.mindbox.pe.unittest.TestObjectMother;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement;

public class ServerTestObjectMother {

	public static RuleDefinition attachAction(RuleDefinition ruleDefinition, ActionTypeDefinition actionTypeDefinition) {
		RuleAction ruleAction = RuleElementFactory.getInstance().createRuleAction();
		ruleAction.setActionType(actionTypeDefinition);
		ruleDefinition.updateAction(ruleAction);
		return ruleDefinition;
	}

	public static GridTemplateColumn attachColumnDataSpecDigest(GridTemplateColumn gridTemplateColumn) {
		gridTemplateColumn.setDataSpecDigest(createColumnDataSpecDigest());
		return gridTemplateColumn;
	}

	public static Condition attachColumnReference(Condition condition) {
		condition.setValue(RuleElementFactory.getInstance().createValue(createColumnReference()));
		return condition;
	}

	public static DomainClass attachDomainAttributes(DomainClass domainClass, int count) {
		for (int i = 0; i < count; i++) {
			domainClass.addDomainAttribute(createDomainAttribute());
		}
		return domainClass;
	}

	public static EnumValues<EnumValue> attachEnumValue(EnumValues<EnumValue> enumValues, int count) {
		EnumValue[] values = createEnumValues(count);
		for (int i = 0; i < values.length; i++) {
			enumValues.add(values[i]);
		}
		return enumValues;
	}

	public static GridTemplate attachGridTemplateColumn(GridTemplate gridTemplate, int columnNo) {
		gridTemplate.addColumn(createGridTemplateColumn(columnNo, gridTemplate.getUsageType()));
		return gridTemplate;
	}

	public static GridTemplate attachGridTemplateColumns(GridTemplate gridTemplate, int columnCount) {
		for (int c = 1; c <= columnCount; c++) {
			gridTemplate.addColumn(createGridTemplateColumn(c, gridTemplate.getUsageType()));
		}
		return gridTemplate;
	}

	public static ParameterGrid attachParameterTemplate(ParameterGrid grid) {
		grid.setTemplate(createParameterTemplate());
		return grid;
	}

	public static Role attachPrivileges(Role role, int count) {
		for (int i = 0; i < count; i++) {
			role.addPrivilege(createPrivilege());
		}
		return role;
	}

	public static Condition attachReference(Condition condition) {
		condition.setReference(createReference());
		return condition;
	}

	public static TimeSliceContainer attachTimeSlice(TimeSliceContainer timeSliceContainer) {
		timeSliceContainer.add(TimeSlice.createInstance(createDateSynonym(), null));
		return timeSliceContainer;
	}

	public static ActionTypeDefinition createActionTypeDefinition() {
		int idToUse = getNextUniqueId();
		return new ActionTypeDefinition(idToUse, "Action-" + idToUse, "description of action " + idToUse);
	}

	/**
	 * Create an instance of {@link AuditEvent} of KB modification type.
	 * 
	 * @return
	 */
	public static MutableAuditEvent createAuditDataBuilderForKBMod() {
		MutableAuditEvent auditEvent = new MutableAuditEvent();
		auditEvent.setAuditID(createInt());
		auditEvent.setAuditType(AuditEventType.KB_MOD);
		auditEvent.setDate(new Date());
		auditEvent.setUserName("user-" + createString());
		auditEvent.setDescription(createString());
		return auditEvent;
	}

	public static CBRAttribute createCBRAttribute() {
		int id = createInt();
		return new CBRAttribute(id, "cbrAttr-" + id, "cbrAttr-" + id + " disp-desc");
	}

	public static CBRAttributeElement createCBRAttributeElement() {
		int id = createInt();
		final CBRAttributeElement cbrAttributeElement = new CBRAttributeElement();
		cbrAttributeElement.setId(id);
		cbrAttributeElement.setName("cbrAttr-" + id);
		cbrAttributeElement.setDescription("cbrAttr-" + id + " disp-desc");
		return cbrAttributeElement;
	}

	public static CBRAttributeType createCBRAttributeType() {
		int id = createInt();
		return new CBRAttributeType(id, "cbrAttrType-" + id, "cbrAttrType-" + id + " disp-name", "cbrAttrType-" + id + " desc");
	}

	public static CBRCaseAction createCBRCaseAction() {
		int id = createInt();
		return new CBRCaseAction(id, "cbrCaseAction-" + id, "cbrCaseAction-" + id + " disp-name");
	}

	public static CBRCaseBase createCBRCaseBase() {
		int id = createInt();
		return new CBRCaseBase(id, "caseBase-" + id, "caseBase-" + id + " disp-name");
	}

	public static CBRCaseBaseElement createCbrCaseBaseElement() {
		final CBRCaseBaseElement cbrCaseBaseElement = new CBRCaseBaseElement();
		cbrCaseBaseElement.setId(createInt());
		cbrCaseBaseElement.setName("caseBase-" + cbrCaseBaseElement.getId());
		cbrCaseBaseElement.setDescription("caseBase-" + cbrCaseBaseElement.getId() + "desc");
		return cbrCaseBaseElement;
	}

	public static CBRCaseClass createCBRCaseClass() {
		int id = createInt();
		return new CBRCaseClass(id, "CBRCaseClass-" + id, "CBRCaseClass-" + id + " disp-name");
	}

	public static CBRCaseElement createCbrCaseElement() {
		final CBRCaseElement cbrCaseElement = new CBRCaseElement();
		int id = createInt();
		cbrCaseElement.setId(id);
		cbrCaseElement.setName("case-" + id);
		cbrCaseElement.setDescription("case-" + id + "desc");
		return cbrCaseElement;
	}

	public static CBRScoringFunction createCBRScoringFunction() {
		int id = createInt();
		return new CBRScoringFunction(id, "CBRScoringFunction-" + id, "CBRScoringFunction-" + id + " disp-name");
	}

	public static CBRValueRange createCBRValueRange() {
		int id = createInt();
		return new CBRValueRange(id, "CBRValueRange-" + id, "CBRValueRange-" + id + " name", "CBRValueRange-" + id + " desc", false, false, false, false, false);
	}

	/**
	 * Creates a new instance of {@link ColumnDataSpecDigest} with {@link ColumnDataSpecDigest#TYPE_STRING}.
	 * 
	 * @return ColumnDataSpecDigest
	 */
	public static ColumnDataSpecDigest createColumnDataSpecDigest() {
		ColumnDataSpecDigest columnDataSpecDigest = new ColumnDataSpecDigest();
		columnDataSpecDigest.setIsBlankAllowed(true);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		// defaults to String column
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_STRING);
		return columnDataSpecDigest;
	}

	public static ColumnReference createColumnReference() {
		return RuleElementFactory.getInstance().createColumnReference(createInt());
	}

	public static ColumnReference createColumnReference(int colNum) {
		return RuleElementFactory.getInstance().createColumnReference(colNum);
	}

	public static Condition createCondition() {
		return RuleElementFactory.getInstance().createCondition();
	}

	public static DateSynonym createDateSynonym() {
		return new DateSynonym(getNextUniqueId(), "ds" + getNextUniqueId(), "ds desc for " + getNextUniqueId(), getNextUniqueDate());
	}

	public static DateSynonymInUseRequest createDateSynonymInUseRequest() {
		return new DateSynonymInUseRequest("demo", "session-" + (getNextUniqueId()), createDateSynonym());
	}

	public static DigestedObjectHolder createDigestedObjectHolder() {
		DigestedObjectHolder objectHolder = new DigestedObjectHolder();
		return objectHolder;
	}

	public static DomainAttribute createDomainAttribute() {
		int nextID = getNextUniqueId();
		DomainAttribute domainAttribute = new DomainAttribute();
		domainAttribute.setName("DomainAttribute" + nextID);
		domainAttribute.setDeployLabel("pe:deploy-label:" + nextID);
		domainAttribute.setDisplayLabel("Display Label " + nextID);
		domainAttribute.setDeployType(DeployType.SYMBOL);
		domainAttribute.setAllowRuleUsage("1");
		return domainAttribute;
	}

	public static DomainClass createDomainClass() {
		int nextID = getNextUniqueId();
		DomainClass dc = new DomainClass();
		dc.setName("DomainClass" + nextID);
		dc.setAllowRuleUsage("1");
		dc.setDeployLabel("pe:deploy-label" + nextID);
		dc.setDisplayLabel("Display Label " + nextID);
		return dc;
	}

	public static DomainClassLink createDomainClassLink() {
		DomainClassLink domainClassLink = new DomainClassLink();
		domainClassLink.setParentName("ParentClass" + createInt());
		domainClassLink.setChildName("ChildClass" + createInt());
		return domainClassLink;
	}

	public static DomainClassLinkPattern createDomainClassLinkPattern() {
		DomainClassLinkPattern domainClassLinkPattern = new DomainClassLinkPattern(createDomainClassLink());
		return domainClassLinkPattern;
	}

	public static ColumnDataSpecDigest createEntityColumnDataSpecDigest(String entityType, boolean entityAllowed, boolean categoryAllowed, boolean multiSelect) {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENTITY);
		columnDataSpecDigest.setEntityType(entityType);
		columnDataSpecDigest.setIsEntityAllowed(entityAllowed);
		columnDataSpecDigest.setIsCategoryAllowed(categoryAllowed);
		columnDataSpecDigest.setIsMultiSelectAllowed(multiSelect);
		return columnDataSpecDigest;
	}

	public static EnumValue createEnumValue() {
		EnumValue enumVal = new EnumValue();
		Integer integer = new Integer(getNextUniqueId());
		enumVal.setDeployID(integer);
		enumVal.setDeployValue("deployValue:" + integer);
		enumVal.setDisplayLabel("displayLabel:" + integer);
		return enumVal;
	}

	public static EnumValues<EnumValue> createEnumValues() {
		return new EnumValues<EnumValue>();
	}

	public static EnumValue[] createEnumValues(int count) {
		EnumValue[] enumVals = new EnumValue[count];
		for (int i = 0; i < count; i++) {
			enumVals[i] = createEnumValue();
		}
		return enumVals;
	}

	public static List<EnumValue> createEnumValuesAsList(int count) {
		return Arrays.asList(createEnumValues(count));
	}

	public static ExistExpression createExistExpression() {
		ExistExpression existExpression = RuleElementFactory.getInstance().createExistExpression("exist-class-" + createString());
		return existExpression;
	}

	public static FunctionParameterDefinition createFunctionParameterDefinition() {
		int idToUse = getNextUniqueId();
		return new FunctionParameterDefinition(idToUse, "name");
	}

	public static GenericCategory createGenericCategory(GenericEntityType type) {
		return new GenericCategory(getNextUniqueId(), "category-name" + getNextUniqueId(), type.getCategoryType());
	}

	public static GenericEntity createGenericEntity(GenericEntityType type) {
		return new GenericEntity(getNextUniqueId(), type, "entity-name:" + getNextUniqueId());
	}

	public static GridTemplate createGridTemplate(TemplateUsageType usageType) {
		return new GridTemplate(getNextUniqueId(), "template-name-" + createString(), usageType);
	}

	public static GridTemplateColumn createGridTemplateColumn(int columnNo, TemplateUsageType usageType) {
		GridTemplateColumn gridTemplateColumn = new GridTemplateColumn(columnNo, "col" + columnNo, "column " + columnNo, 100, usageType);
		gridTemplateColumn.setColor("default");
		gridTemplateColumn.setTitle(gridTemplateColumn.getName() + " Title");
		gridTemplateColumn.setFont("arial");
		return gridTemplateColumn;
	}

	/**
	 * Creates an instanceof {@link GuidelineGenerateParams}. Note: {@link TemplateUsageType} must have at least one
	 * instance defined before calling this.
	 * 
	 * @return
	 */
	public static GuidelineGenerateParams createGuidelineGenerateParams() {
		return createGuidelineGenerateParams(TemplateUsageType.getAllInstances()[0]);
	}

	/**
	 * Creates an instanceof {@link GuidelineGenerateParams}. Note: {@link TemplateUsageType} must have at least one
	 * instance defined before calling this. Equivalent to <code>createGuidelineGenerateParams(usageType, false);
	 */
	public static GuidelineGenerateParams createGuidelineGenerateParams(TemplateUsageType usageType) {
		return createGuidelineGenerateParams(usageType, false);
	}

	/**
	 * Creates an instanceof {@link GuidelineGenerateParams}. Note: {@link TemplateUsageType} must have at least one
	 * instance defined before calling this.
	 * 
	 * @param usageType usage type
	 * @param spansMultiple set to <code>true</code> if this spans multiple activations
	 */
	public static GuidelineGenerateParams createGuidelineGenerateParams(TemplateUsageType usageType, boolean spansMultiple) {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(usageType));
		grid.setNumRows(1);
		GuidelineGenerateParams generateParams;
		try {
			generateParams = new GuidelineGenerateParams(null, null, grid, -1, 1, spansMultiple);
			generateParams.setName("RuleName-" + TestObjectMother.createString());
		}
		catch (InvalidDataException e) {
			throw new RuntimeException(e);
		}
		return generateParams;
	}

	public static ProductGrid createGuidelineGrid(GridTemplate template) {
		ProductGrid grid = new ProductGrid(getNextUniqueId(), template, null, null);
		return grid;
	}

	public static ProductGrid createGuidelineGrid(TemplateUsageType usageType) {
		ProductGrid grid = new ProductGrid(getNextUniqueId(), new GridTemplate(getNextUniqueId(), "test", usageType), null, null);
		return grid;
	}

	public static IntegerRange createIntegerRange(int i1, int i2) {
		IntegerRange result = new IntegerRange();
		result.setLowerValue(new Integer(i1 <= i2 ? i1 : i2));
		result.setUpperValue(new Integer(i1 <= i2 ? i2 : i1));
		return result;
	}

	public static MutableTimedAssociationKey createMutableTimedAssociationKey() {
		return new DefaultMutableTimedAssociationKey(getNextUniqueId(), null, null);
	}

	public static ParameterGrid createParameterGrid() {
		ParameterGrid grid = new ParameterGrid(getNextUniqueId(), getNextUniqueId(), null, null);
		return grid;
	}

	public static ParameterTemplate createParameterTemplate() {
		ParameterTemplate template = new ParameterTemplate(getNextUniqueId(), "test", -1, "test");
		return template;
	}

	public static Privilege createPrivilege() {
		int idToUse = getNextUniqueId();
		return new Privilege(idToUse, "privilege" + idToUse, "Privilege " + idToUse, PrivilegeConstants.HARD_CODED_PRIV);
	}

	public static Reference createReference() {
		return createReference("class" + createInt(), "attribute" + createInt());
	}

	public static Reference createReference(DomainClass domainClass) {
		return createReference(domainClass.getName(), ((DomainAttribute) domainClass.getDomainAttributes().get(0)).getName());
	}

	public static Reference createReference(String className, String attributeName) {
		Reference reference = RuleElementFactory.getInstance().createReference(className, attributeName);
		return reference;
	}

	public static Role createRole() {
		int nextID = getNextUniqueId();
		return new Role(nextID, "role-" + nextID, new ArrayList<Privilege>());
	}

	public static RuleDefinition createRuleDefinition() {
		int idToUse = getNextUniqueId();
		return new RuleDefinition(idToUse, "Rule-" + idToUse, "description of rule " + idToUse);
	}

	public static TimeSliceContainer createTimeSliceContainer() {
		TimeSliceContainer timeSliceContainer = new TimeSliceContainer();
		return timeSliceContainer;
	}

	@SuppressWarnings("unchecked")
	public static TemplateUsageType createUsageType() {
		int idToUse = getNextUniqueId();
		TemplateUsageType usageType = TemplateUsageType.createInstance("usage" + idToUse, "Usage " + idToUse, "Privilege" + idToUse);
		((Map<String, TemplateUsageType>) ReflectionUtil.getStaticPrivate(TemplateUsageType.class, "knownTypes")).clear();
		return usageType;
	}

	public static User createUser() {
		int id = getNextUniqueId();
		User user = new User("demo" + id, "demo" + id, "active", false, 0, null, null);
		user.setPassword("demo" + id);
		return user;
	}

	private ServerTestObjectMother() {
	}
}
