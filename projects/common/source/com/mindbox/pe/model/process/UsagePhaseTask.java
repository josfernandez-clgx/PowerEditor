/*
 * Created on 2004. 6. 24.
 */
package com.mindbox.pe.model.process;

import com.mindbox.pe.model.TemplateUsageType;


/**
 * A task for phase that corresponds to a {@link com.mindbox.pe.model.TemplateUsageType}. 
 *
 * @author kim
 * @since PowerEditor 3.3.0 
 */
public class UsagePhaseTask implements PhaseTask {

	private static final long serialVersionUID = 2004060240003L;

	
	private final TemplateUsageType usageType;
	
	/**
	 * 
	 * @param usageType
	 * @throws NullPointerException if <code>usageType</code> is null
	 */
	public UsagePhaseTask(TemplateUsageType usageType) {
		if (usageType == null) throw new NullPointerException("usageType cannot be null");
		this.usageType = usageType;
	}
	
	public String getName() {
		return usageType.getDisplayName();
	}
	
	public TemplateUsageType toUsageType() {
		return usageType;
	}
	
	public String getStorageName() {
		return usageType.toString();
	}
	
	public String toString() {
		return getName();
	}
}
