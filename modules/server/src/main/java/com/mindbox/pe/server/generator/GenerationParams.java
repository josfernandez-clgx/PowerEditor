package com.mindbox.pe.server.generator;

import java.util.List;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Providers details for generation process.
 * @author Geneho Kim
 * @since 5.1.0
 */
public interface GenerationParams {

	int getColumnNum();

	int getID();

	Integer getPrecisionAsInteger(int columnNo);

	List<Object> getRowData();

	int getRowNum();

	String getStatus();

	DateSynonym getSunrise();

	DateSynonym getSunset();

	GridTemplate getTemplate();

	TemplateUsageType getUsage();

	boolean hasGenericCategoryAsCellValue();
}
