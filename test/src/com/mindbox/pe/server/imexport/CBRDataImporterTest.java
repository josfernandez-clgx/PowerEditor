package com.mindbox.pe.server.imexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeType;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.CBRCaseClass;
import com.mindbox.pe.model.CBRScoringFunction;
import com.mindbox.pe.model.CBRValueRange;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.imexport.digest.ActivationDates;
import com.mindbox.pe.server.imexport.digest.CBRCaseBaseDigest;
import com.mindbox.pe.server.imexport.digest.CBRCaseDigest;

public class CBRDataImporterTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("CBRDataImporterTest Tests");
		suite.addTestSuite(CBRDataImporterTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CBRDataImporter cbrDataImporter;
	private ImportBusinessLogic importBusinessLogicMock;
	private MockControl importBusinessLogicMockControl;

	public CBRDataImporterTest(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpMock();
		cbrDataImporter = new CBRDataImporter(importBusinessLogicMock);
	}

	public void testProcessCaseBaseWithNoCaseClassAddsError() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(null);
		callProcessData(caseBase);
		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("case class is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseBaseWithInvalidCaseClassAddsError() throws Exception {
		int incorrectId = ObjectMother.createInt();
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(new CBRCaseClass(-1, String.valueOf(incorrectId), "disp-" + incorrectId));
		callProcessData(caseBase);
		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case class of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseBaseWithNoScoringFunctionAddsError() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(ObjectMother.createCBRCaseClass());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseBase, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("scoring function is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseBaseWithInvalidScoringFunctionAddsError() throws Exception {
		int incorrectId = ObjectMother.createInt();
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(ObjectMother.createCBRCaseClass());
		caseBase.setScoringFunction(new CBRScoringFunction(-1, String.valueOf(incorrectId), "disp-" + incorrectId));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseBase, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(
				"No scoring function of id " + incorrectId + " found",
				cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseBaseWithInvalidEffectiveDateAddsError() throws Exception {
		int incorrectId = ObjectMother.createInt();
		CBRCaseBaseDigest caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(ObjectMother.createCBRCaseClass());
		caseBase.setScoringFunction(ObjectMother.createCBRScoringFunction());
		ActivationDates activationDates = new ActivationDates();
		activationDates.setEffectiveDateID(incorrectId);
		caseBase.setActivationDates(activationDates);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseBase, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(
				"No date synonym with id " + incorrectId + " found",
				cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseBaseWithInvalidExpirationDateAddsError() throws Exception {
		int incorrectId = ObjectMother.createInt();
		CBRCaseBaseDigest caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(ObjectMother.createCBRCaseClass());
		caseBase.setScoringFunction(ObjectMother.createCBRScoringFunction());
		ActivationDates activationDates = new ActivationDates();
		activationDates.setExpirationDateID(incorrectId);
		caseBase.setActivationDates(activationDates);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseBase, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(
				"No date synonym with id " + incorrectId + " found",
				cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseBaseHappyCase() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBaseDigest();
		caseBase.setCaseClass(ObjectMother.createCBRCaseClass());
		caseBase.setScoringFunction(ObjectMother.createCBRScoringFunction());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRCaseBase(caseBase, false, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(caseBase, false, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithNoCaseBaseAddsError() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("case base is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithInvalidCaseBaseAndMergeAddsError() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		int incorrectId = ObjectMother.createInt();
		caseDigest.setCaseBase(new CBRCaseBase(-1, String.valueOf(incorrectId), incorrectId + "-dsec"));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, true, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithInvalidCaseBaseAndNoMergeAddsError() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		int incorrectId = ObjectMother.createInt();
		caseDigest.setCaseBase(new CBRCaseBase(-1, String.valueOf(incorrectId), incorrectId + "-dsec"));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithInvalidEffectiveDateAddsError() throws Exception {
		int incorrectId = ObjectMother.createInt();
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		caseDigest.setCaseBase(ObjectMother.createCBRCaseBase());
		ActivationDates activationDates = new ActivationDates();
		activationDates.setEffectiveDateID(incorrectId);
		caseDigest.setActivationDates(activationDates);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(
				"No date synonym with id " + incorrectId + " found",
				cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithInvalidExpirationDateAddsError() throws Exception {
		int incorrectId = ObjectMother.createInt();
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		caseDigest.setCaseBase(ObjectMother.createCBRCaseBase());
		ActivationDates activationDates = new ActivationDates();
		activationDates.setExpirationDateID(incorrectId);
		caseDigest.setActivationDates(activationDates);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(
				"No date synonym with id " + incorrectId + " found",
				cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithInvalidCaseActionAddsError() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		int incorrectId = ObjectMother.createInt();
		caseDigest.setCaseBase(ObjectMother.createCBRCaseBase());
		List<CBRCaseAction> caseActions = new ArrayList<CBRCaseAction>();
		caseActions.add(new CBRCaseAction(-1, String.valueOf(incorrectId), "name-" + incorrectId));
		caseDigest.setCaseActions(caseActions);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case action of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseWithInvalidAttributeAddsError() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		int incorrectId = ObjectMother.createInt();
		caseDigest.setCaseBase(ObjectMother.createCBRCaseBase());
		List<CBRAttributeValue> attributeValues = new ArrayList<CBRAttributeValue>();
		CBRAttributeValue attributeValue = new CBRAttributeValue(1, "av1", "av1");
		attributeValue.setAttribute(new CBRAttribute(-1, String.valueOf(incorrectId), "name-" + incorrectId));
		attributeValues.add(attributeValue);
		caseDigest.setAttributeValues(attributeValues);

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(caseDigest, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No attribute of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseHappyCaseWithNoMerge() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		caseDigest.setCaseBase(ObjectMother.createCBRCaseBase());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRCase(caseDigest, false, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(caseDigest, false, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseHappyCaseWithMergeAndCachedCaseBase() throws Exception {
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		caseDigest.setCaseBase(ObjectMother.createCBRCaseBase());
		CBRManager.getInstance().addCBRCaseBase(caseDigest.getCaseBase());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRCase(caseDigest, true, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(caseDigest, true, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessCaseHappyCaseWithMergeAndNonCachedCaseBase() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(caseBase);
		CBRCaseDigest caseDigest = ObjectMother.createCBRCaseDigest();
		int specifiedId = ObjectMother.createInt();
		caseDigest.setCaseBase(new CBRCaseBase(-1, String.valueOf(specifiedId), specifiedId + "-dsec"));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		cbrDataidMap.put("casebase-" + specifiedId, caseBase.getId());
		importBusinessLogicMock.importCBRCase(caseDigest, true, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(caseDigest, true, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(caseBase.getId(), caseDigest.getCaseBase().getId());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeWithNoAttributeTypeAddsError() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(attribute, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("attribute type is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeWithInalidAttributeTypeAddsError() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		int incorrectId = ObjectMother.createInt();
		attribute.setAttributeType(new CBRAttributeType(-1, String.valueOf(incorrectId), "name-" + incorrectId));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(attribute, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(
				"No attribute type of id " + incorrectId + " found",
				cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeWithInalidValueRangeAddsError() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());
		attribute.setCaseBase(ObjectMother.createCBRCaseBase());
		int incorrectId = ObjectMother.createInt();
		attribute.setValueRange(new CBRValueRange(
				-1,
				String.valueOf(incorrectId),
				"name-" + incorrectId,
				"desc-" + incorrectId,
				true,
				true,
				true,
				false,
				false));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(attribute, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No value range of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeWithNoCaseBaseAddsError() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(attribute, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("case base is required.", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeWithInvalidCaseBaseAndMergeAddsError() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());
		int incorrectId = ObjectMother.createInt();
		attribute.setCaseBase(new CBRCaseBase(-1, String.valueOf(incorrectId), incorrectId + "-dsec"));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(attribute, true, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeWithInvalidCaseBaseAndNoMergeAddsError() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());
		int incorrectId = ObjectMother.createInt();
		attribute.setCaseBase(new CBRCaseBase(-1, String.valueOf(incorrectId), incorrectId + "-dsec"));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		callProcessData(attribute, false, cbrDataidMap);

		assertTrue(cbrDataImporter.importResult.hasError());
		assertEquals(1, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals("No case base of id " + incorrectId + " found", cbrDataImporter.importResult.getErrorMessages().get(0).getMessage());
		assertEquals(0, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeHappyCaseWithNoMerge() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());
		attribute.setCaseBase(ObjectMother.createCBRCaseBase());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRAttribute(attribute, false, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(attribute, false, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeHappyCaseWithMergeAndCachedCaseBase() throws Exception {
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());
		attribute.setCaseBase(ObjectMother.createCBRCaseBase());
		CBRManager.getInstance().addCBRCaseBase(attribute.getCaseBase());

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		importBusinessLogicMock.importCBRAttribute(attribute, true, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(attribute, true, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	public void testProcessAttributeHappyCaseWithMergeAndNonCachedCaseBase() throws Exception {
		CBRCaseBase caseBase = ObjectMother.createCBRCaseBase();
		CBRManager.getInstance().addCBRCaseBase(caseBase);
		CBRAttribute attribute = ObjectMother.createCBRAttribute();
		attribute.setAttributeType(ObjectMother.createCBRAttributeType());
		int specifiedId = ObjectMother.createInt();
		attribute.setCaseBase(new CBRCaseBase(-1, String.valueOf(specifiedId), specifiedId + "-dsec"));

		HashMap<String, Integer> cbrDataidMap = new HashMap<String, Integer>();
		cbrDataidMap.put("casebase-" + specifiedId, caseBase.getId());
		importBusinessLogicMock.importCBRAttribute(attribute, true, cbrDataidMap, null);
		importBusinessLogicMockControl.replay();

		callProcessData(attribute, true, cbrDataidMap);
		importBusinessLogicMockControl.verify();

		assertFalse(cbrDataImporter.importResult.hasError());
		assertEquals(caseBase.getId(), attribute.getCaseBase().getId());
		assertEquals(0, cbrDataImporter.importResult.getErrorMessages().size());
		assertEquals(1, cbrDataImporter.importResult.getMessages().size());
	}

	protected void setUpMock() throws Exception {
		importBusinessLogicMockControl = MockControl.createControl(ImportBusinessLogic.class);
		importBusinessLogicMock = (ImportBusinessLogic) importBusinessLogicMockControl.getMock();
	}

	private void callProcessData(Object cbrData) throws Exception {
		callProcessData(cbrData, false);
	}

	private void callProcessData(Object cbrData, boolean merge) throws Exception {
		callProcessData(cbrData, merge, new HashMap<String, Integer>());
	}

	private void callProcessData(Object cbrData, boolean merge, Map<String, Integer> cbrDataIdMap) throws Exception {
		callProcessData(cbrData, merge, cbrDataIdMap, new HashMap<Integer, Integer>());
	}

	private void callProcessData(Object cbrData, boolean merge, Map<String, Integer> cbrDataIdMap, Map<Integer, Integer> dateSynonymIdMap)
			throws Exception {
		DigestedObjectHolder objectHolder = new DigestedObjectHolder();
		objectHolder.addObject(cbrData);
		cbrDataImporter.importResult = new ImportResult();
		cbrDataImporter.merge = merge;
		cbrDataImporter.processData("filename", objectHolder, new CBRImportOptionalData(cbrDataIdMap, dateSynonymIdMap));
	}

	@Override
	public void tearDown() throws Exception {
		CBRManager.getInstance().startDbLoading();
		super.tearDown();
	}
}
