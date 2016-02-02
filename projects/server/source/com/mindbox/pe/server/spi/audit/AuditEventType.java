package com.mindbox.pe.server.spi.audit;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;

public final class AuditEventType implements java.io.Serializable {

	private static final long serialVersionUID = 2007042500000L;

	public static final AuditEventType LOGON = new AuditEventType(1, "Logon");
	public static final AuditEventType LOGOFF = new AuditEventType(2, "Logoff");
	public static final AuditEventType SERVER_STARTUP = new AuditEventType(3, "Server Startup");
	public static final AuditEventType SERVER_SHUTDOWN = new AuditEventType(4, "Server Shutdown");
	public static final AuditEventType KB_MOD = new AuditEventType(5, "KB Modification");

	public static AuditEventType forID(int id) {
		switch (id) {
		case 1:
			return LOGON;
		case 2:
			return LOGOFF;
		case 3:
			return SERVER_STARTUP;
		case 4:
			return SERVER_SHUTDOWN;
		case 5:
			return KB_MOD;
		default:
			throw new IllegalArgumentException(String.valueOf(id));
		}
	}

	private final int id;
	private final String name;

	private AuditEventType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private Object readResolve() throws ObjectStreamException {
		try {
			return forID(this.id);
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidObjectException(ex.getMessage());
		}
	}

	public String toString() {
		return name;
	}
}
