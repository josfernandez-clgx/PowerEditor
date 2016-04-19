package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface CompoundRuleElement<E extends RuleElement> extends RuleElement {

	void add(E element);
	void insert(int index, E element);
	void remove(E element);
	void remove(int index);
	void replace(E element1, E element2);
	int size();
	/**
	 * 
	 * @param index index
	 * @return rule element at the specified index
	 * @throws IndexOutOfBoundsException if index &lt; 0 or index &gt;= {@link #size()}
	 */
	E get(int index);
	void removeAll();
	
	/**
	 * Swaps rule elements at index1 and index2.
	 * @param index1
	 * @param index2
	 * @throws IndexOutOfBoundsException if any of the specified index is invalid (&lt; 0 or &gt;= size()).
	 */
	void swapRuleElements(int index1, int index2);
	
	/**
	 * Tests if this is empty or contains only the empty compound elements.
	 * @return <code>true</code> if this is empty or contains only the empty compound elements; <code>false</code>, otherwise
	 * @since 4.3.2
	 */
	boolean isEmpty();
}
