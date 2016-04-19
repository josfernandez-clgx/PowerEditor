package com.mindbox.pe.model.rule;

import java.util.List;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.GuidelineActionProvider;
import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class RuleElementFactory {

	private static final String DELIMITER_LEAF = "||";
	protected static final String DELIMITER_BEGIN = "{{";
	protected static final String DELIMITER_END = "}}";

	private static final String PATTERN_STR_DELIMITER_LEAF = "\\|\\|";

	private static final CompoundMatcher compoundMatcher = new CompoundMatcher();

	public static String asCopyString(RuleDefinition rule) {
		StringBuilder buff = new StringBuilder();
		buff.append("R[");
		buff.append(rule.getID());
		buff.append(DELIMITER_LEAF);
		buff.append(rule.getActionTypeID());
		buff.append("]");
		return buff.toString();
	}

	public static String asCopyString(CompoundLHSElement element) {
		StringBuilder buff = new StringBuilder();
		appendCopyString(buff, element);
		return buff.toString();
	}

	public static String asCopyString(Condition element) {
		StringBuilder buff = new StringBuilder();
		appendCopyString(buff, element);
		return buff.toString();
	}

	private static void appendCopyString(StringBuilder buff, CompoundLHSElement element) {
		switch (element.getType()) {
		case CompoundLHSElement.TYPE_AND:
			buff.append("&");
			break;
		case CompoundLHSElement.TYPE_NOT:
			buff.append("N");
			break;
		case CompoundLHSElement.TYPE_OR:
			buff.append("O");
			break;
		}
		buff.append("[");

		for (int i = 0; i < element.size(); i++) {
			buff.append(DELIMITER_BEGIN);
			RuleElement child = element.get(i);
			if (child instanceof Condition) {
				appendCopyString(buff, (Condition) child);
			}
			else if (child instanceof TestCondition) {
				appendCopyString(buff, (TestCondition) child);
			}
			else if (child instanceof CompoundLHSElement) {
				appendCopyString(buff, (CompoundLHSElement) child);
			}
			else if (child instanceof ExistExpression) {
				appendCopyString(buff, (ExistExpression) child);
			}
			else {
			}
			buff.append(DELIMITER_END);
		}
		buff.append("]");
	}

	private static void appendCopyString(StringBuilder buff, Condition element) {
		buff.append("C[");
		buff.append(element.getReference().getClassName());
		buff.append(".");
		buff.append(element.getReference().getAttributeName());
		buff.append(DELIMITER_LEAF);
		buff.append(Condition.Aux.toOpString(element.getOp()));
		buff.append(DELIMITER_LEAF);
		buff.append(element.getValue().toString());
		buff.append(DELIMITER_LEAF);
		buff.append((element.getComment() == null ? "" : element.getComment()));
		if (element.getObjectName() != null) {
			buff.append(DELIMITER_LEAF);
			buff.append(element.getObjectName());
		}
		buff.append("]");
	}

	private static void appendCopyString(StringBuilder buff, ExistExpression element) {
		buff.append("E[");
		buff.append(element.getClassName());
		buff.append(DELIMITER_LEAF);
		String s = element.getObjectName();
		buff.append(s == null ? "" : s);
		buff.append(DELIMITER_LEAF);
		s = element.getExcludedObjectName();
		buff.append(s == null ? "" : s);
		buff.append(DELIMITER_LEAF);
		CompoundLHSElement e = element.getCompoundLHSElement();
		if (e != null) {
			appendCopyString(buff, e);
		}
		buff.append("]");
	}

	public static String asCopyString(RuleAction element) {
		StringBuilder buff = new StringBuilder();
		buff.append("A[");
		buff.append(element.getActionType().getID());

		for (int i = 0; i < element.size(); i++) {
			buff.append(DELIMITER_BEGIN);
			buff.append(asCopyString((FunctionParameter) element.get(i)));
			buff.append(DELIMITER_END);
		}

		buff.append("]");
		return buff.toString();
	}

	public static void appendCopyString(StringBuilder buff, TestCondition element) {
		buff.append("T[");
		buff.append(element.getTestType().getID());

		for (int i = 0; i < element.size(); i++) {
			buff.append(DELIMITER_BEGIN);
			buff.append(asCopyString((FunctionParameter) element.get(i)));
			buff.append(DELIMITER_END);
		}

		buff.append("]");
	}

	public static String asCopyString(TestCondition element) {
		StringBuilder buff = new StringBuilder();
		appendCopyString(buff, element);
		return buff.toString();
	}

	public static String asCopyString(FunctionParameter element) {
		StringBuilder buff = new StringBuilder();
		buff.append("P[");
		buff.append(element.index());
		buff.append(DELIMITER_LEAF);
		buff.append(element.toDisplayName());
		buff.append(DELIMITER_LEAF);
		buff.append(element.valueString());
		buff.append("]");
		return buff.toString();
	}

	private static String toMeatStr(String str) {
		return str.substring(2, str.length() - 1);
	}

	public static LHSElement toLHSElement(String copyString, DomainClassProvider domainClassProvider, GuidelineActionProvider guidelineActionProvider) {
		String meatStr = toMeatStr(copyString);
		if (copyString.charAt(0) == 'C') {
			String[] tokens = meatStr.split(PATTERN_STR_DELIMITER_LEAF);
			if (tokens.length < 3) {
				return null;
			}

			Reference ref = getInstance().createReference(tokens[0]);

			Condition cond = getInstance().createCondition();
			cond.setReference(ref);
			cond.setOp(Condition.Aux.toOpInt(tokens[1]));
			cond.setValue(getInstance().createConditionValue(tokens[2], domainClassProvider));
			if (tokens.length > 3) {
				cond.setComment(tokens[3]);
			}
			if (tokens.length > 4) {
				cond.setObjectName(tokens[4]);
			}
			return cond;
		}
		else if (copyString.charAt(0) == 'T') {
			return toTestCondition(copyString, guidelineActionProvider);
		}
		else if (copyString.charAt(0) == 'E') {
			return toExistExpression(copyString, domainClassProvider, guidelineActionProvider);
		}
		else {
			CompoundLHSElement compound = null;
			if (copyString.charAt(0) == '&') {
				compound = getInstance().createAndCompoundCondition();
			}
			else if (copyString.charAt(0) == 'O') {
				compound = getInstance().createOrCompoundCondition();
			}
			else if (copyString.charAt(0) == 'N') {
				compound = getInstance().createNotCompoundCondition();
			}

			if (compound != null) {
				CompoundMatcher compoundMatcher = new CompoundMatcher();
				compoundMatcher.reset(meatStr);
				while (compoundMatcher.find()) {
					String s = meatStr.subSequence(compoundMatcher.start() + 2, compoundMatcher.end() - 2).toString();
					compound.add(toLHSElement(s, domainClassProvider, guidelineActionProvider));
				}
				return compound;
			}
		}
		return null;
	}

	public static RuleAction toRuleAction(String copyString, GuidelineActionProvider guidelineActionProvider) {
		if (copyString.charAt(0) == 'A') {
			String meatStr = toMeatStr(copyString);

			int index = meatStr.indexOf(DELIMITER_BEGIN);
			if (index > -1) {
				try {
					int id = Integer.parseInt(meatStr.substring(0, index));

					RuleAction action = getInstance().createRuleAction();
					action.setActionType(guidelineActionProvider.getActionType(id));

					String strToMatch = meatStr.substring(index);
					compoundMatcher.reset(strToMatch);
					while (compoundMatcher.find()) {
						action.add(toFunctionParameter(strToMatch.subSequence(compoundMatcher.start() + 2, compoundMatcher.end() - 2).toString()));
					}
					return action;
				}
				catch (Exception ex) {
					return null;
				}
			}
		}
		return null;
	}

	public static TestCondition toTestCondition(String copyString, GuidelineActionProvider guidelineActionProvider) {
		if (copyString.charAt(0) == 'T') {
			String meatStr = toMeatStr(copyString);

			int index = meatStr.indexOf(DELIMITER_BEGIN);
			if (index > -1) {
				try {
					int id = Integer.parseInt(meatStr.substring(0, index));

					TestCondition test = getInstance().createTestCondition();
					test.setTestType(guidelineActionProvider.getTestType(id));

					String strToMatch = meatStr.substring(index);
					compoundMatcher.reset(strToMatch);
					while (compoundMatcher.find()) {
						test.add(toFunctionParameter(strToMatch.subSequence(compoundMatcher.start() + 2, compoundMatcher.end() - 2).toString()));
					}
					return test;
				}
				catch (Exception ex) {
					return null;
				}
			}
		}
		return null;
	}

	public static ExistExpression toExistExpression(String copyString, DomainClassProvider domainClassProvider, GuidelineActionProvider guidelineActionProvider) {
		if (copyString.charAt(0) == 'E') {
			String meatStr = toMeatStr(copyString);
			int index = 0;
			int nextIndex = meatStr.indexOf(DELIMITER_LEAF);
			String className = meatStr.substring(index, nextIndex);
			index = nextIndex + 2;
			nextIndex = meatStr.indexOf(DELIMITER_LEAF, index);
			String objectName = meatStr.substring(index, nextIndex);
			index = nextIndex + 2;
			nextIndex = meatStr.indexOf(DELIMITER_LEAF, index);
			String excludedObjectName = meatStr.substring(index, nextIndex);
			index = nextIndex + 2;
			String compound = meatStr.substring(index);

			if (objectName.length() == 0) objectName = null;
			if (excludedObjectName.length() == 0) excludedObjectName = null;
			CompoundLHSElement compoundElement = (CompoundLHSElement) toLHSElement(compound, domainClassProvider, guidelineActionProvider);
			ExistExpression ee = getInstance().createExistExpression(className);
			ee.setObjectName(objectName);
			ee.setExcludedObjectName(excludedObjectName);
			ee.setCompoundLHSElement(compoundElement);
			return ee;
		}
		return null;
	}

	public static FunctionParameter toFunctionParameter(String copyString) {
		if (copyString.charAt(0) == 'P') {
			String meatStr = toMeatStr(copyString);
			String[] tokens = meatStr.split(PATTERN_STR_DELIMITER_LEAF);
			if (tokens.length > 2) {
				if (ColumnRefParameterImpl.isValueString(tokens[2])) {
					return getInstance().createFunctionParameter(Integer.parseInt(tokens[0]), tokens[1], ColumnRefParameterImpl.extractColumnNumber(tokens[2]));
				}
				else {
					return getInstance().createFunctionParameter(Integer.parseInt(tokens[0]), tokens[1], tokens[2]);
				}
			}
			else if (tokens.length == 2) return getInstance().createFunctionParameter(Integer.parseInt(tokens[0]), tokens[1], "");
		}
		return null;
	}

	public static RuleAction unmodifiableRuleAction(RuleAction action) {
		return new UnmodRuleActionImpl(action);
	}

	public static CompoundLHSElement unmodifiableCompoundLHSElement(CompoundLHSElement element) {
		return new UnmodConditionSet(element);
	}

	public static CompoundLHSElement deepCopyCompoundLHSElement(CompoundLHSElement element) {
		return new ConditionSet(element);
	}

	public static RuleAction deepCopyRuleAction(RuleAction action) {
		return new RuleActionImpl(action);
	}

	public static TestCondition deepCopyTestCondition(TestCondition test) {
		return new TestConditionImpl(test);
	}

	public static Value deepCopyValue(Value value) {
		if (value instanceof Reference) {
			return new ReferenceValueImpl((Reference) value);
		}
		else if (value instanceof ColumnReference) {
			return new ColumnRefValue(((ColumnReference) value).getColumnNo());
		}
		else if (value instanceof MathExpressionValue) {
			return new MathExpressionValueImpl((MathExpressionValue) value);
		}
		else {
			return ValueImpl.getInstance((value == null ? null : value.toString()));
		}
	}

	public static Condition deepCopyCondition(Condition cond) {
		return new ConditionImpl(cond);
	}

	public static ExistExpression deepCopyExistExpression(ExistExpression source) {
		ExistExpression expression = new ExistExpressionImpl(source.getClassName(), deepCopyCompoundLHSElement(source.getCompoundLHSElement()));
		expression.setObjectName(source.getObjectName());
		expression.setExcludedObjectName(source.getExcludedObjectName());
		return expression;
	}

	public static FunctionParameter deepCopyFunctionParameter(FunctionParameter param) {
		FunctionParameter copy = null;
		if (param instanceof ColumnReference) {
			copy = getInstance().createFunctionParameter(param.index(), param.toDisplayName(), (ColumnReference) param);
		}
		else if (param instanceof Reference) {
			copy = getInstance().createAttributeRefParameter(param.index(), param.toDisplayName(), ((Reference) param).getAttributeName(), ((Reference) param).getClassName());
		}
		else if (param instanceof ClassReference) {
			copy = getInstance().createClassRefParameter(param.index(), param.toDisplayName(), ((ClassReference) param).getClassName());
		}
		else {
			copy = getInstance().createFunctionParameter(param.index(), param.toDisplayName(), param.valueString());
		}
		copy.setComment(param.getComment());
		return copy;
	}

	private static class ConditionSet extends AbstractCompoundLHSElement {

		private static final long serialVersionUID = -3193209055755075599L;

		private ConditionSet(int type, String dispName) {
			super(type, dispName);
		}

		private ConditionSet(CompoundLHSElement ce) {
			super(ce.getType(), ce.toDisplayName());
			super.setComment(ce.getComment());
			for (int i = 0; i < ce.size(); i++) {
				RuleElement element = ce.get(i);
				if (element instanceof Condition) {
					add(deepCopyCondition((Condition) element));
				}
				else if (element instanceof CompoundLHSElement) {
					add(deepCopyCompoundLHSElement((CompoundLHSElement) element));
				}
				else if (element instanceof ExistExpression) {
					add(deepCopyExistExpression((ExistExpression) element));
				}
				else if (element instanceof TestCondition) {
					add(deepCopyTestCondition((TestCondition) element));
				}
				else {
					throw new IllegalArgumentException("Invalid element: " + element + " (" + element.getClass().getName() + ")");
				}
			}
		}
	}

	private static class UnmodConditionSet extends AbstractCompoundLHSElement {

		private static final long serialVersionUID = 629348936333571898L;

		private UnmodConditionSet(CompoundLHSElement element) {
			super(element.getType(), element.toDisplayName());
			super.setComment(element.getComment());
			for (int i = 0; i < element.size(); i++) {
				super.add(element.get(i));
			}
		}

		public void add(LHSElement element) {
			throw new UnsupportedOperationException();
		}

		public void insert(int index, LHSElement element) {
			throw new UnsupportedOperationException();
		}

		public void remove(int index) {
			throw new UnsupportedOperationException();
		}

		public void remove(LHSElement element) {
			throw new UnsupportedOperationException();
		}

		public void setComment(String string) {
			throw new UnsupportedOperationException();
		}

	}

	private static class UnmodRuleActionImpl extends RuleActionImpl implements RuleAction {

		private static final long serialVersionUID = 7249780751341486650L;

		private UnmodRuleActionImpl(RuleAction action) {
			super(action);
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unused")
		public void setActionType() {
			throw new UnsupportedOperationException();
		}

		public void add(FunctionParameter element) {
			super.add(element);
		}

		public void insert(int index, FunctionParameter element) {
			throw new UnsupportedOperationException();
		}

		public void remove(int index) {
			throw new UnsupportedOperationException();
		}

		public void remove(FunctionParameter element) {
			super.remove(element);
		}

		public void setComment(String string) {
			throw new UnsupportedOperationException();
		}

	}

	private static class RuleActionImpl extends FunctionCall implements RuleAction {

		private static final long serialVersionUID = 4122969537484019434L;

		private RuleActionImpl() {
			super("ACTION");
		}

		private RuleActionImpl(RuleAction action) {
			super((FunctionCall) action);
		}

		public ActionTypeDefinition getActionType() {
			return (ActionTypeDefinition) getFunctionType();
		}

		public void setActionType(ActionTypeDefinition type) {
			this.setFunctionType(type);
		}

	}

	private static class TestConditionImpl extends FunctionCall implements TestCondition {

		private static final long serialVersionUID = -5428009738794797004L;

		private TestConditionImpl() {
			super("TEST");
		}

		private TestConditionImpl(TestCondition test) {
			super((FunctionCall) test);
		}

		public TestTypeDefinition getTestType() {
			return (TestTypeDefinition) getFunctionType();
		}

		public void setTestType(TestTypeDefinition type) {
			this.setFunctionType(type);
		}

	}

	private static class ConditionImpl extends AbstractCondition {

		private static final long serialVersionUID = 1763436253719337158L;

		protected ConditionImpl() {
			super("CONDITION", OP_EQUAL);
		}

		private ConditionImpl(Condition condition) {
			this();
			super.setComment(condition.getComment());
			super.setObjectName(condition.getObjectName());
			super.setOp(condition.getOp());
			// copy reference
			super.setReference(new ReferenceImpl(condition.getReference()));
			// copy value
			super.setValue(deepCopyValue(condition.getValue()));
		}
	}

	private static RuleElementFactory instance = null;

	public static RuleElementFactory getInstance() {
		if (instance == null) {
			instance = new RuleElementFactory();
		}
		return instance;
	}

	private RuleElementFactory() {
	}

	public ExistExpression createExistExpression(String className) {
		return new ExistExpressionImpl(className, createAndCompoundCondition());
	}

	public CompoundLHSElement createAndCompoundCondition() {
		return new ConditionSet(CompoundLHSElement.TYPE_AND, "AND");
	}

	public CompoundLHSElement createOrCompoundCondition() {
		return new ConditionSet(CompoundLHSElement.TYPE_OR, "OR");
	}

	public CompoundLHSElement createNotCompoundCondition() {
		return new ConditionSet(CompoundLHSElement.TYPE_NOT, "NOT");
	}

	public RuleAction createRuleAction() {
		return new RuleActionImpl();
	}

	public TestCondition createTestCondition() {
		return new TestConditionImpl();
	}

	public Condition createCondition() {
		return new ConditionImpl();
	}

	public Reference createReference(String refStr) {
		if (refStr == null || refStr.length() == 0) {
			throw new IllegalArgumentException("Reference string cannot be empty");
		}
		String[] strings = refStr.split("\\.");
		return new ReferenceImpl(strings[0], strings[1]);
	}

	public Reference createReference(String className, String attrName) {
		return new ReferenceImpl(className, attrName);
	}

	public Value createValue(String str) {
		return ValueImpl.getInstance(str);
	}

	public Value createValue(ColumnReference columnRef, String operator, Reference attrReference) {
		MathExpressionValueImpl mathExp = new MathExpressionValueImpl();
		mathExp.setColumnReference(columnRef);
		mathExp.setOperator(operator);
		mathExp.setAttributeReference(attrReference);
		return mathExp;
	}

	public Value createValue(String value, String operator, Reference attrReference) {
		MathExpressionValueImpl mathExp = new MathExpressionValueImpl();
		mathExp.setValue(value);
		mathExp.setOperator(operator);
		mathExp.setAttributeReference(attrReference);
		return mathExp;
	}

	public Value createValue(Reference ref) {
		return new ReferenceValueImpl(ref);
	}

	public Value createValue(ColumnReference columnRef) {
		return new ColumnRefValue(columnRef.getColumnNo());
	}

	public ColumnReference createColumnReference(int columnNo) {
		return new ColumnRefImpl(columnNo);
	}

	public Value createConditionValue(String str, DomainClassProvider domainClassProvider) {
		if (str.startsWith("Column")) {
			String[] valStr = str.substring(7).split(" ");
			if (valStr.length == 1) {
				try {
					int colNo = Integer.parseInt(valStr[0]);
					return createValue(createColumnReference(colNo));
				}
				catch (NumberFormatException x) {
				}
			}
			else if (valStr.length == 3) {
				try {
					return createValue(createColumnReference(Integer.parseInt(valStr[0])), valStr[1], createReference(valStr[2]));
				}
				catch (Exception x) {
				}

			}
		}
		String[] refStr = str.split("\\.");
		if (refStr.length == 2) {
			DomainClass dc = domainClassProvider.getDomainClass(refStr[0]);
			if (dc != null) {
				DomainAttribute da = dc.getDomainAttribute(refStr[1]);
				if (da != null) return createValue(createReference(refStr[0], refStr[1]));
			}
		}
		return ValueImpl.getInstance(str);
	}

	public FunctionParameter createFunctionParameterFromXMLStr(int index, String name, String value) {
		// check for column reference
		if (ColumnRefParameterImpl.isValueString(value)) {
			return createFunctionParameter(index, name, ColumnRefParameterImpl.extractColumnNumber(value));
		}
		else if (ClassRefParameterImpl.isValueString(value)) {
			return new ClassRefParameterImpl(index, name, ClassRefParameterImpl.extractClassName(value));
		}
		else if (AttributeRefParameterImpl.isValueString(value)) {
			return new AttributeRefParameterImpl(index, name, AttributeRefParameterImpl.extractAttributeName(value), AttributeRefParameterImpl.extractClassName(value));
		}
		else {
			return createFunctionParameter(index, name, value);
		}
	}

	public FunctionParameter createFunctionParameter(int index, String name, String value) {
		return new StringParameterImpl(index, name, value);
	}

	public FunctionParameter createFunctionParameter(int index, String name, ColumnReference columnRef) {
		return new ColumnRefParameterImpl(index, name, columnRef.getColumnNo());
	}

	public FunctionParameter createAttributeRefParameter(int index, String name, String attrName, String className) {
		return new AttributeRefParameterImpl(index, name, attrName, className);
	}

	public FunctionParameter createClassRefParameter(int index, String name, String className) {
		return new ClassRefParameterImpl(index, name, className);
	}

	private FunctionParameter createFunctionParameter(int index, String name, int columnNo) {
		return new ColumnRefParameterImpl(index, name, columnNo);
	}

	public FunctionParameter updateFunctionParameter(FunctionParameter parameter, String value) {
		if (parameter instanceof StringParameterImpl) {
			((StringParameterImpl) parameter).setValue(value);
			return parameter;
		}
		else {
			return createFunctionParameter(parameter.index(), parameter.toDisplayName(), value);
		}
	}

	public FunctionParameter updateFunctionParameter(FunctionParameter parameter, ColumnReference ref) {
		if (parameter instanceof ColumnRefParameterImpl) {
			((ColumnRefParameterImpl) parameter).setColumnNo(ref.getColumnNo());
			return parameter;
		}
		else {
			return createFunctionParameter(parameter.index(), parameter.toDisplayName(), ref);
		}
	}

	public FunctionParameter updateAttributeRefParameter(FunctionParameter parameter, String attrName, String className) {
		if (parameter instanceof AttributeRefParameterImpl) {
			((AttributeRefParameterImpl) parameter).setAttributeName(attrName);
			((AttributeRefParameterImpl) parameter).setClassName(className);
			return parameter;
		}
		else {
			return createAttributeRefParameter(parameter.index(), parameter.toDisplayName(), attrName, className);
		}
	}

	public FunctionParameter updateClassRefParameter(FunctionParameter parameter, String className) {
		if (parameter instanceof ClassRefParameterImpl) {
			((ClassRefParameterImpl) parameter).setClassName(className);
			return parameter;
		}
		else {
			return createClassRefParameter(parameter.index(), parameter.toDisplayName(), className);
		}
	}

	public void createParametersForFunctionCall(FunctionCall function, FunctionTypeDefinition typeDef, List<FunctionParameter> oldParams, FunctionTypeDefinition oldTypeDef) {
		function.setFunctionType(typeDef);
		function.removeAll();
		FunctionParameterDefinition[] paramDefs = typeDef.getParameterDefinitions();
		for (int i = 0; i < paramDefs.length; i++) {
			FunctionParameterDefinition oldParamDef = null;
			if (oldParams != null && oldParams.size() > i) {
				oldParamDef = oldTypeDef.getParameterDefinitionAt(i + 1);
			}
			String val = "";
			if (oldParamDef != null
					&& DataTypeCompatibilityValidator.getGenericDataType(oldParamDef.getDeployType()) == DataTypeCompatibilityValidator.getGenericDataType(paramDefs[i].getDeployType()))
				val = oldParams.get(i).valueString();

			function.add(RuleElementFactory.getInstance().createFunctionParameterFromXMLStr(i + 1, paramDefs[i].getName(), val));
		}
	}
}

// I cannot see how to get regular expressions to handle recursively
// nested expressions so I wrote this manual matcher that does.

class CompoundMatcher {

	String str = null;
	int start = 0;
	int end = 0;

	public CompoundMatcher() {
	}

	public void reset(String str) {
		this.str = str;
		start = 0;
		end = 0;
	}

	public int start() {
		return start;
	}

	public int end() {
		return end;
	}

	public boolean find() {
		int depth = 0;
		if (start == -1 || end == -1) return false;
		start = str.indexOf(RuleElementFactory.DELIMITER_BEGIN, end);
		if (start == -1) return false;
		depth = 1;
		int index = start + 2;
		while (depth > 0) {
			int closeIndex = str.indexOf(RuleElementFactory.DELIMITER_END, index);
			if (closeIndex == -1) return false;
			int openIndex = str.indexOf(RuleElementFactory.DELIMITER_BEGIN, index);
			if (openIndex != -1 && openIndex < closeIndex) {
				index = openIndex + 2;
				depth++;
			}
			else if (openIndex == -1 || closeIndex < openIndex) {
				index = closeIndex + 2;
				depth--;
			}
		}
		end = index;
		return true;
	}
}
