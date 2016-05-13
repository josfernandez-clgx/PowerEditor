/*
 * Created on 2003. 11. 10.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.server.generator.processor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.aeobjects.AbstractAeCompoundCondition;
import com.mindbox.pe.server.generator.aeobjects.AbstractAeValue;
import com.mindbox.pe.server.generator.aeobjects.AeActivationDateValue;
import com.mindbox.pe.server.generator.aeobjects.AeAttributePattern;
import com.mindbox.pe.server.generator.aeobjects.AeCategoryIDValue;
import com.mindbox.pe.server.generator.aeobjects.AeCategoryNameValue;
import com.mindbox.pe.server.generator.aeobjects.AeCellValue;
import com.mindbox.pe.server.generator.aeobjects.AeChannelIDValue;
import com.mindbox.pe.server.generator.aeobjects.AeColumnValue;
import com.mindbox.pe.server.generator.aeobjects.AeCreateSequenceValue;
import com.mindbox.pe.server.generator.aeobjects.AeExpirationDateValue;
import com.mindbox.pe.server.generator.aeobjects.AeInstanceAttrValue;
import com.mindbox.pe.server.generator.aeobjects.AeInvestorIDValue;
import com.mindbox.pe.server.generator.aeobjects.AeLineageIDValue;
import com.mindbox.pe.server.generator.aeobjects.AeLiteralValue;
import com.mindbox.pe.server.generator.aeobjects.AeMessageLiteral;
import com.mindbox.pe.server.generator.aeobjects.AeObjectPattern;
import com.mindbox.pe.server.generator.aeobjects.AePatternSet;
import com.mindbox.pe.server.generator.aeobjects.AeProductIDValue;
import com.mindbox.pe.server.generator.aeobjects.AeReferenceValue;
import com.mindbox.pe.server.generator.aeobjects.AeRowNumberValue;
import com.mindbox.pe.server.generator.aeobjects.AeRule;
import com.mindbox.pe.server.generator.aeobjects.AeRuleNameValue;
import com.mindbox.pe.server.generator.aeobjects.LinkHelper;
import com.mindbox.pe.server.model.rule.RuleActionMethod;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ActivationDateLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.AllQualifier;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.AndExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ArgumentList;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ArgumentLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Arguments;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.BooleanLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.CategoryIDLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.CategoryNameLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ChannelIDLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ConditionalExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ConditionalOperator;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ExcludingQualifier;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ExistExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ExistQualifier;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ExistQuantifier;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ExpirationDateLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.InvestorIDLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.LHS;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.LineageIDLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ListCreationArguments;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.LiteralExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.LiteralList;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.MembershipOperator;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.MessageLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.NodeListOptional;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.NullLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.OrExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ProductIDLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.RHS;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Reference;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ReferenceInArgument;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.RowNumberLiteral;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.RuleNameLiteral;
import com.mindbox.pe.server.parser.jtb.rule.visitor.ObjectDepthFirst;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class AERuleBuilder extends ObjectDepthFirst {

	private final Logger logger;
	private boolean errorEncountered;
	private String errorString;
	private TemplateUsageType usageType = null;

	public AERuleBuilder() {
		this.logger = Logger.getLogger(getClass());
	}

	@SuppressWarnings("unchecked")
	private void buildReferenceChain(NodeListOptional nodeListOptional, AeReferenceValue refValue) {
		for (Iterator<NodeSequence> iter = nodeListOptional.nodes.iterator(); iter.hasNext();) {
			NodeSequence seq = (NodeSequence) iter.next();
			refValue.addName(((NodeToken) seq.nodes.get(1)).tokenImage);
		}
	}

	public AeRule generateRuleSkeleton(DeploymentRule deploymentrule) throws SapphireException {
		logger.info(">>> generateRuleSkeleton with " + deploymentrule);
		initForEachRule(usageType);
		AeRule aeRule = process(deploymentrule);
		if (errorEncountered)
			throw new SapphireException("Could not parse deployment rule: " + errorString);
		else
			return aeRule;
	}

	private Object getLiteralValue(ArgumentLiteral node, Object obj) {
		logger.debug(">>> getLiteralValue(ArgumentLiteral,Object): " + node + ", " + obj);
		logger.debug("getLiteralValue(ArgumentLiteral,Object): nodeChoice.which = " + node.f0.which);

		Object value = null;

		switch (node.f0.which) {
		case 1: // <INTEGER_LITERAL>
			value = new AeLiteralValue(node, Integer.valueOf(((NodeToken) node.f0.choice).tokenImage));
			break;
		case 2: // <FLOATING_POINT_LITERAL>
			value = new AeLiteralValue(node, Float.valueOf(((NodeToken) node.f0.choice).tokenImage));
			break;
		case 0: // <IDENTIFIER>
		case 3: // <CHARACTER_LITERAL>
		case 4: // <STRING_LITERAL>
			value = new AeLiteralValue(node, ((NodeToken) node.f0.choice).tokenImage);
			break;
		default:
			value = node.f0.choice.accept(this, obj);
			break;
		}
		logger.debug("<<< getLiteralValue(ArgumentLiteral,Object): " + value);
		return value;
	}

	private Object getLiteralValue(LiteralExpression node, Object obj) {
		logger.debug(">>> getLiteralValue(LiteralExpression,Object): " + node + ", " + obj);
		logger.debug("getLiteralValue(LiteralExpression,Object): nodeChoice.which = " + node.f0.which);

		Object value = null;

		switch (node.f0.which) {
		case 0: // <INTEGER_LITERAL>
			value = new AeLiteralValue(node, Integer.valueOf(((NodeToken) node.f0.choice).tokenImage));
			break;
		case 1: // <FLOATING_POINT_LITERAL>
			value = new AeLiteralValue(node, Float.valueOf(((NodeToken) node.f0.choice).tokenImage));
			break;
		case 2: // <CHARACTER_LITERAL>
		case 3: // <STRING_LITERAL>
			value = new AeLiteralValue(node, ((NodeToken) node.f0.choice).tokenImage);
			break;
		default:
			value = node.f0.choice.accept(this, obj);
			break;
		}
		logger.debug("<<< getLiteralValue(LiteralExpression,Object): " + value);
		return value;
	}

	private void initForEachRule(TemplateUsageType useType) {
		errorEncountered = false;
		errorString = null;
		usageType = useType;
	}

	private Object process(AndExpression andExpression, AbstractAeCompoundCondition compoundCondition) {
		logger.debug(">>> visit(AndExpression,AbstractAeCompoundCondition): " + andExpression + "," + compoundCondition);
		Object obj = null;
		if (andExpression.f1.present()) { // f1 - NodeListOptional
			logger.debug("visit(AndExpression,AbstractAeCompoundCondition): * Creating new AND AePatternSet for " + andExpression);
			AePatternSet aepatternset = new AePatternSet(andExpression.f1);
			aepatternset.setConditionType(AePatternSet.CONDITION_AND);
			LinkHelper.link(compoundCondition, aepatternset);
			andExpression.f0.accept(this, aepatternset); // f0 - ExistExpression
			andExpression.f1.accept(this, aepatternset);
			obj = aepatternset;
		}
		else {
			obj = andExpression.f0.accept(this, compoundCondition);
		}
		return obj;
	}

	private AeRule process(DeploymentRule deploymentRule) {
		logger.info(">>> process: " + deploymentRule);
		logger.info("* creating new AeRule with " + deploymentRule);

		AeRule aerule = new AeRule(deploymentRule);
		deploymentRule.f1.accept(this, aerule); // f1 = LHS

		logger.info("process(DeploymentRule): processing action of the rule...");
		deploymentRule.f3.accept(this, aerule); // f3 = RHS
		return aerule;
	}

	private Object process(ExistExpression existExpression, AbstractAeCompoundCondition compoundCondition) {
		logger.debug(">>> visit(ExistExpression,AbstractAeCompoundCondition): " + existExpression + "," + compoundCondition);

		AeObjectPattern aeobjectpattern = null;
		logger.debug("visit(ExistExpression,AbstractAeCompoundCondition): * Creating new AeObjectPattern");
		aeobjectpattern = new AeObjectPattern(existExpression);
		LinkHelper.link(compoundCondition, aeobjectpattern);

		NodeSequence nodeSeq = (NodeSequence) existExpression.f0.choice;
		((Node) nodeSeq.nodes.get(0)).accept(this, aeobjectpattern);
		((Node) nodeSeq.nodes.get(2)).accept(this, aeobjectpattern);

		return aeobjectpattern;
	}

	private Object process(LHS lhsNode, AbstractAeCompoundCondition compoundCondition) {
		logger.debug(">>> process(LHS,AbstractAeCompoundCondition): " + lhsNode + "," + compoundCondition);
		return visit(lhsNode.f0, compoundCondition);
	}

	private Object process(OrExpression orExpression, AbstractAeCompoundCondition compoundCondition) {
		logger.debug(">>> process(OrExpression,AbstractAeCompoundCondition): " + orExpression + "," + compoundCondition);
		Object obj = null;
		if (orExpression.f1.present()) { // f1 = NodeListOptional
			logger.debug("process(OrExpression,AbstractAeCompoundCondition): * Creating new OR AePatternSet for " + orExpression);
			AePatternSet aepatternset = new AePatternSet(orExpression.f1);
			aepatternset.setConditionType(AePatternSet.CONDITION_OR);
			LinkHelper.link(compoundCondition, aepatternset);
			orExpression.f0.accept(this, aepatternset); // f0 = AndExpression
			orExpression.f1.accept(this, aepatternset);
			obj = aepatternset;
		}
		else {
			obj = orExpression.f0.accept(this, compoundCondition);
		}
		return obj;
	}

	private void reverseComparisonStr(AeAttributePattern attrPattern) {
		String str = attrPattern.getComparatorFunction();
		if (str == null || str.length() == 0) return;
		if (str.equals("<")) {
			attrPattern.setComparatorFunction(">");
		}
		else if (str.equals("<=")) {
			attrPattern.setComparatorFunction(">=");
		}
		else if (str.equals(">")) {
			attrPattern.setComparatorFunction("<");
		}
		else if (str.equals(">=")) {
			attrPattern.setComparatorFunction("<=");
		}
	}

	private boolean setValue(AeAttributePattern aeattributepattern, Object obj) {
		logger.debug(">>> setValue(AeAttributePattern,Object): " + aeattributepattern + ", " + obj);
		if (aeattributepattern.hasValue()) {
			logger.error("*** Value already set!. Ignoring new value: " + obj);
			return false;
		}
		else {
			aeattributepattern.setValue((AbstractAeValue) obj);
			logger.debug("<<< setValue(AeAttributePattern,Object) with true");
			return true;
		}
	}

	public Object visit(ActivationDateLiteral n, Object argu) {
		AeActivationDateValue value = new AeActivationDateValue(n);
		return value;
	}

	/*
	public Object visit(ExistExpressionPrefix node, Object obj) {
		logger.debug(">>> visit(ExistExpressionPrefix): " + node);
		if (obj instanceof AeObjectPattern) {
			populate(node, (AeObjectPattern) obj);
			return null;
		}
		else {
			logger.warn("visit(ExistExpressionPrefix): Passthrough - unknown arg type: " + obj);
			return super.visit(node, obj);
		}
	}*/

	public Object visit(AllQualifier n, Object obj) {
		logger.debug(">>> visiit(AllQualifier): " + n + "," + obj);
		if (obj instanceof AeObjectPattern) {
			((AeObjectPattern) obj).setCardinalityType(AeObjectPattern.CARDINALITY_ALL);
			return null;
		}
		else {
			logger.warn("visit(AllQualifier): passthrough - unknown arg: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(AndExpression andExpression, Object obj) {
		logger.debug(">>> visit(AndExpression): " + andExpression + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {
			return process(andExpression, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.warn("visit(AndExpression): Passthrough - unknown arg type: " + obj);
			return super.visit(andExpression, obj);
		}
	}

	public Object visit(ArgumentList objmentList, Object obj) {
		logger.debug(">>> visit(ArgumentList) with " + objmentList + ", " + obj);
		Object value = null;
		value = objmentList.f0.accept(this, obj);
		value = objmentList.f1.accept(this, obj);
		logger.debug("<<< visit(ArgumentList): " + value);
		return value;
	}

	public Object visit(ArgumentLiteral node, Object obj) {
		logger.debug(">>> visit(ArgumentLiteral): " + node + "," + obj);
		Object value = null;
		if (obj instanceof AeRule) {
			AbstractAeValue aeValue = (AbstractAeValue) getLiteralValue(node, obj);
			((AeRule) obj).addActionParm(aeValue);
			value = aeValue;
		}
		else if (obj instanceof AeCreateSequenceValue) {
			AbstractAeValue aeValue = (AbstractAeValue) getLiteralValue(node, obj);
			((AeCreateSequenceValue) obj).addValue(aeValue);
			value = aeValue;
		}
		else {
			logger.warn("visit(ArgumentLiteral): passthrough - invalid object: " + obj);
			value = super.visit(node, obj);
		}
		logger.debug("<<< visit(ArgumentLiteral): " + value);
		return value;
	}

	public Object visit(Arguments objments, Object obj) {
		logger.debug(">>> visit(Arguments): " + objments + "," + obj);
		Object value = objments.f1.accept(this, obj); // f1 = NodeListOptional
		logger.debug("<<< visit(Arguments): " + value);
		return value;
	}

	public Object visit(BooleanLiteral node, Object obj) {
		logger.debug(">> visit(BooleanLiteral): " + node + "," + obj);
		AeLiteralValue aeliteralvalue = new AeLiteralValue(node);
		aeliteralvalue.setValue((node.f0.which == 0 ? Boolean.TRUE : Boolean.FALSE));
		return aeliteralvalue;
	}

	public Object visit(CategoryIDLiteral n, Object obj) {
		AeCategoryIDValue value = new AeCategoryIDValue(n);
		return value;
	}

	public Object visit(CategoryNameLiteral n, Object obj) {
		AeCategoryNameValue value = new AeCategoryNameValue(n);
		return value;
	}

	public Object visit(CellValueLiteral n, Object obj) {
		AeCellValue value = new AeCellValue(n);
		return value;
	}

	public Object visit(ChannelIDLiteral n, Object obj) {
		AeChannelIDValue value = new AeChannelIDValue(n);
		return value;
	}

	public Object visit(ColumnLiteral n, Object obj) {
		String s = n.f2.tokenImage;
		int colNo = Integer.parseInt(s);

		AeColumnValue value = new AeColumnValue(n);
		value.setColumnNumber(colNo);
		return value;
	}

	public Object visit(ConditionalExpression node, Object obj) {
		logger.debug(">>> visit(ConditionalExpression): " + node + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {

			logger.debug("visit(ConditionalExpression): * Creating new AeAttributePattern");
			AeAttributePattern attributePattern = new AeAttributePattern(node);
			LinkHelper.link((AbstractAeCompoundCondition) obj, attributePattern);

			node.f0.accept(this, attributePattern);
			node.f1.accept(this, attributePattern);

			return attributePattern;
		}
		else {
			logger.warn("visit(ConditionalExpression): passthrough - invalid obj: " + obj);
			return super.visit(node, obj);
		}
	}

	public Object visit(ConditionalOperator node, Object obj) {
		logger.debug(">>> visit(ConditionalOperator): " + node + "," + obj);
		if (obj instanceof AeAttributePattern) {
			String opStr = ((NodeToken) node.f0.choice).tokenImage;
			((AeAttributePattern) obj).setComparatorFunction(opStr);
			return null;
		}
		else {
			logger.warn("visit(ConditionalOperator): passthrough - invalid obj: " + obj);
			return super.visit(node, obj);
		}
	}

	public Object visit(ExcludingQualifier n, Object obj) {
		logger.debug(">>> visiit(ExcludingQualifier): " + n + "," + obj);
		if (obj instanceof AeObjectPattern) {
			String excObjectName = n.f1.tokenImage;
			((AeObjectPattern) obj).setExcludedObjectName(excObjectName);
			return null;
		}
		else {
			logger.warn("visit(ExcludingQualifier): passthrough - unknown arg: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(ExistExpression existExpression, Object obj) {
		logger.debug(">>> visit(ExistExpression): " + existExpression);
		if (existExpression.f0.which == 0) { // exist expression			
			if (obj instanceof AbstractAeCompoundCondition) {
				return process(existExpression, (AbstractAeCompoundCondition) obj);
			}
			else {
				logger.warn("visit(ExistExpression): Passthrough - unknown arg type: " + obj);
				return super.visit(existExpression, obj);
			}
		}
		else {
			return super.visit(existExpression, obj);
		}
	}

	public Object visit(ExistQualifier n, Object obj) {
		logger.debug(">>> visiit(ExistQualifier): " + n + "," + obj);
		if (obj instanceof AeObjectPattern) {
			String tokenStr = ((NodeToken) n.f0.choice).tokenImage;
			if (tokenStr.equals("exists")) {
				((AeObjectPattern) obj).setCardinalityType(AeObjectPattern.CARDINALITY_EXISTS);
			}
			else {
				((AeObjectPattern) obj).setCardinalityType(AeObjectPattern.CARDINALITY_ANY);
			}
			return null;
		}
		else {
			logger.warn("visit(ExistQualifier): passthrough - unknown arg: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(ExistQuantifier n, Object obj) {
		logger.debug(">>> visiit(ExistQuantifier): " + n + "," + obj);
		if (obj instanceof AeObjectPattern) {
			String tokenStr = ((NodeToken) n.f1.choice).tokenImage;
			if (tokenStr.equals("most")) {
				((AeObjectPattern) obj).setCardinalityType(AeObjectPattern.CARDINALITY_AT_MOST);
			}
			else {
				((AeObjectPattern) obj).setCardinalityType(AeObjectPattern.CARDINALITY_AT_LEAST);
			}
			int quantity = Integer.parseInt(n.f2.tokenImage);
			((AeObjectPattern) obj).setCardinalityLimit(quantity);
			return null;
		}
		else {
			logger.warn("visit(ExistQuantifier): passthrough - unknown arg: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(ExpirationDateLiteral n, Object argu) {
		AeExpirationDateValue value = new AeExpirationDateValue(n);
		return value;
	}

	public Object visit(InvestorIDLiteral n, Object obj) {
		AeInvestorIDValue value = new AeInvestorIDValue(n);
		return value;
	}

	public Object visit(LHS lhsNode, Object obj) {
		logger.debug(">>> visit(LHS): " + lhsNode + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {
			return process(lhsNode, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.warn("visit(LHS): Passthrough - unknown arg type: " + obj);
			return super.visit(lhsNode, obj);
		}
	}

	public Object visit(LineageIDLiteral n, Object argu) {
		AeLineageIDValue value = new AeLineageIDValue(n);
		return value;
	}

	public Object visit(ListCreationArguments node, Object obj) {
		logger.debug(">>> visit(ListCreationArguments): " + node + "," + obj);
		Object value = null;
		if (obj instanceof AeRule || obj instanceof AeCreateSequenceValue) {
			AeCreateSequenceValue seqValue = new AeCreateSequenceValue(node);
			if (node.f1.present()) {
				node.f1.accept(this, seqValue);
			}
			value = seqValue;
		}
		else {
			logger.warn("visit(ListCreationArguments): passthrough - invalid object: " + obj);
			value = super.visit(node, obj);
		}
		logger.debug("<<< visit(ListCreationArguments): " + value);
		return value;
	}

	@SuppressWarnings("unchecked")
	public Object visit(LiteralExpression node, Object obj) {
		logger.debug(">>> visit(LiteralExpression): " + node + "," + obj);
		if (obj instanceof AeAttributePattern) {
			Object value = getLiteralValue(node, obj);
			setValue((AeAttributePattern) obj, value);
			return value;
		}
		else if (obj instanceof List) {
			Object value = getLiteralValue(node, obj);
			((List<Object>) obj).add(value);
			return value;
		}
		else {
			logger.warn("visit(LiteralExpression): passthrough - invalid obj: " + obj);
			return super.visit(node, obj);
		}
	}

	public Object visit(LiteralList node, Object obj) {
		logger.debug(">>> visit(LiteralList): " + node + "," + obj);
		if (obj instanceof AeAttributePattern) {
			AeLiteralValue value = new AeLiteralValue(node);
			List<Object> list = new LinkedList<Object>();
			value.setValue(list);

			node.f1.accept(this, list);
			node.f2.accept(this, list);

			((AeAttributePattern) obj).setValue(value);
			return value;
		}
		else {
			logger.warn("visit(LiteralList): passthrough - invalid obj: " + obj);
			return super.visit(node, obj);
		}
	}

	public Object visit(MembershipOperator node, Object obj) {
		logger.debug(">>> visit(MembershipOperator): " + node + "," + obj);
		if (obj instanceof AeAttributePattern) {
			String opStr = ((NodeToken) node.f0.choice).tokenImage;
			((AeAttributePattern) obj).setComparatorFunction(opStr);
			return null;
		}
		else {
			logger.warn("visit(MembershipOperator): passthrough - invalid obj: " + obj);
			return super.visit(node, obj);
		}
	}

	public Object visit(MessageLiteral n, Object obj) {
		AeMessageLiteral value = new AeMessageLiteral(n);
		return value;
	}

	public Object visit(NodeToken n, Object obj) {
		logger.debug(">>> visiit(NodeToken): " + n + "," + obj);
		if (obj instanceof AeObjectPattern) {
			// identifier (instance name)
			String instanceName = n.tokenImage;
			((AeObjectPattern) obj).setObjectName(instanceName);
			if (instanceName != null && instanceName.length() > 0) {
				AeRule aerule = ((AeObjectPattern) obj).getParentRule();
				if (aerule != null) {
					aerule.addUserRegisteredName(instanceName);
				}
			}
			return null;
		}
		else {
			logger.warn("visit(NodeToken): passthrough - unknown arg: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(NullLiteral n, Object obj) {
		AeLiteralValue value = new AeLiteralValue(n);
		value.setValue("Nil");
		return value;
	}

	public Object visit(OrExpression n, Object obj) {
		logger.debug(">>> process(OrExpression): " + n + "," + obj);
		if (obj instanceof AbstractAeCompoundCondition) {
			return process(n, (AbstractAeCompoundCondition) obj);
		}
		else {
			logger.warn("visit(OrExpression): Passthrough - unknown arg type: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(ProductIDLiteral n, Object obj) {
		AeProductIDValue value = new AeProductIDValue(n);
		return value;
	}

	public Object visit(Reference n, Object obj) {
		logger.debug(">>> visiit(Reference): " + n + "," + obj);
		if (obj instanceof AeObjectPattern) {
			String className = n.f0.tokenImage;
			((AeObjectPattern) obj).setClassName(className);
			return null;
		}
		else if (obj instanceof AeAttributePattern) {
			AeAttributePattern attrPattern = (AeAttributePattern) obj;
			//((AeAttributePattern)obj).set
			String className = null;
			String attrName = null;
			if (n.f1.present()) {
				className = n.f0.tokenImage;
				NodeSequence seq = (NodeSequence) n.f1.nodes.get(0);
				attrName = ((NodeToken) seq.nodes.get(1)).tokenImage;
			}
			else {
				attrName = n.f0.tokenImage;
			}

			if (attrPattern.isClassAttributeSet()) {
				logger.info("visit(Reference): * Creating new AeInstanceAttr Value");
				AeInstanceAttrValue attrValue = new AeInstanceAttrValue(n);
				attrValue.setClassOrObjectName(className);
				attrValue.setAttributeName(attrName);

				setValue(attrPattern, attrValue);
			}
			else {
				attrPattern.setClassName(className);
				attrPattern.setAttributeName(attrName);

				reverseComparisonStr(attrPattern);
			}
			return null;
		}
		else {
			logger.warn("visit(Reference): passthrough - unknown arg: " + obj);
			return super.visit(n, obj);
		}
	}

	public Object visit(ReferenceInArgument node, Object obj) {
		logger.debug(">>> visit(ReferenceInArgument): " + node + "," + obj);
		Object value = null;
		AeReferenceValue refValue = new AeReferenceValue(node);
		String className = node.f1.tokenImage;
		refValue.addName(className);
		if (node.f2.present()) {
			buildReferenceChain(node.f2, refValue);
		}
		value = refValue;
		logger.debug("<<< visit(ReferenceInArgument): " + value);
		return value;
	}

	public Object visit(RHS node, Object obj) {
		logger.debug(">>> visit(RHS): " + node + "," + obj);
		Object value = null;
		if (obj instanceof AeRule) {
			final String functionName = node.f0.tokenImage; // f0 is the name of the function
			final RuleActionMethod raMethod = RuleActionMethod.valueOf(functionName, !ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).isPEActionOn());
			((AeRule) obj).setActionMethod(raMethod);

			value = node.f1.accept(this, obj); // f1 is objments
		}
		else {
			logger.warn("visit(RHS): passthrough - invalid obj: " + obj);
			value = super.visit(node, obj);
		}
		logger.debug("<<< visit(RHS): " + value);
		return value;
	}

	public Object visit(RowNumberLiteral node, Object obj) {
		AeRowNumberValue value = new AeRowNumberValue(node);
		return value;
	}

	public Object visit(RuleNameLiteral node, Object obj) {
		AeRuleNameValue ruleNameValue = new AeRuleNameValue(node);
		return ruleNameValue;
	}

}
