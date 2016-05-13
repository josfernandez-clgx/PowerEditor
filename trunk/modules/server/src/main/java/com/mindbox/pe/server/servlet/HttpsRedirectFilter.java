package com.mindbox.pe.server.servlet;

import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class HttpsRedirectFilter implements Filter {

	private int allowedPort = -1;
	private Logger log = Logger.getLogger(getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (!isEmptyAfterTrim(filterConfig.getInitParameter("ALLOWED-PORT"))) {
			this.allowedPort = Integer.valueOf(filterConfig.getInitParameter("ALLOWED-PORT").trim());
		}
		log.info(String.format("Initialized <%d>", allowedPort));
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		if (request.getServerPort() == allowedPort) {
			filterChain.doFilter(request, response);
		}
		else if (request.getScheme().equalsIgnoreCase("http")) {
			String redirectUrlString = String.format(
					"https://%s%s",
					request.getServerName(),
					HttpServletRequest.class.cast(request).getRequestURI());

			log.info(String.format("redirecting to %s...", redirectUrlString));

			HttpServletResponse.class.cast(response).sendRedirect(redirectUrlString);
		}
	}

}
