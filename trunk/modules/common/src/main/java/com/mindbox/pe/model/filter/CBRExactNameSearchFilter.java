package com.mindbox.pe.model.filter;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRCase;

/**
 * @author deklerk
 *
 */
public class CBRExactNameSearchFilter extends NameSearchFilter<AbstractIDNameDescriptionObject> {

	private static final long serialVersionUID = 4778742164114669315L;

	private int caseBaseID;
	
	public CBRExactNameSearchFilter(PeDataType entityType, String name, int caseBaseID) {
		super(entityType);
		this.setNameCriterion(name);
		this.caseBaseID = caseBaseID;
	}
	
	public boolean isAcceptable(AbstractIDNameDescriptionObject object) {
		if (object instanceof CBRAttribute) {
			return isAcceptable((CBRAttribute)object);
		}
		else if (object instanceof CBRCase) {
			return isAcceptable((CBRCase)object);
		}
		else {
			return false;
		}
	}
	
	protected boolean isAcceptable(CBRAttribute att) {
		return att.getCaseBase().getID() == caseBaseID && UtilBase.trim(att.getName()).equalsIgnoreCase(this.getNameCriterion());
	}

	protected boolean isAcceptable(CBRCase c) {
		return c.getCaseBase().getID() == caseBaseID && UtilBase.trim(c.getName()).equalsIgnoreCase(this.getNameCriterion());
	}

	public String toString() {
		return "CBRNameSearchFilter[" + entityType + ",name=" + getNameCriterion() + ",caseBaseID="+caseBaseID+"]";
	}


}
