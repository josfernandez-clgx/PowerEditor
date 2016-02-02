package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.config.KbDateFilterConfig;
import com.mindbox.pe.communication.FetchEntityConfigurationRequest;
import com.mindbox.pe.communication.FetchEntityConfigurationResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.DateFilterConfig;
import com.mindbox.pe.server.model.PowerEditorSession;

public final class FetchEntityConfigurationRequestHandler extends AbstractActionRequestHandler<FetchEntityConfigurationRequest> {

	private static KbDateFilterConfig asKbDateFilterConfig(DateFilterConfig dateFilterConfig) {
		if (dateFilterConfig == null) {
			return null;
		}
		else {
			return new KbDateFilterConfig(dateFilterConfig.getBeginDate(), dateFilterConfig.getEndDate());
		}
	}

	public ResponseComm handleRequest(FetchEntityConfigurationRequest request, HttpServletRequest httpservletrequest) {
		PowerEditorSession session = SessionManager.getInstance().getSession(request.getSessionID());
		if (session == null) {
			return new FetchEntityConfigurationResponse(request.getSessionID(), null, null, null);
		}
		else {
			FetchEntityConfigurationResponse response = new FetchEntityConfigurationResponse(
					request.getSessionID(),
					ConfigurationManager.getInstance().getEntityConfiguration(),
					asKbDateFilterConfig(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig().getDateFilterConfig()),
					TypeEnumValueManager.getInstance().getTypeEnumValueMap());
			return response;
		}
	}
}