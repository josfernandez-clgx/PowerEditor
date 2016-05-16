package com.mindbox.pe.server.cache;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBRCaseClass;
import com.mindbox.pe.model.cbr.CBRScoringFunction;
import com.mindbox.pe.model.cbr.CBRValueRange;

/**
 * CBR model cache manager.
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRManager extends AbstractCacheManager {

	private static CBRManager mSingleton = null;


	public static synchronized CBRManager getInstance() {
		if (mSingleton == null) mSingleton = new CBRManager();
		return mSingleton;
	}

	private Map<Integer, CBRCaseBase> cbrCaseBaseMap; // from db

	private Map<Integer, CBRCase> cbrCaseMap; // from db

	private Map<Integer, CBRCaseClass> cbrCaseClassMap;

	private Map<Integer, CBRScoringFunction> cbrScoringFunctionMap;

	private Map<Integer, CBRCaseAction> cbrCaseActionMap;

	private Map<Integer, CBRAttributeType> cbrAttributeTypeMap;

	private Map<Integer, CBRAttribute> cbrAttributeMap;

	private Map<Integer, CBRValueRange> cbrValueRangeMap;

	private CBRManager() {
		cbrCaseBaseMap = new Hashtable<Integer, CBRCaseBase>();
		cbrCaseClassMap = new Hashtable<Integer, CBRCaseClass>();
		cbrScoringFunctionMap = new Hashtable<Integer, CBRScoringFunction>();
		cbrCaseActionMap = new Hashtable<Integer, CBRCaseAction>();
		cbrAttributeTypeMap = new Hashtable<Integer, CBRAttributeType>();
		cbrAttributeMap = new Hashtable<Integer, CBRAttribute>();
		cbrValueRangeMap = new Hashtable<Integer, CBRValueRange>();
		cbrCaseMap = new Hashtable<Integer, CBRCase>();
	}

	/**
	 * Adds the specified cbr attribute to the cache.
	 * @param cbrAttribute cbrAttribute
	 */
	public synchronized void addCBRAttribute(CBRAttribute cbrAttribute) {
		if (cbrAttribute == null) throw new NullPointerException("cannot add null CBR attribute");
		if (cbrAttributeMap.containsKey(new Integer(cbrAttribute.getId()))) {
			// update existing attribute
			updateCBRAttribute(cbrAttribute);
		}
		else {
			cbrAttributeMap.put(new Integer(cbrAttribute.getId()), cbrAttribute);
		}
	}

	/**
	 * Adds the specified cbr attribute type to the cache.
	 * @param cbrAttributeType cbrAttributeType
	 */
	public synchronized void addCBRAttributeType(CBRAttributeType cbrAttributeType) {
		if (cbrAttributeType == null) throw new NullPointerException("cannot add null CBR attribute type");
		if (cbrAttributeType.getSymbol() == null || cbrAttributeType.getSymbol().length() == 0)
			throw new IllegalArgumentException("Cannot add a CBR attribute type with no symbol");
		if (cbrAttributeTypeMap.containsKey(new Integer(cbrAttributeType.getId()))) {
			// update existing attribute type
			updateCBRAttributeType(cbrAttributeType);
		}
		else {
			cbrAttributeTypeMap.put(new Integer(cbrAttributeType.getId()), cbrAttributeType);
		}
	}

	/**
	 * Adds the specified cbr case to the cache.
	 * @param cbrCase cbrCase
	 */
	public synchronized void addCBRCase(CBRCase cbrCase) {
		if (cbrCase == null) throw new NullPointerException("cannot add null CBR case");
		if (cbrCaseMap.containsKey(new Integer(cbrCase.getId()))) {
			// update existing cbr case
			updateCBRCase(cbrCase);
		}
		else {
			cbrCaseMap.put(new Integer(cbrCase.getId()), cbrCase);
		}
	}

	/**
	 * Adds the specified cbr case action to the cache.
	 * @param cbrCaseAction cbrCaseAction
	 */
	public synchronized void addCBRCaseAction(CBRCaseAction cbrCaseAction) {
		if (cbrCaseAction == null) throw new NullPointerException("cannot add null CBR case action");
		if (cbrCaseAction.getSymbol() == null || cbrCaseAction.getSymbol().length() == 0) throw new IllegalArgumentException("Cannot add a CBR case action with no symbol");
		if (cbrCaseActionMap.containsKey(new Integer(cbrCaseAction.getId()))) {
			// update existing case action
			updateCBRCaseAction(cbrCaseAction);
		}
		else {
			cbrCaseActionMap.put(new Integer(cbrCaseAction.getId()), cbrCaseAction);
		}
	}

	/**
	 * Adds the specified cbr case base in the cache.
	 * @param cbrCaseBase the cbr case base to add
	 */
	public synchronized void addCBRCaseBase(CBRCaseBase cbrCaseBase) {
		if (cbrCaseBase == null) throw new NullPointerException("cannot add null CBR case base");
		if (cbrCaseBase.getID() == 0) throw new IllegalArgumentException("Cannot add a CBR case base with no id");
		if (cbrCaseBaseMap.containsKey(new Integer(cbrCaseBase.getID()))) {
			// update existing case base
			updateCBRCaseBase(cbrCaseBase);
		}
		else {
			cbrCaseBaseMap.put(new Integer(cbrCaseBase.getID()), cbrCaseBase);
		}
	}

	/**
	 * Adds the specified cbr case class in the cache.
	 * @param cbrCaseClass the cbr case class to add
	 */
	public synchronized void addCBRCaseClass(CBRCaseClass cbrCaseClass) {
		if (cbrCaseClass == null) throw new NullPointerException("cannot add null CBR case class");
		if (cbrCaseClass.getSymbol() == null || cbrCaseClass.getSymbol().length() == 0) throw new IllegalArgumentException("Cannot add a CBR case class with no symbol");
		if (cbrCaseClassMap.containsKey(new Integer(cbrCaseClass.getId()))) {
			// update existing case class
			updateCBRCaseClass(cbrCaseClass);
		}
		else {
			cbrCaseClassMap.put(new Integer(cbrCaseClass.getId()), cbrCaseClass);
		}
	}

	/**
	 * Adds the specified cbr scoring function to the cache.
	 * @param cbrScoringFunction cbrScoringFunction
	 */
	public synchronized void addCBRScoringFunction(CBRScoringFunction cbrScoringFunction) {
		if (cbrScoringFunction == null) throw new NullPointerException("cannot add null CBR scoring function");
		if (cbrScoringFunction.getSymbol() == null || cbrScoringFunction.getSymbol().length() == 0)
			throw new IllegalArgumentException("Cannot add a CBR scoring function with no symbol");
		if (cbrScoringFunctionMap.containsKey(new Integer(cbrScoringFunction.getId()))) {
			// update existing scoring function
			updateCBRScoringFunction(cbrScoringFunction);
		}
		else {
			cbrScoringFunctionMap.put(new Integer(cbrScoringFunction.getId()), cbrScoringFunction);
		}
	}

	public synchronized void addCBRValueRange(CBRValueRange cbrValueRange) {
		if (cbrValueRange == null) throw new NullPointerException("cannot add null CBR ValueRange");
		if (cbrValueRangeMap.containsKey(new Integer(cbrValueRange.getId()))) {
			// update existing value range
			updateCBRValueRange(cbrValueRange);
		}
		else {
			cbrValueRangeMap.put(new Integer(cbrValueRange.getId()), cbrValueRange);
		}
	}

	public synchronized void addObject(Object obj) {
		if (obj == null) throw new NullPointerException("cannot add null object");
		if (obj instanceof CBRCaseClass) {
			addCBRCaseClass((CBRCaseClass) obj);
		}
		else if (obj instanceof CBRScoringFunction) {
			addCBRScoringFunction((CBRScoringFunction) obj);
		}
		else if (obj instanceof CBRCaseAction) {
			addCBRCaseAction((CBRCaseAction) obj);
		}
		else if (obj instanceof CBRAttributeType) {
			addCBRAttributeType((CBRAttributeType) obj);
		}
		else if (obj instanceof CBRAttribute) {
			addCBRAttribute((CBRAttribute) obj);
		}
		else if (obj instanceof CBRValueRange) {
			addCBRValueRange((CBRValueRange) obj);
		}
		else if (obj instanceof CBRCaseBase) {
			addCBRCaseBase((CBRCaseBase) obj);
		}
		else if (obj instanceof CBRCase) {
			addCBRCase((CBRCase) obj);
		}
		else {
			logger.warn("Invalid object type in CBRManager.addObject: " + obj.getClass().getName());
		}
	}

	public synchronized void finishLoading() {
		logger.info(">>> finishLoading");

		logger.info("<<< finishLoading");
	}

	public CBRAttribute getCBRAttribute(int id) {
		CBRAttribute cbrAttribute = (CBRAttribute) cbrAttributeMap.get(new Integer(id));
		if (cbrAttribute == null) {
			String msg = "Failed to find a cbr attribute: " + id;
			logger.warn(msg);
		}
		return cbrAttribute;
	}

	public List<CBRAttribute> getCBRAttributes() {
		LinkedList<CBRAttribute> linkedlist = new LinkedList<CBRAttribute>();
		CBRAttribute cbrAttribute;
		for (Iterator<CBRAttribute> iter = cbrAttributeMap.values().iterator(); iter.hasNext();) {
			cbrAttribute = iter.next();
			linkedlist.add(cbrAttribute);
		}

		return linkedlist;
	}

	public CBRAttributeType getCBRAttributeType(int id) {
		CBRAttributeType cbrAttributeType = (CBRAttributeType) cbrAttributeTypeMap.get(new Integer(id));
		if (cbrAttributeType == null) {
			String msg = "Failed to find a cbr attribute type: " + id;
			logger.warn(msg);
		}
		return cbrAttributeType;
	}

	public List<CBRAttributeType> getCBRAttributeTypes() {
		LinkedList<CBRAttributeType> linkedlist = new LinkedList<CBRAttributeType>();
		CBRAttributeType cbrAttributeType;
		for (Iterator<CBRAttributeType> iter = cbrAttributeTypeMap.values().iterator(); iter.hasNext();) {
			cbrAttributeType = iter.next();
			linkedlist.add(cbrAttributeType);
		}

		return linkedlist;
	}


	public CBRCase getCBRCase(int id) {
		CBRCase cbrCase = (CBRCase) cbrCaseMap.get(new Integer(id));
		if (cbrCase == null) {
			String msg = "Failed to find a cbr case: " + id;
			logger.warn(msg);
		}
		return cbrCase;
	}

	public CBRCaseAction getCBRCaseAction(int id) {
		CBRCaseAction cbrCaseAction = (CBRCaseAction) cbrCaseActionMap.get(new Integer(id));
		if (cbrCaseAction == null) {
			String msg = "Failed to find a cbr case base: " + id;
			logger.warn(msg);
		}
		return cbrCaseAction;
	}

	public List<CBRCaseAction> getCBRCaseActions() {
		LinkedList<CBRCaseAction> linkedlist = new LinkedList<CBRCaseAction>();
		CBRCaseAction cbrCaseAction;
		for (Iterator<CBRCaseAction> iter = cbrCaseActionMap.values().iterator(); iter.hasNext();) {
			cbrCaseAction = iter.next();
			linkedlist.add(cbrCaseAction);
		}
		return linkedlist;
	}

	public CBRCaseBase getCBRCaseBase(int id) {
		if (id < 0) {
			logger.warn("<<< getCBRCaseBase: id is invalid: " + id);
			return null;
		}
		CBRCaseBase cbrCaseBase = (CBRCaseBase) cbrCaseBaseMap.get(new Integer(id));
		if (cbrCaseBase == null) {
			String msg = "Failed to find a cbr case base: " + id;
			logger.warn(msg);
		}
		return cbrCaseBase;
	}

	public List<CBRCaseBase> getCBRCaseBases() {
		LinkedList<CBRCaseBase> linkedlist = new LinkedList<CBRCaseBase>();
		CBRCaseBase cbrCaseBase;
		for (Iterator<CBRCaseBase> iter = cbrCaseBaseMap.values().iterator(); iter.hasNext();) {
			cbrCaseBase = iter.next();
			linkedlist.add(cbrCaseBase);
		}

		return linkedlist;
	}

	public CBRCaseClass getCBRCaseClass(int id) {
		CBRCaseClass cbrCaseClass = (CBRCaseClass) cbrCaseClassMap.get(new Integer(id));
		if (cbrCaseClass == null) {
			String msg = "Failed to find a cbr case class: " + id;
			logger.warn(msg);
		}
		return cbrCaseClass;
	}

	public List<CBRCaseClass> getCBRCaseClasses() {
		LinkedList<CBRCaseClass> linkedlist = new LinkedList<CBRCaseClass>();
		CBRCaseClass cbrCaseClass;
		for (Iterator<CBRCaseClass> iter = cbrCaseClassMap.values().iterator(); iter.hasNext();) {
			cbrCaseClass = iter.next();
			linkedlist.add(cbrCaseClass);
		}

		return linkedlist;
	}

	public List<CBRCase> getCBRCases() {
		LinkedList<CBRCase> linkedlist = new LinkedList<CBRCase>();
		CBRCase cbrCase;
		for (Iterator<CBRCase> iter = cbrCaseMap.values().iterator(); iter.hasNext();) {
			cbrCase = iter.next();
			linkedlist.add(cbrCase);
		}

		return linkedlist;
	}

	public CBRScoringFunction getCBRScoringFunction(int id) {
		CBRScoringFunction cbrScoringFunction = (CBRScoringFunction) cbrScoringFunctionMap.get(new Integer(id));
		if (cbrScoringFunction == null) {
			String msg = "Failed to find a cbr scoring function: " + id;
			logger.warn(msg);
		}
		return cbrScoringFunction;
	}

	public List<CBRScoringFunction> getCBRScoringFunctions() {
		LinkedList<CBRScoringFunction> linkedlist = new LinkedList<CBRScoringFunction>();
		CBRScoringFunction cbrScoringFunction;
		for (Iterator<CBRScoringFunction> iter = cbrScoringFunctionMap.values().iterator(); iter.hasNext();) {
			cbrScoringFunction = iter.next();
			linkedlist.add(cbrScoringFunction);
		}

		return linkedlist;
	}

	public CBRValueRange getCBRValueRange(int id) {
		CBRValueRange cbrValueRange = (CBRValueRange) cbrValueRangeMap.get(new Integer(id));
		if (cbrValueRange == null) {
			String msg = "Failed to find a cbr ValueRange: " + id;
			logger.warn(msg);
		}
		return cbrValueRange;
	}

	public List<CBRValueRange> getCBRValueRanges() {
		LinkedList<CBRValueRange> linkedlist = new LinkedList<CBRValueRange>();
		CBRValueRange cbrValueRange;
		for (Iterator<CBRValueRange> iter = cbrValueRangeMap.values().iterator(); iter.hasNext();) {
			cbrValueRange = iter.next();
			linkedlist.add(cbrValueRange);
		}

		return linkedlist;
	}

	public boolean removeCBRAttributeFromCache(int id) {
		return cbrAttributeMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRAttributeTypeFromCache(int id) {
		return cbrAttributeTypeMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRCaseActionFromCache(int id) {
		return cbrCaseActionMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRCaseBaseFromCache(int id) {
		return cbrCaseBaseMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRCaseClassFromCache(int id) {
		return cbrCaseClassMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRCaseFromCache(int id) {
		return cbrCaseMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRScoringFunctionFromCache(int id) {
		return cbrScoringFunctionMap.remove(new Integer(id)) != null;
	}

	public boolean removeCBRValueRangeFromCache(int id) {
		return cbrValueRangeMap.remove(new Integer(id)) != null;
	}

	public synchronized void startDbLoading() {
		cbrCaseClassMap.clear();
		cbrScoringFunctionMap.clear();
		cbrCaseActionMap.clear();
		cbrValueRangeMap.clear();
		cbrAttributeTypeMap.clear();
		cbrCaseBaseMap.clear();
		cbrAttributeMap.clear();
		cbrCaseMap.clear();
	}

	public synchronized void startLoading() {
	}

	@Override
	public String toString() {
		String s = "";
		s += "CBRManager with " + cbrCaseClassMap.size() + " CBRCaseClasses!";
		s += cbrCaseClassMap.toString() + "\n";
		s += " and " + cbrScoringFunctionMap.size() + " CBRScoringFunctions!";
		s += cbrScoringFunctionMap.toString() + "\n";
		s += " and " + cbrCaseActionMap.size() + " CBRCaseAction!";
		s += cbrCaseActionMap.toString() + "\n";
		s += " and " + cbrAttributeTypeMap.size() + " CBRAttributeType!";
		s += cbrAttributeTypeMap.toString() + "\n";
		s += " and " + cbrCaseBaseMap.size() + " CBRCaseBase!";
		s += cbrCaseBaseMap.toString() + "\n";
		s += " and " + cbrAttributeMap.size() + " CBRAttribute!";
		s += cbrAttributeMap.toString() + "\n";
		s += " and " + cbrCaseMap.size() + " CBRCase!";
		s += cbrCaseMap.toString() + "\n";
		return s;
	}

	private void updateCBRAttribute(CBRAttribute cbrAttribute) {
		// Option [1] - simply override the old one with the new one
		cbrAttributeMap.remove(new Integer(cbrAttribute.getId()));
		cbrAttributeMap.put(new Integer(cbrAttribute.getId()), cbrAttribute);
	}

	private void updateCBRAttributeType(CBRAttributeType cbrAttributeType) {
		// Option [1] - simply override the old one with the new one
		cbrAttributeTypeMap.remove(new Integer(cbrAttributeType.getId()));
		cbrAttributeTypeMap.put(new Integer(cbrAttributeType.getId()), cbrAttributeType);
	}

	private void updateCBRCase(CBRCase cbrCase) {
		// Option [1] - simply override the old one with the new one
		cbrCaseMap.remove(new Integer(cbrCase.getId()));
		cbrCaseMap.put(new Integer(cbrCase.getId()), cbrCase);
	}

	private void updateCBRCaseAction(CBRCaseAction cbrCaseAction) {
		// Option [1] - simply override the old one with the new one
		cbrCaseActionMap.remove(new Integer(cbrCaseAction.getId()));
		cbrCaseActionMap.put(new Integer(cbrCaseAction.getId()), cbrCaseAction);
	}

	private void updateCBRCaseBase(CBRCaseBase cbrCaseBase) {
		// Option [1] - simply override the old one with the new one
		cbrCaseBaseMap.remove(new Integer(cbrCaseBase.getID()));
		cbrCaseBaseMap.put(new Integer(cbrCaseBase.getID()), cbrCaseBase);
	}

	private void updateCBRCaseClass(CBRCaseClass cbrCaseClass) {
		// Option [1] - simply override the old one with the new one
		cbrCaseClassMap.remove(new Integer(cbrCaseClass.getId()));
		cbrCaseClassMap.put(new Integer(cbrCaseClass.getId()), cbrCaseClass);
	}

	private void updateCBRScoringFunction(CBRScoringFunction cbrScoringFunction) {
		// Option [1] - simply override the old one with the new one
		cbrScoringFunctionMap.remove(new Integer(cbrScoringFunction.getId()));
		cbrScoringFunctionMap.put(new Integer(cbrScoringFunction.getId()), cbrScoringFunction);
	}

	private void updateCBRValueRange(CBRValueRange cbrValueRange) {
		// Option [1] - simply override the old one with the new one
		cbrValueRangeMap.remove(new Integer(cbrValueRange.getId()));
		cbrValueRangeMap.put(new Integer(cbrValueRange.getId()), cbrValueRange);
	}

}