package com.mindbox.pe.common.validate;


/**
 * Text warning consumer. 
 * Use {@link #toString()} to retrieve a text representation of all warnings in this.
 * @author Geneho Kim
 * @author MindBox
 */
public class TextWarningConsumer implements WarningConsumer {

	private StringBuilder buff = new StringBuilder();
	private int count = 0;

	public void clear() {
		buff = null;
		buff = new StringBuilder();
		count = 0;
	}

	public void addWarning(int level, String message) {
		addWarning(level, message, null);
	}
	
	public void addWarning(int level, String message, String resource) {
		buff.append(WarningInfo.toString(level) + ": " + message + (resource == null ? "" : " at " + resource));
		buff.append(System.getProperty("line.separator"));
		++count;
	}
	
	public boolean hasWarnings() {
		return count > 0;
	}
	
	public String toString() {
		return buff.toString();
	}
}
