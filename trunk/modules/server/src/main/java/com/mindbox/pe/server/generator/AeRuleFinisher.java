package com.mindbox.pe.server.generator;

import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCompoundCondition;
import com.mindbox.pe.server.generator.aemodel.AbstractAeCondition;
import com.mindbox.pe.server.generator.aemodel.AeAttributePattern;
import com.mindbox.pe.server.generator.aemodel.AeObjectPattern;
import com.mindbox.pe.server.generator.aemodel.AePatternSet;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.pe.server.generator.aemodel.AeTestFunctionPattern;

public class AeRuleFinisher {

	static void expandLineages(AeObjectPattern aeobjectpattern) throws SapphireException {
		AbstractAeCondition abstractaecondition = aeobjectpattern.getNestedCondition();
		if (abstractaecondition instanceof AePatternSet) {
			AePatternSet aepatternset = (AePatternSet) abstractaecondition;
			List<AbstractAeCondition> list = new java.util.ArrayList<AbstractAeCondition>(aepatternset.getConditions());
			for (int i = 0; i < list.size(); i++)
				if (list.get(i) instanceof AeObjectPattern) {
					AeObjectPattern aeobjectpattern1 = (AeObjectPattern) list.get(i);
					DomainClassLink adomainclasslink[] =
						DomainManager.getInstance().getLinkage(
							aeobjectpattern1.getClassName(),
							aeobjectpattern.getClassName());
					if (adomainclasslink != null) {
						if (adomainclasslink.length > 1) {
							aepatternset.getConditions().remove(aeobjectpattern1);
							AeObjectPattern aeobjectpattern2 = aeobjectpattern1;
							for (int j = 0; j < adomainclasslink.length - 1; j++) {
								DomainClassLink domainclasslink = adomainclasslink[j];
								AeObjectPattern aeobjectpattern3 = new AeObjectPattern(aeobjectpattern1.getNode());
								aeobjectpattern3.setClassName(domainclasslink.getParentName());
								AeRuleBuilder.link(aeobjectpattern3, aeobjectpattern2);
								initializeNames(aeobjectpattern3);
								aeobjectpattern2 = aeobjectpattern3;
							}

							AeRuleBuilder.link(aepatternset, aeobjectpattern2);
						}
					}
				}
		}
	}

	private final Logger logger;

	public AeRuleFinisher(AeMapper aemapper) {
		this.logger = Logger.getLogger(getClass());
		setMapper(aemapper);
	}

	public void process(AeRule aerule, GridTemplate gridtemplate) throws SapphireException {
		getMapper().reInitRuleVariables();
		aerule.setRuleset(AeMapper.getRuleset(gridtemplate));
		process(aerule.getLhs());
		aerule.setProcessed(true);
	}

	private void process(AbstractAeCondition abstractaecondition) throws SapphireException {
		if (abstractaecondition instanceof AbstractAeCompoundCondition)
			process((AbstractAeCompoundCondition) abstractaecondition);
		else if (abstractaecondition instanceof AeAttributePattern)
			process((AeAttributePattern) abstractaecondition);
		else if (abstractaecondition instanceof AeTestFunctionPattern) {
			process((AeTestFunctionPattern) abstractaecondition);
		}
		else
			logger.error("Unknown AbstractAeCondition pattern: " + abstractaecondition);
	}

	private void process(AbstractAeCompoundCondition abstractaecompoundcondition) throws SapphireException {
		if (abstractaecompoundcondition instanceof AePatternSet)
			process((AePatternSet) abstractaecompoundcondition);
		else if (abstractaecompoundcondition instanceof AeObjectPattern)
			process((AeObjectPattern) abstractaecompoundcondition);
		else
			logger.error("Unknown AbstractAeCompoundCondition pattern: " + abstractaecompoundcondition);
	}

	private void process(AePatternSet aepatternset) throws SapphireException {
		List<AbstractAeCondition> list = new java.util.ArrayList<AbstractAeCondition>(aepatternset.getConditions());
		for (int i = 0; i < list.size(); i++) {
			AbstractAeCondition abstractaecondition = (AbstractAeCondition) list.get(i);
			if (!aepatternset.equals(abstractaecondition.getParentCondition())) {
				logger.error(
					"Invalid uplink for "
						+ abstractaecondition
						+ "...fixing from "
						+ abstractaecondition.getParentCondition());
				abstractaecondition.setParentCondition(aepatternset);
			}
			process(abstractaecondition);
		}
	}

	private void process(AeObjectPattern aeobjectpattern) throws SapphireException {
		printOnDebug(
			"Processing AeObjectPattern: " + aeobjectpattern + " Parent=" + aeobjectpattern.getParentCondition());
		initializeNames(aeobjectpattern);
		AbstractAeCondition abstractaecondition = aeobjectpattern.getNestedCondition();
		if (abstractaecondition != null) {
			process(abstractaecondition);
			expandLineages(aeobjectpattern);
			flattenNestedPatterns(aeobjectpattern);
		}
	}


	private void process(AeAttributePattern aeattributepattern) throws SapphireException {
		printOnDebug("Processing AeAttributePattern: " + aeattributepattern);
		if (aeattributepattern.getClassName() != null) {
			//printOnDebug("Detected fake attrib pattern: " + aeattributepattern);
			AbstractAeCompoundCondition abstractaecompoundcondition = aeattributepattern.getParentCondition();
			abstractaecompoundcondition.removeCondition(aeattributepattern);
			AeObjectPattern aeobjectpattern = new AeObjectPattern(aeattributepattern.getNode());
			aeobjectpattern.setClassName(aeattributepattern.getClassName());
			AeRuleBuilder.link(abstractaecompoundcondition, aeobjectpattern);
			printOnDebug("New obj's parent = " + abstractaecompoundcondition);
			aeattributepattern.setClassName(null);
			AeRuleBuilder.link(aeobjectpattern, aeattributepattern);
			process(aeobjectpattern);
		}
		else if (aeattributepattern.getAeNameVar() == null) {
			logger.info("process: calling mMapper.generateAEVariable() with " + aeattributepattern);
			String s = mMapper.generateAEVariable(aeattributepattern.getAttributeName(), true);
			aeattributepattern.setAeNameVar(s);
		}
	}

	private void process(AeTestFunctionPattern testPattern) throws SapphireException {
		printOnDebug("Processing AeTestFunctionPattern: " + testPattern);
		// to preverse ordering as specified in the rule
		AbstractAeCompoundCondition parent = testPattern.getParentCondition();
		parent.removeCondition(testPattern);
		testPattern.setParentCondition(parent);
		parent.addCondition(testPattern);
	}

	private AeMapper getMapper() {
		return mMapper;
	}

	private void setMapper(AeMapper aemapper) {
		mMapper = aemapper;
	}

	static void initializeNames(AeObjectPattern aeobjectpattern) {
		if (aeobjectpattern.getAeObjectNameVar() == null) {
			String s = aeobjectpattern.getObjectName();
			if (s == null)
				s = AeMapper.generateInstanceName(aeobjectpattern.getClassName());
			s = AeMapper.makeAEVariable(s);
			aeobjectpattern.setAeObjectNameVar(s);
		}
		if (aeobjectpattern.getAeExcludedObjectNameVar() == null) {
			String s1 = aeobjectpattern.getExcludedObjectName();
			if (s1 != null)
				aeobjectpattern.setAeExcludedObjectNameVar(AeMapper.makeAEVariable(AeMapper.makeAEName(s1)));
		}
	}

	private void printOnDebug(String s) {
		logger.debug(s);
	}

	private void flattenNestedObjectPattern(AeObjectPattern aeobjectpattern, AeObjectPattern aeobjectpattern1)
		throws SapphireException {
		AbstractAeCondition abstractaecondition = aeobjectpattern.getNestedCondition();
		if (abstractaecondition == null || !(abstractaecondition instanceof AePatternSet))
			return;
		AePatternSet aepatternset = (AePatternSet) abstractaecondition;
		AePatternSet aepatternset1 = getContainingPatternSet(aeobjectpattern);
		if (aepatternset1 != null) {
			if (aepatternset1.getConditionType() != 1) {
				AePatternSet aepatternset2 = new AePatternSet(aepatternset1.getNode());
				aepatternset2.setConditionType(1);
				AeRuleBuilder.link(aepatternset2, aeobjectpattern);
				AeRuleBuilder.link(aepatternset1, aepatternset2);
				aepatternset1 = aepatternset2;
			}
			AeRuleBuilder.link(aepatternset1, aeobjectpattern1);
		}
		else {
			AeRule aerule = aeobjectpattern.getParentRule();
			AbstractAeCondition abstractaecondition1 = aerule.getLhs();
			AePatternSet aepatternset3 = new AePatternSet(abstractaecondition1.getNode());
			aepatternset3.setConditionType(1);
			AeRuleBuilder.link(aepatternset3, abstractaecondition1);
			AeRuleBuilder.link(aepatternset3, aeobjectpattern1);
		}
		AeAttributePattern aeattributepattern = new AeAttributePattern(aeobjectpattern1.getNode());
		aeattributepattern.setAttributeName(aeobjectpattern1.getClassName());
		aeattributepattern.setAeNameVar(aeobjectpattern1.getAeObjectNameVar());
		AeRuleBuilder.link(aepatternset, aeattributepattern);
		flattenNestedPatterns(aeobjectpattern1);
	}

	private void flattenNestedPatterns(AeObjectPattern aeobjectpattern) throws SapphireException {
		AbstractAeCondition abstractaecondition = aeobjectpattern.getNestedCondition();
		if (abstractaecondition instanceof AePatternSet) {
			AePatternSet aepatternset = (AePatternSet) abstractaecondition;
			List<AbstractAeCondition> list = new java.util.ArrayList<AbstractAeCondition>(aepatternset.getConditions());
			for (int i = 0; i < list.size(); i++)
				if (list.get(i) instanceof AeObjectPattern) {
					AeObjectPattern aeobjectpattern2 = (AeObjectPattern) list.get(i);
					aepatternset.getConditions().remove(aeobjectpattern2);
					flattenNestedObjectPattern(aeobjectpattern, aeobjectpattern2);
				}

		}
		else if (abstractaecondition instanceof AeObjectPattern) {
			AeObjectPattern aeobjectpattern1 = (AeObjectPattern) abstractaecondition;
			AePatternSet aepatternset1 = new AePatternSet(aeobjectpattern1.getNode());
			aepatternset1.setConditionType(1);
			AeRuleBuilder.link(aepatternset1, aeobjectpattern1);
			AeRuleBuilder.link(aeobjectpattern, aepatternset1);
			flattenNestedObjectPattern(aeobjectpattern, aeobjectpattern1);
		}
	}

	private AePatternSet getContainingPatternSet(AbstractAeCondition abstractaecondition) {
		AbstractAeCompoundCondition abstractaecompoundcondition = abstractaecondition.getParentCondition();
		if (abstractaecompoundcondition == null || (abstractaecompoundcondition instanceof AePatternSet))
			return (AePatternSet) abstractaecompoundcondition;
		else
			return getContainingPatternSet(((AbstractAeCondition) (abstractaecompoundcondition)));
	}

	private AeMapper mMapper;
}