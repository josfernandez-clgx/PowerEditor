/*
 * Created on 2004. 3. 18.
 *
 */
package com.mindbox.pe.server.imexport.digest;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Property {

	@NotNull
	@NotEmpty
	private String name = null;
	
	private String value = null;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String string) {
		name = string;
	}

	public void setValue(String string) {
		value = string;
	}

	public String toString() {
		return "["+name+"="+value+"]";
	}
}
