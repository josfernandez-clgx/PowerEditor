package com.mindbox.pe.server.generator;

import java.util.List;

import com.mindbox.pe.model.DateSynonym;

/**
 * Providers details for generation process.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface GenerationParams {

	int getID();

	DateSynonym getSunrise();

	DateSynonym getSunset();

	int getRowNum();

	int getColumnNum();

	String getStatus();

	List<Object> getRowData();
	
	boolean hasGenericCategoryAsCellValue();

}
