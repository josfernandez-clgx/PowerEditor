package com.mindbox.pe.server.generator;

import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCompoundCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeValue;
import com.mindbox.pe.server.generator.aemodel.AeAttributePattern;
import com.mindbox.pe.server.generator.aemodel.AeCellValue;
import com.mindbox.pe.server.generator.aemodel.AeColumnValue;
import com.mindbox.pe.server.generator.aemodel.AeFormulaValue;
import com.mindbox.pe.server.generator.aemodel.AeInstanceAttrValue;
import com.mindbox.pe.server.generator.aemodel.AeLiteralValue;
import com.mindbox.pe.server.generator.aemodel.AeNameValue;
import com.mindbox.pe.server.generator.aemodel.AeObjectPattern;
import com.mindbox.pe.server.generator.aemodel.AePatternSet;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.pe.server.generator.aemodel.AeRuleName;
import com.mindbox.pe.server.generator.aemodel.AeTestFunctionPattern;
import com.mindbox.pe.server.model.rule.RuleActionMethod;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;
import com.mindbox.server.parser.jtb.rule.syntaxtree.AdditiveExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ArgumentList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Arguments;
import com.mindbox.server.parser.jtb.rule.syntaxtree.BooleanLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalAndExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.InstanceName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Literal;
import com.mindbox.server.parser.jtb.rule.syntaxtree.LiteralList;
import com.mindbox.server.parser.jtb.rule.syntaxtree.MultiplicativeExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Name;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeChoice;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeListOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ObjectCondition;
import com.mindbox.server.parser.jtb.rule.syntaxtree.PrimaryExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RelationalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RuleName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.UnaryExpression;
import com.mindbox.server.parser.jtb.rule.visitor.ObjectDepthFirst;

public class AeRuleBuilder extends ObjectDepthFirst {

	static void link(AbstractAeCompoundCondition abstractaecompoundcondition, AbstractAeCondition abstractaecondition) {
		AbstractAeCompoundCondition abstractaecompoundcondition1 = abstractaecondition.getParentCondition();
		if (abstractaecompoundcondition1 != null) abstractaecompoundcondition1.removeCondition(abstractaecondition);
		abstractaecondition.setParentCondition(abstractaecompoundcondition);
		abstractaecompoundcondition.addCondition(abstractaecondition);
	}


	private Logger logger = Logger.getLogger(AeRuleBuilder.class);
	private boolean errorEncountered;
	private String errorString;
	private boolean inNegation = false;
	private int negationCounter = 0;
	private TemplateUsageType usageType = null;

	public AeRuleBuilder() {
		errorEncountered = false;
		errorString = null;
	}

	private void addAttrPatternRhs(AeAttributePattern aeattributepattern, NodeListOptional nodelistoptional) {
		if (nodelistoptional.size() > 1) logger.error("too many rhsElements to compare! : " + nodelistoptional.size());
		NodeSequence nodesequence = (NodeSequence) nodelistoptional.elementAt(0);
		NodeChoice nodechoice = (NodeChoice) nodesequence.elementAt(0);
		String s = ((NodeToken) nodechoice.choice).tokenImage;
		aeattributepattern.setComparatorFunction(s);
		AdditiveExpression additiveexpression = (AdditiveExpression) nodesequence.elementAt(1);
		additiveexpression.accept(this, aeattributepattern);
	}

	private void addTestPatternArgs(AeTestFunctionPattern testPattern, NodeListOptional nodelistoptional) {
		logger.debug(">>> addTestPatternArgs: " + testPattern + "," + nodelistoptional);
		if (nodelistoptional.size() > 1) logger.error("too many rhsElements to compare! : " + nodelistoptional.size());

		NodeSequence nodesequence = (NodeSequence) nodelistoptional.elementAt(0);
		AdditiveExpression additiveexpression = (AdditiveExpression) nodesequence.elementAt(1);
		additiveexpression.accept(this, testPattern);
		logger.debug("<<< addTestPatternArgs");
	}

	private String extractAttributeName(Name name) {
		NodeListOptional nodelistoptional = name.nodeListOptional;
		if (nodelistoptional.size() > 1) logger.error("Multiple derefs not allowed (for now)...ignoring rest");
		NodeSequence nodesequence = (NodeSequence) nodelistoptional.elementAt(0);
		return ((NodeToken) nodesequence.elementAt(1)).tokenImage;
	}

	public AeRule generateRuleSkeleton(DeploymentRule deploymentrule, TemplateUsageType useType) throws SapphireException {
		logger.debug(">>> generateRuleSkeleton with " + deploymentrule);
		initForEachRule(useType);
		AeRule aerule = process(deploymentrule);
		logger.debug(">>> generateRuleSkeleton returning with " + aerule + ": errorEncountered " + errorEncountered);
		if (errorEncountered)
			throw new SapphireException("Could not parse deployment rule: " + errorString);
		else
			return aerule;
	}

	public Object getLiteralValue(Literal literal, Object obj) {
		Object obj1 = null;
		AeLiteralValue aeliteralvalue = new AeLiteralValue(literal);
		obj1 = aeliteralvalue;

		switch (literal.nodeChoice.which) {
		case 0: // '\0'
			Integer integer = new Integer(((NodeToken) literal.nodeChoice.choice).tokenImage);
			aeliteralvalue.setValue(integer);
			break;

		case 1: // '\001'
			Float float1 = new Float(((NodeToken) literal.nodeChoice.choice).tokenImage);
			aeliteralvalue.setValue(float1);
			break;

		case 2: // '\002'
		case 3: // '\003'
			String s = ((NodeToken) literal.nodeChoice.choice).tokenImage;
			aeliteralvalue.setValue(s);
			break;

		default:
			obj1 = literal.nodeChoice.choice.accept(this, obj);
			break;
		}
		return obj1;
	}

	private void initForEachRule(TemplateUsageType useType) {
		errorEncountered = false;
		errorString = null;
		usageType = useType;
	}

	private boolean isConditionalExpression(RelationalExpression relationalexpression) {
		if (relationalexpression.nodeChoice.which == 0) return false;
		NodeSequence nodesequence = (NodeSequence) relationalexpression.nodeChoice.choice;
		NodeListOptional nodelistoptional = (NodeListOptional) nodesequence.elementAt(1);
		return !nodelistoptional.present();
	}

	private boolean isTestPattern(RelationalExpression relationalExpression) {
		// hard-coded traverse - replace when new parser is ready
		NodeSequence nodesequence = (NodeSequence) relationalExpression.nodeChoice.choice;
		AdditiveExpression additiveexpression = (AdditiveExpression) nodesequence.elementAt(0);

		NodeChoice nodeChoice = additiveexpression.multiplicativeExpression.unaryExpression.nodeChoice;
		if (nodeChoice.which == 1) { // non-negated unary expression
			PrimaryExpression primaryExpression = (PrimaryExpression) nodeChoice.choice;

			NodeChoice penc = primaryExpression.nodeChoice;

			if (penc.choice instanceof Name) {
				String className = ((Name) penc.choice).nodeToken.tokenImage;
				boolean result = className.equals("aetest");
				return result;
			}
		}
		return false;
	}

	public void markError(String s) {
		errorString = s;
		errorEncountered = true;
	}

	private void populate(ObjectCondition objectcondition, AeObjectPattern aeobjectpattern) {
		logger.debug(">>> populate(ObjectCondition,AeObjectPattern): " + objectcondition + ", " + aeobjectpattern);
		setCardinality(aeobjectpattern, objectcondition.nodeChoice);
		String s = objectcondition.className.nodeToken.tokenImage;
		aeobjectpattern.setClassName(s);
		String s1 = null;
		if (objectcondition.nodeOptional.present()) {
			InstanceName instancename = (InstanceName) objectcondition.nodeOptional.node;
			s1 = instancename.name.nodeToken.tokenImage;
		}
		aeobjectpattern.setObjectName(s1);
		if (s1 != null) {
			AeRule aerule = aeobjectpattern.getParentRule();
			if (aerule != null) aerule.addUserRegisteredName(s1);
		}
		String s2 = null;
		if (objectcondition.nodeOptional1.present()) {
			NodeSequence nodesequence = (NodeSequence) objectcondition.nodeOptional1.node;
			s2 = ((InstanceName) nodesequence.elementAt(1)).name.nodeToken.tokenImage;
		}
		aeobjectpattern.setExcludedObjectName(s2);
		objectcondition.relationalExpression.accept(this, aeobjectpattern);
	}

	private AeRule process(DeploymentRule deploymentrule) {
		logger.debug(">>> AeRuleBuilder.process with " + deploymentrule);
		AeRule aerule = new AeRule(deploymentrule);
		deploymentrule.conditionalExpression.accept(this, aerule);

		logger.debug("AeRuleBuilder.process(DeploymentRule): processing action of the rule " + aerule);
		deploymentrule.action.accept(this, aerule);
		logger.debug("AeRuleBuilder.process returning with rule " + aerule);
		return aerule;
	}

	private void setCardinality(AeObjectPattern aeobjectpattern, NodeChoice nodechoice) {
		byte byte0 = -1;
		switch (nodechoice.which) {
		case 0: // '\0'
			byte0 = 2;
			break;

		case 1: // '\001'
			byte0 = 1;
			break;

		case 2: // '\002'
			byte0 = 5;
			break;

		case 3: // '\003'
			byte0 = 3;
			NodeSequence nodesequence = (NodeSequence) nodechoice.choice;
			NodeToken nodetoken = (NodeToken) nodesequence.elementAt(1);
			int i = Integer.parseInt(nodetoken.tokenImage);
			aeobjectpattern.setCardinalityLimit(i);
			break;

		case 4: // '\004'
			byte0 = 4;
			NodeSequence nodesequence1 = (NodeSequence) nodechoice.choice;
			NodeToken nodetoken1 = (NodeToken) nodesequence1.elementAt(1);
			int j = Integer.parseInt(nodetoken1.tokenImage);
			aeobjectpattern.setCardinalityLimit(j);
			break;
		}
		aeobjectpattern.setCardinalityType(byte0);
	}

	private boolean setValue(AeAttributePattern aeattributepattern, Object obj) {
		if (aeattributepattern.getValue() != null) {
			logger.error("Value already set!. Ignoring new value: " + obj);
			return false;
		}
		else {
			aeattributepattern.setValue((AbstractAeValue) obj);
			return true;
		}
	}

	public Object visit(Action action, AeTestFunctionPattern obj) {
		Object obj1 = null;
		action.arguments.accept(this, obj);
		return obj1;
	}

	@Override
	public Object visit(Action action, Object obj) {
		Object obj1 = null;
		AeRule aerule = (AeRule) obj;
		String s = action.name.nodeToken.tokenImage;
		aerule.setActionMethod(RuleActionMethod.valueOf(s, !ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).isPEActionOn()));
		action.arguments.accept(this, obj);
		return obj1;
	}

	private Object visit(AdditiveExpression additiveexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		if (additiveexpression.nodeListOptional.present()) {
			AeFormulaValue aeformulavalue = new AeFormulaValue(additiveexpression);
			obj = aeformulavalue;

			additiveexpression.multiplicativeExpression.accept(this, aeformulavalue);

			NodeSequence ns = (NodeSequence) additiveexpression.nodeListOptional.elementAt(0);
			String operator = ((NodeChoice) ns.elementAt(0)).choice.toString();
			logger.debug("visit(AdditiveExpression,AeAttributePattern): operator = " + operator);

			aeformulavalue.setOperator((operator.equals("+") ? AeFormulaValue.PLUS : AeFormulaValue.MINUS));

			ns.elementAt(1).accept(this, aeformulavalue);

			logger.debug("visit(AdditiveExpression,AeAttributePattern): setting value of the pattern...");
			aeattributepattern.setValue(aeformulavalue);
		}
		else {
			obj = additiveexpression.multiplicativeExpression.accept(this, aeattributepattern);
		}
		return obj;
	}

	@Override
	public Object visit(AdditiveExpression additiveexpression, Object obj) {
		if (obj instanceof AeObjectPattern) {
			return super.visit(additiveexpression, obj);
		}
		if (obj instanceof AeAttributePattern) {
			return visit(additiveexpression, (AeAttributePattern) obj);
		}
		else {
			return super.visit(additiveexpression, obj);
		}
	}

	@Override
	public Object visit(ArgumentList argumentlist, Object obj) {
		Object obj1 = null;
		argumentlist.additiveExpression.accept(this, obj);
		argumentlist.nodeListOptional.accept(this, obj);
		return obj1;
	}

	@Override
	public Object visit(Arguments arguments, Object obj) {
		Object obj1 = null;
		obj1 = arguments.nodeOptional.accept(this, obj);
		return obj1;
	}

	@Override
	public Object visit(BooleanLiteral booleanliteral, Object obj) {
		AeLiteralValue aeliteralvalue = null;
		AeLiteralValue aeliteralvalue1 = new AeLiteralValue(booleanliteral);
		aeliteralvalue = aeliteralvalue1;
		if (booleanliteral.nodeChoice.which == 0)
			aeliteralvalue1.setValue(Boolean.TRUE);
		else
			aeliteralvalue1.setValue(Boolean.FALSE);
		return aeliteralvalue;
	}

	@Override
	public Object visit(CellValue cellvalue, Object obj) {
		AeCellValue aecellvalue = new AeCellValue(cellvalue);
		return aecellvalue;
	}

	@Override
	public Object visit(ColumnLiteral columnliteral, Object obj) {
		AeColumnValue aecolumnvalue = null;
		String s = columnliteral.nodeToken2.tokenImage;
		int i = Integer.parseInt(s);
		AeColumnValue aecolumnvalue1 = new AeColumnValue(columnliteral);
		aecolumnvalue1.setColumnNumber(i);
		aecolumnvalue = aecolumnvalue1;
		return aecolumnvalue;
	}

	private Object visit(ConditionalAndExpression conditionalandexpression, AbstractAeCompoundCondition abstractaecompoundcondition) {
		Object obj = null;
		if (conditionalandexpression.nodeListOptional.present()) {
			logger.debug(" --> -->Creating new AND AePatternSet");
			AePatternSet aepatternset = new AePatternSet(conditionalandexpression.nodeListOptional);
			aepatternset.setConditionType(1);
			link(abstractaecompoundcondition, aepatternset);
			conditionalandexpression.relationalExpression.accept(this, aepatternset);
			conditionalandexpression.nodeListOptional.accept(this, aepatternset);
			obj = aepatternset;
		}
		else {
			obj = conditionalandexpression.relationalExpression.accept(this, abstractaecompoundcondition);
		}
		return obj;
	}

	@Override
	public Object visit(ConditionalAndExpression conditionalandexpression, Object obj) {
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(conditionalandexpression, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.debug("Passthrough for Visit ConditionalAndExpression: unknown arg type: " + obj);
			return super.visit(conditionalandexpression, obj);
		}
	}

	private Object visit(ConditionalExpression conditionalexpression, AbstractAeCompoundCondition abstractaecompoundcondition) {
		Object obj = null;
		if (conditionalexpression.nodeListOptional.present()) {
			logger.debug(" -->Creating new OR AePatternSet");
			AePatternSet aepatternset = new AePatternSet(conditionalexpression.nodeListOptional);
			aepatternset.setConditionType(2);
			link(abstractaecompoundcondition, aepatternset);
			conditionalexpression.conditionalAndExpression.accept(this, aepatternset);
			conditionalexpression.nodeListOptional.accept(this, aepatternset);
			obj = aepatternset;
		}
		else {
			obj = conditionalexpression.conditionalAndExpression.accept(this, abstractaecompoundcondition);
		}
		return obj;
	}

	@Override
	public Object visit(ConditionalExpression conditionalexpression, Object obj) {
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(conditionalexpression, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.debug("Passthrough for Visit ConditionalExpression: unknown arg type: " + obj);
			return super.visit(conditionalexpression, obj);
		}
	}

	private Object visit(Literal literal, AeRule aerule) {
		Object obj = getLiteralValue(literal, aerule);
		aerule.addActionParm((AbstractAeValue) obj);
		return obj;
	}

	private Object visit(Literal literal, List<Object> list) {
		String valueStr = null;
		if (literal.nodeChoice.choice instanceof NodeToken) {
			valueStr = ((NodeToken) literal.nodeChoice.choice).tokenImage;
			list.add(valueStr);
		}
		else {
			valueStr = literal.nodeChoice.toString();
		}
		return valueStr;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object visit(Literal literal, Object parent) {
		if (parent instanceof AeAttributePattern) {
			Object obj1 = getLiteralValue(literal, parent);
			setValue((AeAttributePattern) parent, obj1);
			return obj1;
		}

		if (parent instanceof AeTestFunctionPattern) {
			Object value = getLiteralValue(literal, parent);
			if (value instanceof AbstractAeValue) {
				((AeTestFunctionPattern) parent).addParam((AbstractAeValue) value);
			}
			else {
				logger.debug("visit(Listeral,AeTestFunctionPattern): ignored invalid value - " + value);
			}
			return value;
		}

		if (List.class.isInstance(parent)) {
			return visit(literal, (List<Object>) parent);
		}

		if (parent instanceof AeRule) {
			return visit(literal, (AeRule) parent);
		}

		if (parent instanceof MultiplicativeExpression) {
			return visit(literal, (MultiplicativeExpression) parent);
		}

		if (parent instanceof AeFormulaValue) {
			Object obj1 = getLiteralValue(literal, parent);
			((AeFormulaValue) parent).addArgument(obj1);
			return obj1;
		}

		logger.debug("Passthrough for Visit Literal: unknown parent type: " + parent);
		return super.visit(literal, parent);
	}

	@Override
	public Object visit(LiteralList literallist, Object obj) {
		if (obj instanceof AeAttributePattern) {
			Object obj1 = visit(literallist, (AeAttributePattern) obj);
			setValue((AeAttributePattern) obj, obj1);
			return obj1;
		}
		else {
			logger.debug("Passthrough for Visit LiteralList: unknown arg type: " + obj);
			return super.visit(literallist, obj);
		}
	}

	private Object visit(MultiplicativeExpression multiplicativeexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		if (multiplicativeexpression.nodeListOptional.present()) {
			AeFormulaValue aeformulavalue = new AeFormulaValue(multiplicativeexpression);
			obj = aeformulavalue;

			multiplicativeexpression.unaryExpression.accept(this, aeformulavalue);

			NodeSequence ns = (NodeSequence) multiplicativeexpression.nodeListOptional.elementAt(0);
			String operator = ((NodeChoice) ns.elementAt(0)).choice.toString();

			aeformulavalue.setOperator((operator.equals("*") ? AeFormulaValue.MULTIPLY : AeFormulaValue.DIVIDE));

			ns.elementAt(1).accept(this, aeformulavalue);

			aeattributepattern.setValue(aeformulavalue);
		}
		else {
			obj = multiplicativeexpression.unaryExpression.accept(this, aeattributepattern);
		}
		return obj;
	}

	@Override
	public Object visit(MultiplicativeExpression multiplicativeexpression, Object obj) {
		if (obj instanceof AeAttributePattern) {
			return visit(multiplicativeexpression, (AeAttributePattern) obj);
		}
		else {
			logger.debug("Passthrough for Visit MultiplicativeExpression: unknown arg type: " + obj);
			return super.visit(multiplicativeexpression, obj);
		}
	}

	private Object visit(Name name, AeAttributePattern aeattributepattern) {
		Object obj = null;

		AeInstanceAttrValue aeinstanceattrvalue = new AeInstanceAttrValue(name);
		if (name.nodeListOptional.present()) {
			aeinstanceattrvalue.setClassOrObjectName(name.nodeToken.tokenImage);
			NodeListOptional nodelistoptional = name.nodeListOptional;
			if (nodelistoptional.size() > 1) logger.error("Multiple derefs not allowed (for now)...ignoring rest");
			NodeSequence nodesequence = (NodeSequence) nodelistoptional.elementAt(0);
			aeinstanceattrvalue.setAttributeName(((NodeToken) nodesequence.elementAt(1)).tokenImage);
		}
		else {
			aeinstanceattrvalue.setAttributeName(name.nodeToken.tokenImage);
		}

		boolean isNullPattern = aeattributepattern.isNullPattern();
		logger.debug("visit(Name,AeAttributePattern): isNullPattern? = " + isNullPattern);
		// this means the AeAttributePattern is on the RHS of an expression
		if (!(aeattributepattern == null || isNullPattern)) {
			logger.debug("visit(Name,AeAttributePattern): returning " + aeinstanceattrvalue);
			obj = aeinstanceattrvalue;
		}
		else {
			aeattributepattern.setAttributeName(aeinstanceattrvalue.getAttributeName());
			aeattributepattern.setClassName(aeinstanceattrvalue.getClassOrObjectName());
		}

		return obj;
	}

	private Object visit(Name name, AeFormulaValue formula) {
		AeInstanceAttrValue aeinstanceattrvalue = new AeInstanceAttrValue(name);
		if (name.nodeListOptional.present()) {
			aeinstanceattrvalue.setClassOrObjectName(name.nodeToken.tokenImage);
			NodeListOptional nodelistoptional = name.nodeListOptional;
			if (nodelistoptional.size() > 1) logger.error("Multiple derefs not allowed (for now)...ignoring rest");
			NodeSequence nodesequence = (NodeSequence) nodelistoptional.elementAt(0);
			aeinstanceattrvalue.setAttributeName(((NodeToken) nodesequence.elementAt(1)).tokenImage);
		}
		else {
			aeinstanceattrvalue.setAttributeName(name.nodeToken.tokenImage);
		}

		formula.addArgument(aeinstanceattrvalue);

		return aeinstanceattrvalue;
	}

	// test
	private Object visit(Name name, AeRule aerule) {
		AeNameValue nameValue = new AeNameValue(name);
		nameValue.setName(name.nodeToken.tokenImage);
		aerule.addActionParm(nameValue);
		return nameValue;
	}

	private Object visit(Name name, AeTestFunctionPattern testPattern) {
		testPattern.setFunctionName(extractAttributeName(name));
		return null;
	}

	@Override
	public Object visit(Name name, Object parent) {
		if (parent instanceof AeTestFunctionPattern) {
			return visit(name, (AeTestFunctionPattern) parent);
		}

		if (parent instanceof AeAttributePattern) {
			Object value = visit(name, (AeAttributePattern) parent);
			if (value != null) {
				setValue((AeAttributePattern) parent, value);
			}
			return value;
		}
		else if (parent instanceof AeRule) {
			return visit(name, (AeRule) parent);
		}
		else if (parent instanceof AeFormulaValue) {
			return visit(name, (AeFormulaValue) parent);
		}
		else {
			logger.debug("Passthrough for Visit Name: unknown parent type: " + parent);
			return super.visit(name, parent);
		}
	}

	/**
	 * Process node token.
	 * The logic below assumes that the negation operator '~' is followed by
	 * an expression enclosed in paranthesis. For example,
	 * <code>
	 * ~(Class.Attribute in %column 1%)
	 * </code>
	 */
	@Override
	public Object visit(NodeToken nodetoken, Object obj) {
		if (nodetoken.tokenImage.equals("~") || nodetoken.tokenImage.equals("!")) {
			++negationCounter;
		}
		else if (nodetoken.tokenImage.equals("(")) {
			//logger.debug("visit(NodeToken,Object): open paran detected!!!");
			// double negation is no negation (boolean toggle)
			// this is logically equivalent to inNegation = !inNegation;
			if (negationCounter > 0) {
				inNegation = (negationCounter % 2 == 1);
			}
		}
		else if (nodetoken.tokenImage.equals(")")) {
			// toggle negation flag, and decrement negation counter, if negation has detected
			if (negationCounter > 0) {
				inNegation = !inNegation;
				--negationCounter;
			}
		}
		Object result = super.visit(nodetoken, obj);
		return result;
	}

	private Object visit(ObjectCondition objectcondition, AbstractAeCompoundCondition abstractaecompoundcondition) {
		AeObjectPattern aeobjectpattern = null;
		AeObjectPattern aeobjectpattern1 = new AeObjectPattern(objectcondition);
		link(abstractaecompoundcondition, aeobjectpattern1);
		aeobjectpattern = aeobjectpattern1;
		populate(objectcondition, aeobjectpattern1);
		return aeobjectpattern;
	}

	@Override
	public Object visit(ObjectCondition objectcondition, Object obj) {
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(objectcondition, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.debug("Passthrough for Visit ObjectCondition: unknown arg type: " + obj);
			return super.visit(objectcondition, obj);
		}
	}

	private Object visit(PrimaryExpression primaryexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		primaryexpression.nodeChoice.accept(this, aeattributepattern);
		aeattributepattern.setNegated(inNegation);
		return obj;
	}

	@Override
	public Object visit(PrimaryExpression primaryexpression, Object obj) {
		if (obj instanceof AeAttributePattern) {
			return visit(primaryexpression, (AeAttributePattern) obj);
		}
		else {
			logger.debug("Passthrough for visit(PrimaryExpression,Object): unknown arg type: " + obj);
			return super.visit(primaryexpression, obj);
		}
	}

	private Object visit(RelationalExpression relationalexpression, AbstractAeCompoundCondition abstractaecompoundcondition) {
		Object obj = null;
		if (relationalexpression.nodeChoice.which == 0) {
			obj = relationalexpression.nodeChoice.accept(this, abstractaecompoundcondition);
		}
		else if (isConditionalExpression(relationalexpression)) {
			obj = relationalexpression.nodeChoice.accept(this, abstractaecompoundcondition);
		}
		else if (isTestPattern(relationalexpression)) {
			AeTestFunctionPattern testPattern = new AeTestFunctionPattern(relationalexpression);
			link(abstractaecompoundcondition, testPattern);

			obj = testPattern;

			NodeSequence nodesequence = (NodeSequence) relationalexpression.nodeChoice.choice;
			AdditiveExpression additiveexpression = (AdditiveExpression) nodesequence.elementAt(0);
			additiveexpression.accept(this, testPattern);

			NodeListOptional nodelistoptional = (NodeListOptional) nodesequence.elementAt(1);
			addTestPatternArgs(testPattern, nodelistoptional);
		}
		else {
			AeAttributePattern aeattributepattern = new AeAttributePattern(relationalexpression);
			link(abstractaecompoundcondition, aeattributepattern);
			obj = aeattributepattern;

			NodeSequence nodesequence = (NodeSequence) relationalexpression.nodeChoice.choice;
			AdditiveExpression additiveexpression = (AdditiveExpression) nodesequence.elementAt(0);
			additiveexpression.accept(this, aeattributepattern);

			NodeListOptional nodelistoptional = (NodeListOptional) nodesequence.elementAt(1);
			addAttrPatternRhs(aeattributepattern, nodelistoptional);
		}
		return obj;
	}

	@Override
	public Object visit(RelationalExpression relationalexpression, Object obj) {
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(relationalexpression, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.debug("Passthrough for Visit RelationalExpression: unknown arg type: " + obj);
			return super.visit(relationalexpression, obj);
		}
	}

	@Override
	public Object visit(RuleName rulename, Object obj) {
		AeRuleName aerulename = new AeRuleName(rulename);
		return aerulename;
	}

	private Object visit(UnaryExpression unaryexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		switch (unaryexpression.nodeChoice.which) {
		case 0: // '\0'
			Logger.getLogger(AeRuleBuilder.class).debug("+-!~ UnaryExpression not yet handled");
			break;

		case 1: // '\001'
			obj = unaryexpression.nodeChoice.accept(this, aeattributepattern);
			break;
		}
		return obj;
	}

	private Object visit(UnaryExpression unaryexpression, AeFormulaValue formulaValue) {
		return super.visit(unaryexpression, formulaValue);
	}

	@Override
	public Object visit(UnaryExpression unaryexpression, Object obj) {
		if (obj instanceof AeAttributePattern) {
			return visit(unaryexpression, (AeAttributePattern) obj);
		}
		else if (obj instanceof AeFormulaValue) {
			return visit(unaryexpression, (AeFormulaValue) obj);
		}
		else {
			logger.debug("Passthrough for visit(UnaryExpression,Object): unknown arg type: " + obj + " - " + obj.getClass().getName());
			return super.visit(unaryexpression, obj);
		}
	}

}