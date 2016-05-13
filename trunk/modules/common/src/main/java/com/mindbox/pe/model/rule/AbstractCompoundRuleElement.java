package com.mindbox.pe.model.rule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
abstract class AbstractCompoundRuleElement<E extends RuleElement> extends AbstractRuleElement implements CompoundRuleElement<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3656090025287504162L;

	private final LinkedList<E> elementList;

	/**
	 * 
	 */
	protected AbstractCompoundRuleElement(String dispName) {
		super(dispName);
		this.elementList = new LinkedList<E>();
	}

	public void adjustChangedColumnReferences(int originalColNum, int newColNum) {
		for (E element : elementList) {
			element.adjustChangedColumnReferences(originalColNum, newColNum);
		}
	}

	public void adjustDeletedColumnReferences(int colNo) {
		for (E element : elementList) {
			element.adjustDeletedColumnReferences(colNo);
		}
	}

	/**
	 * 
	 * @param index1
	 * @param index2
	 * @throws IndexOutOfBoundsException
	 */
	public final void swapRuleElements(int index1, int index2) {
		if (index1 == index2) return;
		synchronized (elementList) {
			E obj1 = elementList.get(index1);
			elementList.set(index1, elementList.get(index2));
			elementList.set(index2, obj1);
		}
	}

	public void add(E element) {
		elementList.add(element);
	}

	public void insert(int index, E element) {
		elementList.add(index, element);
	}

	public void remove(E element) {
		elementList.remove(element);
	}

	public void replace(E element1, E element2) {
		int i = elementList.indexOf(element1);
		if (i >= 0) {
			elementList.remove(element1);
			elementList.add(i, element2);
		}
	}

	public final int size() {
		return elementList.size();
	}

	public final E get(int index) {
		return elementList.get(index);
	}

	public void remove(int index) {
		elementList.remove(index);
	}

	public void removeAll() {
		elementList.clear();
	}

	public String toString() {
		return super.toString() + "[size=" + elementList.size() + "]";
	}

	public List<E> getElements() {
		List<E> ret = new ArrayList<E>();
		for (E element : elementList) {
			ret.add(element);
		}
		return ret;
	}

	public final boolean isEmpty() {
		return elementList.isEmpty() || !hasNonEmptyChild();
	}

	protected final boolean hasNonEmptyChild() {
		for (E element : elementList) {
			if (element instanceof CompoundLHSElement) {
				if (!((CompoundLHSElement) element).isEmpty()) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}
}
