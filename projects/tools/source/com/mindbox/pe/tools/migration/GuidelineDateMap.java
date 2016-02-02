/*
 * Created on 2004. 12. 20.
 *
 */
package com.mindbox.pe.tools.migration;

import java.util.Date;


/**
 * Guideline date mapping.
 * @author Geneho Kim
 * @since PowerEditor 4.2
 */
public class GuidelineDateMap {

	private final Date date;
	private String name, description;

	public GuidelineDateMap(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}
}