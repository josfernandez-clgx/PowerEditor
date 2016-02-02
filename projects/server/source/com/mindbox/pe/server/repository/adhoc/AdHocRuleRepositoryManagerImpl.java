package com.mindbox.pe.server.repository.adhoc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.repository.AbstractRepositoryManager;
import com.mindbox.pe.server.repository.AdHocRuleRepositoryManager;
import com.mindbox.pe.server.repository.RepositoryException;

/**
 * AdHocRule repository manager that uses XML file as a backing storage.
 * This is not optimized. This rewrites the entire file on every update.
 * This should be optimized using NIO.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class AdHocRuleRepositoryManagerImpl extends AbstractRepositoryManager implements AdHocRuleRepositoryManager {

	private File repositoryFile = null;
	private final Map<Integer, RuleDefinition> ruleMap = new HashMap<Integer, RuleDefinition>();

	private synchronized void flushAll() throws IOException {
		AdHocRuleWriter writer = new AdHocRuleWriter(repositoryFile);
		writer.write(ruleMap.values().toArray(new RuleDefinition[0]));
		writer.close();
	}

	public synchronized void add(RuleDefinition rule) throws RepositoryException {
		logger.debug(">>> add: " + rule);
		Integer key = new Integer(rule.getID());
		ruleMap.put(key, rule);

		logger.debug("add: added to cache. writing " + ruleMap.size() + " rules...");
		try {
			flushAll();
			logger.debug("<<< add");
		}
		catch (Exception e) {
			logger.error("Failed to add rule: " + rule, e);
			ruleMap.remove(key);
			throw new RepositoryException(e.getMessage());
		}
	}

	public synchronized void update(RuleDefinition rule) throws RepositoryException {
		logger.debug(">>> update: " + rule);
		logger.debug(rule.toDebugString());

		Integer key = new Integer(rule.getID());
		RuleDefinition oldRule = ruleMap.get(key);

		ruleMap.remove(key);
		ruleMap.put(key, rule);
		try {
			flushAll();
		}
		catch (Exception e) {
			logger.error("Failed to update rule: " + rule, e);
			ruleMap.remove(key);
			ruleMap.put(key, oldRule);
			throw new RepositoryException(e.getMessage());
		}
	}

	public synchronized void remove(int ruleID) throws RepositoryException {
		Integer key = new Integer(ruleID);
		RuleDefinition oldRule = ruleMap.get(key);
		ruleMap.remove(key);
		try {
			flushAll();
		}
		catch (Exception e) {
			logger.error("Failed to remove rule: " + ruleID, e);
			ruleMap.put(key, oldRule);
			throw new RepositoryException(e.getMessage());
		}
	}

	public Collection<RuleDefinition> getAllRules() throws RepositoryException {
		return Collections.unmodifiableCollection(ruleMap.values());
	}

	public synchronized void initialize(Object initParam) throws RepositoryException {
		if (initParam == null) {
			throw new NullPointerException("initParam cannot be null");
		}

		logger.info(">>> initialize: " + initParam);

		if (repositoryFile != null) {
			deinitialize_internal();
		}

		if (initParam instanceof File) {
			repositoryFile = (File) initParam;
		}
		else {
			repositoryFile = new File(initParam.toString());
		}

		try {
			loadRulesFromRepository();

			logger.info("<<< initialize: " + initParam);
		}
		catch (Exception ex) {
			logger.error("Error loading rules from repository", ex);
			throw new RepositoryException(ex.getMessage());
		}
	}

	private void loadRulesFromRepository() throws SAXException, IOException, ParserConfigurationException {

		ruleMap.clear();

		if (repositoryFile.exists()) {
			if (repositoryFile.length() > 2) {
				logger.debug("loadRulesFromRepository: processing file " + repositoryFile.getAbsolutePath());
				// lock the XML file
				AdHocRuleReader in = new AdHocRuleReader(repositoryFile);
				RuleDefinition[] rules = in.readRules();
				in.close();

				for (int i = 0; i < rules.length; i++) {
					ruleMap.put(new Integer(rules[i].getID()), rules[i]);
				}
			}

			logger.debug("loadRulesFromRepository " + ruleMap.size() + " loaded");
		}
		else {
			logger.debug(
				"loadRulesFromRepository: creating new repository file at " + repositoryFile.getAbsolutePath());
			// create an empty file and lock it
			repositoryFile.createNewFile();
		}
	}

	public synchronized void deinitialize() {
		deinitialize_internal();
	}

	private void deinitialize_internal() {
		repositoryFile = null;
	}

	public void finalize() {
		deinitialize_internal();
	}
}
