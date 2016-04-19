package com.mindbox.pe.communication;

/**
 * Redirect response.
 *
 */
public class RedirectResponse extends AbstractSimpleResponse {

	private static final long serialVersionUID = 201008301007000L;

	private final String redirectUrl;

	public RedirectResponse(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	@Override
	public String toString() {
		return String.format("RedirectResponse[url=%s]", redirectUrl);
	}
}
