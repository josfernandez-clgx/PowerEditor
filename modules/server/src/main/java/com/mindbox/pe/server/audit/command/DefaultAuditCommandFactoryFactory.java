package com.mindbox.pe.server.audit.command;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.model.Auditable;

/**
 * This is thread safe.
 *
 */
public class DefaultAuditCommandFactoryFactory {

	public static AuditCommandFactory getAuditCommandFactory(Class<?> dataClass) {
		synchronized (instanceMap) {
			if (instanceMap.containsKey(dataClass)) {
				return instanceMap.get(dataClass);
			}
			else {
				AuditCommandFactory commandFactory = new DefaultAuditCommandFactory(dataClass);
				instanceMap.put(dataClass, commandFactory);
				return commandFactory;
			}
		}
	}

	private static final Map<Class<?>, AuditCommandFactory> instanceMap = new HashMap<Class<?>, AuditCommandFactory>();


	private static class DefaultAuditCommandFactory implements AuditCommandFactory {

		private final Class<?> dataClass;

		public DefaultAuditCommandFactory(Class<?> dataClass) {
			this.dataClass = dataClass;
		}

		public AuditCommand getDeleteInstance(Auditable auditable, Date date, String userID) {
			checkAuditableClass(auditable);
			return new GenericDeleteKBModAuditCommand(auditable, date, userID);
		}

		public AuditCommand getImportInstance(Auditable auditable, Date date, String userID) {
			checkAuditableClass(auditable);
			return new GenericImportKBModAuditCommand(auditable, date, userID);
		}

		public AuditCommand getNewInstance(Auditable auditable, Date date, String userID) {
			checkAuditableClass(auditable);
			return new GenericNewKBModAuditCommand(auditable, date, userID);
		}

		public AuditCommand getUpdateInstance(Auditable newAuditable, Auditable oldAuditable, Date date, String userID) {
			checkAuditableClass(oldAuditable);
			checkAuditableClass(newAuditable);
			return new GenericUpdateKBModAuditCommand(newAuditable, oldAuditable, date, userID);
		}

		private void checkAuditableClass(Auditable auditable) {
			if (!dataClass.isInstance(auditable)) throw new IllegalArgumentException(auditable + " must be of type " + dataClass.getName());
		}
	}
}
