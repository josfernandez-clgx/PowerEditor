package com.mindbox.pe.server.imexport.digest;


import java.util.Date;

import com.mindbox.pe.common.config.ConfigUtil;

/** For Import only
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public class UserPassword {

	private String encryptedPassword;
	private Date passwordChangeDate;
	
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String password) {
        this.encryptedPassword = password;
    }

    public Date getPasswordChangeDate() {
        return passwordChangeDate;
    }

    public void setPasswordChangeDate(String dateStr) {
        this.passwordChangeDate =  ConfigUtil.toDate(dateStr);;
    }
    
    public void setPasswordChangeDateString(String dateStr) {
        this.passwordChangeDate =  ConfigUtil.toDate(dateStr);;
    }
    
    public String toString(){
    	return "[ encryptedPassword="+encryptedPassword+" , passwordChangeDate="+passwordChangeDate+" ]";
    }

}
