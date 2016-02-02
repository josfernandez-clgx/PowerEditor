package com.mindbox.pe.server.imexport;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.server.imexport.digest.User;

final class UserImporter extends AbstractImporter<Object> {

	protected UserImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, Object optionalData) throws ImportException {
		int count;
		try {
			count = processUsers(objectHolder.getObjects(User.class));
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " users", "File: " + filename);
			}
		}
		catch (NoSuchAlgorithmException e) {
			logger.error(e);
			throw new ImportException(e.getMessage());
		}
	}

	private int processUsers(List<User> list) throws NoSuchAlgorithmException {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			User userDigest = list.get(i);
			try {
				importBusinessLogic.importUser(ObjectConverter.asUser(userDigest), super.user);
				++count;
			}
			catch (ImportException ex) {
				addError(userDigest.toString(), ex);
			}
		}
		return count;
	}


}
