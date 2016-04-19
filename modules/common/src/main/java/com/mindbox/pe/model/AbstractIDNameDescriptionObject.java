package com.mindbox.pe.model;

/**
 * Object with id, name, description.
 * @author Geneho Kim, MindBox, Inc.
 * @since PowerEditor 1.0
 */
public abstract class AbstractIDNameDescriptionObject extends AbstractIDNameObject {
	
	private static final long serialVersionUID = 2003052016150200L;


	private String desc = null;

	/**
	 * Constructs a new KeyedNameDescription with the specified id and empty name and description.
	 */
	protected AbstractIDNameDescriptionObject(String name, String desc) {
		super(name);
		this.desc = desc;
	}

	/**
	 * Constructs a new KeyedNameDescription object with the specified details.
	 * @param id the id
	 * @param name the name
	 * @param desc the description
	 */
	public AbstractIDNameDescriptionObject(int id, String name, String desc) {
		super(id, name);
		this.desc = desc;
	}
	
	protected AbstractIDNameDescriptionObject(AbstractIDNameDescriptionObject source) {
		super(source);
		this.desc = source.desc;
	}

	public boolean equals(Object obj) {
		if (obj instanceof AbstractIDNameDescriptionObject) {
			return (super.equals(obj) && desc.equals(((AbstractIDNameDescriptionObject)obj).desc));
		}
		else {
			return false;
		}
	}

	public final String getDescription() {
		return desc;
	}
	
	public final void setName(String name) {
		super.setName(name);
	}
	
	public final void setDescription(String desc) {
		this.desc = desc;
	}
	
	public String toString() {
		return super.toString() + "[desc=" + desc + "]";
	}
}
