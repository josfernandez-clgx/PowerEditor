package com.mindbox.pe.server.spi;

public interface UserAuthenticationProviderPlugin extends UserAuthenticationProvider {

	/**
	 * Does the user authentication module store passwords externally to PE?
	 * Or, put another way, return false if PE has anything to do with storing user password info.
	 * 
	 * All externally provided user authentication plugins 
	 * must return true (see {@link UserAuthenticationProviderPluginWrapper}).
	 * 
	 * User authentication modules provided as part of the PowerEditor product
	 * may return true or false as appropriate.
	 * @return true if password stored externally; false, otherwise
	 */
	boolean arePasswordsStoredExternally();
}
