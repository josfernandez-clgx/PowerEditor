package com.mindbox.pe.model;

import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

/**
 * Object with ID and name.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public abstract class AbstractIDNameObject extends AbstractIDObject implements Comparable<AbstractIDNameObject>, IDNameObject {

	private static final long serialVersionUID = 200306132300010L;

	@NotNull
	@NotBlank
	private String name = null;
	
	/**
	 * Constructs a new IDName object with the specified name.
	 * @param name the name
	 * @throws NullPointerException if name is null
	 */
	protected AbstractIDNameObject(String name) {
		super();
		if (name == null) throw new NullPointerException("name cannot be null"); 
		this.name = name;
	}

	/**
	 * Constructs a new IDName object with the specified id and name.
	 * @param id the id
	 * @param name the name
	 * @throws NullPointerException if name is null
	 */
	protected AbstractIDNameObject(int id, String name) {
		super(id);
		if (name == null) throw new NullPointerException("name cannot be null"); 
		this.name = name;
	}

	protected AbstractIDNameObject(AbstractIDNameObject source) {
		this(source.getID(), source.name);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof AbstractIDNameObject) {
			return (super.equals(obj) && name.equals(((AbstractIDNameObject)obj).name));
		}
		else {
			return false;
		}
	}
	
	public String getAuditName() {
		return getName();
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @throws NullPointerException if name is null
	 */
	protected void setName(String name) {
		if (name == null) throw new NullPointerException("name cannot be null"); 
		this.name = name;
	}
	
	/**
	 * Comparable's compareTo method that compares name.
	 */
	public int compareTo(AbstractIDNameObject target) {
		if (this == target) {
			return 0;}
		else {
			if (this.getID() == target.getID()) {
				return 0;
			}
			else {
				return this.name.compareTo(target.name);
			}
		}
	}
	
	public String toString() {
		return "[name=" + name + ",id=" + getID() + "]";
	}
}
