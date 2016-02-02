package com.mindbox.pe.server.webservices;

import java.util.Collection;
import java.util.Iterator;

public class PowerEditorInterfaceReturnStructure {
	public boolean errorFlag = false;
	public String errorMessages[] = null;
	public String warningMessages[] = null;
	public String generalMessages[] = null;
	public String content = null;

	public void setErrorMessages(String [] errorMessages) {
		this.errorMessages = errorMessages;
		errorFlag = true;
	}
	
	public void setErrorMessages(Collection<?> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		errorMessages = new String[objects.size()];
		int count = 0;
		for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
			errorMessages[count] = iter.next().toString();
			count++;
		}
		errorFlag = true;
	}

	public void addErrorMessage(String errorMessage) {
		// TT 2067 - also check for blank string
		if (errorMessage == null || errorMessage.trim().isEmpty()) { return; }
		
		errorFlag = true;
		if (errorMessages == null) {
			errorMessages = new String[1];
			errorMessages[0] = errorMessage;
		} else {
		
			int len = errorMessages.length;
			String [] oldErrorMessages = errorMessages;
			errorMessages = new String[len + 1];
			for (int loop = 0; loop < len; loop++) {
				errorMessages[loop] = oldErrorMessages[loop];
			}
			errorMessages[len] = errorMessage;
		}
	}
	public void setWarningMessages(String [] warningMessages) {
		this.warningMessages = warningMessages;
	}
	
	public void setWarningMessages(Collection<?> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		warningMessages = new String[objects.size()];
		int count = 0;
		for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
			warningMessages[count] = iter.next().toString();
			count++;
		}
	}
	public void addWarningMessage(String warningMessage) {
		if (warningMessage == null) { return; }
		
		if (warningMessages == null) {
			warningMessages = new String[1];
			warningMessages[0] = warningMessage;
		} else {
			int len = warningMessages.length;
			String [] oldWarningMessages = warningMessages;
			warningMessages = new String[len + 1];
			for (int loop = 0; loop < len; loop++) {
				warningMessages[loop] = oldWarningMessages[loop];
			}
			warningMessages[len] = warningMessage;
		}
	}

	public void setGeneralMessages(String [] messages) {
		this.generalMessages = messages;
	}
	
	public void setGeneralMessages(Collection<?> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		generalMessages = new String[objects.size()];
		int count = 0;
		for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
			generalMessages[count] = iter.next().toString();
			count++;
		}
	}
	public void addGeneralMessage(String message) {
		if (message == null) { return; }
		
		if (generalMessages == null) {
			generalMessages = new String[1];
			generalMessages[0] = message;
		} else {
			int len = generalMessages.length;
			String [] oldMessages = generalMessages;
			generalMessages = new String[len + 1];
			for (int loop = 0; loop < len; loop++) {
				generalMessages[loop] = oldMessages[loop];
			}
			generalMessages[len] = message;
		}
}

public void setContent(String content) {
		this.content = content;
	}
}
