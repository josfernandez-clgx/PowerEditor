package com.mindbox.pe.server.imexport.digest;

import com.mindbox.pe.common.validate.oval.PositiveOrUnassigned;

import net.sf.oval.constraint.AssertValid;

/**
 * @author Vineet Khosla
 * @author MDA MindBox, Inc
 * @since PowerEditor 5.0.0
 */
public class Parent {
	
	@AssertValid
	private ActivationDates activationDates = null;
	
	@PositiveOrUnassigned
	private int id = -1;
	
	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates dates) {
		activationDates = dates;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		return "Parent[id="+id+" ,activationDates="+activationDates+"]";
	}
}
