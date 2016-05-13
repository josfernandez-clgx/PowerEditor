package com.mindbox.pe.server.imexport;

import java.util.List;

import com.mindbox.pe.xsd.data.RolesElement.Role;
import com.mindbox.pe.xsd.data.SecurityDataElement;

final class RoleImporter extends AbstractImporter<SecurityDataElement, Object> {

	private static String asErrorContext(final Role role) {
		return String.format("Role[id=%s,name=%s]", role.getId(), role.getName());
	}

	protected RoleImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(final SecurityDataElement dataToImport, final Object optionalData) throws ImportException {
		if (dataToImport != null && dataToImport.getRoles() != null && dataToImport.getPrivileges() != null) {
			final int count = processRoles(dataToImport.getRoles().getRole(), dataToImport.getPrivileges().getPrivilege());
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " roles", "");
			}
		}
		else {
			logger.info("No roles to import.");
		}
	}

	private int processRoles(List<com.mindbox.pe.xsd.data.RolesElement.Role> roles, List<com.mindbox.pe.xsd.data.PrivilegesElement.Privilege> privileges) {
		int count = 0;
		for (final com.mindbox.pe.xsd.data.RolesElement.Role role : roles) {
			if (role != null) {
				try {
					importBusinessLogic.importRole(ObjectConverter.asRole(role, privileges), ObjectConverter.unknownPrivsForRole(role, privileges), user);
					++count;
				}
				catch (ImportException ex) {
					addError(asErrorContext(role), ex);
				}
			}
		}
		return count;
	}

}
