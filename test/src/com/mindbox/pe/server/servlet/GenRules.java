package com.mindbox.pe.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGenerator;

/**
 * A simple servlet to invoke the PE rules generator. 

 * <dl>1 optional parameter
 * 		<dt>status</dt>
 * 		<dd>The KB status to which the rules should deployed.</dd>
 * </dl>
 */
public class GenRules extends HttpServlet {
	public static final String DEFAULT_STATUS = "Draft";

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.getWriter().println(generateRules(getParameter(req, "status", DEFAULT_STATUS)));
	}

	private GenerateStats generateRules(String status) throws ServletException {
		try {
			RuleGenerator ruleGenerator = new RuleGenerator(new OutputController(status));
			GuidelineReportFilter filter = new GuidelineReportFilter();
			filter.setIncludeGuidelines(true);
			filter.setIncludeParameters(true);
			filter.setIncludeProcessData(true);
			filter.setIncludeCBR(true);
			filter.setThisStatusAndAbove(status);
			ruleGenerator.generate(filter);
			return ruleGenerator.getGuidelineStats();
		}
		catch (RuleGenerationException e) {
			throw new ServletException(e);
		}
	}

	private String getParameter(HttpServletRequest req, String key, String def) {
		return req.getParameter(key) == null ? def : req.getParameter(key);
	}
}
