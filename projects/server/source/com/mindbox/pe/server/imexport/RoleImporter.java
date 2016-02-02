package com.mindbox.pe.server.imexport;

import java.util.List;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.server.imexport.digest.Privilege;
import com.mindbox.pe.server.imexport.digest.Role;

final class RoleImporter extends AbstractImporter<Object> {

	protected RoleImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, Object optionalData) throws ImportException {
		int count = processRoles(objectHolder.getObjects(Role.class), objectHolder.getObjects(Privilege.class));
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " roles", "File: " + filename);
		}
	}

	private int processRoles(List<Role> roles, List<Privilege> privileges) {
		int count = 0;
		for (int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			try {
				importBusinessLogic.importRole(ObjectConverter.asRole(role, privileges), ObjectConverter.unknownPrivsForRole(
						role,
						privileges), user);
				++count;
			}
			catch (ImportException ex) {
				addError(role.toString(), ex);
			}
		}
		return count;
	}

}
