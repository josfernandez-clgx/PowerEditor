package com.mindbox.pe.model.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Abstract report specification.
 * This class is not intended to be extended outside of the package this is in.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public abstract class AbstractReportSpec implements Serializable {

	private static final long serialVersionUID = 200412091130000L;
	
	private String localFilename;
	private final Map<String,Boolean> attributeMap;
	
	protected AbstractReportSpec() {
		attributeMap = new HashMap<String,Boolean>();
	}
	
	/**
	 * Gets the local filename of this.
	 * @return the local filename
	 */
	public String getLocalFilename() {
		return localFilename;
	}
	
	/**
	 * Sets the local filename to the specified value.
	 * @param localFilename new local filename
	 */
	public void setLocalFilename(String localFilename) {
		this.localFilename = localFilename;
	}
	
	/**
	 * Tests if the specified atttribute is turned on for reporting.
	 * @param attributeName the attribute name to test
	 * @return <code>true</code> if <code>attributeName</code> is turned on; <code>false</code>, otherwise
	 */
	synchronized boolean isAttributeOn(String attributeName) {
		if (attributeMap.containsKey(attributeName)) {
			return attributeMap.get(attributeName);
		}
		else return false;
	}
	
	/**
	 * Sets the turned on status of the specified attribute for reporting.
	 * @param attributeName the attribute to set the turn on status
	 * @param on new status
	 */
	synchronized void setAttributeOn(String attributeName, boolean on) {
		if (attributeMap.containsKey(attributeName)) {
			attributeMap.remove(attributeName);
		}
		attributeMap.put(attributeName, on);
	}
}
