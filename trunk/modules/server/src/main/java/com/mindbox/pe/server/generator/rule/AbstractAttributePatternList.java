package com.mindbox.pe.server.generator.rule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Abstract implementation of {@link AttributePatternList}.
 */
public abstract class AbstractAttributePatternList implements AttributePatternList {

	// Be sure to use LinkedList to preserve the order (i.e., dont use ArrayList)
	private final List<AttributePattern> list = new LinkedList<AttributePattern>();

	protected final boolean contains(AttributePattern attributePattern) {
		if (attributePattern == null) throw new NullPointerException("attributePattern cannot be null");
		return list.contains(attributePattern);
	}

	/**
	 * 
	 * @param attributePattern
	 * @return
	 * @throws NullPointerException if <code>attributePattern</code> is <code>null</code>
	 */
	protected final AttributePattern find(AttributePattern attributePattern) {
		if (attributePattern == null) throw new NullPointerException("attributePattern cannot be null");
		for (Iterator<AttributePattern> iter = list.iterator(); iter.hasNext();) {
			AttributePattern element = iter.next();
			if (element.equals(attributePattern)) { return element; }
		}
		return null;
	}

	/**
	 * 
	 * @param oldPattern
	 * @param newPattern
	 * @throws NullPointerException if <code>oldPattern</code> or <code>newPattern</code> is <code>null</code>
	 */
	protected final void replace(AttributePattern oldPattern, AttributePattern newPattern) {
		if (oldPattern == null) throw new NullPointerException("oldPattern cannot be null");
		if (newPattern == null) throw new NullPointerException("newPattern cannot be null");
		if (list.contains(oldPattern)) list.set(list.indexOf(oldPattern), newPattern);
	}

	/**
	 * This blindly adds the specified attribute pattern to this.
	 * Sub-classes should overwrite this to provide more meaningful implementation.
	 */
	public void add(AttributePattern attributePattern) throws RuleGenerationException {
		list.add(attributePattern);
	}

	public void insert(AttributePattern attributePattern) throws RuleGenerationException {
		list.add(0, attributePattern);
	}
	
	public final AttributePattern get(int index) {
		return list.get(index);
	}

	public final void remove(AttributePattern attributePattern) {
		list.remove(attributePattern);
	}

	public final boolean isEmpty() {
		return list.isEmpty();
	}

	public final int size() {
		return list.size();
	}

}
