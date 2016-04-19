package com.mindbox.pe.server.spi.pwd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import com.mindbox.pe.server.spi.PasswordValidatorProvider;

public class RegexpPasswordValidator implements PasswordValidatorProvider {

	/**
	 * Implementation that allows the configuration of regular expressions, 
	 * number of regexps to match, the number of history elements to compare against and
     * the description.
	 */
    
    private String description;
    private Set<Pattern> patterns;
    private int minLength;
    private int minRegexpMatch;   
    private boolean minRegexpMatchSet = false;

    public boolean isValidPassword(String candidatePasswordAsClearText,String candidatePasswordAsOneWayHash, String[] recentPasswords) {
		return isValidLength(candidatePasswordAsClearText) && notReused(candidatePasswordAsOneWayHash, recentPasswords) && hasRequiredChars(candidatePasswordAsClearText);
	}

	private boolean isValidLength(String candidatePassword) {
		return candidatePassword != null && candidatePassword.length() >= minLength;
	}

	private boolean notReused(String candidatePassword, String[] recentPasswords) {
		if (recentPasswords == null) {
			return true;
		}
		
		for (int i = 0; i < recentPasswords.length; i++) {
			String recentPwd = recentPasswords[i];
			if (candidatePassword.equals(recentPwd)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasRequiredChars(String candidatePassword) {
	    if (patterns == null) { 
	        return true;
	    } else {
	        short matchedCount = 0;
	        for (Iterator<Pattern> i = patterns.iterator(); i.hasNext();) {
	            Pattern pattern = i.next();
	            if (pattern.matcher(candidatePassword).matches()) {
	                matchedCount++;
	            }
	        }
	        
	        if(minRegexpMatchSet){
	        return matchedCount >= minRegexpMatch;
	        }else{
	        	return matchedCount == patterns.size();
	        }
	    }
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRegexp(String pattern) {
        if (patterns == null) {
            patterns = new HashSet<Pattern>();
        }
        patterns.add(Pattern.compile(pattern));
    }

    public void setMinLength(String minLength) {
        this.minLength = Integer.parseInt(minLength);
    }

    public void setMinRegexpMatch(String minRegexpMatch) {
        this.minRegexpMatch = Integer.parseInt(minRegexpMatch);
        minRegexpMatchSet = true;
    }

}
