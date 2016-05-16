package com.mindbox.pe.communication;

import com.mindbox.pe.model.Persistent;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class SaveRequest extends SessionRequest<SaveResponse> {

	private static final long serialVersionUID = 2003061611404000L;

	private final Persistent object;
	private final boolean lockEntity;
	private final boolean forClone;
	private final boolean validate;

	/**
	 * 
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param object object
	 * @param lockEntity lockEntity
	 * @param forClone forClone
	 */
	public SaveRequest(String userID, String sessionID, Persistent object, boolean lockEntity, boolean forClone) {
		this(userID, sessionID, object, lockEntity, forClone, true);
	}

	/**
	 * 
	 * @param userID userID
	 * @param sessionID sessionID
	 * @param object object
	 * @param lockEntity lockEntity
	 * @param forClone forClone
	 * @param validate validate
	 */
	public SaveRequest(String userID, String sessionID, Persistent object, boolean lockEntity, boolean forClone, boolean validate) {
		super(userID, sessionID);
		this.object = object;
		this.lockEntity = lockEntity;
		this.forClone = forClone;
		this.validate = validate;
	}

	public boolean doLockEntity() {
		return lockEntity;
	}

	public Persistent getPersistent() {
		return object;
	}

	public boolean isForClone() {
		return forClone;
	}

	public boolean isValidate() {
		return validate;
	}

	@Override
	public String toString() {
		return "SaveRequest[" + object + ",lock?=" + lockEntity + ",clone?=" + forClone + "]";
	}
}
