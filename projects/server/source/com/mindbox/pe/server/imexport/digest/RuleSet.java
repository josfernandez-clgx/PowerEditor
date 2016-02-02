/*
 * Created on 2004. 3. 24.
 *
 */
package com.mindbox.pe.server.imexport.digest;

import java.util.List;

import com.mindbox.pe.common.config.AbstractDigestedObjectHolder;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class RuleSet extends AbstractDigestedObjectHolder {

	private static final long serialVersionUID = 4219340155560487132L;

	private int id = -1;
	private String name;
	private String status;
	private String usage;
	private String description;
	private ActivationDates activationDates = null;

	public List<Rule> getRules() {
		return super.getObjects(Rule.class);
	}

	public List<EntityIdentity> getContextElements() {
		return super.getObjects(EntityIdentity.class);
	}

	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates dates) {
		activationDates = dates;
	}

	public int getId() {
		return id;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public String getUsage() {
		return usage;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		status = string;
	}

	/**
	 * @param string
	 */
	public void setUsage(String string) {
		usage = string;
	}

	public String toString() {
		return "RuleSet["
			+ id
			+ ",name="
			+ name
			+ ",status="
			+ status
			+ ",usage="
			+ usage
			+ ",act="
			+ activationDates
			+ ",desc="
			+ description
			+ "]";
	}

}
