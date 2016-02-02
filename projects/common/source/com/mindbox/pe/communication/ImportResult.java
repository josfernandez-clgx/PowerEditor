package com.mindbox.pe.communication;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.common.validate.MessageDetail;


/**
 * Import result with status message and error messages, if any.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public final class ImportResult implements Serializable {

	private static final long serialVersionUID = 2005012409560000L;

	private final List<MessageDetail> errorList = new LinkedList<MessageDetail>();
	private final List<MessageDetail> messageList = new LinkedList<MessageDetail>();
	private boolean templateImported;
	private long elapsedTime = 0L;

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public boolean isTemplateImported() {
		return templateImported;
	}

	public void setTemplateImported(boolean templateImported) {
		this.templateImported = templateImported;
	}

	public boolean hasError() {
		return !errorList.isEmpty();
	}

	public List<MessageDetail> getErrorMessages() {
		return Collections.unmodifiableList(errorList);
	}

//	public void addErrorMessages(MessageDetail[] errorMessages) {
//		for (int i = 0; i < errorMessages.length; i++) {
//			errorList.add(errorMessages[i]);
//		}
//	}
//
	public void addErrorMessage(String errorMessage, Object context) {
		errorList.add(new MessageDetail(errorMessage, context));
	}

	public List<MessageDetail> getMessages() {
		return Collections.unmodifiableList(messageList);
	}

	public void addMessage(String message, Object context) {
		messageList.add(new MessageDetail(message, context));
	}

	public String toString() {
		return "ImportResult[noMsgs=" + messageList.size() + ",noErrors=" + errorList.size() + ",time=" + elapsedTime + "]";
	}
}