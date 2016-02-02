package com.mindbox.pe.model;

import java.io.Serializable;

/**
 * @author deklerk
 */
public interface ColumnReferenceContainer extends Serializable {
	void adjustChangedColumnReferences(int originalColNum, int newColNo);
	void adjustDeletedColumnReferences(int colNo);
	boolean containsColumnReference(int colNo);
}
