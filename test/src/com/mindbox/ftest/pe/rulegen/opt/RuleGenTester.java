package com.mindbox.ftest.pe.rulegen.opt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.RuleMessageContainer;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.comparator.IDObjectComparator;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.GuidelineRuleGenerator;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.imexport.digest.ImportXMLDigester;
import com.mindbox.pe.server.imexport.digest.RulePrecondition;
import com.mindbox.pe.server.imexport.digest.TemplateRule;
import com.mindbox.pe.server.imexport.digest.TemplateRuleContainer;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.server.model.TimeSliceContainer;

public class RuleGenTester extends AbstractTestWithTestConfig {

	private static String getFileContent(File file) throws IOException {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		BufferedReader in = new BufferedReader(new FileReader(file));
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			out.println(line);
		}
		out.flush();
		out.close();
		in.close();
		return writer.toString();
	}

	private static void populateGridData(ProductGrid grid, int numRows) {
		grid.setNumRows(numRows);
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		for (int i = 0; i < numRows; i++) {
			List<Object> row = new ArrayList<Object>();
			row.add("Citizen");
			row.add("[650-800]");
			row.add("A+");
			row.add("Code");
			row.add("Loan status");
			dataList.add(row);
		}
		grid.setDataList(dataList);
	}

	private static GridTemplate findGuidelineTemplate(List<GridTemplate> templateList, int id) {
		for (Iterator<GridTemplate> iter = templateList.iterator(); iter.hasNext();) {
			GridTemplate element = iter.next();
			if (element.getID() == id) return element;
		}
		return null;
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RuleGenTester Tests");
		suite.addTestSuite(RuleGenTester.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public RuleGenTester(String name) {
		super(name);
	}

	private GridTemplate template;
	private ActionTypeDefinition actionTypeDefinition;
	private DateSynonym ds1, ds2;
	private ProductGrid grid1, grid2, grid3;
	private TimeSliceContainer timeSliceContainer;
	private int rowCount = 1000;
	private int diffCount = 10;
	private int iteration = 3;
	private boolean deinitOutputController = false;

	public void testGatherRuleGenStats() throws Exception {
		logBegin();

		long oldTotal = 0L;
		long newTotal = 0L;
		long startTime;
		for (int i = 0; i < iteration; i++) {
			startTime = System.currentTimeMillis();
			generateRulesOptimized();
			newTotal += System.currentTimeMillis() - startTime;
			logger.info("    NEW: Number of rules: " + GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());

			startTime = System.currentTimeMillis();
			generateRules();
			oldTotal += System.currentTimeMillis() - startTime;
			logger.info("    OLD: Number of rules: " + GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());
		}

		logger.info("*** OLD: Elapsed Time = " + (long) (oldTotal / 3) + " (ms)");
		logger.info("*** NEW: Elapsed Time = " + (long) (newTotal / 3) + " (ms)");
		logEnd();
	}

	private void generateRules() throws Exception {
		try {
			GuidelineRuleGenerator.getInstance().init(new OutputController("Draft"));
			deinitOutputController = true;

			GuidelineReportFilter filter = new GuidelineReportFilter();
			filter.setIncludeGuidelines(true);
			GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, template, filter);
		}
		finally {
			if (deinitOutputController) GuidelineRuleGenerator.getInstance().writeAll();
			if (deinitOutputController) GuidelineRuleGenerator.getInstance().getOutputController().closeErrorWriters();
			Thread.sleep(100);
		}
	}

	private void generateRulesOptimized() throws Exception {
		try {
			GuidelineRuleGenerator.getInstance().init(new OutputController("Draft"));
			deinitOutputController = true;

			GuidelineReportFilter filter = new GuidelineReportFilter();
			filter.setIncludeGuidelines(true);
			GuidelineRuleGenerator.getInstance().generateOptimized(timeSliceContainer, template, filter);
		}
		finally {
			if (deinitOutputController) GuidelineRuleGenerator.getInstance().writeAll();
			if (deinitOutputController) GuidelineRuleGenerator.getInstance().getOutputController().closeErrorWriters();
			Thread.sleep(100);
		}
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		Logger.getLogger("com.mindbox").setLevel(Level.INFO);

		config.initServer();

		DomainClass borrowerClass = ObjectMother.createDomainClass();
		borrowerClass.setName("Borrower");
		borrowerClass.setDeployLabel("pe:borrower");
		DomainAttribute attribute = ObjectMother.createDomainAttribute();
		attribute.setName("Citizenship");
		attribute.setDeployLabel("pe:citizenship");
		attribute.setDeployType(DeployType.STRING);
		borrowerClass.addDomainAttribute(attribute);
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("CreditScore");
		attribute.setDeployLabel("pe:credit-score");
		attribute.setDeployType(DeployType.FLOAT);
		borrowerClass.addDomainAttribute(attribute);
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("CreditLevel");
		attribute.setDeployLabel("pe:credit-level");
		attribute.setDeployType(DeployType.STRING);
		borrowerClass.addDomainAttribute(attribute);
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("Symbol");
		attribute.setDeployLabel("pe:symbol");
		attribute.setDeployType(DeployType.SYMBOL);
		borrowerClass.addDomainAttribute(attribute);

		DomainClass domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);

		DomainClass dealClass = ObjectMother.createDomainClass();
		dealClass.setName("deal");
		dealClass.setDeployLabel("pe:deal");
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("product");
		attribute.setDeployLabel("pe:productcontext");
		attribute.setDeployType(DeployType.SYMBOL);
		dealClass.addDomainAttribute(attribute);
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("channel");
		attribute.setDeployLabel("pe:channel-ctx");
		attribute.setDeployType(DeployType.SYMBOL);
		dealClass.addDomainAttribute(attribute);
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("investor");
		attribute.setDeployLabel("pe:investor-ctx");
		attribute.setDeployType(DeployType.SYMBOL);
		dealClass.addDomainAttribute(attribute);
		attribute = ObjectMother.createDomainAttribute();
		attribute.setName("programID");
		attribute.setDeployLabel("pe:program-ctx");
		attribute.setDeployType(DeployType.INTEGER);
		dealClass.addDomainAttribute(attribute);

		DomainManager.getInstance().addDomainClass(domainClass);
		DomainManager.getInstance().addDomainClass(borrowerClass);
		DomainManager.getInstance().addDomainClass(dealClass);
		DomainManager.getInstance().finishLoading();

		TypeEnumValueManager.getInstance().startLoading();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		TypeEnumValueManager.getInstance().finishLoading();
		ds1 = ObjectMother.createDateSynonym();
		ds2 = ObjectMother.createDateSynonym();

		timeSliceContainer = ObjectMother.createTimeSliceContainer();
		timeSliceContainer.add(TimeSlice.createInstance(null, ds1));
		timeSliceContainer.add(TimeSlice.createInstance(ds1, ds2));
		timeSliceContainer.add(TimeSlice.createInstance(ds2, null));
		timeSliceContainer.freeze();

		DigestedObjectHolder objectHolder = ImportXMLDigester.getInstance().digestImportXML(
				getFileContent(new File("test/data/rule-gen-tester.xml")));
		actionTypeDefinition = objectHolder.getObjects(ActionTypeDefinition.class).get(0);
		GuidelineFunctionManager.getInstance().insertActionTypeDefinition(actionTypeDefinition);

		setupTemplate(objectHolder);
		setupGrids();
	}

	private void setupTemplate(DigestedObjectHolder objectHolder) throws Exception {
		// set up template
		List<GridTemplate> templateList = objectHolder.getObjects(GridTemplate.class, new IDObjectComparator<GridTemplate>());
		this.template = templateList.get(0);
		List<TemplateRuleContainer> ruleContainerList = objectHolder.getObjects(TemplateRuleContainer.class);
		for (TemplateRuleContainer element : ruleContainerList) {
			GridTemplate template = findGuidelineTemplate(templateList, element.getId());
			if (template == null) {
				throw new ImportException("No template of " + element.getId() + " found; element = " + element);
			}
			List<TemplateRule> ruleList = element.getObjects(TemplateRule.class);
			if (ruleList != null) {
				for (TemplateRule rule : ruleList) {
					if (rule.hasPrecondition()) {
						// Just check for the first one for now
						RulePrecondition preCond = rule.getPreconditions()[0];
						if (preCond.getColumnID() > 0) {
							setRulesMessages((RuleMessageContainer) template.getColumn(preCond.getColumnID()), rule);
						}
						else {
							setRulesMessages(template, rule);
						}
					}
					else {
						setRulesMessages(template, rule);
					}
				}
			}
		}
		GuidelineTemplateManager.getInstance().addTemplate(template);
		GuidelineTemplateManager.getInstance().finishLoading();
		assertNotNull(template.getRuleDefinition());
	}

	private void setupGrids() throws Exception {
		// setup grids
		grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setEffectiveDate(null);
		grid1.setExpirationDate(ds1);
		grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setEffectiveDate(ds1);
		grid2.setExpirationDate(ds2);
		grid3 = ObjectMother.createGuidelineGrid(template);
		grid3.setEffectiveDate(ds2);
		grid3.setExpirationDate(null);

		populateGridData(grid1, rowCount);
		populateGridData(grid2, rowCount);
		populateGridData(grid3, rowCount);

		// set up differences
		int row = 1;
		for (int i = 0; i < diffCount; i++) {
			grid2.setValue(row, 1, grid1.getCellValueObject(row, 1, null) + "-2");
			++row;
		}
		for (int i = 0; i < diffCount; i++) {
			grid3.setValue(row, 1, grid2.getCellValueObject(row, 1, null) + "-3");
			row = (row + 1) % rowCount;
		}

		GridManager.getInstance().addProductGrid(grid1);
		GridManager.getInstance().addProductGrid(grid2);
		GridManager.getInstance().addProductGrid(grid3);
		GridManager.getInstance().finishLoading();
	}

	private void setRulesMessages(RuleMessageContainer rmContainer, TemplateRule rule) throws ImportException {
		try {
			RuleDefinition ruleDef = RuleDefinitionUtil.parseToRuleDefinition(rule.getDefinition(), null);
			rmContainer.setRuleDefinition(ruleDef);

			TemplateMessageDigest[] messages = rule.getMessages();
			for (int i = 0; i < messages.length; i++) {
				if (messages[i].getChannel() != null) {
					messages[i].setEntityIDStr(messages[i].getChannel());
				}
				rmContainer.addMessageDigest(messages[i]);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to import " + rmContainer, ex);
			throw new ImportException("Failed to import " + rmContainer + ": " + ex.getMessage());
		}
	}

	@Override
	public void tearDown() throws Exception {
		EntityManager.getInstance().startLoading();
		TypeEnumValueManager.getInstance().startLoading();
		DomainManager.getInstance().startLoading();
		GridManager.getInstance().startLoading();
		GuidelineFunctionManager.getInstance().startLoading();
		GuidelineTemplateManager.getInstance().startLoading();
		clearDir(new File(ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig().getBaseDir()));
		super.tearDown();
	}
}
