/*
 * Created on 2004. 6. 30.
 */
package com.mindbox.pe.server.generator;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseReference;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.server.cache.ProcessManager;


/**
 * Process data (phases & requests) generator.
 *
 * @author kim
 * @since PowerEditor  3.3.0
 */
public class ProcessGenerator extends AbstractBufferedGenerator {

	private static final String PROCESS_FILENAME = "app-phase-data";


	private static ProcessGenerator instance = null;

	public static ProcessGenerator getInstance() {
		if (instance == null) {
			instance = new ProcessGenerator();
		}
		return instance;
	}

	private Phase currentPhase = null;
	private ProcessRequest currentRequest = null;

	private ProcessGenerator() {
	}

	public synchronized void init(OutputController outputController) {
		super.init(outputController);
		resetStatus(outputController.getStatus());
		currentPhase = null;
		currentRequest = null;
	}

	public synchronized void generateProcessData() {
		logger.info(">>> generateProcessData");
		List<ProcessRequest> processRequestList = ProcessManager.getInstance().getAllRequests();
		List<Phase> phaseList = ProcessManager.getInstance().getAllPhases();
		print(";; Generated on " + UIConfiguration.FORMAT_DATE_TIME_SEC.format(new Date()) + " by PowerEditor");
		nextLine();

		nextLine();
		print(";;------- REQUEST INSTANCES -------");
		nextLine();
		int totalCount = processRequestList.size() + phaseList.size();
		int count = 0;
		for (ProcessRequest element : processRequestList) {
			generateRequest(element);
			++count;
			setPercentageComplete(100 * count / totalCount);
		}
		currentRequest = null;

		nextLine();
		print(";;-------  PHASE INSTANCES  -------");
		nextLine();
		for (Phase phase : phaseList) {
			if (!(phase instanceof PhaseReference)) {
				generatePhase(phase);
			}
			++count;
			setPercentageComplete(100 * count / totalCount);
		}
		setPercentageComplete(100); // TT 2054 - if no process data, still mark as complete.
		currentPhase = null;
	}

	private void generateRequest(ProcessRequest request) {
		currentRequest = request;
		nextLine();
		openParan();
		print("define-instance ");
		print(request.getName().toUpperCase());
		print(" TS:REQUEST");
		nextLineIndent();
		writeAttributeValueAsString("ts:description", request.getDescription());
		nextLine();
		writeAttributeValueAsString("ts:display-name", request.getDisplayName());
		nextLine();
		writeAttributeValue("ts:type", request.getRequestType().toUpperCase());
		nextLine();
		writeAttributeValue("ts:purpose", request.getPurpose().toUpperCase());
		nextLine();
		writeAttributeValue("ts:init-function", request.getInitFunction());
		nextLine();
		writeAttributeValue("ts:process-flow-link", (request.getPhase() == null
				? "NOT-SPECIFIED"
				: request.getPhase().getName().toUpperCase()));
		nextLineOutdent();
		closeParan();
		nextLine();
	}

	private void writeAttributeValueAsString(String attribute, String value) {
		writeAttributeValue(attribute, '"' + value + '"');
	}

	private void writeAttributeValue(String attribute, String value) {
		openParan();
		print(attribute);
		print(" ");
		print(value);
		closeParan();
	}

	private void generatePhase(Phase phase) {
		currentPhase = phase;
		nextLine();
		openParan();
		print("define-instance ");
		print(phase.getName());
		print(" pa:phase");
		nextLineIndent();
		if (phase.hasPhaseTask()) {
			writeAttributeValue("pa:task", phase.getPhaseTask().getStorageName());
			if (phase.hasPrerequisites() || phase.hasSubPhases()) nextLine();
		}
		if (phase.hasPrerequisites()) {
			openParan();
			print("pa:start-prerequisites ");
			openParan();
			if (phase.isDisjunctivePrereqs()) {
				print("OR ");
			}
			Phase[] phases = phase.getPrerequisites();
			for (int i = 0; i < phases.length; i++) {
				if (i != 0) print(" ");
				print(phases[i].getName());
			}
			closeParan();
			closeParan();
			if (phase.hasSubPhases()) nextLine();
		}
		if (phase.hasSubPhases()) {
			openParan();
			print("pa:sub-phases");
			Phase[] phases = phase.getSubPhases();
			for (int i = 0; i < phases.length; i++) {
				print(" ");
				if (phases[i] instanceof PhaseReference) {
					print(((PhaseReference) phases[i]).getReferecePhase().getName());
				}
				else {
					print(phases[i].getName());
				}
			}
			closeParan();
		}
		nextLineOutdent();
		closeParan();
		nextLine();
	}


	public synchronized void writeAll() throws RuleGenerationException {
		super.writeAll();
		getOutputController().closeRuleWriters();
	}

	protected PrintWriter getPrintWriter(String status, OutputController outputController) throws RuleGenerationException {
		return outputController.getAEFileWriter(status, PROCESS_FILENAME);
	}

	protected String getErrorContext() {
		return (currentRequest == null
				? (currentPhase == null ? "" : currentPhase.getID() + ": " + currentPhase.getName())
				: currentRequest.getId() + ": " + currentRequest.getName());
	}


}