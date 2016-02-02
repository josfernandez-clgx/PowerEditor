package com.mindbox.pe.model;

import java.io.Serializable;

/**
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public abstract class AbstractCBRConfigClass extends AbstractIDNameDescriptionObject implements Serializable, Persistent, Comparable<AbstractIDNameObject> {

	private static final long serialVersionUID = 20041005151900L;

    private String symbol = null;

	/**
	 * Default constructor.
	 * Equivalent to <code>new AbstractCBRConfigClass(-1, "", "")</code>.
	 */
	public AbstractCBRConfigClass() {
		super(UNASSIGNED_ID, "", "");
	}	

	/**
	 * 
	 * @param id
	 * @param symbol
	 * @param name
	 */
	public AbstractCBRConfigClass(int id, String symbol, String name) {
		super(id, name, null);
		this.symbol = symbol;
	}

	/**
	 * 
	 * @param id
	 * @param symbol
	 * @param name
	 * @param description
	 */
	public AbstractCBRConfigClass(int id, String symbol, String name, String description) {
		super(id, name, description);
		this.symbol = symbol;
	}

	public synchronized void copyFrom(AbstractCBRConfigClass in) {
		setID(in.getID());
		setSymbol(in.getSymbol());
		setName(in.getName());
		setDescription(in.getDescription());
	}


	/**
	 * @return Returns the symbol.
	 */
	public String getSymbol() {
		return symbol;
	}
	/**
	 * @param symbol The symbol to set.
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String toString() {
		return "AbstractCBRConfigClass[symbol=" + getSymbol() +
				",display name=" + getName() + "]";
	}
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof AbstractCBRConfigClass) {
			return this.getID() == ((AbstractCBRConfigClass) obj).getID();
		}
		else {
			return false;
		}
	}
	/**
	 * Comparable's compareTo method that compares name.
	 */
	public int compareTo(AbstractIDNameObject arg0) {
		if (this == arg0) {
			return 0;}
		else {
			AbstractCBRConfigClass target = (AbstractCBRConfigClass)arg0;
			if (this.getID() == target.getID()) {
				return 0;
			}
			else {
				return this.getName().compareTo(target.getName());
			}
		}
	}


}
