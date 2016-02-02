package com.mindbox.pe.server.imexport.digest;

import java.util.List;

import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.AssertValid;


/**
 * Digest objects that hold generic categories.
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public class CategoryDigest extends EntityIdentityParentIDProperties {

	/**
	 * @return List : list of Parent objects
	 * @since 5.0.0
	 */
	@IsInvariant
	@AssertValid(requireValidElements=true, message="Contains invalid parent list")
	public List<Parent> getParents() {
		return getObjects(Parent.class);
	}

	public boolean isRoot() {
		List<Parent> parentList = getParents();
		return getParentID() == -1 && (parentList.isEmpty() || (parentList.size() == 1 && ((Parent) parentList.get(0)).getId() == -1));
	}
	
	public String toString() {
		return "Category" + super.toString()+ " ["+getParents().toString()+"]";
	}
	
}
