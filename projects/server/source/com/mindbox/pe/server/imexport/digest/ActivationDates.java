package com.mindbox.pe.server.imexport.digest;

import java.util.Date;

import net.sf.oval.constraint.CheckWith;
import net.sf.oval.constraint.CheckWithCheck;

import com.mindbox.pe.common.config.ConfigUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ActivationDates {

	public static final int UNSPECIFIED_ID = -99999999;

	@SuppressWarnings("serial")
	private static class DateIDCheck implements CheckWithCheck.SimpleCheck {

		@Override
		public boolean isSatisfied(Object validatedObject, Object value) {
			if (value instanceof Integer) {
				int intValue = ((Integer) value).intValue();
				return intValue == UNSPECIFIED_ID || intValue > 0;
			}
			return false;
		}
		
	}
	
	private Date actDate = null;
	private Date expDate = null;
	
	@CheckWith(value=DateIDCheck.class, message="Effective date id must be unspecified or greater than 0")
	private int effectiveDateID = UNSPECIFIED_ID;

	@CheckWith(value=DateIDCheck.class, message="Expiration date id must be unspecified or greater than 0")
	private int expirationDateID = UNSPECIFIED_ID;

	public boolean hasEffectiveDateID() {
		return effectiveDateID != UNSPECIFIED_ID;
	}

	public int getEffectiveDateID() {
		return effectiveDateID;
	}

	public boolean hasExpirationDateID() {
		return expirationDateID != UNSPECIFIED_ID;
	}

	public int getExpirationDateID() {
		return expirationDateID;
	}

	public void setEffectiveDateID(int effectiveDateID) {
		this.effectiveDateID = effectiveDateID;
	}

	public void setExpirationDateID(int expirationDateID) {
		this.expirationDateID = expirationDateID;
	}

	public Date effectiveDate() {
		return actDate;
	}

	public Date expirationDate() {
		return expDate;
	}

	public String toString() {
		return ConfigUtil.toDateXMLString(actDate) + "-" + ConfigUtil.toDateXMLString(expDate);
	}

	public void setActivationDate(String dateStr) {
		this.actDate = ConfigUtil.toDate(dateStr);
	}

	public void setExpirationDate(String dateStr) {
		this.expDate = ConfigUtil.toDate(dateStr);
	}

}