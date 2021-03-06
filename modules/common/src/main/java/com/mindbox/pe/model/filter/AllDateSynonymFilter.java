package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.PeDataType;

/**
 * @author deklerk
 *
 */
public class AllDateSynonymFilter extends AbstractSearchFilter<DateSynonym> {

	private static final long serialVersionUID = -8604911913622935633L;

	public AllDateSynonymFilter() {
		super(PeDataType.DATE_SYNONYM);

	}

	public boolean isAcceptable(DateSynonym object) {
		return true;
	}


}
