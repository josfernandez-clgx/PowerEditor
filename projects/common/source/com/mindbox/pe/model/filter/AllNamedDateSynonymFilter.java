package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EntityType;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class AllNamedDateSynonymFilter extends AbstractSearchFilter<DateSynonym> {

	private static final long serialVersionUID = 6057998229807218448L;

	public AllNamedDateSynonymFilter() {
		super(EntityType.DATE_SYNONYM);
	}

	public boolean isAcceptable(DateSynonym object) {
		return object.isNamed();
	}

}
