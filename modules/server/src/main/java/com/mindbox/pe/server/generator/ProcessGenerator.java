/*
 * Created on 2004. 6. 30.
 */
package com.mindbox.pe.server.generator;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseReference;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.server.cache.ProcessManager;

/**
 * Process data (phases and requests) generator.
 *
 * @author kim
 * @since PowerEditor  3.3.0
 */
public class ProcessGenerator implements ErrorContextProvider {

	private static final Logger LOG = Logger.getLogger(ProcessGenerator.class);

	private Phase currentPhase = null;
	private ProcessRequest currentRequest = null;
	private final BufferedGenerator bufferedGenerator;

	public ProcessGenerator(GenerateStats generateStats, OutputController outputController) throws RuleGenerationException {
		this.bufferedGenerator = new DefaultBufferedGenerator(generateStats, outputController, outputController.getProcessFile(), this);
		currentPhase = null;
		currentRequest = null;
	}

	private void generatePhase(Phase phase) {
		currentPhase = phase;
		bufferedGenerator.nextLine();
		bufferedGenerator.openParan();
		bufferedGenerator.print("define-instance ");
		bufferedGenerator.print(phase.getName());
		bufferedGenerator.print(" pa:phase");
		bufferedGenerator.nextLineIndent();
		if (phase.hasPhaseTask()) {
			writeAttributeValue("pa:task", phase.getPhaseTask().getStorageName());
			if (phase.hasPrerequisites() || phase.hasSubPhases()) {
				bufferedGenerator.nextLine();
			}
		}
		if (phase.hasPrerequisites()) {
			bufferedGenerator.openParan();
			bufferedGenerator.print("pa:start-prerequisites ");
			bufferedGenerator.openParan();
			if (phase.isDisjunctivePrereqs()) {
				bufferedGenerator.print("OR ");
			}
			Phase[] phases = phase.getPrerequisites();
			for (int i = 0; i < phases.length; i++) {
				if (i != 0) {
					bufferedGenerator.print(" ");
				}
				bufferedGenerator.print(phases[i].getName());
			}
			bufferedGenerator.closeParan();
			bufferedGenerator.closeParan();
			if (phase.hasSubPhases()) {
				bufferedGenerator.nextLine();
			}
		}
		if (phase.hasSubPhases()) {
			bufferedGenerator.openParan();
			bufferedGenerator.print("pa:sub-phases");
			Phase[] phases = phase.getSubPhases();
			for (int i = 0; i < phases.length; i++) {
				bufferedGenerator.print(" ");
				if (phases[i] instanceof PhaseReference) {
					bufferedGenerator.print(((PhaseReference) phases[i]).getReferecePhase().getName());
				}
				else {
					bufferedGenerator.print(phases[i].getName());
				}
			}
			bufferedGenerator.closeParan();
		}
		bufferedGenerator.nextLineOutdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
	}

	public synchronized void generateProcessData(final int percentageAllocation) throws RuleGenerationException {
		LOG.info(">>> generateProcessData");
		int percentageAdded = 0;
		try {
			final List<ProcessRequest> processRequestList = ProcessManager.getInstance().getAllRequests();
			final List<Phase> phaseList = ProcessManager.getInstance().getAllPhases();

			bufferedGenerator.startGeneration();

			bufferedGenerator.print(";; Generated on " + Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(new Date()) + " by PowerEditor");
			bufferedGenerator.nextLine();

			bufferedGenerator.nextLine();
			bufferedGenerator.print(";;------- REQUEST INSTANCES -------");
			bufferedGenerator.nextLine();

			bufferedGenerator.writeOut();

			final int totalCount = processRequestList.size() + phaseList.size();
			final int percentageToAdd = percentageAllocation / totalCount;

			for (ProcessRequest element : processRequestList) {
				generateRequest(element);

				bufferedGenerator.writeOut();
				bufferedGenerator.getGenerateStats().addPercentComplete(percentageToAdd);
				percentageAdded += percentageToAdd;
			}
			currentRequest = null;

			bufferedGenerator.nextLine();
			bufferedGenerator.print(";;-------  PHASE INSTANCES  -------");
			bufferedGenerator.nextLine();
			bufferedGenerator.writeOut();

			for (Phase phase : phaseList) {
				if (!(phase instanceof PhaseReference)) {
					generatePhase(phase);
					bufferedGenerator.writeOut();
				}
				bufferedGenerator.getGenerateStats().addPercentComplete(percentageToAdd);
				percentageAdded += percentageToAdd;
			}
		}
		catch (Exception ex) {
			LOG.error("Error while generating Process data", ex);
			bufferedGenerator.reportError(ex.getMessage());
		}
		finally {
			bufferedGenerator.endGeneration();
			bufferedGenerator.getGenerateStats().addPercentComplete(percentageAllocation - percentageAdded);
			currentPhase = null;
		}
	}

	private void generateRequest(ProcessRequest request) {
		currentRequest = request;
		bufferedGenerator.nextLine();
		bufferedGenerator.openParan();
		bufferedGenerator.print("define-instance ");
		bufferedGenerator.print(request.getName().toUpperCase());
		bufferedGenerator.print(" TS:REQUEST");
		bufferedGenerator.nextLineIndent();
		writeAttributeValueAsString("ts:description", request.getDescription());
		bufferedGenerator.nextLine();
		writeAttributeValueAsString("ts:display-name", request.getDisplayName());
		bufferedGenerator.nextLine();
		writeAttributeValue("ts:type", request.getRequestType().toUpperCase());
		bufferedGenerator.nextLine();
		writeAttributeValue("ts:purpose", request.getPurpose().toUpperCase());
		bufferedGenerator.nextLine();
		writeAttributeValue("ts:init-function", request.getInitFunction());
		bufferedGenerator.nextLine();
		writeAttributeValue("ts:process-flow-link", (request.getPhase() == null ? "NOT-SPECIFIED" : request.getPhase().getName().toUpperCase()));
		bufferedGenerator.nextLineOutdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
	}

	@Override
	public String getErrorContext() {
		return (currentRequest == null
				? (currentPhase == null ? "" : currentPhase.getID() + ": " + currentPhase.getName())
				: currentRequest.getId() + ": " + currentRequest.getName());
	}

	private void writeAttributeValue(String attribute, String value) {
		bufferedGenerator.openParan();
		bufferedGenerator.print(attribute);
		bufferedGenerator.print(" ");
		bufferedGenerator.print(value);
		bufferedGenerator.closeParan();
	}

	private void writeAttributeValueAsString(String attribute, String value) {
		writeAttributeValue(attribute, '"' + value + '"');
	}
}