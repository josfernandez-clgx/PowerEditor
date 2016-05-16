package com.mindbox.pe.server.generator.rule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.RuleGenerationException;

public abstract class AbstractLHSPatternList implements LHSPatternList {

	private final List<LHSPattern> list = new LinkedList<LHSPattern>();
	private final Logger logger = Logger.getLogger(getClass());
	private int type;

	protected AbstractLHSPatternList(int type) {
		this.type = type;
	}

	@Override
	public void append(FunctionCallPattern testPattern) {
		list.add(testPattern);
	}

	@Override
	public void append(LHSPatternList patternList) {
		list.add(patternList);
	}

	/**
	 * This blindly appends the specified object pattern to this.
	 * Sub-classes should overwrite this to provide more meaningful implementation.
	 */
	@Override
	public void append(ObjectPattern objectPattern) throws RuleGenerationException {
		list.add(objectPattern);
	}

	protected final ObjectPattern find(String variableName) {
		if (variableName == null) throw new NullPointerException("variableName cannot be null");
		for (Iterator<LHSPattern> iter = list.iterator(); iter.hasNext();) {
			LHSPattern element = iter.next();
			if (element instanceof ObjectPattern) {
				if (OptimizingObjectPattern.isMatchingVariableName(variableName, ((ObjectPattern) element).getVariableName())) {
					return (ObjectPattern) element;
				}
			}
			// DO NOT check embedded LHSPatternList
		}
		return null;
	}

	@Override
	public LHSPattern get(int index) {
		return list.get(index);
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public boolean hasConflictingAttributePattern(String objectVarName, AttributePattern attributePattern) {
		if (attributePattern == null) throw new NullPointerException("attributePattern cannot be null");
		for (Iterator<LHSPattern> iter = list.iterator(); iter.hasNext();) {
			LHSPattern element = iter.next();
			if (element instanceof ObjectPattern) {
				if (objectVarName.equalsIgnoreCase(((ObjectPattern) element).getVariableName())
						&& OptimizingObjectPattern.hasConflictingAttributePattern((ObjectPattern) element, attributePattern)) {
					return true;
				}
			}
			else if (element instanceof LHSPatternList) {
				if (((LHSPatternList) element).hasConflictingAttributePattern(objectVarName, attributePattern)) {
					return true;
				}
			}
		}
		return false;
	}

	protected final boolean hasPatternForClassName(String className) {
		for (Iterator<LHSPattern> iter = list.iterator(); iter.hasNext();) {
			LHSPattern element = iter.next();
			if (element instanceof ObjectPattern) {
				if (((ObjectPattern) element).getClassName().equals(className)) {
					return true;
				}
			}
			else if (element instanceof AbstractLHSPatternList) {
				boolean result = ((AbstractLHSPatternList) element).hasPatternForClassName(className);
				if (result) return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasPatternForReference(Reference reference) {
		if (reference == null) throw new NullPointerException("reference cannot be null");
		logger.debug(">>> hasPatternForReference: " + reference);
		for (Iterator<LHSPattern> iter = list.iterator(); iter.hasNext();) {
			LHSPattern element = iter.next();
			if (element instanceof ObjectPattern) {
				String varNameToCheck = AeMapper.makeAEVariable(reference.getAttributeName());
				boolean result = ((ObjectPattern) element).containsAttribute(varNameToCheck);
				if (result) return true;
			}
			else if (element instanceof LHSPatternList) {
				boolean result = ((LHSPatternList) element).hasPatternForReference(reference);
				if (result) return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param variableName variableName
	 * @return true if has pattern; false, otherwise
	 * @throws NullPointerException if variableName is <code>null</code>
	 */
	protected final boolean hasPatternForVariableName(String variableName) {
		if (variableName == null) throw new NullPointerException("variableName cannot be null");
		for (Iterator<LHSPattern> iter = list.iterator(); iter.hasNext();) {
			LHSPattern element = iter.next();
			if (element instanceof ObjectPattern) {
				if (OptimizingObjectPattern.isMatchingVariableName(variableName, ((ObjectPattern) element).getVariableName())) {
					return true;
				}
			}
		}
		return false;
	}

	protected final int indexOfPatternWithVariable(String variableName) {
		if (variableName == null) throw new NullPointerException("variableName cannot be null");
		for (int i = 0; i < list.size(); i++) {
			LHSPattern element = list.get(i);
			if (element instanceof ObjectPattern) {
				if (OptimizingObjectPattern.isMatchingVariableName(variableName, ((ObjectPattern) element).getVariableName())) {
					return i;
				}
			}
			// DO NOT check embedded LHSPatternList
		}
		return -1;
	}

	/**
	 * This blindly inserts the specified object pattern to this.
	 * Sub-classes should overwrite this to provide more meaningful implementation.
	 */
	@Override
	public final void insert(ObjectPattern objectPattern) throws RuleGenerationException {
		insert(objectPattern, false);
	}

	/**
	 * This blindly inserts the specified object pattern to this, ignoring <code>preserveOrderIfPatternExists</code>.
	 * Sub-classes should overwrite this to provide more meaningful implementation.
	 */
	@Override
	public void insert(ObjectPattern objectPattern, boolean preserveOrderIfPatternExists) throws RuleGenerationException {
		list.add(0, objectPattern);
	}

	@Override
	public void insertBefore(ObjectPattern objectPattern, String variableName) throws RuleGenerationException {
		int index = indexOfPatternWithVariable(variableName);
		if (index >= 0) {
			list.add(index, objectPattern);
		}
		else {
			list.add(objectPattern);
		}
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void remove(ObjectPattern objectPattern) {
		list.remove(objectPattern);
	}

	@Override
	public int size() {
		return list.size();
	}

}
