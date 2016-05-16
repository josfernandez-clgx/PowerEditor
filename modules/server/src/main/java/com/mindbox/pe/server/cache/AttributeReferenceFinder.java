package com.mindbox.pe.server.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCompoundCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeValue;
import com.mindbox.pe.server.generator.aemodel.AeAttributePattern;
import com.mindbox.pe.server.generator.aemodel.AeCellValue;
import com.mindbox.pe.server.generator.aemodel.AeColumnValue;
import com.mindbox.pe.server.generator.aemodel.AeFormulaValue;
import com.mindbox.pe.server.generator.aemodel.AeInstanceAttrValue;
import com.mindbox.pe.server.generator.aemodel.AeLiteralValue;
import com.mindbox.pe.server.generator.aemodel.AeObjectPattern;
import com.mindbox.pe.server.generator.aemodel.AePatternSet;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.AdditiveExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.CellValue;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ColumnLiteral;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalAndExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ConditionalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.server.parser.jtb.rule.syntaxtree.InstanceName;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Literal;
import com.mindbox.server.parser.jtb.rule.syntaxtree.MultiplicativeExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Name;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeChoice;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeListOptional;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;
import com.mindbox.server.parser.jtb.rule.syntaxtree.ObjectCondition;
import com.mindbox.server.parser.jtb.rule.syntaxtree.PrimaryExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.RelationalExpression;
import com.mindbox.server.parser.jtb.rule.syntaxtree.UnaryExpression;
import com.mindbox.server.parser.jtb.rule.visitor.ObjectDepthFirst;

public class AttributeReferenceFinder extends ObjectDepthFirst {

	private Logger logger = Logger.getLogger(AttributeReferenceFinder.class);
	private Map<String, Integer> refMap = new HashMap<String, Integer>();

	/**
	 * @param deploymentRule deploymentRule
	 * @deprecated as of PowerEditor 4.0; DO NOT USE
	 */
	public AttributeReferenceFinder(DeploymentRule deploymentRule) {
		deploymentRule.conditionalExpression.accept(this, new AeRule(deploymentRule));
	}

	public AttributeReferenceFinder(RuleDefinition ruleDef) {
		process(ruleDef.getRootElement());
	}

	private void addAttrPatternRhs(AeAttributePattern aeattributepattern, NodeListOptional nodelistoptional) {
		if (nodelistoptional.size() > 1) logger.error("too many rhsElements to compare! : " + nodelistoptional.size());
		NodeSequence nodesequence = (NodeSequence) nodelistoptional.elementAt(0);
		NodeChoice nodechoice = (NodeChoice) nodesequence.elementAt(0);
		String s = ((NodeToken) nodechoice.choice).tokenImage;
		aeattributepattern.setComparatorFunction(s);
	}

	private String findClassName(AeAttributePattern aeattributepattern) {
		if (aeattributepattern.getClassName() != null) {
			return aeattributepattern.getClassName();
		}
		else if (aeattributepattern.getParentObjectPattern() != null) {
			return aeattributepattern.getParentObjectPattern().getClassName();
		}
		else {
			logger.info("findClassName: parent of " + aeattributepattern + " is " + aeattributepattern.getParentCondition());

			throw new IllegalStateException("Failed to find class name for " + aeattributepattern);
		}
	}

	/**
	 * Gets all attribute references in this.
	 * @return all attribute reference keys
	 * @since PowerEditor 3.2.0
	 */
	public String[] getAllAttributeReferences() {
		return refMap.keySet().toArray(new String[0]);
	}

	public int getColumnNoForAttributeReference(String className, String attribName) {
		Integer value = refMap.get(className + "." + attribName);
		if (value == null) {
			return -1;
		}
		else {
			return value.intValue();
		}
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

	public boolean hasAttributeReference(String className, String attribName) {
		return refMap.containsKey(className + "." + attribName);
	}

	private boolean isConditionalExpression(RelationalExpression relationalexpression) {
		if (relationalexpression.nodeChoice.which == 0) return false;
		NodeSequence nodesequence = (NodeSequence) relationalexpression.nodeChoice.choice;
		NodeListOptional nodelistoptional = (NodeListOptional) nodesequence.elementAt(1);
		return !nodelistoptional.present();
	}

	private void link(AbstractAeCompoundCondition abstractaecompoundcondition, AbstractAeCondition abstractaecondition) {
		logger.debug(">>> link: " + abstractaecompoundcondition + "," + abstractaecondition);

		AbstractAeCompoundCondition abstractaecompoundcondition1 = abstractaecondition.getParentCondition();
		if (abstractaecompoundcondition1 != null) abstractaecompoundcondition1.removeCondition(abstractaecondition);
		abstractaecondition.setParentCondition(abstractaecompoundcondition);
		abstractaecompoundcondition.addCondition(abstractaecondition);
	}

	private void populate(ObjectCondition objectcondition, AeObjectPattern aeobjectpattern) {
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

	private void process(CompoundLHSElement element) {
		for (int i = 0; i < element.size(); i++) {
			LHSElement child = (LHSElement) element.get(i);
			process(child);
		}
	}

	private void process(Condition condition) {
		// store in the map iff condition's value is column reference
		if (condition != null && condition.getReference() != null && condition.getValue() != null && condition.getValue() instanceof ColumnReference) {
			storeRefMap(condition.getReference().getClassName(), condition.getReference().getAttributeName(), ((ColumnReference) condition.getValue()).getColumnNo());
		}
	}

	private void process(ExistExpression existExpression) {
		// TBD: Just processing compound condition for now
		//      - modify to use exist class name, as well
		process(existExpression.getCompoundLHSElement());
	}

	private void process(LHSElement element) {
		if (element instanceof CompoundLHSElement) {
			process((CompoundLHSElement) element);
		}
		else if (element instanceof Condition) {
			process((Condition) element);
		}
		else if (element instanceof ExistExpression) {
			process((ExistExpression) element);
		}
	}

	private boolean setValue(AeAttributePattern aeattributepattern, Object obj) {
		if (aeattributepattern.getValue() != null) {
			logger.error("Value already set!. Ignoring new value: " + obj);
			return false;
		}
		else {
			if (obj instanceof AeColumnValue) {
				String attribName = aeattributepattern.getAttributeName();
				storeRefMap(findClassName(aeattributepattern), attribName, ((AeColumnValue) obj).getColumnNumber());
			}
			else if (obj instanceof AeCellValue) {
				storeRefMap(findClassName(aeattributepattern), aeattributepattern.getAttributeName(), 0);
			}
			else {
				aeattributepattern.setValue((AbstractAeValue) obj);
			}
			logger.debug("<<< setValue(AeAttributePattern,Object) with true");
			return true;
		}
	}

	private void storeRefMap(String className, String attribName, int columnNo) {
		logger.info("storing refmap: " + className + "." + attribName + " --> " + columnNo);
		refMap.put(className + "." + attribName, new Integer(columnNo));
	}

	private Object visit(AdditiveExpression additiveexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		if (additiveexpression.nodeListOptional.present()) {
			AeFormulaValue aeformulavalue = new AeFormulaValue(additiveexpression);
			obj = aeformulavalue;

			additiveexpression.multiplicativeExpression.accept(this, aeformulavalue);

			NodeSequence ns = (NodeSequence) additiveexpression.nodeListOptional.elementAt(0);
			String operator = ((NodeChoice) ns.elementAt(0)).choice.toString();

			aeformulavalue.setOperator((operator.equals("+") ? AeFormulaValue.PLUS : AeFormulaValue.MINUS));

			ns.elementAt(1).accept(this, aeformulavalue);

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
			logger.warn("Passthrough for Visit AdditiveExpression: unknown arg type: " + obj);
			return super.visit(additiveexpression, obj);
		}
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
			logger.warn("Passthrough for Visit ConditionalAndExpression: unknown arg type: " + obj);
			return super.visit(conditionalandexpression, obj);
		}
	}

	private Object visit(ConditionalExpression conditionalexpression, AbstractAeCompoundCondition abstractaecompoundcondition) {
		Object obj = null;
		if (conditionalexpression.nodeListOptional.present()) {
			AePatternSet aepatternset = new AePatternSet(conditionalexpression.nodeListOptional);
			aepatternset.setConditionType(2);
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
		logger.debug("visit(ConditionalExpression,Object): " + conditionalexpression + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(conditionalexpression, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.warn("Passthrough for Visit ConditionalExpression: unknown arg type: " + obj);
			return super.visit(conditionalexpression, obj);
		}
	}

	@Override
	public Object visit(Literal literal, Object parent) {
		if (parent instanceof AeAttributePattern) {
			Object obj1 = getLiteralValue(literal, parent);
			setValue((AeAttributePattern) parent, obj1);
			logger.debug("<<< visit(Literal,Object) with " + obj1);
			return obj1;
		}

		/*if (parent instanceof AeTestFunctionPattern) {
		 Object value = getLiteralValue(literal, parent);
		 if (value instanceof AbstractAeValue) {
		 ((AeTestFunctionPattern) parent).addParam((AbstractAeValue) value);
		 }
		 else {
		 logger.warn("visit(Listeral,AeTestFunctionPattern): ignored invalid value - " + value);
		 }
		 return value;
		 }
		 
		 if (parent instanceof List) {
		 return visit(literal, (List) parent);
		 }*/

		/*if (parent instanceof AeRule) {
		 return visit(literal, (AeRule) parent);
		 }*/

		if (parent instanceof MultiplicativeExpression) {
			return visit(literal, (MultiplicativeExpression) parent);
		}

		logger.warn("Passthrough for Visit Literal: unknown parent type: " + parent);
		return super.visit(literal, parent);
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
			logger.warn("Passthrough for Visit MultiplicativeExpression: unknown arg type: " + obj);
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
		// this means the AeAttributePattern is on the RHS of an expression
		if (!(aeattributepattern == null || isNullPattern)) {
			obj = aeinstanceattrvalue;
		}
		else {
			aeattributepattern.setAttributeName(aeinstanceattrvalue.getAttributeName());
			aeattributepattern.setClassName(aeinstanceattrvalue.getClassOrObjectName());
		}
		return obj;
	}

	@Override
	public Object visit(Name name, Object parent) {
		logger.debug(">>> visit(Name): " + name + "," + parent);
		if (parent instanceof AeAttributePattern) {
			Object value = visit(name, (AeAttributePattern) parent);
			if (value != null) {
				setValue((AeAttributePattern) parent, value);
			}
			return value;
		}
		else {
			logger.warn("Passthrough for Visit Name: unknown parent type: " + parent);
			return super.visit(name, parent);
		}
	}

	private Object visit(ObjectCondition objectcondition, AbstractAeCompoundCondition abstractaecompoundcondition) {
		AeObjectPattern aeobjectpattern = null;
		logger.debug(" --> Creating new AeObjectPattern");
		AeObjectPattern aeobjectpattern1 = new AeObjectPattern(objectcondition);
		link(abstractaecompoundcondition, aeobjectpattern1);
		aeobjectpattern = aeobjectpattern1;
		populate(objectcondition, aeobjectpattern1);
		return aeobjectpattern;
	}

	@Override
	public Object visit(ObjectCondition objectcondition, Object obj) {
		logger.debug("visit(ObjectCondition,Object): " + objectcondition + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(objectcondition, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.warn("Passthrough for Visit ObjectCondition: unknown arg type: " + obj);
			return super.visit(objectcondition, obj);
		}
	}

	private Object visit(PrimaryExpression primaryexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		primaryexpression.nodeChoice.accept(this, aeattributepattern);
		return obj;
	}

	@Override
	public Object visit(PrimaryExpression primaryexpression, Object obj) {
		if (obj instanceof AeAttributePattern) {
			return visit(primaryexpression, (AeAttributePattern) obj);
		}
		else {
			logger.warn("Passthrough for visit(PrimaryExpression,Object): unknown arg type: " + obj);
			return super.visit(primaryexpression, obj);
		}
	}

	private Object visit(RelationalExpression relationalexpression, AbstractAeCompoundCondition abstractaecompoundcondition) {
		logger.debug("visit(RelationalExpression,AbstractAeCompoundCondition): " + relationalexpression + "," + abstractaecompoundcondition);
		Object obj = null;
		if (relationalexpression.nodeChoice.which == 0) {
			obj = relationalexpression.nodeChoice.accept(this, abstractaecompoundcondition);
		}
		else if (isConditionalExpression(relationalexpression)) {
			obj = relationalexpression.nodeChoice.accept(this, abstractaecompoundcondition);
		}
		else {
			logger.debug("--> Creating new AeAttributePattern");

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
		logger.debug("visit(RelationalExpression,Object): " + relationalexpression + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {
			return visit(relationalexpression, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.warn("Passthrough for Visit RelationalExpression: unknown arg type: " + obj);
			return super.visit(relationalexpression, obj);
		}
	}

	private Object visit(UnaryExpression unaryexpression, AeAttributePattern aeattributepattern) {
		Object obj = null;
		switch (unaryexpression.nodeChoice.which) {
		case 0: // '\0'
			Logger.getLogger(AttributeReferenceFinder.class).warn("+-!~ UnaryExpression not yet handled");
			break;

		case 1: // '\001'
			obj = unaryexpression.nodeChoice.accept(this, aeattributepattern);
			break;
		}
		logger.debug("<<< visit(UnaryExpression,AeAttributePattern): " + obj);
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
			logger.warn("Passthrough for visit(UnaryExpression,Object): unknown arg type: " + obj + " - " + obj.getClass().getName());
			return super.visit(unaryexpression, obj);
		}
	}

}