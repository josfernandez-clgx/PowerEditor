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

	public void addErrorMessage(final String errorMessage, final Serializable context) {
		errorList.add(new MessageDetail(errorMessage, context));
	}

	public void addMessage(final String message, final Serializable context) {
		messageList.add(new MessageDetail(message, context));
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public List<MessageDetail> getErrorMessages() {
		return Collections.unmodifiableList(errorList);
	}

	public List<MessageDetail> getMessages() {
		return Collections.unmodifiableList(messageList);
	}

	public boolean hasError() {
		return !errorList.isEmpty();
	}

	public boolean isTemplateImported() {
		return templateImported;
	}

	public void setElapsedTime(final long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public void setTemplateImported(final boolean templateImported) {
		this.templateImported = templateImported;
	}

	@Override
	public String toString() {
		return String.format("ImportResult[noMsgs=%d,noErrors=%d,time=%d (ms)]", messageList.size(), errorList.size(), elapsedTime);
	}
}