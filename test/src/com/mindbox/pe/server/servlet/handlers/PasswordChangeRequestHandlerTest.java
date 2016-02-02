package com.mindbox.pe.server.servlet.handlers;

import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.communication.PasswordChangeRequest;
import com.mindbox.pe.communication.PasswordChangeResponse;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletTest;

/**
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public class PasswordChangeRequestHandlerTest extends ServletTest{
	
	private final String userID = "user";
	private final String passwordAsClearText = "password";
	private final String passwordAsOneWayHash = "5f4dcc3b5aa765d61d8327deb882cf99";
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("PasswordChangeRequestHandler Tests");
		suite.addTestSuite(PasswordChangeRequestHandlerTest.class);
		return suite;
	}
	
	public PasswordChangeRequestHandlerTest(String name) {
		super(name);
	}
	
	public void testFailedAuthentication() throws Exception {
		// record
		User user = ObjectMother.createUser();
		
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);
		
		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(false);
		
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);

		replay();
		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, passwordAsClearText, passwordAsClearText);

		verify();
		assertFailedAuthentication(response, "Invalid Id or Password. Please try again.");
	}
	
	public void testFailedPasswordMatch() throws Exception {
		// record
		User user = ObjectMother.createUser();
		
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);
		
		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(true);
        
		replay();
		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, "abcd", "aABs");

		verify();
		assertFailedPasswordMatch(response, "New passwords dont match. Please retype.");
	}
    
    public void testFailPasswordValidatorRules() throws Exception {
        // record
    	User user = ObjectMother.createUser();
		
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);
		
        getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
        getMockUserAuthenticationProviderControl().setReturnValue(true);
        
		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(ObjectMother.createUser());
        
        replay();
        PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, "new", "new");

        verify();
        assertFailedPasswordMatch(response, "New password doesn't meet validation rules.");
    }

    public void testFailedOldPasswordAndNewOldPasswordNotSame() throws Exception {
		// record
    	User user = ObjectMother.createUser();
		
		getMockSecurityCacheManager().hasUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(true);
		
		getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);
		
		getMockUserAuthenticationProvider().authenticate(userID, passwordAsClearText);
		getMockUserAuthenticationProviderControl().setReturnValue(true);
        
        user.setPassword(passwordAsOneWayHash);

        getMockSecurityCacheManager().getUser(userID);
		getMockSecurityCacheManagerControl().setReturnValue(user);
        
		replay();
        
		PasswordChangeResponse response = doPasswordChange(userID, passwordAsClearText, passwordAsClearText, passwordAsClearText);

		verify();
        
        assertFailedPasswordMatch(response, "New password doesn't meet validation rules.");        
	}
	
	private PasswordChangeResponse doPasswordChange(String user, String oldPwd, String newPwd, String confirmNewPwd) {
		PasswordChangeResponse response = (PasswordChangeResponse) 
				new PasswordChangeRequestHandler().serviceRequest(new PasswordChangeRequest(user, oldPwd, newPwd , confirmNewPwd), getMockHttpServletRequest());
		return response;
	}
	
	private void assertFailedAuthentication(PasswordChangeResponse response, String expectedMsg){
		assertFalse(response.succeeded());
		assertEquals(expectedMsg, response.getMsg());
	}
	
	private void assertFailedPasswordMatch(PasswordChangeResponse response, String expectedMsg){
		assertFalse(response.succeeded());
		assertEquals(expectedMsg, response.getMsg());
	}
	
}
