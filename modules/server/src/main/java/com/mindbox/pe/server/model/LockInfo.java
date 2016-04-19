package com.mindbox.pe.server.model;

import java.util.Date;

/**
 * Captures lock information, user name and time of lock.
 * This is used as key in a map.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class LockInfo {

	public User getLockedBy() {
		return mLockedBy;
	}

	public String toString() {
		return "LockInfo[" + getLockedBy() + "@" + getLockedOn() + "]";
	}

	public LockInfo(User user, Date date) {
		setLockedBy(user);
		setLockedOn(date);
	}

	public void setLockedOn(Date date) {
		mLockedOn = date;
	}

	public Date getLockedOn() {
		return mLockedOn;
	}

	public void setLockedBy(User user) {
		mLockedBy = user;
	}

	public boolean equals(Object object) {
		if (object instanceof LockInfo) {
			return mLockedBy.equals(((LockInfo)object).getLockedBy());
		}
		else {
			return false;
		}
	}
	
	public int hashCode() {
		return mLockedBy.hashCode();
	}
	
	private User mLockedBy;
	private Date mLockedOn;
}