package com.mindbox.pe.server.repository;

import java.util.Collection;

import com.mindbox.pe.model.rule.RuleDefinition;

/**
 * Manager of ad hoc rule repository.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface AdHocRuleRepositoryManager extends RepositoryManager {

	void add(RuleDefinition rule) throws RepositoryException;
	
	void update(RuleDefinition rule) throws RepositoryException;
	
	void remove(int ruleID) throws RepositoryException;
	
	Collection<RuleDefinition> getAllRules() throws RepositoryException;
	
}
