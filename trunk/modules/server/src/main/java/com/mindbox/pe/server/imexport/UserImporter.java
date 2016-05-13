package com.mindbox.pe.server.imexport;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.mindbox.pe.xsd.data.SecurityDataElement;
import com.mindbox.pe.xsd.data.UsersElement.User;

final class UserImporter extends AbstractImporter<SecurityDataElement, Object> {

	private static String asErrorContext(final User user) {
		return String.format("User[id=%s,name=%s]", user.getId(), user.getName());
	}

	protected UserImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(final SecurityDataElement dataToImport, final Object optionalData) throws ImportException {
		if (dataToImport != null && dataToImport.getUsers() != null) {
			try {
				final int count = processUsers(dataToImport.getUsers().getUser());
				if (count > 0) {
					importResult.addMessage("  Imported " + count + " users", "");
				}
			}
			catch (NoSuchAlgorithmException e) {
				logger.error(e);
				throw new ImportException(e.getMessage());
			}
		}
		else {
			logger.info("No users to import");
		}
	}

	private int processUsers(List<com.mindbox.pe.xsd.data.UsersElement.User> list) throws NoSuchAlgorithmException {
		int count = 0;
		for (final com.mindbox.pe.xsd.data.UsersElement.User userElement : list) {
			try {
				importBusinessLogic.importUser(ObjectConverter.asUser(userElement), super.user);
				++count;
			}
			catch (ImportException ex) {
				addError(asErrorContext(userElement), ex);
			}
		}
		return count;
	}


}
