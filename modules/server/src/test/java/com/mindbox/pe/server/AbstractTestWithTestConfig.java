package com.mindbox.pe.server;

import static com.mindbox.pe.common.ReflectionUtil.getPrivate;
import static com.mindbox.pe.common.ReflectionUtil.setPrivate;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;

import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;
import com.mindbox.pe.server.config.LinkPatternConfigHelper;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserAuthenticationProvider;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

/**
 * Base test case for all PowerEdtior tests. Check the available protected fields and methods.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public abstract class AbstractTestWithTestConfig extends AbstractTestBase {

	public static void replayMocks(Object... mocks) {
		for (Object mock : mocks) {
			if (mock != null) {
				replay(mock);
			}
		}
	}

	public static void verifyMocks(Object... mocks) {
		for (Object mock : mocks) {
			if (mock != null) {
				verify(mock);
			}
		}
	}

	/**
	 * Reference to an instance of {@link TestConfig}.
	 */
	protected TestConfig config;

	/* Mock objects and controllers */
	private UserAuthenticationProviderPlugin origUserAuthenticationProvider;
	private UserAuthenticationProvider userAuthenticationProviderMock;
	private SecurityCacheManager origSecurityCacheManager;
	private SecurityCacheManager securityCacheManagerMock;
	private SessionManager origSessionManager;
	private SessionManager sessionManagerMock;
	private AuditLogger origAuditLogger;
	private AuditLogger auditLoggerMock;
	private DBIdGenerator dbIdGeneratorMock;

	private HttpServletRequest httpServletRequestMock;
	private HttpSession httpSessionMock;

	protected final AuditLogger getMockAuditLogger() {
		if (auditLoggerMock == null) {
			origAuditLogger = AuditLogger.getInstance();
			auditLoggerMock = createMock(AuditLogger.class);
			setPrivate(AuditLogger.class, "instance", auditLoggerMock);
		}
		return auditLoggerMock;
	}

	protected final DBIdGenerator getMockDbIdGenerator() {
		if (dbIdGeneratorMock == null) {
			dbIdGeneratorMock = createMock(DBIdGenerator.class);
			setPrivate(DBIdGenerator.class, "instance", dbIdGeneratorMock);
		}
		return dbIdGeneratorMock;
	}

	protected final HttpServletRequest getMockHttpServletRequest() {
		if (httpServletRequestMock == null) {
			httpServletRequestMock = createMock(HttpServletRequest.class);
		}
		return httpServletRequestMock;
	}

	protected final HttpSession getMockHttpSession() {
		if (httpSessionMock == null) {
			httpSessionMock = createMock(HttpSession.class);
		}
		return httpSessionMock;
	}

	protected final SecurityCacheManager getMockSecurityCacheManager() {
		if (origSecurityCacheManager == null && securityCacheManagerMock == null) {
			origSecurityCacheManager = SecurityCacheManager.getInstance();
			securityCacheManagerMock = createMock(SecurityCacheManager.class);
			setPrivate(SecurityCacheManager.class, "instance", securityCacheManagerMock);
		}
		return securityCacheManagerMock;
	}

	protected final SessionManager getMockSessionManager() {
		if (sessionManagerMock == null) {
			sessionManagerMock = createMock(SessionManager.class);
			origSessionManager = SessionManager.getInstance();
			setPrivate(SessionManager.class, "instance", sessionManagerMock);
		}
		return sessionManagerMock;
	}

	protected final UserAuthenticationProvider getMockUserAuthenticationProvider() {
		if (origUserAuthenticationProvider == null && userAuthenticationProviderMock == null) {
			origUserAuthenticationProvider = ServiceProviderFactory.getUserAuthenticationProvider();
			userAuthenticationProviderMock = createMock(UserAuthenticationProviderPlugin.class);
			setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", userAuthenticationProviderMock);
		}
		return userAuthenticationProviderMock;
	}

	protected void replayAllMocks() {
		replayMocks(auditLoggerMock, dbIdGeneratorMock, httpServletRequestMock, httpSessionMock, securityCacheManagerMock, sessionManagerMock, userAuthenticationProviderMock);
	}

	@SuppressWarnings("unchecked")
	protected final void resetControlPatternConfigInvariants(boolean generate, String className, Map<String, String> attributes) {
		ControlPatternConfigHelper controlPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getControlPatternConfig();
		controlPatternConfig.getPattern().setClazz(className);
		controlPatternConfig.getPattern().setGenerate(generate ? Boolean.TRUE : Boolean.FALSE);
		if (attributes != null) {
			Map<String, String> map = (Map<String, String>) getPrivate(controlPatternConfig, "attributes");
			map.clear();
			map.putAll(attributes);
		}
	}

	protected final void resetLinkPatternConfigInvariants(String testFunctionName, String variableSuffix, boolean useTestFunction) {
		final LinkPatternConfigHelper linkPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLinkPatternConfig();
		linkPatternConfig.getPattern().setClazz(testFunctionName);
		linkPatternConfig.getPattern().setPrefix(variableSuffix);
		linkPatternConfig.getPattern().setGenerate(useTestFunction ? Boolean.TRUE : Boolean.FALSE);
	}

	protected final void resetRequestPatternConfigInvariants(boolean generate, String className, String prefix, boolean usageAsFocus) {
		Pattern requestPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getRequestPatternConfig();
		requestPatternConfig.setClazz(className);
		requestPatternConfig.setGenerate(generate ? Boolean.TRUE : Boolean.FALSE);
		requestPatternConfig.setUsageTypeAsFocus(usageAsFocus ? Boolean.TRUE : Boolean.FALSE);
		requestPatternConfig.setPrefix(prefix);
	}

	@Before
	public void setUp() throws Exception {
		log("in setUp()");
		config = TestConfig.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		if (config != null) config.resetConfiguration();
		if (userAuthenticationProviderMock != null) {
			setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", origUserAuthenticationProvider);
		}

		if (securityCacheManagerMock != null) {
			setPrivate(SecurityCacheManager.class, "instance", origSecurityCacheManager);
		}

		if (sessionManagerMock != null) {
			setPrivate(SessionManager.class, "instance", origSessionManager);
		}

		if (auditLoggerMock != null) {
			setPrivate(AuditLogger.class, "instance", origAuditLogger);
		}

		if (dbIdGeneratorMock != null) {
			setPrivate(DBIdGenerator.class, "instance", null);
		}
	}

	protected void verifyAllMocks() {
		verifyMocks(auditLoggerMock, dbIdGeneratorMock, httpServletRequestMock, httpSessionMock, securityCacheManagerMock, sessionManagerMock, userAuthenticationProviderMock);
	}

}
