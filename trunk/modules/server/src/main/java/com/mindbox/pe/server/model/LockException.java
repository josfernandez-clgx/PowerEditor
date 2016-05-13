package com.mindbox.pe.server.model;

import com.mindbox.pe.model.exceptions.SapphireException;

public class LockException extends SapphireException {

	private static final long serialVersionUID = 8711482666382153210L;

	public String getLockedBy() {
		return mLockedBy;
	}

	public LockException() {
	}

	public LockException(String s, String s1) {
		super(s);
		setLockedBy(s1);
	}

	public void setLockedBy(String s) {
		mLockedBy = s;
	}

	private String mLockedBy;
}