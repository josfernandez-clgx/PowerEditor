package com.mindbox.pe.server.cache;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.server.parser.jtb.rule.ParseException;
import com.mindbox.server.parser.jtb.rule.RuleParser;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;

/**
 * Guideline action manager.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.0.0
 */
public class GuidelineFunctionManager extends AbstractCacheManager {

	private static GuidelineFunctionManager instance = null;

	public static GuidelineFunctionManager getInstance() {
		if (instance == null) {
			instance = new GuidelineFunctionManager();
		}
		return instance;
	}

	/**
	 * Inserts the specified action definition to cache.
	 * If the action contains a rule that generates a parse exception, the action
	 * will NOT be stored in the cache.
	 * @param <T> function type
	 * @param functionDef the function definition to insert
	 * @param actionMap actionMap
	 * @param typeMap typeMap
	 * @throws ParseException if <code>actionDef</code> contains an unparsable rule;
	 *         when this happends, the action will not be stored in the cache
	 */
	private static <T extends FunctionTypeDefinition> void insertFunctionTypeDefinition(T functionDef, Map<Integer, Action> actionMap, Map<Integer, T> typeMap)
			throws ParseException {
		Integer key = new Integer(functionDef.getID());
		synchronized (typeMap) {
			synchronized (actionMap) {
				if (functionDef.getDeploymentRule() != null && functionDef.getDeploymentRule().length() > 0) {
					RuleParser.getInstance(new StringReader(functionDef.getDeploymentRule()));
					Action action = RuleParser.Action();
					actionMap.put(key, action);
				}

				// insert after parsing
				typeMap.put(key, functionDef);
			}
		}
	}

	private static <T extends FunctionTypeDefinition> void removeFunctionTypeDefinition(int id, Map<Integer, Action> actionMap, Map<Integer, T> typeMap) {
		Integer key = new Integer(id);
		synchronized (typeMap) {
			synchronized (actionMap) {
				actionMap.remove(key);
				typeMap.remove(key);
			}
		}
	}

	/**
	 * Updates the specified action definition in the cache.
	 * If the action contains a rule that generates a parse exception, 
	 * the action in the cache will NOT be updated.
	 * @param actionDef actionDef
	 * @return The cached function.
	 * @throws ParseException if <code>actionDef</code> contains an unparsable rule;
	 *         when this happends, the action in the cache will not be updated
	 */
	private static <T extends FunctionTypeDefinition> FunctionTypeDefinition updateFunctionTypeDefinition(T functionDef, Map<Integer, Action> actionMap, Map<Integer, T> typeMap)
			throws ParseException {
		Integer key = new Integer(functionDef.getID());
		synchronized (actionMap) {
			T cachedFunction = typeMap.get(key);

			// update action object, if deployment rule has changed
			if (cachedFunction.getDeploymentRule() != null && !cachedFunction.getDeploymentRule().equals(functionDef.getDeploymentRule())) {
				RuleParser.getInstance(new StringReader(functionDef.getDeploymentRule()));
				Action action = RuleParser.Action();

				actionMap.put(key, action);
			}

			// update after parsing
			return cachedFunction;
		}
	}

	private final Map<Integer, ActionTypeDefinition> actionTypeMap = new HashMap<Integer, ActionTypeDefinition>();

	private final Map<Integer, Action> actionTypeActionMap = new HashMap<Integer, Action>();
	private final Map<Integer, TestTypeDefinition> testTypeMap = new HashMap<Integer, TestTypeDefinition>();

	private final Map<Integer, Action> testTypeActionMap = new HashMap<Integer, Action>();

	public void finishLoading() {
	}

	public Action getActionObject(int actionTypeID) {
		synchronized (actionTypeActionMap) {
			return actionTypeActionMap.get(new Integer(actionTypeID));
		}
	}

	public ActionTypeDefinition getActionTypeDefinition(int id) {
		synchronized (actionTypeMap) {
			return actionTypeMap.get(new Integer(id));
		}
	}

	public ActionTypeDefinition getActionTypeDefinition(String name) {
		return getFunctionTypeDefinition(name, actionTypeMap);
	}

	public List<ActionTypeDefinition> getActionTypesForUsage(TemplateUsageType usage) {
		synchronized (actionTypeMap) {
			List<ActionTypeDefinition> list = new ArrayList<ActionTypeDefinition>();
			for (Iterator<ActionTypeDefinition> iter = actionTypeMap.values().iterator(); iter.hasNext();) {
				ActionTypeDefinition element = iter.next();
				if (element.hasUsageType(usage)) {
					list.add(element);
				}
			}
			return list;
		}
	}

	public List<ActionTypeDefinition> getAllActionTypes() {
		synchronized (actionTypeMap) {
			List<ActionTypeDefinition> list = new ArrayList<ActionTypeDefinition>();
			list.addAll(actionTypeMap.values());
			return list;
		}
	}

	public List<TestTypeDefinition> getAllTestTypes() {
		synchronized (testTypeMap) {
			List<TestTypeDefinition> list = new ArrayList<TestTypeDefinition>();
			list.addAll(testTypeMap.values());
			return list;
		}
	}

	/**
	 * Gets the first action type definition with the specified name
	 * @param name the name of action
	 * @return the action type definition, if found; <code>null</code>, otherwise
	 * @since PowerEditor 4.0
	 */
	private <T extends FunctionTypeDefinition> T getFunctionTypeDefinition(String name, Map<Integer, T> typeMap) {
		if (name == null) return null;
		synchronized (typeMap) {
			for (Iterator<T> iter = typeMap.values().iterator(); iter.hasNext();) {
				T element = iter.next();
				if (element.getName().equals(name)) {
					return element;
				}
			}
		}
		return null;
	}

	public Action getTestActionObject(int testTypeID) {
		synchronized (testTypeActionMap) {
			return testTypeActionMap.get(new Integer(testTypeID));
		}
	}

	public TestTypeDefinition getTestTypeDefinition(int id) {
		synchronized (testTypeMap) {
			return testTypeMap.get(new Integer(id));
		}
	}

	public TestTypeDefinition getTestTypeDefinition(String name) {
		return getFunctionTypeDefinition(name, testTypeMap);
	}

	public void insertActionTypeDefinition(ActionTypeDefinition actionDef) throws ParseException {
		insertFunctionTypeDefinition(actionDef, actionTypeActionMap, actionTypeMap);
	}

	public void insertTestTypeDefinition(TestTypeDefinition testDef) throws ParseException {
		insertFunctionTypeDefinition(testDef, testTypeActionMap, testTypeMap);
	}

	public void removeActionTypeDefinition(int id) {
		removeFunctionTypeDefinition(id, actionTypeActionMap, actionTypeMap);
	}

	public void removeTestTypeDefinition(int id) {
		removeFunctionTypeDefinition(id, testTypeActionMap, testTypeMap);
	}

	public void startLoading() {
		synchronized (actionTypeMap) {
			synchronized (actionTypeActionMap) {
				actionTypeMap.clear();
				testTypeMap.clear();
			}
		}
	}

	/**
	 * Updates the specified action definition in the cache.
	 * If the action contains a rule that generates a parse exception, 
	 * the action in the cache will NOT be updated.
	 * @param actionDef actionDef
	 * @throws ParseException if <code>actionDef</code> contains an unparsable rule;
	 *         when this happends, the action in the cache will not be updated
	 */
	public void updateActionTypeDefinition(ActionTypeDefinition actionDef) throws ParseException {
		FunctionTypeDefinition cachedFunction = updateFunctionTypeDefinition(actionDef, actionTypeActionMap, actionTypeMap);

		// update after parsing
		if (cachedFunction instanceof ActionTypeDefinition) {
			((ActionTypeDefinition) cachedFunction).copyFrom(actionDef);
			// reset cached rule definitions for all rules that use this action
			GuidelineTemplateManager.getInstance().resetOldParserObjectTressForAction(actionDef.getID());
		}
	}

	public void updateTestTypeDefinition(TestTypeDefinition testDef) throws ParseException {
		FunctionTypeDefinition cachedFunction = updateFunctionTypeDefinition(testDef, testTypeActionMap, testTypeMap);

		// update after parsing
		if (cachedFunction instanceof TestTypeDefinition) {
			((TestTypeDefinition) cachedFunction).copyFrom(testDef);
		}
	}

}