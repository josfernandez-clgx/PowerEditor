/*
 * Created on 2004. 4. 15.
 *
 */
package com.mindbox.pe.common.config;

import java.io.Serializable;

/**
 * Encapsulates category type definition in PowerEditor Configuration xml file.
 * This is used by configuration xml digester.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class CategoryTypeDefinition implements Serializable {

	private static final long serialVersionUID = 200404109000L;
	
	private String name;
	private int typeID;
	private boolean useInSelectionTable;

	public void setShowInSelectionTable(String value) {
		useInSelectionTable = ConfigUtil.asBoolean(value);
	}

	public boolean useInSelectionTable() {
		return useInSelectionTable;
	}
	
	public String getName() {
		return name;
	}

	public int getTypeID() {
		return typeID;
	}

	public void setName(String string) {
		name = string;
	}

	public void setTypeID(int i) {
		typeID = i;
	}

	public String toString() {
		return "CategoryTypeDef["+name+"="+typeID+"]";
	}
}
