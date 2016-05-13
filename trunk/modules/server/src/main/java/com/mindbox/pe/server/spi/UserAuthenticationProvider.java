package com.mindbox.pe.server.spi;


/**
 * User authentication provider.
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public interface UserAuthenticationProvider {

	/**
	 * Authenticates the specified user id and password.
	 * If <code>userId</code> is <code>null</code>, this returns <code>false</code>.
	 * If <code>password</code> is <code>null</code>, this returns <code>false</code> only if
	 * empty passwords are not allowed; if allowed, this should treat is as an empty string "".
	 * @param userId user id to authenticate, can be <code>null</code>
	 * @param password password to authenticate, can be <code>null</code>
	 * @return <code>true</code> if the specified user id and pasword are valid; <code>false</code>, otherwise
	 * @throws Exception on error (could not determine authentication)
	 */
	boolean authenticate(String userId, String password) throws Exception;
	
	/**
	 * Notifices succesful SSO authentiation for the specified user id.
	 * @param userId
	 * @throws Exception
	 */
	void notifySsoAuthentication(String userId) throws Exception;
}
