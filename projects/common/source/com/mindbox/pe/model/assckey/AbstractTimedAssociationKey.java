package com.mindbox.pe.model.assckey;

import java.util.Date;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;

import com.mindbox.pe.common.validate.oval.EffectiveDateBeforeExpirationDate;
import com.mindbox.pe.model.DateSynonym;

/**
 * A immutable relationship key with a time period.
 * It includes activation and expiration dates and an ID.
 */
public abstract class AbstractTimedAssociationKey extends AbstractAssociationKey implements TimedAssociationKey {

	private static final long serialVersionUID = 20030527175220000L;

	private static boolean equals(DateSynonym date1, DateSynonym date2) {
		if (date1 == null && date2 == null) {
			return true;
		}
		if (date1 != null && date2 != null && date1.equals(date2)) {
			return true;
		}
		return false;
	}

	@NotNull
	@AssertValid
	@EffectiveDateBeforeExpirationDate
	private DateSynonym activationDate;
	
	@AssertValid
	private DateSynonym expirationDate;
	
	private int hashCode;

	/**
	 * Constructor with associable id, effective date, and expiration date.
	 * This accepts <code>null</code> for both <code>effDate</code> and <code>expDate</code>.
	 * @param associableID
	 * @param effDate effective date; can be <code>null</code>
	 * @param expDate expiration date; can be <code>null</code>
	 */
	public AbstractTimedAssociationKey(int associableID, DateSynonym effDate, DateSynonym expDate) {
		super(associableID);
		this.activationDate = effDate;
		this.expirationDate = expDate;
		resetHashCode();
	}

	protected AbstractTimedAssociationKey(AbstractTimedAssociationKey source) {
		super(source.getAssociableID());
		this.activationDate = source.activationDate;
		this.expirationDate = source.expirationDate;
		this.hashCode = source.hashCode;
	}

	public final DateSynonym getEffectiveDate() {
		return activationDate;
	}

	public final DateSynonym getExpirationDate() {
		return expirationDate;
	}

	void setAssociableID(int id) {
		super.setAssociableID(id);
		resetHashCode();
	}

	void setEffectiveDate(DateSynonym effectiveDate) {
		this.activationDate = effectiveDate;
		resetHashCode();
	}

	void setExpirationDate(DateSynonym expirationDate) {
		this.expirationDate = expirationDate;
		resetHashCode();
	}

	public boolean equals(Object obj) {
		if (super.equals(obj) && obj instanceof TimedAssociationKey) {
			TimedAssociationKey key = (TimedAssociationKey) obj;
			return equals(key.getEffectiveDate(), this.activationDate) && equals(key.getExpirationDate(), this.expirationDate);
		}
		else
			return false;
	}

	private synchronized final void resetHashCode() {
		this.hashCode = (super.hashCode() + ":" + activationDate + ":" + expirationDate).hashCode();
	}

	public int hashCode() {
		return hashCode;
	}

	public boolean isEffectiveAt(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
		if (activationDate == null && expirationDate == null) {
			return true;
		}
		else {
			return (activationDate == null || activationDate.getDate().compareTo(date) <= 0)
					&& (expirationDate == null || expirationDate.getDate().compareTo(date) > 0);
		}
	}

	public boolean overlapsWith(TimedAssociationKey key) {
		if (key == null)
			throw new NullPointerException("key cannot be null");
		else if (activationDate == null) {
			return expirationDate == null || expirationDate.after(key.getEffectiveDate());
		}
		else if (expirationDate == null) {
			return key.getExpirationDate() == null || activationDate.before(key.getExpirationDate());
		}
		else {
			return (key.getEffectiveDate() == null || expirationDate.after(key.getEffectiveDate()))
					&& (key.getExpirationDate() == null || activationDate.before(key.getExpirationDate()));
		}
	}

	public String toString() {
		return super.toString() + "[act=" + activationDate + ",exp=" + expirationDate + ']';
	}
	
	// TT 2029 
	public void updateEffExpDates(DateSynonym ds) {
		if (activationDate != null && activationDate.equals(ds)) {
			activationDate = ds;
		}
		if (expirationDate != null && expirationDate.equals(ds)) {
			expirationDate = ds;
		}
		
	}

}
