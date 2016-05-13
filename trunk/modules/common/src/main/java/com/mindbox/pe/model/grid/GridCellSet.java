package com.mindbox.pe.model.grid;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindbox.pe.model.grid.GridCellCoordinates.GridCellCoordinatesComparator;

/**
 * A set of {@link com.mindbox.pe.model.grid.GridCellCoordinates}.
 */
public class GridCellSet implements Serializable {
	
	private static final long serialVersionUID = 7967246080462023553L;

	private final SortedSet<GridCellCoordinates> cells;
	
	public GridCellSet(GridCellCoordinatesComparator comparator) {
		cells = new TreeSet<GridCellCoordinates>(comparator);
	}
	
	public Iterator<GridCellCoordinates> iterator() {
		return cells.iterator();
	}
	
	public int size() {
		return cells.size();
	}
	
	public boolean isEmpty() {
		return cells.isEmpty();
	}
	
	public boolean add(GridCellCoordinates cellCoordinates) {
		return cellCoordinates != null && cells.add(cellCoordinates);
	}

	public boolean remove(GridCellCoordinates cellCoordinates) {
		return cellCoordinates == null ? false : cells.remove(cellCoordinates);
	}
	
	public void clear() {
		cells.clear();
	}
	
	public boolean contains(GridCellCoordinates cellCoordinates) {
		return cellCoordinates == null ? false : cells.contains(cellCoordinates);
	}
	
	/** @return null if this set doesn't contain row, col */
	public GridCellCoordinates get(int row, int col) {
		GridCellCoordinates searchCoordinates = new GridCellCoordinates(row, col);
		
		if (!contains(searchCoordinates)) { // optimization using TreeSet to avoid cell by cell search when not in the set
			return null;
		}
		
		for (GridCellCoordinates actualCoordinates : cells) {
			if (searchCoordinates.equals(actualCoordinates)) {
				return actualCoordinates;
			}
		}
		return null; // unreachable!
	}

	/** 
	 * @return SortedSet of Integers, one for each column index
	 * that appears at least once in the set of cells.
	 * 
	 * Not thread-safe, not optimized for multiple calls. 
	 */
	public SortedSet<Integer> getColumnIndexes() {
		SortedSet<Integer> result = new TreeSet<Integer>();
		for (Iterator<GridCellCoordinates> cellIter = cells.iterator(); cellIter.hasNext();) {
			result.add(cellIter.next().getColumn());
		}
		return Collections.unmodifiableSortedSet(result);
	}

	/** 
	 * @return SortedSet of Integers, one for each row index
	 * that appears at least once in the set of cells.
	 * 
	 * Not thread-safe, not optimized for multiple calls. 
	 */
	public SortedSet<Integer> getRowIndexes() {
		SortedSet<Integer> result = new TreeSet<Integer>();
		for (Iterator<GridCellCoordinates> cellIter = cells.iterator(); cellIter.hasNext();) {
			result.add(cellIter.next().getRow());
		}
		return Collections.unmodifiableSortedSet(result);
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass().getName().equals(GridCellSet.class.getName())) {
			GridCellSet that = (GridCellSet) o;
			return this.cells.equals(that.cells);
		}
		return false;
	}
	
	public int hashCode() {
		return cells.hashCode();
	}
}
