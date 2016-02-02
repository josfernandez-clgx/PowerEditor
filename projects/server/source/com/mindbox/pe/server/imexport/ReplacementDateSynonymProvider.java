package com.mindbox.pe.server.imexport;

import com.mindbox.pe.model.DateSynonym;

public interface ReplacementDateSynonymProvider {

	DateSynonym getReplacementDateSynonymForImport() throws ImportException;
	
}
