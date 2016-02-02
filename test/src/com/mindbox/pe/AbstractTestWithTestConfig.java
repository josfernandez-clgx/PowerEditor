/*
 * Created on Sep 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserAuthenticationProviderPlugin;

/**
 * Base test case for all PowerEdtior tests. Check the available protected fields and methods.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public abstract class AbstractTestWithTestConfig extends AbstractTestBase {

	/**
	 * Reference to an instance of {@link TestConfig}.
	 */
	protected final TestConfig config;

	protected AbstractTestWithTestConfig(String name) {
		super(name);
		this.config = TestConfig.getInstance();
	}

	/* Mock objects and controllers */
	private UserAuthenticationProviderPlugin origUserAuthenticationProvider;
	private MockControl mockUserAuthenticationProviderControl;
	private SecurityCacheManager origSecurityCacheManager;
	private MockControl mockSecurityCacheManagerControl;

	private SessionManager origSessionManager;
	private MockControl mockSessionManagerControl;
	private AuditLogger origAuditLogger;
	private MockControl mockAuditLoggerControl;
	//	private DBIdGenerator origDbIdGenerator; // can't get instance without connecting to db
	private MockControl mockDbIdGeneratorControl;

	// Seems like these next two should be in ServletTest, but there are ref to HttpServletRequests and HttpSessions outside of the Servlet package. Hum?
	private MockControl mockHttpServletRequestControl;
	private MockControl mockHttpSessionControl;

	protected MockControl getMockUserAuthenticationProviderControl() {
		if (origUserAuthenticationProvider == null && mockUserAuthenticationProviderControl == null) {
			origUserAuthenticationProvider = ServiceProviderFactory.getUserAuthenticationProvider();
			mockUserAuthenticationProviderControl = MockControl.createControl(UserAuthenticationProviderPlugin.class);
			ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", mockUserAuthenticationProviderControl.getMock());
		}
		return mockUserAuthenticationProviderControl;
	}

	protected UserAuthenticationProviderPlugin getMockUserAuthenticationProvider() {
		return (UserAuthenticationProviderPlugin) getMockUserAuthenticationProviderControl().getMock();
	}

	protected MockControl getMockSecurityCacheManagerControl() {
		if (origSecurityCacheManager == null && mockSecurityCacheManagerControl == null) {
			origSecurityCacheManager = SecurityCacheManager.getInstance();
			mockSecurityCacheManagerControl = MockClassControl.createControl(SecurityCacheManager.class);
			ReflectionUtil.setPrivate(SecurityCacheManager.class, "instance", mockSecurityCacheManagerControl.getMock());
		}
		return mockSecurityCacheManagerControl;
	}

	protected SecurityCacheManager getMockSecurityCacheManager() {
		return (SecurityCacheManager) getMockSecurityCacheManagerControl().getMock();
	}

	protected MockControl getMockSessionManagerControl() {
		if (origSessionManager == null && mockSessionManagerControl == null) {
			origSessionManager = SessionManager.getInstance();
			mockSessionManagerControl = MockClassControl.createControl(SessionManager.class);
			ReflectionUtil.setPrivate(SessionManager.class, "instance", mockSessionManagerControl.getMock());
		}
		return mockSessionManagerControl;
	}

	protected SessionManager getMockSessionManager() {
		return (SessionManager) getMockSessionManagerControl().getMock();
	}

	protected MockControl getMockAuditLoggerControl() {
		if (origAuditLogger == null && mockAuditLoggerControl == null) {
			origAuditLogger = AuditLogger.getInstance();
			mockAuditLoggerControl = MockClassControl.createControl(AuditLogger.class);
			ReflectionUtil.setPrivate(AuditLogger.class, "instance", mockAuditLoggerControl.getMock());
		}
		return mockAuditLoggerControl;
	}

	protected AuditLogger getMockAuditLogger() {
		return (AuditLogger) getMockAuditLoggerControl().getMock();
	}

	protected MockControl getMockDbIdGeneratorControl() {
		if (/*origDbIdGenerator == null &&*/mockDbIdGeneratorControl == null) {
			//origDbIdGenerator = DBIdGenerator.getInstance(); // can't getInstance without connecting to the db
			mockDbIdGeneratorControl = MockClassControl.createControl(DBIdGenerator.class);
			ReflectionUtil.setPrivate(DBIdGenerator.class, "instance", mockDbIdGeneratorControl.getMock());
		}
		return mockDbIdGeneratorControl;
	}

	protected DBIdGenerator getMockDbIdGenerator() {
		return (DBIdGenerator) getMockDbIdGeneratorControl().getMock();
	}

	protected MockControl getMockHttpServletRequestControl() {
		if (mockHttpServletRequestControl == null) {
			mockHttpServletRequestControl = MockControl.createControl(HttpServletRequest.class);
		}
		return mockHttpServletRequestControl;
	}

	protected HttpServletRequest getMockHttpServletRequest() {
		return (HttpServletRequest) getMockHttpServletRequestControl().getMock();
	}

	protected MockControl getMockHttpSessionControl() {
		if (mockHttpSessionControl == null) {
			mockHttpSessionControl = MockControl.createControl(HttpSession.class);
		}
		return mockHttpSessionControl;
	}

	protected HttpSession getMockHttpSession() {
		return (HttpSession) getMockHttpSessionControl().getMock();
	}

	protected void replay() {
		getMockUserAuthenticationProviderControl().replay();
		getMockSecurityCacheManagerControl().replay();
		getMockSessionManagerControl().replay();
		getMockAuditLoggerControl().replay();
		getMockHttpServletRequestControl().replay();
		getMockHttpSessionControl().replay();
		getMockDbIdGeneratorControl().replay();
	}

	protected void verify() {
		getMockUserAuthenticationProviderControl().verify();
		getMockSecurityCacheManagerControl().verify();
		getMockSessionManagerControl().verify();
		getMockAuditLoggerControl().verify();
		getMockHttpServletRequestControl().verify();
		getMockHttpSessionControl().verify();
		getMockDbIdGeneratorControl().verify();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (config != null) config.resetConfiguration();
		if (mockUserAuthenticationProviderControl != null) {
			ReflectionUtil.setPrivate(ServiceProviderFactory.class, "userAuthenticationProvider", origUserAuthenticationProvider);
		}
		origUserAuthenticationProvider = null;
		mockUserAuthenticationProviderControl = null;

		if (mockSecurityCacheManagerControl != null) {
			ReflectionUtil.setPrivate(SecurityCacheManager.class, "instance", origSecurityCacheManager);
		}
		origSecurityCacheManager = null;
		mockSecurityCacheManagerControl = null;

		if (mockSessionManagerControl != null) {
			ReflectionUtil.setPrivate(SessionManager.class, "instance", origSessionManager);
		}
		origSessionManager = null;
		mockSessionManagerControl = null;

		if (mockAuditLoggerControl != null) {
			ReflectionUtil.setPrivate(AuditLogger.class, "instance", origAuditLogger);
		}
		origAuditLogger = null;
		mockAuditLoggerControl = null;

		if (mockDbIdGeneratorControl != null) {
			ReflectionUtil.setPrivate(DBIdGenerator.class, "instance", null);
		}
		//origDbIdGenerator = null;
		mockDbIdGeneratorControl = null;

		mockHttpServletRequestControl = null;
		mockHttpSessionControl = null;
	}

	@SuppressWarnings("unchecked")
	protected final void resetControlPatternConfigInvariants(boolean generate, String className, Map<String,String> attributes) {
		RuleGenerationConfiguration.ControlPatternConfig controlPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getControlPatternConfig();
		ReflectionUtil.setPrivate(controlPatternConfig, "className", className);
		ReflectionUtil.setPrivate(controlPatternConfig, "generate", Boolean.valueOf(generate));
		if (attributes != null) {
			Map<String,String> map = (Map<String,String>) ReflectionUtil.getPrivate(controlPatternConfig, "attributes");
			map.clear();
			map.putAll(attributes);
		}
	}

	protected final void resetLinkPatternConfigInvariants(String testFunctionName, String variableSuffix, boolean useTestFunction) {
		RuleGenerationConfiguration.LinkPatternConfig linkPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getLinkPatternConfig();
		ReflectionUtil.setPrivate(linkPatternConfig, "className", testFunctionName);
		ReflectionUtil.setPrivate(linkPatternConfig, "prefix", variableSuffix);
		ReflectionUtil.setPrivate(linkPatternConfig, "generate", Boolean.valueOf(useTestFunction));
	}

	protected final void resetRequestPatternConfigInvariants(boolean generate, String className, String prefix, boolean usageAsFocus) {
		RuleGenerationConfiguration.RequestPatternConfig requestPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getRequestPatternConfig();
		ReflectionUtil.setPrivate(requestPatternConfig, "className", className);
		ReflectionUtil.setPrivate(requestPatternConfig, "generate", Boolean.valueOf(generate));
		ReflectionUtil.setPrivate(requestPatternConfig, "usageTypeAsFocus", Boolean.valueOf(usageAsFocus));
		ReflectionUtil.setPrivate(requestPatternConfig, "prefix", prefix);
	}

}
