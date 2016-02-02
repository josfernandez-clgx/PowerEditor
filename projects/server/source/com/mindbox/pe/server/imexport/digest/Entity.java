package com.mindbox.pe.server.imexport.digest;

import java.util.List;

import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.AssertValid;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Entity extends EntityIdentityParentIDProperties {

	private boolean imported = false;
	
	@IsInvariant
	@AssertValid(message="Contains invalid associattions", requireValidElements=true)
	public List<Association> getAssociations() {
		return getObjects(Association.class);
	}

	public boolean isImported() {
		return imported;
	}
	
	public void markImported() {
		this.imported = true;
	}
	
	public String toString() {
		return "Entity" + super.toString();
	}
}
