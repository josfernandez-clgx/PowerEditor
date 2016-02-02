package com.mindbox.pe.server.spi;

/**
 * Wraps any externally provided user authentication plugin to fulfill
 * the UserAuthenticationProviderPlugin interface by responding "true"
 * to {@link #arePasswordsStoredExternally}.
 */
final class UserAuthenticationProviderPluginWrapper implements UserAuthenticationProviderPlugin {

	private final UserAuthenticationProvider delegate;

	public UserAuthenticationProviderPluginWrapper(UserAuthenticationProvider delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean authenticate(String userId, String password) throws Exception {
		return delegate.authenticate(userId, password);
	}

	@Override
	public boolean arePasswordsStoredExternally() {
		return true;
	}

	@Override
	public void notifySsoAuthentication(String userId) throws Exception {
		delegate.notifySsoAuthentication(userId);
	}
}
