package com.mindbox.pe.server.imexport.digest;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import com.mindbox.pe.common.validate.oval.EntityTypeNameOrCategory;
import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class EntityIdentity {

	@NotNull
	@NotEmpty
	@EntityTypeNameOrCategory
	private String type = null;
	
	@PositiveOrUnassigned
	private int id = -1;

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setId(int i) {
		id = i;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		return "[" + type + ":" + id + "]";
	}
}
