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
	 * @param dispName dispName
	 */
	protected AbstractCompoundRuleElement(String dispName) {
		super(dispName);
		this.elementList = new LinkedList<E>();
	}

	@Override
	public void add(E element) {
		elementList.add(element);
	}

	@Override
	public void adjustChangedColumnReferences(int originalColNum, int newColNum) {
		for (E element : elementList) {
			element.adjustChangedColumnReferences(originalColNum, newColNum);
		}
	}

	@Override
	public void adjustDeletedColumnReferences(int colNo) {
		for (E element : elementList) {
			element.adjustDeletedColumnReferences(colNo);
		}
	}

	@Override
	public final E get(int index) {
		return elementList.get(index);
	}

	public List<E> getElements() {
		List<E> ret = new ArrayList<E>();
		for (E element : elementList) {
			ret.add(element);
		}
		return ret;
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

	@Override
	public void insert(int index, E element) {
		elementList.add(index, element);
	}

	@Override
	public final boolean isEmpty() {
		return elementList.isEmpty() || !hasNonEmptyChild();
	}

	@Override
	public void remove(E element) {
		elementList.remove(element);
	}

	@Override
	public void remove(int index) {
		elementList.remove(index);
	}

	@Override
	public void removeAll() {
		elementList.clear();
	}

	@Override
	public void replace(E element1, E element2) {
		int i = elementList.indexOf(element1);
		if (i >= 0) {
			elementList.remove(element1);
			elementList.add(i, element2);
		}
	}

	@Override
	public final int size() {
		return elementList.size();
	}

	/**
	 * 
	 * @param index1 index1
	 * @param index2 index2
	 */
	@Override
	public final void swapRuleElements(int index1, int index2) {
		if (index1 == index2) return;
		synchronized (elementList) {
			E obj1 = elementList.get(index1);
			elementList.set(index1, elementList.get(index2));
			elementList.set(index2, obj1);
		}
	}

	@Override
	public String toString() {
		return super.toString() + "[size=" + elementList.size() + "]";
	}
}
