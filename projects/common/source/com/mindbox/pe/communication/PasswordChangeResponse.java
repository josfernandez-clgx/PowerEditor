package com.mindbox.pe.communication;

/**
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public class PasswordChangeResponse extends ResponseComm{
	private static final long serialVersionUID = 2003052312001001L;

	private final boolean success;
	private final String msg;

	public static PasswordChangeResponse successInstance() {
		return new PasswordChangeResponse(true, "");
	}
	
	public static PasswordChangeResponse failureInstance(String msg) {
		return new PasswordChangeResponse(false, msg);
	}
	
	private PasswordChangeResponse(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}

	public boolean succeeded() {
		return success;
	}

	public String toString() {
		return "PasswordChangeResponse[success?="+success+"]";
	}

	public String getMsg() {
		return msg;
	}
}
