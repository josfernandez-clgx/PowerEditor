package com.mindbox.pe.communication;

import java.util.HashMap;
import java.util.Map;

import com.mindbox.pe.model.IntegerPair;

public class UpdateGridDataResponse extends ResponseComm {

	private static final long serialVersionUID = 201605012006013L;

	private final Map<IntegerPair, Integer> dateSynonymPairGridIdMap = new HashMap<IntegerPair, Integer>();

	public UpdateGridDataResponse(Map<IntegerPair, Integer> dateSynonymPairGridIdMap) {
		if (dateSynonymPairGridIdMap != null) {
			this.dateSynonymPairGridIdMap.putAll(dateSynonymPairGridIdMap);
		}
	}

	public Map<IntegerPair, Integer> getDateSynonymPairGridIdMap() {
		return dateSynonymPairGridIdMap;
	}

	@Override
	public String toString() {
		return String.format("UpdateGridDataResponse[%s]", dateSynonymPairGridIdMap);
	}
}
