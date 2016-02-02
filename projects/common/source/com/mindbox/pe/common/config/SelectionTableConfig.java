/*
 * Created on 2004. 3. 2.
 *
 */
package com.mindbox.pe.common.config;

import java.io.Serializable;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class SelectionTableConfig implements Serializable {

	private static final long serialVersionUID = 2004030240000L;

	private final String[] attributeNames;
	
	SelectionTableConfig(String[] attributeNames) {
		this.attributeNames = (attributeNames == null ? new String[0]: attributeNames);
	}
	
	public int size() {
		return attributeNames.length;
	}
	
	/**
	 * 
	 * @param index
	 * @return attribute name at index
	 * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= {@link #size}
	 */
	public String getAttributeAt(int index) {
		return attributeNames[index];
	}
	
}
