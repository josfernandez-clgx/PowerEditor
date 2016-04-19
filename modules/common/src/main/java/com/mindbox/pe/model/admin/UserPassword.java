package com.mindbox.pe.model.admin;

import java.io.Serializable;
import java.util.Date;

/**
 * User password.
 * @since PowerEditor 5.1
 */
public final class UserPassword implements Serializable {

	private static final long serialVersionUID = 2003052312327001L;

	private String password;
	private Date passwordChangeDate;

	public UserPassword(String pwd, Date passwordChangeDate) {
		this.password = pwd;
		this.passwordChangeDate = passwordChangeDate;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getPasswordChangeDate() {
        return passwordChangeDate;
    }

    public void setPasswordChangeDate(Date passwordChangeDate) {
        this.passwordChangeDate = passwordChangeDate;
    }
    
    public boolean equals(UserPassword userpassword) {
        return userpassword.getPassword().equals(password) && 
        userpassword.getPasswordChangeDate().equals(passwordChangeDate);
    }
    
    public String toString(){
    	return "[ password="+password+" , passwordChangeDate="+passwordChangeDate+" ]";
    }

}