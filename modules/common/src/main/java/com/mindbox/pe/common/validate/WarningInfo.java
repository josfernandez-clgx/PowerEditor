package com.mindbox.pe.common.validate;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public final class WarningInfo extends MessageDetail {

	private static final long serialVersionUID = -6883314223263639784L;

	public static final int FATAL = 9;
	public static final int ERROR = 8;
	public static final int WARNING = 1;
	public static final int INFO = 0;

	private final int severity;

	public static String toString(int severity) {
		switch (severity) {
			case FATAL :
				return "CRITICAL";
			case ERROR :
				return "ERROR";
			case WARNING :
				return "WARNING";
			case INFO :
				return "INFO";
			default :
				return "ERROR";
		}
	}

	public WarningInfo(int severity, String message, String resource) {
		super(message, resource);
		this.severity = severity;
	}

	/**
	 * @return the resource for this message
	 */
	public String getResource() {
		return (String) getResource();
	}

	/**
	 * @return the severity
	 */
	public int getSeverity() {
		return severity;
	}

}
