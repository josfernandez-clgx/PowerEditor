package com.mindbox.pe.common;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.TestObjectMother.getNextUniqueDate;
import static com.mindbox.pe.unittest.TestObjectMother.getNextUniqueId;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.ParameterTemplate;
import com.mindbox.pe.xsd.config.EntityProperty;
import com.mindbox.pe.xsd.config.EntityPropertyType;
import com.mindbox.pe.xsd.config.EntityType;

public class CommonTestObjectMother {

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

	public static MutableTimedAssociationKey attachEffectiveDateSynonym(MutableTimedAssociationKey key) {
		key.setEffectiveDate(createDateSynonym());
		return key;
	}

	public static EnumValues<EnumValue> attachEnumValue(EnumValues<EnumValue> enumValues, int count) {
		EnumValue[] values = createEnumValues(count);
		for (int i = 0; i < values.length; i++) {
			enumValues.add(values[i]);
		}
		return enumValues;
	}

	public static MutableTimedAssociationKey attachExpirationDateSynonym(MutableTimedAssociationKey key) {
		key.setExpirationDate(createDateSynonym());
		return key;
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

	public static Condition attachReference(Condition condition) {
		condition.setReference(createReference());
		return condition;
	}

	public static ActionTypeDefinition createActionTypeDefinition() {
		int idToUse = getNextUniqueId();
		return new ActionTypeDefinition(idToUse, "Action-" + idToUse, "description of action " + idToUse);
	}

	public static CBRAttribute createCBRAttribute() {
		int id = createInt();
		return new CBRAttribute(id, "cbrAttr-" + id, "cbrAttr-" + id + " disp-desc");
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

	public static CBRCaseClass createCBRCaseClass() {
		int id = createInt();
		return new CBRCaseClass(id, "CBRCaseClass-" + id, "CBRCaseClass-" + id + " disp-name");
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

	public static DateRange createDateRange() {
		return createDateRangeCurrent();
	}

	public static DateRange createDateRangeCurrent() {
		DateRange result = new DateRange();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
		result.setLowerValue(cal.getTime());

		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3); //reset Month just so there is more than one field different

		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
		result.setUpperValue(cal.getTime());

		return result;
	}

	public static DateRange createDateRangeFuture() {
		DateRange result = new DateRange();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 5);
		result.setLowerValue(cal.getTime());

		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 5); //reset Day just so there is more than one field different

		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 2);
		result.setUpperValue(cal.getTime());

		return result;
	}

	public static DateRange createDateRangePast() {
		DateRange result = new DateRange();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) - 2);
		result.setUpperValue(cal.getTime());

		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + 2); //reset seconds just so there is more than one field different

		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
		result.setLowerValue(cal.getTime());

		return result;
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

	public static ColumnDataSpecDigest createEntityColumnDataSpecDigest(String entityType, boolean entityAllowed, boolean categoryAllowed, boolean multiSelect) {
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENTITY);
		columnDataSpecDigest.setEntityType(entityType);
		columnDataSpecDigest.setIsEntityAllowed(entityAllowed);
		columnDataSpecDigest.setIsCategoryAllowed(categoryAllowed);
		columnDataSpecDigest.setIsMultiSelectAllowed(multiSelect);
		return columnDataSpecDigest;
	}

	public static EntityProperty createEntityPropertyDefinition(EntityPropertyType propertyType) {
		return createEntityPropertyDefinition(propertyType, true, true);
	}

	public static EntityProperty createEntityPropertyDefinition(EntityPropertyType propertyType, boolean required, boolean searchable) {
		EntityProperty result = new EntityProperty();
		result.setType(propertyType);
		int id = getNextUniqueId();
		result.setName("EntPropDef_" + id + '_' + propertyType);
		result.setDisplayName("Entity Property Definition " + id + '[' + propertyType + ']');
		result.setIsRequired(required ? Boolean.TRUE : Boolean.FALSE);
		result.setIsSearchable(searchable ? Boolean.TRUE : Boolean.FALSE);
		return result;
	}

	public static EntityProperty createEntityPropertyDefinition(EntityPropertyType propertyType, String name) {
		return createEntityPropertyDefinition(propertyType, name, true, true);
	}

	public static EntityProperty createEntityPropertyDefinition(EntityPropertyType propertyType, String name, boolean required, boolean searchable) {
		EntityProperty result = new EntityProperty();
		result.setType(propertyType);
		int id = getNextUniqueId();
		result.setName(name);
		result.setDisplayName("Entity Property Definition " + id + '[' + propertyType + ']');
		result.setIsRequired(required ? Boolean.TRUE : Boolean.FALSE);
		result.setIsSearchable(searchable ? Boolean.TRUE : Boolean.FALSE);
		return result;
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

	public static EntityType createEntityTypeDefinition() {
		return createEntityTypeDefinition(createInt(), createInt());
	}

	public static EntityType createEntityTypeDefinition(int typeID, int categoryType) {
		EntityType entityTypeDefinition = new EntityType();
		entityTypeDefinition.setName("Generic Entity Name for " + typeID);
		entityTypeDefinition.setDisplayName("Generic Entity Display Namefor " + typeID);
		entityTypeDefinition.setTypeID(typeID);
		entityTypeDefinition.setCanClone(Boolean.TRUE);
		entityTypeDefinition.setCategoryType(categoryType);
		return entityTypeDefinition;
	}

	public static GenericEntityType createGenericEntityType(int typeID, int categoryType) {
		return GenericEntityType.makeInstance(createEntityTypeDefinition(typeID, categoryType));
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

	public static ProductGrid createGuidelineGrid(GridTemplate template) {
		ProductGrid grid = new ProductGrid(getNextUniqueId(), template, null, null);
		return grid;
	}

	public static ProductGrid createGuidelineGrid(TemplateUsageType usageType) {
		ProductGrid grid = new ProductGrid(getNextUniqueId(), new GridTemplate(getNextUniqueId(), "test", usageType), null, null);
		return grid;
	}

	public static ColumnDataSpecDigest createIntegerColumnDataSpecDigest() {
		ColumnDataSpecDigest columnDataSpecDigest = new ColumnDataSpecDigest();
		columnDataSpecDigest.setIsBlankAllowed(true);
		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		// defaults to String column
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_INTEGER);
		return columnDataSpecDigest;
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

	public static RuleDefinition createRuleDefinition() {
		int idToUse = getNextUniqueId();
		return new RuleDefinition(idToUse, "Rule-" + idToUse, "description of rule " + idToUse);
	}

	@SuppressWarnings("unchecked")
	public static TemplateUsageType createUsageType() {
		int idToUse = getNextUniqueId();
		TemplateUsageType usageType = TemplateUsageType.createInstance("usage" + idToUse, "Usage " + idToUse, "Privilege" + idToUse);
		((Map<String, TemplateUsageType>) ReflectionUtil.getStaticPrivate(TemplateUsageType.class, "knownTypes")).clear();
		return usageType;
	}

	private CommonTestObjectMother() {
	}

}
