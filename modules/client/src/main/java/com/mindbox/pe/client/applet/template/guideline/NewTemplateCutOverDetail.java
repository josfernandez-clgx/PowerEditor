package com.mindbox.pe.client.applet.template.guideline;

import java.util.List;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineReportData;

public final class NewTemplateCutOverDetail {

	private final DateSynonym dateSynonym;
	private List<GuidelineReportData> guidelinesToCutOver;

	public NewTemplateCutOverDetail(DateSynonym dateSynonym, List<GuidelineReportData> guidelinesToCutOver) {
		super();
		this.dateSynonym = dateSynonym;
		this.guidelinesToCutOver = guidelinesToCutOver;
	}

	public final DateSynonym getDateSynonym() {
		return dateSynonym;
	}

	public final List<GuidelineReportData> getGuidelinesToCutOver() {
		return guidelinesToCutOver;
	}

}
