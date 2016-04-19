/*
 * Created on 2005. 5. 5.
 *
 */
package com.mindbox.pe.common;


/**
 * Mutable boolean object.
 * This is to emulated pass-by-reference of boolean data.
 * This is thread-safe.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public final class MutableBoolean {

	private boolean state;
	
	public MutableBoolean(boolean state) {
		this.state = state;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof MutableBoolean) {
			return state && ((MutableBoolean)obj).state;
		}
		else {
			return false;
		}
	}
	
	public int hashCode() {
		return (state ? 1 : 0);
	}
	
	public synchronized boolean booleanValue() {
		return state;
	}
	
	public synchronized void setState(boolean state) {
		this.state = state;
	}
	
	public String toString() {
		return String.valueOf(state);
	}
}
