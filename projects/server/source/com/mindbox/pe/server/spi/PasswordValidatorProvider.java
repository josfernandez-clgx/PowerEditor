package com.mindbox.pe.server.spi;

public interface PasswordValidatorProvider {
	public boolean isValidPassword(String candidatePasswordAsClearText,String candidatePasswordAsOneWayHash, String[] recentPasswords);
    
    public String getDescription();    
}
