package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.server.ServerTestObjectMother.createCBRAttributeElement;
import static com.mindbox.pe.server.ServerTestObjectMother.createCBRCaseBase;
import static com.mindbox.pe.server.ServerTestObjectMother.createCBRCaseClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createCBRScoringFunction;
import static com.mindbox.pe.server.ServerTestObjectMother.createCbrCaseBaseElement;
import static com.mindbox.pe.server.ServerTestObjectMother.createCbrCaseElement;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBRCaseClass;
import com.mindbox.pe.model.cbr.CBRScoringFunction;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.data.ActivationDates;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRAttributeElement.AttributeType;
import com.mindbox.pe.xsd.data.CBRAttributeElement.CaseBase;
import com.mindbox.pe.xsd.data.CBRAttributeElement.ValueRange;
import com.mindbox.pe.xsd.data.CBRAttributeValueElement;
import com.mindbox.pe.xsd.data.CBRAttributeValueElement.Attribute;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement.CaseClass;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement.ScoringFunction;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement.AttributeValues;
import com.mindbox.pe.xsd.data.CBRCaseElement.CaseActions;
import com.mindbox.pe.xsd.data.CBRCaseElement.CaseActions.CaseAction;
import com.mindbox.pe.xsd.data.CBRDataElement;

public class CBRDataImporterTest extends AbstractTestBase {

	private CBRDataElement cbrDataElement;
	private CBRDataImporter cbrDataImporter;
	private ImportBusinessLogic importBusinessLogicMock;

	private void callProcessData() throws Exception {
		callProcessData(false);
	}

	private void callProcessData(boolean merge) throws Exception {
		callProcessData(merge, new HashMap<String, Integer>());
	}

	private void callProcessData(boolean merge, Map<String, Integer> cbrDataIdMap) throws Exception {
		callProcessData(merge, cbrDataIdMap, new HashMap<Integer, Integer>());
	}

	private void callProcessData(boolean merge, Map<String, Integer> cbrDataIdMap, Map<Integer, Integer> dateSynonymIdMap) throws Exception {
		cbrDataImporter.importResult = new ImportResult();
		cbrDataImporter.merge = merge;
		cbrDataImporter.processData(cbrDataElement, new CBRImportOptionalData(cbrDataIdMap, dateSynonymIdMap));
	}

	@Before
	public void setUp() throws Exception {
		setUpMock();
		cbrDataElement = new CBRDataElement();
		cbrDataImporter = new CBRDataImporter(importBusinessLogicMock);
	}

	public void setUpMock() throws Exception {
		importBusinessLogicMock = createMock(ImportBusinessLogic.class);
	}

	@After
	public void tearDown() throws Exception {
		CBRManager.getInstance().startDbLoading();
	}

	//@Test -- disabled
	public void testProcessAttributeHappyCaseWithMergeAndCachedCaseBase() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(cbrCaseBase.getId());

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRAttribute(attribute, true, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		cbrDataElement.getCbrAttribute().add(attribute);

		callProcessData(true, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeHappyCaseWithMergeAndNonCachedCaseBase() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		final CBRCaseBase caseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(caseBase);

		final int specifiedId = createInt();
		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(specifiedId);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		cbrDataidMap.put("casebase-" + specifiedId, caseBase.getId());
		importBusinessLogicMock.importCBRAttribute(attribute, true, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		cbrDataElement.getCbrAttribute().add(attribute);

		callProcessData(true, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(caseBase.getId(), attribute.getCaseBase().getId().intValue());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeHappyCaseWithNoMerge() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(cbrCaseBase.getId());

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRAttribute(attribute, false, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		cbrDataElement.getCbrAttribute().add(attribute);

		callProcessData(false, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeWithInalidAttributeTypeAddsError() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(-1);

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(cbrCaseBase.getId());

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();

		cbrDataElement.getCbrAttribute().add(attribute);

		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No attribute type of id -1 found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeWithInalidValueRangeAddsError() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(cbrCaseBase.getId());

		attribute.setValueRange(new ValueRange());
		attribute.getValueRange().setId(-1);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();

		cbrDataElement.getCbrAttribute().add(attribute);

		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No value range of id -1 found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeWithInvalidCaseBaseAndMergeAddsError() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		final int incorrectId = createInt();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(incorrectId);

		cbrDataElement.getCbrAttribute().add(attribute);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(true, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeWithInvalidCaseBaseAndNoMergeAddsError() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		final int incorrectId = createInt();
		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(incorrectId);

		cbrDataElement.getCbrAttribute().add(attribute);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeWithNoAttributeTypeAddsError() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		attribute.setCaseBase(new CaseBase());
		attribute.getCaseBase().setId(cbrCaseBase.getId());

		cbrDataElement.getCbrAttribute().add(attribute);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("attribute type is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessAttributeWithNoCaseBaseAddsError() throws Exception {
		final CBRAttributeElement attribute = createCBRAttributeElement();
		attribute.setAttributeType(new AttributeType());
		attribute.getAttributeType().setId(createInt());

		cbrDataElement.getCbrAttribute().add(attribute);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("case base is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	// ------ Case base import tests

	//@Test -- disabled
	public void testProcessCaseBaseHappyCase() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();
		final CBRCaseClass caseClass = createCBRCaseClass();
		caseBase.setCaseClass(new CaseClass());
		caseBase.getCaseClass().setId(caseClass.getId());

		final CBRScoringFunction scoringFunction = createCBRScoringFunction();
		caseBase.setScoringFunction(new ScoringFunction());
		caseBase.getScoringFunction().setId(scoringFunction.getId());

		cbrDataElement.getCbrCaseBase().add(caseBase);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRCaseBase(caseBase, false, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		callProcessData(false, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseBaseWithInvalidCaseClassAddsError() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();
		caseBase.setCaseClass(new CaseClass());
		caseBase.getCaseClass().setId(-1);

		final CBRScoringFunction scoringFunction = createCBRScoringFunction();
		caseBase.setScoringFunction(new ScoringFunction());
		caseBase.getScoringFunction().setId(scoringFunction.getId());

		cbrDataElement.getCbrCaseBase().add(caseBase);

		callProcessData();
		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case class of id -1 found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseBaseWithInvalidEffectiveDateAddsError() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();
		final CBRCaseClass caseClass = createCBRCaseClass();
		caseBase.setCaseClass(new CaseClass());
		caseBase.getCaseClass().setId(caseClass.getId());

		final CBRScoringFunction scoringFunction = createCBRScoringFunction();
		caseBase.setScoringFunction(new ScoringFunction());
		caseBase.getScoringFunction().setId(scoringFunction.getId());

		cbrDataElement.getCbrCaseBase().add(caseBase);

		ActivationDates activationDates = new ActivationDates();
		activationDates.setEffectiveDateID(createInt());
		caseBase.setActivationDates(activationDates);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No date synonym with id " + activationDates.getEffectiveDateID() + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseBaseWithInvalidExpirationDateAddsError() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();
		final CBRCaseClass caseClass = createCBRCaseClass();
		caseBase.setCaseClass(new CaseClass());
		caseBase.getCaseClass().setId(caseClass.getId());

		final CBRScoringFunction scoringFunction = createCBRScoringFunction();
		caseBase.setScoringFunction(new ScoringFunction());
		caseBase.getScoringFunction().setId(scoringFunction.getId());

		cbrDataElement.getCbrCaseBase().add(caseBase);

		ActivationDates activationDates = new ActivationDates();
		activationDates.setExpirationDateID(createInt());
		caseBase.setActivationDates(activationDates);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No date synonym with id " + activationDates.getExpirationDateID() + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseBaseWithInvalidScoringFunctionAddsError() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();
		final CBRCaseClass caseClass = createCBRCaseClass();
		caseBase.setCaseClass(new CaseClass());
		caseBase.getCaseClass().setId(caseClass.getId());

		caseBase.setScoringFunction(new ScoringFunction());
		caseBase.getScoringFunction().setId(-1);

		cbrDataElement.getCbrCaseBase().add(caseBase);

		callProcessData(false);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No scoring function of id " + -1 + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseBaseWithNoCaseClassAddsError() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();

		final CBRScoringFunction scoringFunction = createCBRScoringFunction();
		caseBase.setScoringFunction(new ScoringFunction());
		caseBase.getScoringFunction().setId(scoringFunction.getId());

		cbrDataElement.getCbrCaseBase().add(caseBase);

		callProcessData();
		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("case class is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseBaseWithNoScoringFunctionAddsError() throws Exception {
		final CBRCaseBaseElement caseBase = createCbrCaseBaseElement();
		final CBRCaseClass caseClass = createCBRCaseClass();
		caseBase.setCaseClass(new CaseClass());
		caseBase.getCaseClass().setId(caseClass.getId());

		cbrDataElement.getCbrCaseBase().add(caseBase);

		callProcessData(false);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("scoring function is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	// ------- Case Tests

	//@Test -- disabled
	public void testProcessCaseHappyCaseWithMergeAndCachedCaseBase() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(cbrCaseBase.getId());

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRCase(caseElement, true, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		callProcessData(true, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseHappyCaseWithMergeAndNonCachedCaseBase() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());

		final int specifiedId = createInt();
		caseElement.getCaseBase().setId(specifiedId);

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		cbrDataidMap.put("casebase-" + specifiedId, cbrCaseBase.getId());
		importBusinessLogicMock.importCBRCase(caseElement, true, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		callProcessData(true, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(cbrCaseBase.getId(), caseElement.getCaseBase().getId().intValue());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseHappyCaseWithNoMerge() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(cbrCaseBase.getId());

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRCase(caseElement, false, cbrDataidMap, null);
		replay(importBusinessLogicMock);

		callProcessData(false, cbrDataidMap);
		verify(importBusinessLogicMock);

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithInvalidAttributeAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(cbrCaseBase.getId());

		cbrDataElement.getCbrCase().add(caseElement);

		final CBRAttributeValueElement attributeValueElement = new CBRAttributeValueElement();
		attributeValueElement.setId(createInt());
		attributeValueElement.setDescription("av1");
		attributeValueElement.setValue("av1");
		attributeValueElement.setAttribute(new Attribute());
		attributeValueElement.getAttribute().setId(-1);
		caseElement.setAttributeValues(new AttributeValues());
		caseElement.getAttributeValues().getAttributeValue().add(attributeValueElement);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No attribute of id " + -1 + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithInvalidCaseActionAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(cbrCaseBase.getId());

		caseElement.setCaseActions(new CaseActions());
		caseElement.getCaseActions().getCaseAction().add(new CaseAction());

		caseElement.getCaseActions().getCaseAction().get(0).setId(-1);

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case action of id " + -1 + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithInvalidCaseBaseAndMergeAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(-1);

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(true, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + -1 + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithInvalidCaseBaseAndNoMergeAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();


		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(-1);

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + -1 + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithInvalidEffectiveDateAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(cbrCaseBase.getId());

		cbrDataElement.getCbrCase().add(caseElement);

		final int incorrectId = createInt();
		ActivationDates activationDates = new ActivationDates();
		activationDates.setEffectiveDateID(incorrectId);
		caseElement.setActivationDates(activationDates);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No date synonym with id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithInvalidExpirationDateAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		final CBRCaseBase cbrCaseBase = createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

		caseElement.setCaseBase(new CBRCaseElement.CaseBase());
		caseElement.getCaseBase().setId(cbrCaseBase.getId());

		cbrDataElement.getCbrCase().add(caseElement);

		final int incorrectId = createInt();
		ActivationDates activationDates = new ActivationDates();
		activationDates.setExpirationDateID(incorrectId);
		caseElement.setActivationDates(activationDates);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No date synonym with id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	//@Test -- disabled
	public void testProcessCaseWithNoCaseBaseAddsError() throws Exception {
		final CBRCaseElement caseElement = createCbrCaseElement();

		cbrDataElement.getCbrCase().add(caseElement);

		final Map<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("case base is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}
}
