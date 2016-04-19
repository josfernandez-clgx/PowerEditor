package com.mindbox.pe.model;

/**
 * Instance to indicate this entity supports clone operation.
 * Note: clone operation in this context is not the same as java.lang.Object#clone(). 
 * Clone operation is a two-step process: (1) Save the entity and (2) Copy guideline/parameter activations of the parent entity.
 * @author Geneho Kim
 * @since 4.5.0
 */
public interface CloneableEntity {

	boolean isForClone();
	
	void setForClone(boolean forClone);
	
	boolean shouldCopyPolicies();
	
	void setCopyPolicies(boolean copyPolicies);
}
