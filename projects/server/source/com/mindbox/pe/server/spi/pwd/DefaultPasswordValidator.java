package com.mindbox.pe.server.spi.pwd;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.spi.PasswordValidatorProvider;

public class DefaultPasswordValidator implements PasswordValidatorProvider {

    private String description;
	/**
	 * Passwords are valid if not blank.
	 */
    
	public boolean isValidPassword(String candidatePasswordAsClearText,String candidatePasswordAsOneWayHash, String[] recentPasswords) {
		return candidatePasswordAsClearText != null && !UtilBase.isEmptyAfterTrim(candidatePasswordAsClearText);
	}

    public String getDescription() {
        return description == null ? "" : description;  
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
